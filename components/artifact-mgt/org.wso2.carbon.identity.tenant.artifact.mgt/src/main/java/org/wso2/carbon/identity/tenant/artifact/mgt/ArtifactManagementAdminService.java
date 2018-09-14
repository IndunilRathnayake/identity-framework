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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifactCategory;

import java.util.List;

/**
 * Tenant artifact management admin service.
 */
public class ArtifactManagementAdminService extends AbstractAdmin {

    private static final Log log = LogFactory.getLog(ArtifactManagementAdminService.class);
    private ArtifactManagementService artifactManagementService;

    /**
     * Create resource artifact category.
     *
     * @param artifactCategory resource artifact category
     * @throws IdentityArtifactManagementException
     */
    public void createArtifactCategory(ResourceArtifactCategory artifactCategory)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            artifactManagementService.createArtifactCategory(artifactCategory, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while creating artifact category: " + artifactCategory.getName() + " for tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Retrieve  resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @return resource artifact category
     * @throws IdentityArtifactManagementException
     */
    public ResourceArtifactCategory getArtifactCategory(String artifactCategoryName)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            return artifactManagementService.getArtifactCategory(artifactCategoryName, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while retrieving artifact category: " + artifactCategoryName + " of tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Check existence of resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @return true if exists
     * @throws IdentityArtifactManagementException
     */
    public boolean isExistingArtifactCategory(String artifactCategoryName)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            return artifactManagementService.isExistingArtifactCategory(artifactCategoryName, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while checking existence of artifact category: " + artifactCategoryName +
                    " in tenant: " + getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Update resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactCategory resource artifact category name
     * @throws IdentityArtifactManagementException
     */
    public void updateArtifactCategory(String artifactCategoryName, ResourceArtifactCategory artifactCategory)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            artifactManagementService.updateArtifactCategory(artifactCategoryName, artifactCategory,
                    getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while updating artifact category: " + artifactCategory.getName() + " in tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Create resource artifact.
     *
     * @param artifact resource artifact
     * @throws IdentityArtifactManagementException
     */
    public void createArtifact(String artifactCategoryName, ResourceArtifact artifact)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            artifactManagementService.createArtifact(artifactCategoryName, artifact, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while creating artifact: " + artifact.getName() + " for tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Get resource artifact.
     *
     * @param artifactName resource artifact name
     * @return resource artifact
     * @throws IdentityArtifactManagementException
     */
    public ResourceArtifact getArtifact(String artifactCategoryName, String artifactName)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            return artifactManagementService.getArtifact(artifactCategoryName, artifactName, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while retrieving artifact: " + artifactName + " in tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Get all the tenant resource artifact info.
     *
     * @return all the tenant resource artifacts
     * @throws IdentityArtifactManagementException
     */
    public List<ResourceArtifact> getAllArtifactInfo() throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            return artifactManagementService.getAllArtifactInfo(getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while retrieving all the artifact info of tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Update resource artifact.
     *
     * @param artifactName resource artifact name
     * @param artifact resource artifact
     * @throws IdentityArtifactManagementException
     */
    public void updateArtifact(String artifactCategoryName, String artifactName, ResourceArtifact artifact)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            artifactManagementService.updateArtifact(artifactCategoryName, artifactName, artifact, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while updating artifact: " + artifactName + " in tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Delete resource artifact.
     *
     * @param artifactName resource artifact name
     * @throws IdentityArtifactManagementException
     */
    public void deleteArtifact(String artifactCategoryName, String artifactName)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            artifactManagementService.deleteArtifact(artifactCategoryName, artifactName, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while deleting artifact: " + artifactName + " in tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }
}
