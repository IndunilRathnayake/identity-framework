/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.identity.application.template.mgt.ui.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.template.mgt.dto.xsd.SpTemplateDTO;
import org.wso2.carbon.identity.application.template.mgt.stub.IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException;
import org.wso2.carbon.identity.application.template.mgt.stub.IdentityApplicationTemplateManagementServiceStub;

import java.rmi.RemoteException;

/**
 * This class invokes the operations of IdentityApplicationTemplateManagementService.
 */
public class ApplicationTemplateManagementServiceClient {

    private IdentityApplicationTemplateManagementServiceStub stub;
    private static final Log log = LogFactory.getLog(ApplicationTemplateManagementServiceClient.class);
    private final boolean debugEnabled = log.isErrorEnabled();

    public ApplicationTemplateManagementServiceClient(String cookie, String backendServerURL,
                                                      ConfigurationContext configCtx) throws AxisFault {

        String serviceURL = backendServerURL + "IdentityApplicationTemplateManagementService";
        stub = new IdentityApplicationTemplateManagementServiceStub(configCtx, serviceURL);

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        if (debugEnabled) {
            log.debug("Invoking service " + serviceURL);
        }

    }

    /**
     * Create an application template.
     *
     * @param spTemplateDTO service provider info
     * @throws AxisFault
     */
    public void importApplicationTemplate(SpTemplateDTO spTemplateDTO) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Registering Service Provider template");
            }
            stub.importApplicationTemplate(spTemplateDTO);
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }

    }

    /**
     * Load an application template.
     *
     * @param templateName template name
     * @return service provider info
     * @throws AxisFault
     */
    public SpTemplateDTO loadSpFromApplicationTemplate(String templateName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Loading Service Provider template with name: " + templateName);
            }
            return stub.loadApplicationTemplate(templateName);
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Get all the application templates.
     *
     * @return array of all the application templates
     * @throws AxisFault
     */
    public SpTemplateDTO[] getAllApplicationTemplates() throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Getting all Service Provider templates");
            }
            return stub.getAllApplicationTemplates();
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Delete a application template.
     *
     * @param templateName name of the template
     * @throws AxisFault
     */
    public void deleteApplicationTemplate(String templateName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Getting all Service Provider templates");
            }
            stub.deleteApplicationTemplate(templateName);
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
    }

    /**
     * Export a application template.
     *
     * @param templateName name of the template
     * @return xml string of the template content
     * @throws AxisFault
     */
    public String exportApplicationTemplate(String templateName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Exporting Service Provider Template to file" );
            }
            return stub.exportApplicationTemplate(templateName);
        } catch (RemoteException | IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
        return null;
    }


    private void handleException(Exception e) throws AxisFault {

        String errorMessage = "Unknown error occurred.";

        if (e instanceof IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException) {
            IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException exception =
                    (IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException) e;
            if (exception.getFaultMessage().getIdentityApplicationTemplateMgtException() != null) {
                errorMessage = exception.getFaultMessage().getIdentityApplicationTemplateMgtException().getMessage();
            }
        } else {
            errorMessage = e.getMessage();
        }

        throw new AxisFault(errorMessage, e);
    }

}
