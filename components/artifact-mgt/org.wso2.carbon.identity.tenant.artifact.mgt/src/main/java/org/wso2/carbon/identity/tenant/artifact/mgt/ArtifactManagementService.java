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

package org.wso2.carbon.identity.tenant.artifact.mgt;

import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifactCategory;

import java.util.List;

/**
 * Tenant artifact management service abstract class.
 */
public abstract class ArtifactManagementService {

    /**
     * Get ArtifactManagementService instance.
     *
     * @return ArtifactManagementService instance
     */
    public static ArtifactManagementService getInstance() {
        return ArtifactManagementServiceImpl.getInstance();
    }

    /**
     * Create resource artifact category.
     *
     * @param artifactCategory resource artifact category
     * @param tenantDomain tenant domain
     * @throws IdentityArtifactManagementException
     */
    public abstract void createArtifactCategory(ResourceArtifactCategory artifactCategory, String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Retrieve  resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @param tenantDomain tenant domain
     * @return resource artifact category
     * @throws IdentityArtifactManagementException
     */
    public abstract ResourceArtifactCategory getArtifactCategory(String artifactCategoryName, String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Check existence of resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @param tenantDomain tenant domain
     * @return true if exists
     * @throws IdentityArtifactManagementException
     */
    public abstract boolean isExistingArtifactCategory(String artifactCategoryName, String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Update resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactCategory resource artifact category
     * @param tenantDomain tenant domain
     * @throws IdentityArtifactManagementException
     */
    public abstract void updateArtifactCategory(String artifactCategoryName,
                                                  ResourceArtifactCategory artifactCategory, String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Create resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifact resource artifact
     * @param tenantDomain tenant domain
     * @throws IdentityArtifactManagementException
     */
    public abstract void createArtifact(String artifactCategoryName, ResourceArtifact artifact, String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Get resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactName resource artifact name
     * @return resource artifact
     * @param tenantDomain tenant domain
     * @throws IdentityArtifactManagementException
     */
    public abstract ResourceArtifact getArtifact(String artifactCategoryName, String artifactName, String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Get all resource artifact info.
     *
     * @param tenantDomain tenant domain
     * @return list of all the resource artifact info
     * @throws IdentityArtifactManagementException
     */
    public abstract List<ResourceArtifact> getAllArtifactInfo(String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Update resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactName resource artifact name
     * @param artifact resource artifact
     * @param tenantDomain tenant domain
     * @throws IdentityArtifactManagementException
     */
    public abstract void updateArtifact(String artifactCategoryName, String artifactName, ResourceArtifact artifact,
                                        String tenantDomain)
            throws IdentityArtifactManagementException;

    /**
     * Delete resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactName resource artifact name
     * @param tenantDomain tenant domain
     * @throws IdentityArtifactManagementException
     */
    public abstract void deleteArtifact(String artifactCategoryName, String artifactName, String tenantDomain)
            throws IdentityArtifactManagementException;
}
