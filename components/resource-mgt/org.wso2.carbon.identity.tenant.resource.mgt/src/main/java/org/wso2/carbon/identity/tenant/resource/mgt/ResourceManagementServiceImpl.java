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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.tenant.resource.mgt.cache.IdentityResourceMgtCache;
import org.wso2.carbon.identity.tenant.resource.mgt.cache.IdentityResourceMgtCacheEntry;
import org.wso2.carbon.identity.tenant.resource.mgt.cache.IdentityResourceMgtCacheKey;
import org.wso2.carbon.identity.tenant.resource.mgt.dao.ResourceMgtDAO;
import org.wso2.carbon.identity.tenant.resource.mgt.dao.impl.ResourceMgtDAOImpl;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.Resource;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.ResourceContent;

/**
 * Tenant resource management service implementation.
 */
public class ResourceManagementServiceImpl extends ResourceManagementService {

    private static final Log log = LogFactory.getLog(ResourceManagementServiceImpl.class);
    private static volatile ResourceManagementServiceImpl artifactMgtService;

    private ResourceManagementServiceImpl() {

    }

    public static ResourceManagementServiceImpl getInstance() {

        if (artifactMgtService == null) {
            synchronized (ResourceManagementServiceImpl.class) {
                if (artifactMgtService == null) {
                    artifactMgtService = new ResourceManagementServiceImpl();
                }
            }
        }
        return artifactMgtService;
    }

    @Override
    public void createResource(Resource resource, String tenantDomain) throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating resource: %s in tenant: %s", resource.getName(), tenantDomain));
        }

        if (isExistingResource(resource.getName(), tenantDomain)) {
            throw new IdentityResourceManagementException(String.format("Resource with name: %s already " +
                    "exists in tenant: %s", resource.getName(), tenantDomain));
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        resourceMgtDAO.createResource(resource, tenantDomain);

        addResourceToCache(resource, tenantDomain);
    }

    @Override
    public Resource getResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieving resource: %s in tenant: %s", resourceName, tenantDomain));
        }

        if (IdentityResourceMgtCache.getInstance().isEnabled()) {
            IdentityResourceMgtCacheKey key = new IdentityResourceMgtCacheKey(resourceName, tenantDomain);
            IdentityResourceMgtCacheEntry entry = IdentityResourceMgtCache.getInstance().getValueFromCache(key);
            if (entry != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Resource: " + resourceName + " is retrieved from cache.");
                }
                return entry.getResource();
            }
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        Resource resource = resourceMgtDAO.getResource(resourceName, tenantDomain);

        if (resource != null) {
            addResourceToCache(resource, tenantDomain);
        }
        return resource;
    }

    @Override
    public boolean isExistingResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Check existence of resource: %s in tenant: %s", resourceName, tenantDomain));
        }

        if (IdentityResourceMgtCache.getInstance().isEnabled()) {
            IdentityResourceMgtCacheKey key = new IdentityResourceMgtCacheKey(resourceName, tenantDomain);
            IdentityResourceMgtCacheEntry entry = IdentityResourceMgtCache.getInstance().getValueFromCache(key);
            if (entry != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Resource: " + resourceName + " is retrieved from cache.");
                }
                return true;
            }
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        return resourceMgtDAO.isExistingResource(resourceName, tenantDomain);
    }

    @Override
    public void updateResource(String resourceName, Resource resource, String tenantDomain)
            throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Updating resource: %s in tenant: %s", resourceName, tenantDomain));
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        resourceMgtDAO.updateResource(resourceName, resource, tenantDomain);

        addResourceToCache(resource, tenantDomain);
    }

    @Override
    public void deleteResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Deleting resource: %s in tenant: %s", resourceName, tenantDomain));
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        resourceMgtDAO.deleteResource(resourceName, tenantDomain);

        doRemoveResourceFromCache(resourceName, tenantDomain);
    }

    @Override
    public void createResourceContent(String resourceName, ResourceContent resourceContent,
                                               String tenantDomain) throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating content for resource: %s in tenant: %s", resourceName,
                    tenantDomain));
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        resourceMgtDAO.createResourceContent(resourceName, resourceContent, tenantDomain);

        if (IdentityResourceMgtCache.getInstance().isEnabled()) {
            IdentityResourceMgtCacheKey key = new IdentityResourceMgtCacheKey(resourceName, tenantDomain);
            IdentityResourceMgtCacheEntry entry = IdentityResourceMgtCache.getInstance().getValueFromCache(key);
            if (entry != null) {
                entry.getResource().setResourceContent(resourceContent);
                IdentityResourceMgtCache.getInstance().addToCache(key, entry);
            }
            if (log.isDebugEnabled()) {
                log.debug("Resource: " + resourceName + " is updated with the content in cache.");
            }
        }
    }

    @Override
    public ResourceContent getContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieving content of resource: %s in tenant: %s", resourceName,
                    tenantDomain));
        }

        if (IdentityResourceMgtCache.getInstance().isEnabled()) {
            IdentityResourceMgtCacheKey key = new IdentityResourceMgtCacheKey(resourceName, tenantDomain);
            IdentityResourceMgtCacheEntry entry = IdentityResourceMgtCache.getInstance().getValueFromCache(key);
            if (entry != null && entry.getResource() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Content of resource: " + resourceName + " is retrieved from cache.");
                }
                return entry.getResource().getResourceContent();
            }
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        return resourceMgtDAO.getContentOfResource(resourceName, tenantDomain);
    }

    @Override
    public void updateContentOfResource(String resourceName, ResourceContent resourceContent,
                                        String tenantDomain)
            throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Updating content of resource: %s in tenant: %s", resourceName, tenantDomain));
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        resourceMgtDAO.updateContentOfResource(resourceName, resourceContent, tenantDomain);

        doRemoveResourceFromCache(resourceName, tenantDomain);
    }

    @Override
    public void deleteContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Deleting content of resource: %s in tenant: %s", resourceName, tenantDomain));
        }

        ResourceMgtDAO resourceMgtDAO = new ResourceMgtDAOImpl();
        resourceMgtDAO.deleteContentOfResource(resourceName, tenantDomain);

        doRemoveResourceFromCache(resourceName, tenantDomain);
    }

    private void addResourceToCache(Resource resource, String tenantDomain) {

        if (IdentityResourceMgtCache.getInstance().isEnabled()) {
            IdentityResourceMgtCacheKey key = new IdentityResourceMgtCacheKey(resource.getName(), tenantDomain);
            IdentityResourceMgtCacheEntry entry = new IdentityResourceMgtCacheEntry(resource);
            IdentityResourceMgtCache.getInstance().addToCache(key, entry);
            if (log.isDebugEnabled()) {
                log.debug("Resource: " + resource.getName() + " is added to cache.");
            }
        }
    }

    private void doRemoveResourceFromCache(String resourceName, String tenantDomain) {
        if (IdentityResourceMgtCache.getInstance().isEnabled()) {
            IdentityResourceMgtCacheKey key = new IdentityResourceMgtCacheKey(resourceName, tenantDomain);
            IdentityResourceMgtCache.getInstance().clearCacheEntry(key);
            if (log.isDebugEnabled()) {
                log.debug("Resource: " + resourceName + " is removed from cache.");
            }
        }
    }
}
