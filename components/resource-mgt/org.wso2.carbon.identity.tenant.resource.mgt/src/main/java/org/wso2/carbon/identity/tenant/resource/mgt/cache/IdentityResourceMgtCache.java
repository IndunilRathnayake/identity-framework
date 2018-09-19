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

package org.wso2.carbon.identity.tenant.resource.mgt.cache;

import org.wso2.carbon.identity.application.common.cache.BaseCache;

/**
 * Cache layer for tenant resource management service.
 */
public class IdentityResourceMgtCache extends BaseCache<IdentityResourceMgtCacheKey, IdentityResourceMgtCacheEntry> {

    private static final String CACHE_NAME = "IdentityResourceMgtCache";
    private static volatile IdentityResourceMgtCache instance;

    private IdentityResourceMgtCache() {
        super(CACHE_NAME);

    }

    public static IdentityResourceMgtCache getInstance() {
        if (instance == null) {
            synchronized (IdentityResourceMgtCache.class) {
                if (instance == null) {
                    instance = new IdentityResourceMgtCache();
                }
            }
        }
        return instance;
    }
}
