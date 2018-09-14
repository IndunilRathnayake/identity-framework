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

package org.wso2.carbon.identity.tenant.artifact.mgt.ui.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifactCategory;
import org.wso2.carbon.identity.tenant.artifact.mgt.stub.IdentityArtifactManagementServiceIdentityArtifactManagementException;
import org.wso2.carbon.identity.tenant.artifact.mgt.stub.IdentityArtifactManagementServiceStub;

import java.rmi.RemoteException;

/**
 * Client for ArtifactManagementService.
 */
public class ArtifactManagementServiceClient {

    private IdentityArtifactManagementServiceStub stub;
    private static final Log log = LogFactory.getLog(ArtifactManagementServiceClient.class);
    private boolean debugEnabled = log.isErrorEnabled();

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

    /**
     * Create resource artifact category.
     *
     * @param artifactCategory resource artifact category
     * @throws AxisFault
     */
    public void createArtifactCategory(ResourceArtifactCategory artifactCategory) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Creating tenant artifact category: %s.", artifactCategory.getName()));
            }
            stub.createArtifactCategory(artifactCategory);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }

    }

    /**
     * Update resource artifact category.
     *
     * @param categoryName     resource artifact category name
     * @param artifactCategory resource artifact category
     * @throws AxisFault
     */
    public void updateArtifactCategory(String categoryName, ResourceArtifactCategory artifactCategory)
            throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Creating tenant artifact category: %s.", artifactCategory.getName()));
            }
            stub.updateArtifactCategory(categoryName, artifactCategory);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
    }

    /**
     * Check existence of resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @return resource artifact category
     * @throws AxisFault
     */
    public boolean isExistingArtifactCategory(String artifactCategoryName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Creating tenant artifact category: %s.", artifactCategoryName));
            }
            return stub.isExistingArtifactCategory(artifactCategoryName);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
        return false;
    }

    /**
     * Get resource artifact category.
     *
     * @param artifactCategoryName resource artifact category name
     * @return resource artifact category
     * @throws AxisFault
     */
    public ResourceArtifactCategory getArtifactCategory(String artifactCategoryName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Creating tenant artifact category: %s.", artifactCategoryName));
            }
            return stub.getArtifactCategory(artifactCategoryName);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Create resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifact             resource artifact
     * @throws AxisFault
     */
    public void createArtifact(String artifactCategoryName, ResourceArtifact artifact) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Creating tenant artifact: %s.", artifact.getName()));
            }
            stub.createArtifact(artifactCategoryName, artifact);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }

    }

    /**
     * Gell all resource artifact info.
     *
     * @return list of resource artifacts
     * @throws AxisFault
     */
    public ResourceArtifact[] getAllArtifactInfo() throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Retrieving all default authentication sequence info.");
            }
            return stub.getAllArtifactInfo();
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Get resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactName         resource artifact name
     * @return resource artifact
     * @throws AxisFault
     */
    public ResourceArtifact getArtifact(String artifactCategoryName, String artifactName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Deleting tenant artifact: %s.", artifactName));
            }
            return stub.getArtifact(artifactCategoryName, artifactName);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Delete resource artifact.
     *
     * @param artifactCategoryName resource artifact category name
     * @param artifactName         resource artifact name
     * @throws AxisFault
     */
    public void deleteArtifact(String artifactCategoryName, String artifactName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug(String.format("Deleting tenant artifact: %s.", artifactName));
            }
            stub.deleteArtifact(artifactCategoryName, artifactName);
        } catch (RemoteException | IdentityArtifactManagementServiceIdentityArtifactManagementException e) {
            handleException(e);
        }
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
