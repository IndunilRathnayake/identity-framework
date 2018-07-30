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

/**
 * This class invokes the operations of IdentityApplicationTemplateManagementService.
 */
public class ApplicationTemplateMgtServiceClient {

    private IdentityApplicationTemplateManagementServiceStub stub;
    private static final Log log = LogFactory.getLog(ApplicationTemplateMgtServiceClient.class);
    private final boolean debugEnabled = log.isErrorEnabled();

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
     * Load application template.
     *
     * @param templateName template name
     * @return service provider template info
     * @throws AxisFault
     */
    public SpTemplateDTO loadSpFromApplicationTemplate(String templateName) throws AxisFault {

        try {
            if (debugEnabled) {
                log.debug("Loading Service Provider template for name: " + templateName);
            }
            return stub.loadApplicationTemplate(templateName);
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Get all application template names.
     *
     * @return Array of all application template names
     * @throws AxisFault
     */
    public String[] getAllApplicationTemplateNames() throws AxisFault {

        String[] templateNames = null;
        try {
            if (debugEnabled) {
                log.debug("Get all service provider template names.");
            }
            templateNames = stub.getAllApplicationTemplateNames();
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
        return templateNames;
    }

    /**
     * Get all the application templates.
     *
     * @return Array of all application templates
     * @throws AxisFault
     */
    public SpTemplateDTO[] getAllApplicationTemplates() throws AxisFault {

        SpTemplateDTO[] templates = null;
        try {
            if (debugEnabled) {
                log.debug("Get all service provider templates.");
            }
            templates = stub.getAllApplicationTemplates();
        } catch (RemoteException |
                IdentityApplicationTemplateManagementServiceIdentityApplicationTemplateMgtException e) {
            handleException(e);
        }
        return templates;
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
