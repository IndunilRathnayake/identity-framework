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

package org.wso2.carbon.identity.application.defaultauth.seq.mgt.ui.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.xsd.DefaultAuthenticationSequence;
import org.wso2.carbon.identity.application.defaultauth.seq.mgt.stub.IdentityDefaultSeqManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.defaultauth.seq.mgt.stub.IdentityDefaultSeqManagementServiceStub;

import java.rmi.RemoteException;

/**
 * Client for ArtifactManagementService.
 */
public class DefaultAuthenticationSeqMgtServiceClient {

    private IdentityDefaultSeqManagementServiceStub stub;
    private static final Log log = LogFactory.getLog(DefaultAuthenticationSeqMgtServiceClient.class);
    private boolean debugEnabled = log.isErrorEnabled();

    public DefaultAuthenticationSeqMgtServiceClient(String cookie, String backendServerURL,
                                                    ConfigurationContext configCtx) throws AxisFault {

        String serviceURL = backendServerURL + "IdentityDefaultSeqManagementService";
        stub = new IdentityDefaultSeqManagementServiceStub(configCtx, serviceURL);

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        if (debugEnabled) {
            log.debug("Invoking service " + serviceURL);
        }
    }

    /**
     * Create default authentication sequence.
     *
     * @param authenticationSequence default authentication sequence
     * @throws AxisFault
     */
    public void createDefaultAuthenticationSeq(DefaultAuthenticationSequence authenticationSequence)
            throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Creating default authentication sequence: %s.", authenticationSequence.getName()));
            }
            stub.createDefaultAuthenticationSeq(authenticationSequence);

        } catch (RemoteException | IdentityDefaultSeqManagementServiceIdentityApplicationManagementException e) {
            handleException(e);
        }
    }

    /**
     * Retrieve default authentication sequence.
     *
     * @return default authentication sequence
     * @throws AxisFault
     */
    public DefaultAuthenticationSequence getDefaultAuthenticationSeq()
            throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Retrieving default authentication sequence."));
            }
            return stub.getDefaultAuthenticationSeq();
        } catch (RemoteException | IdentityDefaultSeqManagementServiceIdentityApplicationManagementException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Delete default authentication sequence.
     *
     * @throws AxisFault
     */
    public void deleteDefaultAuthenticationSeq() throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Deleting default authentication sequence."));
            }
            stub.deleteDefaultAuthenticationSeq();
        } catch (RemoteException | IdentityDefaultSeqManagementServiceIdentityApplicationManagementException e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) throws AxisFault {
        String errorMessage = "Unknown error occurred.";

        if (e instanceof IdentityDefaultSeqManagementServiceIdentityApplicationManagementException) {
            IdentityDefaultSeqManagementServiceIdentityApplicationManagementException exception =
                    (IdentityDefaultSeqManagementServiceIdentityApplicationManagementException) e;
            if (exception.getFaultMessage().getIdentityApplicationManagementException() != null) {
                errorMessage = exception.getFaultMessage().getIdentityApplicationManagementException().getMessage();
            }
        } else {
            errorMessage = e.getMessage();
        }
        throw new AxisFault(errorMessage, e);
    }
}
