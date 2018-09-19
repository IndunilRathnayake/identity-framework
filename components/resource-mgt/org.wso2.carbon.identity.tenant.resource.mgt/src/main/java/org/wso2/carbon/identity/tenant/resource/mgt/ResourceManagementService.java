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

package org.wso2.carbon.identity.tenant.resource.mgt;

import org.wso2.carbon.identity.tenant.resource.mgt.dto.Resource;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.ResourceContent;

/**
 * Tenant resource management service abstract class.
 */
public abstract class ResourceManagementService {

    /**
     * Get ResourceManagementService instance.
     *
     * @return ResourceManagementService instance
     */
    public static ResourceManagementService getInstance() {
        return ResourceManagementServiceImpl.getInstance();
    }

    /**
     * Create resource.
     *
     * @param resource resource
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract void createResource(Resource resource, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Retrieve resource.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @return resource
     * @throws IdentityResourceManagementException
     */
    public abstract Resource getResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Check existence of resource.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @return true if exists
     * @throws IdentityResourceManagementException
     */
    public abstract boolean isExistingResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Update resource.
     *
     * @param resourceName resource name
     * @param resource resource 
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract void updateResource(String resourceName, Resource resource, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Delete resource.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract void deleteResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Create resource content.
     *
     * @param resourceName resource name
     * @param resourceContent resource content
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract void createResourceContent(String resourceName, ResourceContent resourceContent,
                                               String tenantDomain) throws IdentityResourceManagementException;

    /**
     * Get resource content.
     *
     * @param resourceName resource name
     * @return resource content
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract ResourceContent getContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException;

    /**
     * Update resource content.
     *
     * @param resourceName resource name
     * @param resourceContent resource resourceContent
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract void updateContentOfResource(String resourceName, ResourceContent resourceContent,
                                                 String tenantDomain) throws IdentityResourceManagementException;

    /**
     * Delete resource content.
     *
     * @param resourceName resource name
     * @param tenantDomain tenant domain
     * @throws IdentityResourceManagementException
     */
    public abstract void deleteContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException;
}
