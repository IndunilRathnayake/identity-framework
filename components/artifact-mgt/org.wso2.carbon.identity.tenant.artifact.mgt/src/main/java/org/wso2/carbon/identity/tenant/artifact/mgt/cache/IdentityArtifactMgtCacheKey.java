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

import java.io.Serializable;

/**
 * Cache Key which will use in {@link IdentityArtifactMgtCache}.
 */
public class IdentityArtifactMgtCacheKey implements Serializable {

    private static final long serialVersionUID = 8263255365985309443L;

    private String artifactCategoryName;
    private String tenantDomain;

    public IdentityArtifactMgtCacheKey(String artifactCategoryName, String tenantDomain) {
        this.artifactCategoryName = artifactCategoryName;
        if (tenantDomain != null) {
            this.tenantDomain = tenantDomain.toLowerCase();
        }
    }

    public String getArtifactCategoryName() {

        return artifactCategoryName;
    }

    public void setArtifactCategoryName(String artifactCategoryName) {

        this.artifactCategoryName = artifactCategoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        IdentityArtifactMgtCacheKey that = (IdentityArtifactMgtCacheKey) o;

        if (!artifactCategoryName.equals(that.artifactCategoryName)) {
            return false;
        }

        return tenantDomain.equals(that.tenantDomain);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + artifactCategoryName.hashCode();
        result = 31 * result + tenantDomain.hashCode();
        return result;
    }
}
