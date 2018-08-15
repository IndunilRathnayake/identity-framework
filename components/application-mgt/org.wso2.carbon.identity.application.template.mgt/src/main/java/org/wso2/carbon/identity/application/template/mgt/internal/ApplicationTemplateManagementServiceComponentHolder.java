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

package org.wso2.carbon.identity.application.template.mgt.internal;

import org.wso2.carbon.registry.api.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * A singleton to hold the OSGi services received during the component activation.
 */
public class ApplicationTemplateManagementServiceComponentHolder {

    private static final ApplicationTemplateManagementServiceComponentHolder instance = new
            ApplicationTemplateManagementServiceComponentHolder();

    private RegistryService registryService;

    private RealmService realmService;

    private ConfigurationContextService configContextService;

    private ApplicationTemplateManagementServiceComponentHolder() {
    }

    public static ApplicationTemplateManagementServiceComponentHolder getInstance() {

        return instance;
    }

    /**
     * Get RegistryService instance.
     * @return registryService
     */
    public RegistryService getRegistryService() {

        return registryService;
    }

    /**
     * Set RegistryService instance.
     * @param registryService registryService
     */
    public void setRegistryService(RegistryService registryService) {

        this.registryService = registryService;
    }

    /**
     * Get RealmService instance.
     * @return realmService
     */
    public RealmService getRealmService() {

        return realmService;
    }

    /**
     * Set RealmService instance.
     * @param realmService realmService
     */
    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    /**
     * Get ConfigurationContextService instance.
     * @return configContextService
     */
    public ConfigurationContextService getConfigContextService() {

        return configContextService;
    }

    /**
     * Set ConfigurationContextService instance.
     * @param configContextService configContextService
     */
    public void setConfigContextService(ConfigurationContextService configContextService) {

        this.configContextService = configContextService;
    }
}
