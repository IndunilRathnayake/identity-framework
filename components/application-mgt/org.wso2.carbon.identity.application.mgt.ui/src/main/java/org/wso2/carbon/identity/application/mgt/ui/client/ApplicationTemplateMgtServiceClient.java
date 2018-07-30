package org.wso2.carbon.identity.application.mgt.ui.client;

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

public class ApplicationTemplateMgtServiceClient {

    IdentityApplicationTemplateManagementServiceStub stub;
    Log log = LogFactory.getLog(ApplicationTemplateMgtServiceClient.class);
    boolean debugEnabled = log.isErrorEnabled();

    /**
     * @param cookie
     * @param backendServerURL
     * @param configCtx
     * @throws AxisFault
     */
    public ApplicationTemplateMgtServiceClient(String cookie, String backendServerURL,
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
     * @throws AxisFault
     */
    public SpTemplateDTO loadSpFromApplicationTemplate(String templateName) throws AxisFault {
        try {
            if (debugEnabled) {
                log.debug("Registering Service Provider template" );
            }
            return stub.loadApplicationTemplate(templateName);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @throws AxisFault
     */
    public String[] getAllApplicationTemplateNames() throws AxisFault {
        String[] templateNames = null;

        try {
            if (debugEnabled) {
                log.debug("Registering Service Provider template" );
            }
            templateNames = stub.getAllApplicationTemplateNames();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            e.printStackTrace();
        }
        return templateNames;
    }
}
