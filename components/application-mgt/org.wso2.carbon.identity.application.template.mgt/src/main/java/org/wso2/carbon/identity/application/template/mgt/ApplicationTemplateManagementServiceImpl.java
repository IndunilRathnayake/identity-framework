/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.identity.application.template.mgt;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.listener.ApplicationMgtListener;
import org.wso2.carbon.identity.application.template.mgt.cache.ServiceProviderTemplateCache;
import org.wso2.carbon.identity.application.template.mgt.cache.ServiceProviderTemplateCacheKey;
import org.wso2.carbon.identity.application.template.mgt.dao.ApplicationTemplateDAO;
import org.wso2.carbon.identity.application.template.mgt.dao.impl.ApplicationTemplateDAOImpl;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;
import org.wso2.carbon.identity.application.template.mgt.internal.ApplicationMgtListenerServiceComponent;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * Default service implementation of {@link ApplicationTemplateManagementService}. This handles all the application
 * template related functionality.
 */
public class ApplicationTemplateManagementServiceImpl extends ApplicationTemplateManagementService {

    private static Log log = LogFactory.getLog(ApplicationTemplateManagementServiceImpl.class);
    private static volatile ApplicationTemplateManagementServiceImpl appTemplateMgtService;

    private ApplicationTemplateManagementServiceImpl() {

    }

    public static ApplicationTemplateManagementService getInstance() {

        if (appTemplateMgtService == null) {
            synchronized (ApplicationTemplateManagementService.class) {
                if (appTemplateMgtService == null) {
                    appTemplateMgtService = new ApplicationTemplateManagementServiceImpl();
                }
            }
        }
        return appTemplateMgtService;
    }

    @Override
    public void createServiceProviderAsTemplate(ServiceProvider serviceProvider, SpTemplateDTO spTemplateDTO,
                                                String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (serviceProvider != null) {
            ServiceProvider updatedSP = removeUnsupportedTemplateConfigs(serviceProvider);
            String serviceProviderTemplateXml = marshalSP(updatedSP, tenantDomain);
            spTemplateDTO.setSpContent(serviceProviderTemplateXml);
        }
        importApplicationTemplate(spTemplateDTO, tenantDomain);
    }

    @Override
    public void importApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        try {
            validateTemplateXMLSyntax(spTemplateDTO);

            ServiceProvider serviceProvider = unmarshalSP(spTemplateDTO.getSpContent(), tenantDomain);
            validateUnsupportedTemplateConfigs(serviceProvider);

            /**
             * Invoking the application mgt listeners to validate the existence of the configured property values
             * in template
             */
            Collection<ApplicationMgtListener> listeners =
                    ApplicationMgtListenerServiceComponent.getApplicationMgtListeners();
            for (ApplicationMgtListener listener : listeners) {
                if (listener.isEnable()) {
                    listener.doImportServiceProvider(serviceProvider);
                }
            }

            ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
            applicationTemplateDAO.createApplicationTemplate(spTemplateDTO, tenantDomain);

            ServiceProviderTemplateCacheKey templateCacheKey = new ServiceProviderTemplateCacheKey(spTemplateDTO.getName(),
                    tenantDomain);
            ServiceProviderTemplateCache.getInstance().addToCache(templateCacheKey, spTemplateDTO);
        } catch (IdentityApplicationManagementException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error when importing SP template: ",
                    spTemplateDTO.getName()), e);
        }
    }

    @Override
    public SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        String loadingSpTemplateName = templateName;
        if (StringUtils.isNotBlank(loadingSpTemplateName) && !isExistingTemplate(loadingSpTemplateName, tenantDomain)) {
            throw new IdentityApplicationTemplateMgtException(String.format("Template with name: %s, is not configured "
                    + "for tenant: %s.", loadingSpTemplateName, tenantDomain));
        }
        if (StringUtils.isEmpty(loadingSpTemplateName)) {
            if (isExistingTemplate(ApplicationTemplateMgtConstants.TENANT_DEFAULT_SP_TEMPLATE_NAME, tenantDomain)) {
                loadingSpTemplateName = ApplicationTemplateMgtConstants.TENANT_DEFAULT_SP_TEMPLATE_NAME;
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Template name is not specified. Checking for the default template of " +
                            "tenant: %s", tenantDomain));
                }
            }
        }

        ServiceProviderTemplateCacheKey templateCacheKey = new ServiceProviderTemplateCacheKey(loadingSpTemplateName,
                tenantDomain);
        SpTemplateDTO spTemplateDTO = getSpTemplateFromCache(loadingSpTemplateName, tenantDomain, templateCacheKey);
        if (spTemplateDTO != null) {
            return spTemplateDTO;
        }

        spTemplateDTO = getSpTemplateFromDB(loadingSpTemplateName, tenantDomain, templateCacheKey);
        if (spTemplateDTO != null) {
            return spTemplateDTO;
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Template with name: %s is not registered.", loadingSpTemplateName));
        }
        return new SpTemplateDTO();
    }

    @Override
    public boolean isExistingTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        return applicationTemplateDAO.isExistingTemplate(templateName, tenantDomain);
    }

    @Override
    public List<SpTemplateDTO> getAllApplicationTemplates(String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        return applicationTemplateDAO.getAllApplicationTemplates(tenantDomain);
    }

    @Override
    public List<String> getAllApplicationTemplateNames(String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        return applicationTemplateDAO.getAllApplicationTemplateNames(tenantDomain);
    }

    @Override
    public void deleteApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        applicationTemplateDAO.deleteApplicationTemplate(templateName, tenantDomain);

        ServiceProviderTemplateCacheKey templateCacheKey = new ServiceProviderTemplateCacheKey(templateName,
                tenantDomain);
        ServiceProviderTemplateCache.getInstance().clearCacheEntry(templateCacheKey);
    }

    @Override
    public void updateApplicationTemplate(String templateName, SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (StringUtils.isNotBlank(spTemplateDTO.getName()) && isExistingTemplate(spTemplateDTO.getName(),
                tenantDomain)) {
            throw new IdentityApplicationTemplateMgtException(String.format("Template with name: %s, is already " +
                    "configured for tenant: %s.", spTemplateDTO.getName(), tenantDomain));
        }
        validateTemplateXMLSyntax(spTemplateDTO);

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        applicationTemplateDAO.updateApplicationTemplate(templateName, spTemplateDTO, tenantDomain);

        ServiceProviderTemplateCacheKey templateCacheKey = new ServiceProviderTemplateCacheKey(templateName,
                tenantDomain);
        ServiceProviderTemplateCache.getInstance().clearCacheEntry(templateCacheKey);
    }

    @Override
    public String exportApplicationTemplate(String templateName, boolean exportSecrets, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        try {
            String templateXml = loadApplicationTemplate(templateName, tenantDomain).getSpContent();
            ServiceProvider serviceProvider = unmarshalSP(templateXml, tenantDomain);
            // invoking the application mgt listeners
            Collection<ApplicationMgtListener> listeners =
                    ApplicationMgtListenerServiceComponent.getApplicationMgtListeners();
            for (ApplicationMgtListener listener : listeners) {
                if (listener.isEnable()) {
                    listener.doExportServiceProvider(serviceProvider, exportSecrets);
                }
            }
            return marshalSP(serviceProvider, tenantDomain);
        } catch (IdentityApplicationManagementException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error when exporting SP template: ",
                    templateName), e);
        }
    }

    private void validateTemplateXMLSyntax(SpTemplateDTO spTemplateDTO) throws IdentityApplicationTemplateMgtException {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.parse(new InputSource(new StringReader(spTemplateDTO.getSpContent())));
        } catch (SAXException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Template: %s is not well-formed.",
                    spTemplateDTO.getName()), e);
        } catch (IOException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error in validating the xml syntax of " +
                    "template :", spTemplateDTO.getName()), e);
        }
    }

    private SpTemplateDTO getSpTemplateFromDB(String templateName, String tenantDomain,
                                              ServiceProviderTemplateCacheKey templateCacheKey)
            throws IdentityApplicationTemplateMgtException {

        SpTemplateDTO spTemplateDTO;
        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        spTemplateDTO = applicationTemplateDAO.loadApplicationTemplate(templateName, tenantDomain);

        if (spTemplateDTO != null) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Template with name: %s, is taken from database for tenant: %s ",
                        templateName, tenantDomain));
            }
            ServiceProviderTemplateCache.getInstance().addToCache(templateCacheKey, spTemplateDTO);
            return spTemplateDTO;
        }
        return null;
    }

    private SpTemplateDTO getSpTemplateFromCache(String templateName, String tenantDomain,
                                                 ServiceProviderTemplateCacheKey templateCacheKey) {

        SpTemplateDTO spTemplateDTO = ServiceProviderTemplateCache.getInstance().getValueFromCache(templateCacheKey);
        if (spTemplateDTO != null) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Template with name: %s, is taken from cache of tenant: %s ",
                        templateName, tenantDomain));
            }
            return spTemplateDTO;
        }
        return null;
    }

    private ServiceProvider unmarshalSP(String spTemplateXml, String tenantDomain)
            throws IdentityApplicationManagementException {

        if (StringUtils.isEmpty(spTemplateXml)) {
            throw new IdentityApplicationManagementException("Empty SP template configuration is provided to " +
                    "unmarshal");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceProvider.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (ServiceProvider) unmarshaller.unmarshal(new ByteArrayInputStream(
                    spTemplateXml.getBytes(StandardCharsets.UTF_8)));

        } catch (JAXBException e) {
            throw new IdentityApplicationManagementException("Error in reading Service Provider template " +
                    "configuration ", e);
        }
    }

    private String marshalSP(ServiceProvider serviceProvider, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceProvider.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            DocumentBuilderFactory docBuilderFactory = IdentityUtil.getSecuredDocumentBuilderFactory();
            Document document = docBuilderFactory.newDocumentBuilder().newDocument();
            marshaller.marshal(serviceProvider, document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS,
                    "AuthenticationScript inboundConfiguration");

            StringWriter stringBuilder = new StringWriter();
            StreamResult result = new StreamResult(stringBuilder);
            transformer.transform(new DOMSource(document), result);
            return stringBuilder.getBuffer().toString();
        } catch (JAXBException | ParserConfigurationException | TransformerException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error in exporting Service Provider " +
                    "template from SP %s@%s", serviceProvider.getApplicationName(), tenantDomain), e);
        }
    }

    private ServiceProvider removeUnsupportedTemplateConfigs(ServiceProvider serviceProvider) {

        ServiceProvider updatedSp = serviceProvider;
        if (updatedSp != null) {
            updatedSp.setApplicationName(null);
            updatedSp.setDescription(null);
            updatedSp.setApplicationID(0);
            updatedSp.setCertificateContent(null);
            updatedSp.setInboundAuthenticationConfig(null);
        }
        return updatedSp;
    }

    private void validateUnsupportedTemplateConfigs(ServiceProvider serviceProvider)
            throws IdentityApplicationTemplateMgtException {

        if (serviceProvider.getInboundAuthenticationConfig() != null) {
            throw new IdentityApplicationTemplateMgtException("Inbound configurations are not supported in application "
                    + "template");
        }
        if (serviceProvider.getApplicationID() != 0) {
            throw new IdentityApplicationTemplateMgtException("Application ID is not supported in application " +
                    "template");
        }
        if (serviceProvider.getApplicationName() != null) {
            throw new IdentityApplicationTemplateMgtException("Application name is not supported in application " +
                    "template");
        }
        if (serviceProvider.getDescription() != null) {
            throw new IdentityApplicationTemplateMgtException("Application description is not supported in application "
                    + "template");
        }
        if (serviceProvider.getCertificateContent() != null) {
            throw new IdentityApplicationTemplateMgtException("Application certificate not supported in application " +
                    "template");
        }
    }
}
