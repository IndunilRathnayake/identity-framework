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

import org.wso2.carbon.identity.application.authentication.framework.config.model.ApplicationConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.ClaimFilter;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.common.model.Claim;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Default implementation of the ClaimFilter.
 */
public class DefaultClaimFilter implements ClaimFilter {

    @Override
    public int getPriority() {

        return 56;
    }

    @Override
    public ApplicationConfig getFilteredClaims(AuthenticationContext context, ApplicationConfig appConfig) {

        List<ClaimMapping> spClaimMappings = getSpClaimMappings(appConfig);

        List<ClaimMapping> requestedClaimsInRequest = null;
        if (context != null) {
            requestedClaimsInRequest = (List<ClaimMapping>) context.getProperty(
                    FrameworkConstants.SP_REQUESTED_CLAIMS_IN_REQUEST);
        }
        List<ClaimMapping> selectedRequestedClaims = filterRequestedClaims(spClaimMappings, requestedClaimsInRequest);
        getMandatoryAndRequestedClaims(appConfig, selectedRequestedClaims);
        return appConfig;
    }

    @Override
    public List<ClaimMapping> filterRequestedClaims(List<ClaimMapping> spClaimMappings,
                                                    List<ClaimMapping> requestedClaimsInRequest) {

        List<ClaimMapping> selectedRequestedClaims = new ArrayList<>();
        if (requestedClaimsFromRequest(spClaimMappings, requestedClaimsInRequest)) {
            selectedRequestedClaims.addAll(requestedClaimsInRequest);
        } else if (requestedClaimsFromSpConfig(spClaimMappings, requestedClaimsInRequest)) {
            selectedRequestedClaims.addAll(spClaimMappings);
        } else if (requestedClaimsFromSpConfigAndRequest(spClaimMappings, requestedClaimsInRequest)) {
            for (ClaimMapping claimMappingInSPConfig : spClaimMappings) {
                for (ClaimMapping claimMappingInRequest : requestedClaimsInRequest) {
                    if (claimMappingInRequest.getRemoteClaim().equals(claimMappingInSPConfig.getRemoteClaim())) {
                        claimMappingInRequest.setLocalClaim(claimMappingInSPConfig.getLocalClaim());
                        selectedRequestedClaims.add(claimMappingInRequest);
                    }
                }
            }
        }
        return selectedRequestedClaims;
    }

    private List<ClaimMapping> getSpClaimMappings(ApplicationConfig appConfig) {

        Map<String, String> spClaimMapping = appConfig.getClaimMappings();
        Map<String, String> mandatoryClaims = appConfig.getMandatoryClaimMappings();
        Map<String, String> requestedClaims = appConfig.getRequestedClaimMappings();

        List<ClaimMapping> spClaimMappingsList = new ArrayList<>();
        spClaimMapping.forEach((key, value) -> {
            Claim remoteClaim = new Claim();
            remoteClaim.setClaimUri(key);
            Claim localClaim = new Claim();
            localClaim.setClaimUri(value);
            ClaimMapping claimMapping = new ClaimMapping();
            claimMapping.setRemoteClaim(remoteClaim);
            claimMapping.setLocalClaim(localClaim);

            mandatoryClaims.entrySet().stream().filter(entry1 -> key.equals(entry1.getKey()))
                    .map(entry1 -> true).forEach(claimMapping::setMandatory);
            requestedClaims.entrySet().stream().filter(entry2 -> key.equals(entry2.getKey()))
                    .map(entry2 -> true).forEach(claimMapping::setRequested);
            spClaimMappingsList.add(claimMapping);
        });
        return spClaimMappingsList;
    }

    private void getMandatoryAndRequestedClaims(ApplicationConfig appConfig,
                                                List<ClaimMapping> selectedRequestedClaims) {

        Map<String, String> claimMappings = new HashMap<>();
        Map<String, String> requestedClaims = new HashMap<>();
        Map<String, String> mandatoryClaims = new HashMap<>();

        if (isNotEmpty(selectedRequestedClaims)) {
            selectedRequestedClaims.stream().filter(claim -> claim.getRemoteClaim() != null
                    && claim.getRemoteClaim().getClaimUri() != null).forEach(claim -> {
                if (claim.getLocalClaim() != null) {
                    setClaimsWhenLocalClaimExists(claimMappings, requestedClaims, mandatoryClaims, claim);
                } else {
                    setClaimsWhenLocalClaimNotExists(claimMappings, requestedClaims, mandatoryClaims, claim);
                }
            });
        }
        appConfig.setClaimMappings(claimMappings);
        appConfig.setRequestedClaims(requestedClaims);
        appConfig.setMandatoryClaims(mandatoryClaims);
        appConfig.setSelectedClaimMappings(selectedRequestedClaims);
    }

    private boolean requestedClaimsFromSpConfigAndRequest(List<ClaimMapping> claimMappings,
                                                          List<ClaimMapping> requestedClaimsInRequest) {

        return !isEmpty(claimMappings) && !isEmpty(requestedClaimsInRequest);
    }

    private boolean requestedClaimsFromSpConfig(List<ClaimMapping> claimMappings,
                                                List<ClaimMapping> requestedClaimsInRequest) {

        return !isEmpty(claimMappings) && isEmpty(requestedClaimsInRequest);
    }

    private boolean requestedClaimsFromRequest(List<ClaimMapping> claimMappings,
                                               List<ClaimMapping> requestedClaimsInRequest) {

        return isEmpty(claimMappings) && !isEmpty(requestedClaimsInRequest);
    }

    private void setClaimsWhenLocalClaimNotExists(Map<String, String> claimMappings,
                                                  Map<String, String> requestedClaims,
                                                  Map<String, String> mandatoryClaims, ClaimMapping claim) {

        claimMappings.put(claim.getRemoteClaim().getClaimUri(), null);
        if (claim.isRequested()) {
            requestedClaims.put(claim.getRemoteClaim().getClaimUri(), null);
        }
        if (claim.isMandatory()) {
            mandatoryClaims.put(claim.getRemoteClaim().getClaimUri(), null);
        }
    }

    private void setClaimsWhenLocalClaimExists(Map<String, String> claimMappings, Map<String, String> requestedClaims,
                                               Map<String, String> mandatoryClaims, ClaimMapping claim) {

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
    }
}
