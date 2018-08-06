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
import org.wso2.carbon.identity.application.template.mgt.cache.ServiceProviderTemplateCache;
import org.wso2.carbon.identity.application.template.mgt.cache.ServiceProviderTemplateCacheKey;
import org.wso2.carbon.identity.application.template.mgt.dao.ApplicationTemplateDAO;
import org.wso2.carbon.identity.application.template.mgt.dao.impl.ApplicationTemplateDAOImpl;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;

import java.util.List;

/**
 * Application template management service implementation.
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
    public void importApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        applicationTemplateDAO.createApplicationTemplate(spTemplateDTO, tenantDomain);

        ServiceProviderTemplateCacheKey templateCacheKey = new ServiceProviderTemplateCacheKey(spTemplateDTO.getName(),
                tenantDomain);
        ServiceProviderTemplateCache.getInstance().addToCache(templateCacheKey, spTemplateDTO);
    }

    @Override
    public SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        String loadingSpTemplateName = templateName;
        if (StringUtils.isNotBlank(loadingSpTemplateName) && !isExistingTemplate(loadingSpTemplateName, tenantDomain)) {
            throw new IdentityApplicationTemplateMgtException(String.format("Template with name: %s, is not configured " +
                    "for tenant: %s.", loadingSpTemplateName, tenantDomain));
        }
        if (StringUtils.isEmpty(loadingSpTemplateName)) {
            if (isExistingTemplate(ApplicationTemplateMgtConstants.TENANT_DEFAULT_SP_TEMPLATE_NAME, tenantDomain)) {
                loadingSpTemplateName = ApplicationTemplateMgtConstants.TENANT_DEFAULT_SP_TEMPLATE_NAME;
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Template name is not specified. Checking for the default template of " +
                            "tenant: %s", tenantDomain));
                }
            } else {
                loadingSpTemplateName = ApplicationTemplateMgtConstants.SYSTEM_DEFAULT_SP_TEMPLATE_NAME;
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Template name is not specified. Checking for the system wide " +
                            "default template"));
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
    public boolean isExistingTemplate(String templateName, String tenantDomain) throws IdentityApplicationTemplateMgtException {

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
    public void deleteApplicationTemplate(String templateName, String tenantDomain) throws IdentityApplicationTemplateMgtException {

        ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
        applicationTemplateDAO.deleteApplicationTemplate(templateName, tenantDomain);

        ServiceProviderTemplateCacheKey templateCacheKey = new ServiceProviderTemplateCacheKey(templateName,
                tenantDomain);
        ServiceProviderTemplateCache.getInstance().clearCacheEntry(templateCacheKey);
    }

    @Override
    public String exportApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        return loadApplicationTemplate(templateName, tenantDomain).getSpContent();
    }

    private SpTemplateDTO getSpTemplateFromDB(String templateName, String tenantDomain,
                                              ServiceProviderTemplateCacheKey templateCacheKey)
            throws IdentityApplicationTemplateMgtException {

        SpTemplateDTO spTemplateDTO;ApplicationTemplateDAO applicationTemplateDAO = new ApplicationTemplateDAOImpl();
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
}
