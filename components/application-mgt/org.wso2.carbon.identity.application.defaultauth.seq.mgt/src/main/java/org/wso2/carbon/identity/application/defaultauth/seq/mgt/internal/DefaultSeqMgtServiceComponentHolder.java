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

package org.wso2.carbon.identity.application.defaultauth.seq.mgt.internal;

import org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactManagementService;
import org.wso2.carbon.identity.tenant.artifact.mgt.internal.ArtifactMgtServiceComponentHolder;
import org.wso2.carbon.registry.api.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * OSGI component holder for Artifact Management Component.
 */
public class DefaultSeqMgtServiceComponentHolder {

    private static DefaultSeqMgtServiceComponentHolder instance = new DefaultSeqMgtServiceComponentHolder();

    private RegistryService registryService;

    private RealmService realmService;

    private ConfigurationContextService configContextService;

    private ArtifactManagementService artifactManagementService;

    private DefaultSeqMgtServiceComponentHolder(){
    }

    public static DefaultSeqMgtServiceComponentHolder getInstance() {

        return instance;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public ConfigurationContextService getConfigContextService() {
        return configContextService;
    }

    public void setConfigContextService(
            ConfigurationContextService configContextService) {
        this.configContextService = configContextService;
    }

    /**
     * Set ArtifactManagementService instance.
     *
     * @param artifactManagementService ArtifactManagementService instance
     */
    public void setArtifactManagementService(ArtifactManagementService artifactManagementService) {

        this.artifactManagementService = artifactManagementService;
    }

    /**
     * Get ArtifactManagementService instance.
     *
     * @return ArtifactManagementService instance
     */
    public ArtifactManagementService getArtifactManagementService() {

        return artifactManagementService;
    }
}
