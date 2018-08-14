<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.ui.ApplicationBean" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.ui.util.ApplicationMgtUIUtil" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.ui.client.ApplicationManagementServiceClient" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.dto.xsd.SpTemplateDTO" %>

<%
    String templateName = request.getParameter("templateName");
    String templateDesc = request.getParameter("templateDesc");

    String oldSPName = request.getParameter("oldSPName");

    ApplicationBean appBean = ApplicationMgtUIUtil.getApplicationBeanFromSession(session, oldSPName);
    appBean.update(request);

    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    ApplicationManagementServiceClient serviceClient = new ApplicationManagementServiceClient(cookie,
            backendServerURL, configContext);
    SpTemplateDTO spTemplateDTO = new SpTemplateDTO();
    spTemplateDTO.setName(templateName);
    spTemplateDTO.setDescription(templateDesc);
    ServiceProvider sp = appBean.getServiceProvider();
    serviceClient.createServiceProviderAsTemplate(sp, spTemplateDTO);
%>
