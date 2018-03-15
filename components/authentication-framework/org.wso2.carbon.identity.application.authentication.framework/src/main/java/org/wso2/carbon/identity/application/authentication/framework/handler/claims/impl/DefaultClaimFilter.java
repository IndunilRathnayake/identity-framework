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

package org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.ClaimFilter;
import org.wso2.carbon.identity.application.common.model.ClaimConfig;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultClaimFilter implements ClaimFilter {

    @Override
    public int getPriority() {
        return 56;
    }

    @Override
    public void getClaimsFilteredByRequestedClaims(Map<String, String> claimMappings, Map<String, String> requestedClaims,
                                                   Map<String, String> mandatoryClaims, ClaimConfig claimConfig,
                                                   List<ClaimMapping> requestedClaimsInRequest) {
        List<ClaimMapping> selectedRequestedClaims = null;
        if (!ArrayUtils.isEmpty(claimConfig.getClaimMappings())) {
            selectedRequestedClaims = Arrays.asList(claimConfig.getClaimMappings());
        } else if(! requestedClaimsInRequest.isEmpty()) {
            selectedRequestedClaims = requestedClaimsInRequest;
        }
        if (CollectionUtils.isNotEmpty(selectedRequestedClaims)) {
            for (ClaimMapping claim : selectedRequestedClaims) {
                if (claim.getRemoteClaim() != null
                        && claim.getRemoteClaim().getClaimUri() != null) {
                    if (claim.getLocalClaim() != null) {
                        claimMappings.put(claim.getRemoteClaim().getClaimUri(), claim
                                .getLocalClaim().getClaimUri());

                        if (claim.isRequested()) {
                            requestedClaims.put(claim.getRemoteClaim().getClaimUri(), claim
                                    .getLocalClaim().getClaimUri());
                        }

                        if (claim.isMandatory()) {
                            mandatoryClaims.put(claim.getRemoteClaim().getClaimUri(), claim
                                    .getLocalClaim().getClaimUri());
                        }

                    } else {
                        claimMappings.put(claim.getRemoteClaim().getClaimUri(), null);
                        if (claim.isRequested()) {
                            requestedClaims.put(claim.getRemoteClaim().getClaimUri(), null);
                        }

                        if (claim.isMandatory()) {
                            mandatoryClaims.put(claim.getRemoteClaim().getClaimUri(), null);
                        }
                    }
                }
            }
        }
    }
}
