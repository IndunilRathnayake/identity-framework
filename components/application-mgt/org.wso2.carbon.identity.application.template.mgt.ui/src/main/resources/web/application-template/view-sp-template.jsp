<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page
        import="org.wso2.carbon.identity.application.template.mgt.ui.client.ApplicationTemplateManagementServiceClient" %>
<%@ page import="org.wso2.carbon.identity.application.template.mgt.dto.xsd.SpTemplateDTO" %>

<%
    String templateName = request.getParameter("templateName");
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);

    ApplicationTemplateManagementServiceClient serviceClient = new
            ApplicationTemplateManagementServiceClient(cookie, backendServerURL, configContext);
    SpTemplateDTO spTemplateDTO = serviceClient.loadSpFromApplicationTemplate(templateName);
    String policyText = spTemplateDTO.getSpContent();
%>

<div id='XMLHolder' >  </div>

<LINK href='css/XMLDisplay.css' type='text/css' rel='stylesheet'>

<script type='text/javascript' src='js/XMLDisplay.js'></script>

<script>LoadXML(policyText); </script>