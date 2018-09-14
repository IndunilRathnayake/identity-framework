<%--
  ~ Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~  Licensed under the Apache License, Version 2.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  --%>

<%@ page import="org.apache.axis2.context.ConfigurationContext"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.wso2.carbon.CarbonConstants"%>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifact"%>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifactCategory" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.ui.client.ArtifactManagementServiceClient" %>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.ui.ArtifactMgtUIConstants" %>

<%
    String httpMethod = request.getMethod();
    if (!"post".equalsIgnoreCase(httpMethod)) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return;
    }

    String BUNDLE = "org.wso2.carbon.identity.tenant.artifact.mgt.ui.i18n.Resources";
    ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE, request.getLocale());

    String seqContent = request.getParameter("sequence-file-content").trim();
    String seqName = request.getParameter("sequence-name").trim();
    String seqDescription = request.getParameter("sequence-description").trim();
    if (StringUtils.isNotEmpty(seqContent)) {
        try {
            String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
            String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
            ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                    .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
            ArtifactManagementServiceClient serviceClient = new ArtifactManagementServiceClient(
                    cookie, backendServerURL, configContext);

            ResourceArtifactCategory artifactCategory = new ResourceArtifactCategory();
            ResourceArtifact artifact = new ResourceArtifact();
            artifact.setName(seqName);
            artifact.setDescription(seqDescription);
            artifact.setValue(seqContent);
            ResourceArtifact[] resourceArtifacts = new ResourceArtifact[] { artifact };
            artifactCategory.setName(ArtifactMgtUIConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME);
            artifactCategory.setDescription(ArtifactMgtUIConstants.DEFAULT_AUTH_SEQ_CATEGORY_DESC);
            artifactCategory.setResourceArtifacts(resourceArtifacts);

            if(serviceClient.isExistingArtifactCategory(ArtifactMgtUIConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME)) {
                serviceClient.updateArtifactCategory(ArtifactMgtUIConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME,
                        artifactCategory);
            } else {
                serviceClient.createArtifactCategory(artifactCategory);
            }
%>
<script>
    location.href = 'list-default-authSeq.jsp';
</script>
<%
} catch (Exception e) {
    String message = resourceBundle.getString("error.add.artifact") + " : " + e.getMessage();
    CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request, e);
%>
<script>
    location.href = 'add-default-authSeq.jsp';
</script>
<%
    }
} else {
%>
<script>
    location.href = 'add-default-authSeq.jsp';
</script>
<%
    }
%>
