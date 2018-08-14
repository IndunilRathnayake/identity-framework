<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page
        import="org.wso2.carbon.identity.application.template.mgt.ui.client.ApplicationTemplateManagementServiceClient" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page
        import="java.util.ResourceBundle" %>

<%! public static final String BYTES = "bytes";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String ACCEPT_RANGES = "Accept-Ranges";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream;";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String XML = ".xml";
    private static final String ATTACHMENT_FILENAME = "attachment;filename=\"";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String BUNDLE = "org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources";
%>
<%
    String httpMethod = request.getMethod();
    if (!"post".equalsIgnoreCase(httpMethod)) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return;
    }

    String templateName = request.getParameter("templateName");
    String secrets = request.getParameter("exportSecrets");
    boolean exportSecrets = "on".equals(secrets);
    ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE, request.getLocale());

    if (StringUtils.isNotEmpty(templateName)) {
        try {
            String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
            String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
            ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                    .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);

            ApplicationTemplateManagementServiceClient serviceClient = new
                    ApplicationTemplateManagementServiceClient(cookie, backendServerURL, configContext);
            String appData = serviceClient.exportApplicationTemplate(templateName, exportSecrets);
            out.clearBuffer();
            response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + templateName + XML + "\"");
            response.setHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM);
            response.setHeader(ACCEPT_RANGES, BYTES);
            response.setHeader(CONTENT_LENGTH, String.valueOf(appData.length()));
            out.write(appData);
        } catch (Exception e) {
            String message = resourceBundle.getString("template.list.error.while.exporting.template") + " : " + e.getMessage();
            CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request, e);
        }
    }
%>
<script>
    location.href = 'list-sp-templates.jsp';
</script>
