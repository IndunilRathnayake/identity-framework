/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.identity.application.mgt.defaultauth;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementValidationException;
import org.wso2.carbon.identity.application.common.model.DefaultAuthenticationSequence;
import org.wso2.carbon.identity.application.common.model.LocalAndOutboundAuthenticationConfig;
import org.wso2.carbon.identity.application.mgt.ApplicationMgtValidator;
import org.wso2.carbon.identity.application.mgt.cache.DefaultAuthSeqMgtCache;
import org.wso2.carbon.identity.application.mgt.cache.DefaultAuthSeqMgtCacheEntry;
import org.wso2.carbon.identity.application.mgt.cache.IdentityServiceProviderCache;
import org.wso2.carbon.identity.application.mgt.dao.DefaultAuthSeqMgtDAO;
import org.wso2.carbon.identity.application.mgt.dao.impl.DefaultAuthSeqMgtDAOImpl;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
import javax.xml.transform.stream.StreamSource;

/**
 * This service provides the services needed to manage tenant wise default authentication sequences.
 */
public class DefaultAuthSeqMgtServiceImpl extends DefaultAuthSeqMgtService {

    private static final Log log = LogFactory.getLog(DefaultAuthSeqMgtServiceImpl.class);
    private final ApplicationMgtValidator applicationMgtValidator = new ApplicationMgtValidator();
    private static volatile DefaultAuthSeqMgtServiceImpl defaultAuthSeqMgtService;

    private DefaultAuthSeqMgtServiceImpl() {

    }

    public static DefaultAuthSeqMgtServiceImpl getInstance() {

        if (defaultAuthSeqMgtService == null) {
            synchronized (DefaultAuthSeqMgtServiceImpl.class) {
                if (defaultAuthSeqMgtService == null) {
                    defaultAuthSeqMgtService = new DefaultAuthSeqMgtServiceImpl();
                }
            }
        }
        return defaultAuthSeqMgtService;
    }

    @Override
    public void createDefaultAuthenticationSeq(DefaultAuthenticationSequence sequence, String tenantDomain)
            throws DefaultAuthSeqMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating default authentication sequence in tenant: %s", tenantDomain));
        }

        if (sequence.getContent() == null && sequence.getContentXml() != null) {
            sequence.setContent(unmarshalDefaultAuthSeq(sequence.getContentXml(), tenantDomain));
        }
        validateAuthSeqConfiguration(sequence, tenantDomain, "Validation error when creating default " +
                "authentication sequence in : ");

        DefaultAuthSeqMgtDAO seqMgtDAO = new DefaultAuthSeqMgtDAOImpl();
        if (seqMgtDAO.isDefaultAuthSeqExists(tenantDomain)) {
            throw new DefaultAuthSeqMgtClientException(new String[]{"Default auth sequence is already exists in " +
                    "tenant: " + tenantDomain});
        }

        try {
            seqMgtDAO.createDefaultAuthenticationSeq(sequence, tenantDomain);
            addDefaultAuthSeqToCache(sequence, tenantDomain);

            IdentityServiceProviderCache.getInstance().clear();
            if (log.isDebugEnabled()) {
                log.debug("Clearing ServiceProviderCache of tenant: " + tenantDomain);
            }
        } catch (DefaultAuthSeqMgtServerException e) {
            throw new DefaultAuthSeqMgtServerException("Error when creating default authentication sequence for " +
                    "tenant:" + tenantDomain, e);
        }
    }

    @Override
    public DefaultAuthenticationSequence getDefaultAuthenticationSeq(String tenantDomain)
            throws DefaultAuthSeqMgtException {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving default authentication sequence of tenant: " + tenantDomain);
        }

        if (DefaultAuthSeqMgtCache.getInstance().isEnabled()) {
            DefaultAuthSeqMgtCacheEntry entry = DefaultAuthSeqMgtCache.getInstance().getValueFromCache(tenantDomain);
            if (entry != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Default authentication sequence of tenant: " + tenantDomain +
                            " is retrieved from cache.");
                }
                return entry.getSequence();
            }
        }

        DefaultAuthSeqMgtDAO seqMgtDAO = new DefaultAuthSeqMgtDAOImpl();
        DefaultAuthenticationSequence sequence = seqMgtDAO.getDefaultAuthenticationSeq(tenantDomain);

        if (sequence != null) {
            addDefaultAuthSeqToCache(sequence, tenantDomain);
        }
        return sequence;
    }

    @Override
    public DefaultAuthenticationSequence getDefaultAuthenticationSeqInXML(String tenantDomain)
            throws DefaultAuthSeqMgtException {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving default authentication sequence of tenant: " + tenantDomain);
        }

        DefaultAuthenticationSequence sequence = null;
        if (DefaultAuthSeqMgtCache.getInstance().isEnabled()) {
            DefaultAuthSeqMgtCacheEntry entry = DefaultAuthSeqMgtCache.getInstance().getValueFromCache(tenantDomain);
            if (entry != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Default authentication sequence of tenant: " + tenantDomain +
                            " is retrieved from cache.");
                }
                sequence = entry.getSequence();
            }
        }

        if (sequence == null) {
            DefaultAuthSeqMgtDAO seqMgtDAO = new DefaultAuthSeqMgtDAOImpl();
            sequence = seqMgtDAO.getDefaultAuthenticationSeq(tenantDomain);
            addDefaultAuthSeqToCache(sequence, tenantDomain);
        }

        if (sequence != null) {
            if (sequence.getContentXml() == null) {
                String sequenceInXML = marshalDefaultAuthSeq(sequence.getContent(), tenantDomain);
                sequence.setContentXml(sequenceInXML);
            }
        }
        return sequence;
    }

    @Override
    public void deleteDefaultAuthenticationSeq(String tenantDomain) throws DefaultAuthSeqMgtException {

        if (log.isDebugEnabled()) {
            log.debug("Deleting default authentication sequence of tenant: " + tenantDomain);
        }

        DefaultAuthSeqMgtDAO seqMgtDAO = new DefaultAuthSeqMgtDAOImpl();
        seqMgtDAO.deleteDefaultAuthenticationSeq(tenantDomain);

        removeDefaultAuthSeqFromCache(tenantDomain);

        IdentityServiceProviderCache.getInstance().clear();
        if (log.isDebugEnabled()) {
            log.debug("Clearing ServiceProviderCache of tenant: " + tenantDomain);
        }
    }

    @Override
    public void updateDefaultAuthenticationSeq(DefaultAuthenticationSequence sequence, String tenantDomain)
            throws DefaultAuthSeqMgtException {

        if (log.isDebugEnabled()) {
            log.debug("Updating default authentication sequence of tenant: " + tenantDomain);
        }

        validateAuthSeqConfiguration(sequence, tenantDomain, "Validation error when updating default " +
                "authentication sequence in : ");

        DefaultAuthSeqMgtDAO seqMgtDAO = new DefaultAuthSeqMgtDAOImpl();
        seqMgtDAO.updateDefaultAuthenticationSeq(sequence, tenantDomain);

        addDefaultAuthSeqToCache(sequence, tenantDomain);

        IdentityServiceProviderCache.getInstance().clear();
        if (log.isDebugEnabled()) {
            log.debug("Clearing ServiceProviderCache of tenant: " + tenantDomain);
        }
    }

    private void addDefaultAuthSeqToCache(DefaultAuthenticationSequence sequence, String tenantDomain) {

        if (DefaultAuthSeqMgtCache.getInstance().isEnabled()) {
            DefaultAuthSeqMgtCacheEntry entry = new DefaultAuthSeqMgtCacheEntry(sequence);
            DefaultAuthSeqMgtCache.getInstance().addToCache(tenantDomain, entry);
            if (log.isDebugEnabled()) {
                log.debug("Default authentication sequence for tenant: " + tenantDomain + " is added to cache.");
            }
        }
    }

    private void removeDefaultAuthSeqFromCache(String tenantDomain) {
        if (DefaultAuthSeqMgtCache.getInstance().isEnabled()) {
            DefaultAuthSeqMgtCache.getInstance().clearCacheEntry(tenantDomain);
            if (log.isDebugEnabled()) {
                log.debug("Default authentication sequence for tenant: " + tenantDomain + " is removed from cache.");
            }
        }
    }

    private void validateAuthSeqConfiguration(DefaultAuthenticationSequence sequence, String tenantDomain,
                                              String errorMsg)
            throws DefaultAuthSeqMgtClientException, DefaultAuthSeqMgtServerException {
        try {
            applicationMgtValidator.validateLocalAndOutBoundAuthenticationConfig(sequence.getContent(), tenantDomain);
        } catch (IdentityApplicationManagementValidationException e) {
            log.error(errorMsg + tenantDomain);
            for (String msg : e.getValidationMsg()) {
                log.error(msg);
            }
            throw new DefaultAuthSeqMgtClientException(e.getValidationMsg());
        } catch (IdentityApplicationManagementException e) {
            throw new DefaultAuthSeqMgtServerException(errorMsg, e);
        }
    }

    private String marshalDefaultAuthSeq(LocalAndOutboundAuthenticationConfig sequence, String tenantDomain)
            throws DefaultAuthSeqMgtException {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(LocalAndOutboundAuthenticationConfig.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            DocumentBuilderFactory docBuilderFactory = IdentityUtil.getSecuredDocumentBuilderFactory();
            Document document = docBuilderFactory.newDocumentBuilder().newDocument();
            marshaller.marshal(sequence, document);
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
            throw new DefaultAuthSeqMgtException("Error in marshalling default authentication sequence in: " +
                    tenantDomain, e);
        }
    }

    /**
     * Convert xml file of default authentication sequence to object.
     *
     * @param defaultAuthSeq xml string of the default authentication sequence
     * @param tenantDomain   tenant domain name
     * @return LocalAndOutboundAuthenticationConfig instance
     * @throws DefaultAuthSeqMgtException Auth Sequence Management Exception
     */
    private LocalAndOutboundAuthenticationConfig unmarshalDefaultAuthSeq(String defaultAuthSeq, String tenantDomain)
            throws DefaultAuthSeqMgtException {

        if (StringUtils.isEmpty(defaultAuthSeq)) {
            throw new DefaultAuthSeqMgtClientException(new String[]{"Empty default authentication sequence " +
                    "configuration is provided"});
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(LocalAndOutboundAuthenticationConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<LocalAndOutboundAuthenticationConfig> root = unmarshaller.unmarshal(
                    new StreamSource(new ByteArrayInputStream(defaultAuthSeq.getBytes(StandardCharsets.UTF_8))),
                    LocalAndOutboundAuthenticationConfig.class);
            if (root.getName().getLocalPart().equalsIgnoreCase(LocalAndOutboundAuthenticationConfig.class
                    .getSimpleName())) {
                return root.getValue();
            }
            throw new DefaultAuthSeqMgtClientException(new String[]{"Syntax error in the provided default " +
                    "authentication sequence"});
        } catch (JAXBException e) {
            throw new DefaultAuthSeqMgtException(String.format("Error in reading default authentication " +
                    "sequence configuration in tenant: %s", tenantDomain), e);
        }
    }
}
