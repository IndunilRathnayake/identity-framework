/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authentication.framework.handler.claims;

import org.wso2.carbon.identity.application.common.model.ClaimConfig;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;

import java.util.List;
import java.util.Map;

public interface ClaimFilter {

    /**
     * Priority of the Claim Filter. Claims filters will be sorted based on their priority value and by default only
     * the claim filter with the highest priority will be executed.
     *
     * @return priority of the filter.
     */
    int getPriority();

    /**
     * Filtering out and selecting the claim mappings, requested and mandatory claims
     *
     * @param claimMappings SP configured claim mappings
     * @param requestedClaims SP requested claims
     * @param mandatoryClaims SP mandatory claims
     * @param claimConfig SP claim configuration
     * @param requestedClaimsInRequest Requested claims in the request
     */
    void getFilteredRequestedClaims(Map<String, String> claimMappings, Map<String, String> requestedClaims,
                                    Map<String, String> mandatoryClaims, ClaimConfig claimConfig,
                                    List<ClaimMapping> requestedClaimsInRequest);
}
