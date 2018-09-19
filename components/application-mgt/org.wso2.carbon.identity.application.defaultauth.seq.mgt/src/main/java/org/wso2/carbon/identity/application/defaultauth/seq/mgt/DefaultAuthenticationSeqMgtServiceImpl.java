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

package org.wso2.carbon.identity.application.defaultauth.seq.mgt;

import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.DefaultAuthenticationSequence;
import org.wso2.carbon.identity.tenant.resource.mgt.IdentityResourceManagementException;
import org.wso2.carbon.identity.tenant.resource.mgt.ResourceManagementService;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.Resource;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.ResourceContent;


public class DefaultAuthenticationSeqMgtServiceImpl extends DefaultAuthenticationSeqMgtService {

    private static volatile DefaultAuthenticationSeqMgtServiceImpl authenticationSeqMgtService;
    private ResourceManagementService resourceManagementService;

    private DefaultAuthenticationSeqMgtServiceImpl() {

    }

    public static DefaultAuthenticationSeqMgtServiceImpl getInstance() {

        if (authenticationSeqMgtService == null) {
            synchronized (DefaultAuthenticationSeqMgtServiceImpl.class) {
                if (authenticationSeqMgtService == null) {
                    authenticationSeqMgtService = new DefaultAuthenticationSeqMgtServiceImpl();
                }
            }
        }
        return authenticationSeqMgtService;
    }

    @Override
    public void createDefaultAuthenticationSeq(DefaultAuthenticationSequence sequence, String tenantDomain)
            throws IdentityApplicationManagementException {

        resourceManagementService = ResourceManagementService.getInstance();

        Resource resource = new Resource();
        ResourceContent content = new ResourceContent();
        content.setContent(sequence.getContent());
        content.setContentType(sequence.getContent().getClass().getSimpleName());
        resource.setName(DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_NAME);
        resource.setDescription(DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_DESC);
        resource.setResourceContent(content);

        try {
            if (resourceManagementService.isExistingResource(
                    DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_NAME, tenantDomain)) {
                resourceManagementService.updateResource(DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_NAME,
                        resource, tenantDomain);
                //IdentityServiceProviderCache.getInstance().clear();
            } else {
                resourceManagementService.createResource(resource,  tenantDomain);
            }
        } catch (IdentityResourceManagementException e) {
            throw new IdentityApplicationManagementException("Error when creating default authentication sequence for " +
                    "tenant:" + tenantDomain, e);
        }
    }

    @Override
    public DefaultAuthenticationSequence getDefaultAuthenticationSeq(String tenantDomain)
            throws IdentityApplicationManagementException {

        resourceManagementService = ResourceManagementService.getInstance();
        ResourceContent content;

        try {
            content = resourceManagementService.getContentOfResource(
                    DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_NAME,
                    tenantDomain);
        } catch (IdentityResourceManagementException e) {
            throw new IdentityApplicationManagementException("Error when retrieving default authentication sequence " +
                    "for tenant:" + tenantDomain, e);
        }

        if (content == null) {
            return new DefaultAuthenticationSequence();
        }

        DefaultAuthenticationSequence authenticationSequence = new DefaultAuthenticationSequence();
        authenticationSequence.setName(DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_NAME);
        authenticationSequence.setDescription(DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_DESC);
        authenticationSequence.setContent((String) content.getContent());
        return authenticationSequence;
    }

    @Override
    public void deleteDefaultAuthenticationSeq(String tenantDomain)
            throws IdentityApplicationManagementException {

        resourceManagementService = ResourceManagementService.getInstance();
        try {
            resourceManagementService.deleteResource(DefualtAuthSeqMgtConstants.DEFAULT_AUTH_SEQ_RESOURCE_NAME,
                    tenantDomain);
           // IdentityServiceProviderCache.getInstance().clear();
        } catch (IdentityResourceManagementException e) {
            throw new IdentityApplicationManagementException("Error when deleting default authentication sequence " +
                    "for tenant:" + tenantDomain, e);
        }
    }
}
