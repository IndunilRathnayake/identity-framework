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

import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;

import java.util.List;

/**
 * Application template management service abstract class.
 */
public abstract class ApplicationTemplateManagementService {

    /**
     * Add configured service provider as a template.
     *
     * @param serviceProvider Service provider to be configured as a template
     * @param spTemplateDTO service provider template basic info
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract void createServiceProviderAsTemplate(ServiceProvider serviceProvider, SpTemplateDTO spTemplateDTO,
                                                         String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Import Service provider template.
     *
     * @param spTemplateDTO service provider template info
     * @param tenantDomain tenant domain
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract void importApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Load Service provider template.
     *
     * @param templateName template name
     * @param tenantDomain tenant domain
     * @return service provider template info
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Check existence of a application template.
     *
     * @param templateName template name
     * @param tenantDomain tenant domain
     * @return true if a template with the specified name exists
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract boolean isExistingTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Get all service provider templates.
     *
     * @param tenantDomain tenant domain
     * @return list of all application template info
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract List<SpTemplateDTO> getAllApplicationTemplates(String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Get all Service provider template names.
     *
     * @param tenantDomain tenant domain
     * @return list of application template names
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract List<String> getAllApplicationTemplateNames(String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Delete a application template.
     *
     * @param templateName name of the template
     * @param tenantDomain tenant domain
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract void deleteApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Update an application template.
     *
     * @param spTemplateDTO SP template info to be updated
     * @param tenantDomain tenant domain
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract void updateApplicationTemplate(String templateName, SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Export a application template.
     *
     * @param templateName name of the template
     * @param tenantDomain tenant domain
     * @param exportSecrets is export the secrets
     * @return XML string of the template content
     * @throws IdentityApplicationTemplateMgtException
     */
    public abstract String exportApplicationTemplate(String templateName, boolean exportSecrets, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;
}
