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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;

import java.util.List;

/**
 * Application template management admin service.
 */
public class ApplicationTemplateManagementAdminService extends AbstractAdmin {

    private static final Log log = LogFactory.getLog(ApplicationTemplateManagementAdminService.class);
    private ApplicationTemplateManagementService applicationTemplateMgtService;

    public void createServiceProviderAsTemplate(ServiceProvider serviceProvider, SpTemplateDTO spTemplateDTO)
            throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            applicationTemplateMgtService.createServiceProviderAsTemplate(serviceProvider, spTemplateDTO,
                    getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while importing service provider as a template for tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Import application template as a XML file from UI.
     *
     * @param spTemplateDTO service provider template info
     * @throws IdentityApplicationTemplateMgtException
     */
    public void importApplicationTemplate(SpTemplateDTO spTemplateDTO)
            throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            applicationTemplateMgtService.importApplicationTemplate(spTemplateDTO, getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while importing application template for tenant: " + getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Load application template.
     *
     * @param templateName service provider template name
     * @return service provider template info
     * @throws IdentityApplicationTemplateMgtException
     */
    public SpTemplateDTO loadApplicationTemplate(String templateName) throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            return applicationTemplateMgtService.loadApplicationTemplate(templateName, getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while loading application template:" + templateName + " for tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Get all the application templates.
     *
     * @return list of application templates
     * @throws IdentityApplicationTemplateMgtException
     */
    public List<SpTemplateDTO> getAllApplicationTemplates() throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            return applicationTemplateMgtService.getAllApplicationTemplates(getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while getting all the application templates for tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Get all the application template names.
     *
     * @return list of application template names
     * @throws IdentityApplicationTemplateMgtException
     */
    public List<String> getAllApplicationTemplateNames() throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            return applicationTemplateMgtService.getAllApplicationTemplateNames(getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while getting all the application template names for tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Delete an application template.
     *
     * @param templateName name of the template
     * @throws IdentityApplicationTemplateMgtException
     */
    public void deleteApplicationTemplate(String templateName) throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            applicationTemplateMgtService.deleteApplicationTemplate(templateName, getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while deleting application template:" + templateName + " in tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Update an application template.
     *
     * @param spTemplateDTO SP template info to be updated
     * @throws IdentityApplicationTemplateMgtException
     */
    public void updateApplicationTemplate(String templateName, SpTemplateDTO spTemplateDTO) throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            applicationTemplateMgtService.updateApplicationTemplate(templateName, spTemplateDTO, getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while updating application template:" + spTemplateDTO.getName() + " in tenant: " +
                            getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Export a application template.
     *
     * @param templateName name of the template
     * @param exportSecrets is export the secrets
     * @return XML string of the
     * @throws IdentityApplicationTemplateMgtException
     */
    public String exportApplicationTemplate(String templateName, boolean exportSecrets) throws IdentityApplicationTemplateMgtException {

        try {
            applicationTemplateMgtService = ApplicationTemplateManagementServiceImpl.getInstance();
            return applicationTemplateMgtService.exportApplicationTemplate(templateName, exportSecrets, getTenantDomain());
        } catch (IdentityApplicationTemplateMgtException idpException) {
            log.error("Error while exporting application template: " + templateName + " in tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }
}