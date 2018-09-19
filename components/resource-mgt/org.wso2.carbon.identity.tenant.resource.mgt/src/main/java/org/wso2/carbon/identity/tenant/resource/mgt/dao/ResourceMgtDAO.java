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

package org.wso2.carbon.identity.tenant.resource.mgt.dao;

import org.wso2.carbon.identity.tenant.resource.mgt.IdentityResourceManagementException;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.Resource;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.ResourceContent;

/**
 * This interface access the data storage layer to store/update and delete tenant resources.
 */
public interface ResourceMgtDAO {

    /**
     * Create tenant resource.
     *
     * @param resource resource artifact
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    void createResource(Resource resource, String tenantDomain) throws IdentityResourceManagementException;

    /**
     * Retrieve tenant resource.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @return resource artifact category
     * @throws IdentityResourceManagementException
     */
    Resource getResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException;

    /**
     * Check existence of resource.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @return true if exists
     * @throws IdentityResourceManagementException
     */
    boolean isExistingResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException;

    /**
     * Update resource.
     *
     * @param resourceName resource name
     * @param resource updated resource
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    void updateResource(String resourceName, Resource resource, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Delete resource.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    void deleteResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException;

    /**
     * Create resource content.
     *
     * @param resourceName resource name
     * @param resourceContent resource content
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    void createResourceContent(String resourceName, ResourceContent resourceContent, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Get resource content.
     *
     * @param resourceName resource name
     * @return resource
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    ResourceContent getContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Update resource content.
     *
     * @param resourceName resource name
     * @param resourceContent resource content
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    void updateContentOfResource(String resourceName, ResourceContent resourceContent, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Delete resource content.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    void deleteContentOfResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException;
}
