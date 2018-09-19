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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.DefaultAuthenticationSequence;

import java.util.List;

/**
 * Tenant artifact management admin service.
 */
public class DefaultAuthenticationSeqMgtAdminService extends AbstractAdmin {

    private static final Log log = LogFactory.getLog(DefaultAuthenticationSeqMgtAdminService.class);
    private DefaultAuthenticationSeqMgtService authenticationSeqMgtService;

    /**
     * Create default authentication sequence.
     *
     * @param authenticationSequence default authentication sequence
     * @throws IdentityApplicationManagementException
     */
    public void createDefaultAuthenticationSeq(DefaultAuthenticationSequence authenticationSequence)
            throws IdentityApplicationManagementException {

        try {
            authenticationSeqMgtService = DefaultAuthenticationSeqMgtService.getInstance();
            authenticationSeqMgtService.createDefaultAuthenticationSeq(authenticationSequence, getTenantDomain());
        } catch (IdentityApplicationManagementException idpException) {
            log.error("Error while creating default authentication seq: " + authenticationSequence.getName()
                    + " for tenant: " + getTenantDomain(), idpException);
            throw idpException;
        }
    }

    /**
     * Retrieve default authentication sequence.
     *
     * @return default authentication sequence
     * @throws IdentityApplicationManagementException
     */
    public DefaultAuthenticationSequence getDefaultAuthenticationSeq()
            throws IdentityApplicationManagementException {

        try {
            authenticationSeqMgtService = DefaultAuthenticationSeqMgtService.getInstance();
            return authenticationSeqMgtService.getDefaultAuthenticationSeq(getTenantDomain());
        } catch (IdentityApplicationManagementException idpException) {
            log.error("Error while retrieving default authentication seq of tenant: " + getTenantDomain(),
                    idpException);
            throw idpException;
        }
    }

    /**
     * Delete default authentication sequence.
     *
     * @throws IdentityApplicationManagementException
     */
    public void deleteDefaultAuthenticationSeq() throws IdentityApplicationManagementException {

        try {
            authenticationSeqMgtService = DefaultAuthenticationSeqMgtService.getInstance();
            authenticationSeqMgtService.deleteDefaultAuthenticationSeq(getTenantDomain());
        } catch (IdentityApplicationManagementException idpException) {
            log.error("Error while deleting default authentication seqence in tenant: " +
                    getTenantDomain(), idpException);
            throw idpException;
        }
    }
}
