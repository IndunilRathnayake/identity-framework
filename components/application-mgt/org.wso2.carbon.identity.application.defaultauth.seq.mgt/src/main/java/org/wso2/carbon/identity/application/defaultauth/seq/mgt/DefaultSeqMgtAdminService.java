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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.identity.application.common.model.DefaultAuthenticationSequence;
import org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactManagementAdminService;
import org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactManagementService;
import org.wso2.carbon.identity.tenant.artifact.mgt.IdentityArtifactManagementException;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifactCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Tenant artifact management admin service.
 */
public class DefaultSeqMgtAdminService extends AbstractAdmin {

    private static final Log log = LogFactory.getLog(ArtifactManagementAdminService.class);
    private ArtifactManagementService artifactManagementService;

    /**
     * Create default authentication sequence.
     *
     * @param authenticationSequence default authentication sequence
     * @throws IdentityArtifactManagementException
     */
    public void createDefaultAuthSeq(DefaultAuthenticationSequence authenticationSequence)
            throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();

            ResourceArtifactCategory artifactCategory = new ResourceArtifactCategory();
            ResourceArtifact artifact = new ResourceArtifact();
            artifact.setName(authenticationSequence.getSequenceName());
            artifact.setDescription(authenticationSequence.getSequenceDescription());
            artifact.setValue(authenticationSequence.getSequenceContent());
            ResourceArtifact[] resourceArtifacts = new ResourceArtifact[] { artifact };
            artifactCategory.setName(DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME);
            artifactCategory.setDescription(DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_DESC);
            artifactCategory.setResourceArtifacts(resourceArtifacts);

            if (artifactManagementService.isExistingArtifactCategory(
                    DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME,  getTenantDomain())) {
                artifactManagementService.updateArtifactCategory(DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME,
                        artifactCategory, getTenantDomain());
            } else {
                artifactManagementService.createArtifactCategory(artifactCategory,  getTenantDomain());
            }
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while creating default authentication seq: " + authenticationSequence.getSequenceName()
                    + " for tenant: " + getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Retrieve default authentication sequence.
     *
     * @return default authentication sequence
     * @throws IdentityArtifactManagementException
     */
    public DefaultAuthenticationSequence getDefaultAuthSeq(String seqName) throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            ResourceArtifact resourceArtifact = artifactManagementService.getArtifact(
                    DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME, seqName, getTenantDomain());
            if (resourceArtifact == null) {
                throw new IdentityArtifactManagementException("");
            }
            DefaultAuthenticationSequence authenticationSequence = new DefaultAuthenticationSequence();
            authenticationSequence.setSequenceName(seqName);
            authenticationSequence.setSequenceDescription(resourceArtifact.getDescription());
            authenticationSequence.setSequenceContent(resourceArtifact.getValue());
            return authenticationSequence;
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while retrieving default authentication seq: " + seqName + " of tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Retrieve all the default authentication sequence.
     *
     * @return list of all default authentication sequence
     * @throws IdentityArtifactManagementException
     */
    public List<DefaultAuthenticationSequence> getDefaultAuthSeqList() throws IdentityArtifactManagementException {

        try {
            List<DefaultAuthenticationSequence> authenticationSequences = new ArrayList<>();
            artifactManagementService = ArtifactManagementService.getInstance();
            ResourceArtifactCategory artifactCategory = artifactManagementService.getArtifactCategory(
                    DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME, getTenantDomain());
            if (artifactCategory == null) {
                throw new IdentityArtifactManagementException("");
            }
            ResourceArtifact[] resourceArtifacts = artifactCategory.getResourceArtifacts();
            if (ArrayUtils.isNotEmpty(resourceArtifacts)) {
                for (ResourceArtifact artifact : resourceArtifacts) {
                    DefaultAuthenticationSequence authenticationSequence = new DefaultAuthenticationSequence();
                    authenticationSequence.setSequenceName(artifact.getName());
                    authenticationSequence.setSequenceDescription(artifact.getDescription());
                    authenticationSequence.setSequenceContent(artifact.getValue());
                    authenticationSequences.add(authenticationSequence);
                }
            }
            return authenticationSequences;
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while retrieving all default authentication seqences of tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Delete default authentication sequence.
     *
     * @param seqName default authentication sequence name
     * @throws IdentityArtifactManagementException
     */
    public void deleteDefaultAuthSeq(String seqName) throws IdentityArtifactManagementException {

        try {
            artifactManagementService = ArtifactManagementService.getInstance();
            artifactManagementService.deleteArtifact(DefualtSeqMgtConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME,
                    seqName, getTenantDomain());
        } catch (IdentityArtifactManagementException idpException) {
            log.error("Error while deleting default authentication seqence: " + seqName + " in tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }
}
