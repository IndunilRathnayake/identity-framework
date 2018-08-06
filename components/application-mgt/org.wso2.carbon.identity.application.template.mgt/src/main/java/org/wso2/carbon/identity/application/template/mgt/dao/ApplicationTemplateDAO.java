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

package org.wso2.carbon.identity.application.template.mgt.dao;

import org.wso2.carbon.identity.application.template.mgt.IdentityApplicationTemplateMgtException;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;

import java.util.List;

/**
 * This interface access the data storage layer to retrieve, store, delete and update service provider templates.
 */
public interface ApplicationTemplateDAO {

    /**
     * Create an application template.
     *
     * @param spTemplateDTO SP template info
     * @param tenantDomain tenant domain
     * @throws IdentityApplicationTemplateMgtException
     */
    void createApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    /**
     * Load an application template.
     *
     * @param templateName name of the template
     * @param tenantDomain tenant domain
     * @return SP template info
     * @throws IdentityApplicationTemplateMgtException
     */
    SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;

    boolean isExistingTemplate(String templateName, String tenantDomain) throws IdentityApplicationTemplateMgtException;

    /**
     * Get all application templates.
     *
     * @param tenantDomain tenant domain
     * @return Info of the list of all application templates
     * @throws IdentityApplicationTemplateMgtException
     */
    List<SpTemplateDTO> getAllApplicationTemplates(String tenantDomain) throws IdentityApplicationTemplateMgtException;

    /**
     * Get all application template names.
     *
     * @param tenantDomain tenant domain
     * @return Names of the all application templates
     * @throws IdentityApplicationTemplateMgtException
     */
    List<String> getAllApplicationTemplateNames(String tenantDomain) throws IdentityApplicationTemplateMgtException;

    /**
     * Delete an application template.
     *
     * @param templateName name of the template
     * @param tenantDomain tenant domain
     * @throws IdentityApplicationTemplateMgtException
     */
    void deleteApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException;
}
