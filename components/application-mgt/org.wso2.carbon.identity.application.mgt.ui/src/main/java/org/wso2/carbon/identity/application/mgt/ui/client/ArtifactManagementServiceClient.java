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

package org.wso2.carbon.identity.application.mgt.ui.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.DefaultAuthenticationSequence;
import org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtConstants;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifactCategory;
import org.wso2.carbon.identity.tenant.artifact.mgt.stub.IdentityArtifactManagementServiceIdentityArtifactManagementException;
import org.wso2.carbon.identity.tenant.artifact.mgt.stub.IdentityArtifactManagementServiceStub;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ArtifactManagementServiceClient {

    IdentityArtifactManagementServiceStub stub;
    Log log = LogFactory.getLog(ArtifactManagementServiceClient.class);
    boolean debugEnabled = log.isErrorEnabled();

    /**
     * @param cookie
     * @param backendServerURL
     * @param configCtx
     * @throws AxisFault
     */
    public ArtifactManagementServiceClient(String cookie, String backendServerURL,
                                           ConfigurationContext configCtx) throws AxisFault {

        String serviceURL = backendServerURL + "IdentityArtifactManagementService";
        stub = new IdentityArtifactManagementServiceStub(configCtx, serviceURL);

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        if (debugEnabled) {
            log.debug("Invoking service " + serviceURL);
        }

    }

    public List<DefaultAuthenticationSequence> getAllDefaultAuthenticationCategory() throws AxisFault {

        List<DefaultAuthenticationSequence> defaultAuthenticationSequences = new ArrayList<>();
        try {
            if (debugEnabled) {
                log.debug(String.format("Retrieving all default authentication sequence info."));
            }
            ResourceArtifactCategory artifactCategory = stub.getArtifactCategory(
                    ArtifactMgtConstants.DEFAULT_AUTH_SEQ_COLLECTION_NAME);
            ResourceArtifact[] resourceArtifacts = artifactCategory.getResourceArtifacts();
            for (ResourceArtifact resourceArtifact : resourceArtifacts) {
                DefaultAuthenticationSequence defaultAuthenticationSequence = new DefaultAuthenticationSequence();
                defaultAuthenticationSequence.setSequenceName(resourceArtifact.getName());
                defaultAuthenticationSequence.setSequenceDescription(resourceArtifact.getDescription());
                defaultAuthenticationSequences.add(defaultAuthenticationSequence);
            }
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
        return defaultAuthenticationSequences;
    }

    private void handleException(Exception e) throws AxisFault {
        String errorMessage = "Unknown error occurred.";

        if (e instanceof IdentityArtifactManagementServiceIdentityArtifactManagementException) {
            IdentityArtifactManagementServiceIdentityArtifactManagementException exception =
                    (IdentityArtifactManagementServiceIdentityArtifactManagementException) e;
            if (exception.getFaultMessage().getIdentityArtifactManagementException() != null) {
                errorMessage = exception.getFaultMessage().getIdentityArtifactManagementException().getMessage();
            }
        } else {
            errorMessage = e.getMessage();
        }

        throw new AxisFault(errorMessage, e);
    }
}
