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

package org.wso2.carbon.identity.tenant.artifact.mgt.cache;

import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifactCategory;

import java.io.Serializable;

/**
 * Cache entry which will use in {@link IdentityArtifactMgtCache}.
 */
public class IdentityArtifactMgtCacheEntry implements Serializable {

    private static final long serialVersionUID = 3112605038259278777L;

    private ResourceArtifactCategory resourceArtifactCategory;

    public IdentityArtifactMgtCacheEntry(ResourceArtifactCategory resourceArtifactCategory) {

        this.resourceArtifactCategory = resourceArtifactCategory;
    }

    public ResourceArtifactCategory getResourceArtifactCategory() {

        return resourceArtifactCategory;
    }

    public void setResourceArtifactCategory(ResourceArtifactCategory resourceArtifactCategory) {

        this.resourceArtifactCategory = resourceArtifactCategory;
    }
}
