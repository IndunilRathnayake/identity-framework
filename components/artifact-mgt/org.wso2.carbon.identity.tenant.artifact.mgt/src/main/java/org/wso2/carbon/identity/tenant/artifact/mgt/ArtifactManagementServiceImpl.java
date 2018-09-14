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
import org.wso2.carbon.identity.tenant.artifact.mgt.dao.ArtifactMgtDAO;
import org.wso2.carbon.identity.tenant.artifact.mgt.dao.impl.ArtifactMgtDAOImpl;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifactCategory;
import org.wso2.carbon.identity.tenant.artifact.mgt.cache.IdentityArtifactMgtCache;
import org.wso2.carbon.identity.tenant.artifact.mgt.cache.IdentityArtifactMgtCacheEntry;
import org.wso2.carbon.identity.tenant.artifact.mgt.cache.IdentityArtifactMgtCacheKey;

import java.util.Arrays;
import java.util.List;

/**
 * Tenant resource artifact management service implementation.
 */
public class ArtifactManagementServiceImpl extends ArtifactManagementService {

    private static final Log log = LogFactory.getLog(ArtifactManagementServiceImpl.class);
    private static volatile ArtifactManagementServiceImpl artifactMgtService;

    private ArtifactManagementServiceImpl() {

    }

    public static ArtifactManagementServiceImpl getInstance() {

        if (artifactMgtService == null) {
            synchronized (ArtifactManagementServiceImpl.class) {
                if (artifactMgtService == null) {
                    artifactMgtService = new ArtifactManagementServiceImpl();
                }
            }
        }
        return artifactMgtService;
    }

    @Override
    public void createArtifactCategory(ResourceArtifactCategory artifactCategory, String tenantDomain)
            throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating resource artifact category: %s in tenant: %s",
                    artifactCategory.getName(), tenantDomain));
        }

        if (isExistingArtifactCategory(artifactCategory.getName(), tenantDomain)) {
            throw new IdentityArtifactManagementException(String.format("Artifact category with name: %s already " +
                    "exists in tenant: %s", artifactCategory.getName(), tenantDomain));
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        artifactMgtDAO.createArtifactCategory(artifactCategory, tenantDomain);

        addCategoryToCache(artifactCategory, tenantDomain);
    }

    @Override
    public ResourceArtifactCategory getArtifactCategory(String artifactCategoryName, String tenantDomain)
            throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieving resource artifact category: %s in tenant: %s", artifactCategoryName,
                    tenantDomain));
        }

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategoryName, tenantDomain);
            IdentityArtifactMgtCacheEntry entry = IdentityArtifactMgtCache.getInstance().getValueFromCache(key);
            if (entry != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Resource artifact category: " + artifactCategoryName + " is retrieved from cache.");
                }
                return entry.getResourceArtifactCategory();
            }
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        ResourceArtifactCategory category = artifactMgtDAO.getArtifactCategory(artifactCategoryName, tenantDomain);

        if (category != null) {
            addCategoryToCache(category, tenantDomain);
        }
        return category;
    }

    @Override
    public boolean isExistingArtifactCategory(String artifactCategoryName, String tenantDomain)
            throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Check existence of resource artifact category: %s in tenant: %s",
                    artifactCategoryName, tenantDomain));
        }

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategoryName, tenantDomain);
            IdentityArtifactMgtCacheEntry entry = IdentityArtifactMgtCache.getInstance().getValueFromCache(key);
            if (entry != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Resource artifact category: " + artifactCategoryName + " is retrieved from cache.");
                }
                return true;
            }
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        return artifactMgtDAO.isExistingArtifactCategory(artifactCategoryName, tenantDomain);
    }

    @Override
    public void updateArtifactCategory(String artifactCategoryName, ResourceArtifactCategory artifactCategory,
                                       String tenantDomain) throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Updating resource artifact category: %s in tenant: %s", artifactCategoryName,
                    tenantDomain));
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        artifactMgtDAO.updateArtifactCategory(artifactCategoryName, artifactCategory, tenantDomain);

        addCategoryToCache(artifactCategory, tenantDomain);
    }

    @Override
    public void createArtifact(String artifactCategoryName, ResourceArtifact artifact, String tenantDomain)
            throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating resource artifact: %s in tenant: %s", artifact.getName(),
                    tenantDomain));
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        artifactMgtDAO.createArtifact(artifactCategoryName, artifact, tenantDomain);

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategoryName, tenantDomain);
            IdentityArtifactMgtCacheEntry entry = IdentityArtifactMgtCache.getInstance().getValueFromCache(key);
            if (entry != null) {
                List<ResourceArtifact> resourceArtifacts = Arrays.asList(entry.getResourceArtifactCategory()
                        .getResourceArtifacts());
                if (resourceArtifacts != null) {
                    resourceArtifacts.add(artifact);
                    entry.getResourceArtifactCategory().setResourceArtifacts(resourceArtifacts.toArray(
                            new ResourceArtifact[0]));
                    IdentityArtifactMgtCache.getInstance().addToCache(key, entry);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Resource artifact category: " + artifactCategoryName + " is updated with the artifact: "
                        + artifact.getName() + " in cache.");
            }
        }
    }

    @Override
    public ResourceArtifact getArtifact(String artifactCategoryName, String artifactName, String tenantDomain)
            throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieving resource artifact: %s in tenant: %s", artifactName, tenantDomain));
        }

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategoryName, tenantDomain);
            IdentityArtifactMgtCacheEntry entry = IdentityArtifactMgtCache.getInstance().getValueFromCache(key);
            if (entry != null && entry.getResourceArtifactCategory() != null) {
                for (ResourceArtifact artifact : entry.getResourceArtifactCategory().getResourceArtifacts()) {
                    if (artifact.getName().equals(artifactName)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Resource artifact: " + artifactName + " is retrieved from cache.");
                        }
                        return artifact;
                    }
                }
            }
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        return artifactMgtDAO.getArtifact(artifactCategoryName, artifactName, tenantDomain);
    }

    @Override
    public List<ResourceArtifact> getAllArtifactInfo(String tenantDomain) throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieving all resource artifact basic info in tenant: %s", tenantDomain));
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        return artifactMgtDAO.getAllArtifactInfo(tenantDomain);
    }

    @Override
    public void updateArtifact(String artifactCategoryName, String artifactName, ResourceArtifact artifact,
                               String tenantDomain) throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Updating resource artifact: %s in tenant: %s", artifactName, tenantDomain));
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        artifactMgtDAO.updateArtifact(artifactCategoryName, artifactName, artifact, tenantDomain);

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategoryName, tenantDomain);
            IdentityArtifactMgtCache.getInstance().clearCacheEntry(key);
            if (log.isDebugEnabled()) {
                log.debug("Resource artifact category: " + artifactCategoryName + " is removed from cache.");
            }
        }
    }

    @Override
    public void deleteArtifact(String artifactCategoryName, String artifactName, String tenantDomain)
            throws IdentityArtifactManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Deleting resource artifact: %s in tenant: %s", artifactName, tenantDomain));
        }

        ArtifactMgtDAO artifactMgtDAO = new ArtifactMgtDAOImpl();
        artifactMgtDAO.deleteArtifact(artifactCategoryName, artifactName, tenantDomain);

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategoryName, tenantDomain);
            IdentityArtifactMgtCache.getInstance().clearCacheEntry(key);
            if (log.isDebugEnabled()) {
                log.debug("Resource artifact category: " + artifactCategoryName + " is removed " +
                        "from cache.");
            }
        }
    }

    private void addCategoryToCache(ResourceArtifactCategory artifactCategory, String tenantDomain) {

        if (IdentityArtifactMgtCache.getInstance().isEnabled()) {
            IdentityArtifactMgtCacheKey key = new IdentityArtifactMgtCacheKey(artifactCategory.getName(), tenantDomain);
            IdentityArtifactMgtCacheEntry entry = new IdentityArtifactMgtCacheEntry(artifactCategory);
            IdentityArtifactMgtCache.getInstance().addToCache(key, entry);
            if (log.isDebugEnabled()) {
                log.debug("Resource artifact category: " + artifactCategory.getName() + " is added to cache.");
            }
        }
    }
}
