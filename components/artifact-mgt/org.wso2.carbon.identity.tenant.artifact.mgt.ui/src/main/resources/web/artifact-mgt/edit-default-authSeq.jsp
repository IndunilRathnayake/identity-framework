<%--
  ~ Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~ WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<link rel="stylesheet" href="codemirror/lib/codemirror.css">
<link rel="stylesheet" href="codemirror/theme/mdn-like.css">
<link rel="stylesheet" href="codemirror/addon/dialog/dialog.css">
<link rel="stylesheet" href="codemirror/addon/display/fullscreen.css">
<link rel="stylesheet" href="codemirror/addon/fold/foldgutter.css">
<link rel="stylesheet" href="codemirror/addon/hint/show-hint.css">
<link rel="stylesheet" href="codemirror/addon/lint/lint.css">
<link rel="stylesheet" href="css/default-auth-seq-mgt.css">
<link rel="stylesheet" href="css/edit-default-authSeq.css">
<script src="codemirror/lib/codemirror.js"></script>
<script src="codemirror/keymap/sublime.js"></script>
<script src="codemirror/mode/xml/xml.js"></script>
<script src="codemirror/mode/xml/test.js"></script>
<script src="codemirror/addon/lint/jshint.min.js"></script>
<script src="codemirror/addon/lint/lint.js"></script>
<script src="codemirror/addon/lint/javascript-lint.js"></script>
<script src="codemirror/addon/hint/anyword-hint.js"></script>
<script src="codemirror/addon/hint/show-hint.js"></script>
<script src="codemirror/addon/hint/xml-hint.js"></script>
<script src="codemirror/addon/hint/javascript-hint.js"></script>
<script src="codemirror/addon/hint/wso2-hints.js"></script>
<script src="codemirror/addon/edit/closebrackets.js"></script>
<script src="codemirror/addon/edit/matchbrackets.js"></script>
<script src="codemirror/addon/fold/brace-fold.js"></script>
<script src="codemirror/addon/fold/foldcode.js"></script>
<script src="codemirror/addon/fold/foldgutter.js"></script>
<script src="codemirror/addon/display/fullscreen.js"></script>
<script src="codemirror/addon/display/placeholder.js"></script>
<script src="codemirror/addon/comment/comment.js"></script>
<script src="codemirror/addon/selection/active-line.js"></script>
<script src="codemirror/addon/dialog/dialog.js"></script>
<script src="codemirror/addon/display/panel.js"></script>
<script src="codemirror/util/formatting.js"></script>
<script src="js/handlebars.min-v4.0.11.js"></script>
<script src="../admin/js/main.js" type="text/javascript"></script>
<script type="text/javascript" src="extensions/js/vui.js"></script>
<script type="text/javascript" src="../extensions/core/js/vui.js"></script>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="carbon" uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.dto.xsd.ResourceArtifact" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.ui.client.ArtifactManagementServiceClient" %>
<%@ page import="org.wso2.carbon.identity.tenant.artifact.mgt.ui.ArtifactMgtUIConstants" %>

<jsp:include page="../dialog/display_messages.jsp"/>
<fmt:bundle
        basename="org.wso2.carbon.identity.tenant.artifact.mgt.ui.i18n.Resources">
    <carbon:breadcrumb label="breadcrumb.artifact.mgt"
                       resourceBundle="org.wso2.carbon.identity.tenant.artifact.mgt.ui.i18n.Resources"
                       topPage="true" request="<%=request%>"/>
    <script type="text/javascript" src="../carbon/admin/js/breadcrumbs.js"></script>
    <script type="text/javascript" src="../carbon/admin/js/cookies.js"></script>
    <script type="text/javascript" src="../carbon/admin/js/main.js"></script>
    <script type="text/javascript" src="../identity/validation/js/identity-validate.js"></script>
    <%
        String seqContent = "";
        String seqName = "";
        String seqDesc = "";

        String BUNDLE = "org.wso2.carbon.identity.tenant.artifact.mgt.ui.i18n.Resources";
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE, request.getLocale());

        try {
            seqName = request.getParameter("seqName").trim();
            String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
            String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
            ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                    .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
            ArtifactManagementServiceClient serviceClient = new
                    ArtifactManagementServiceClient(cookie, backendServerURL, configContext);
            ResourceArtifact resourceArtifact = serviceClient.getArtifact(
                    ArtifactMgtUIConstants.DEFAULT_AUTH_SEQ_CATEGORY_NAME, seqName);
            if (resourceArtifact == null) {
                String message = resourceBundle.getString("error.load.artifact");
                CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request);
                return;
            }
            seqDesc = resourceArtifact.getDescription();
            seqContent = resourceArtifact.getValue();
        } catch (Exception e) {
            String message = resourceBundle.getString("error.load.artifact") + " : " + e.getMessage();
            CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request, e);
        }
    %>
    <div id="middle">
    <h2>
        <fmt:message key='title.default.auth.sequence.update'/>
    </h2>
    <div id="workArea">
        <form id="update-default-authSeq-form" name="update-default-authSeq-form" method="post"
              action="">
            <table class="carbonFormTable">
                <tr>
                    <td style="width:15%" class="leftCol-med labelField"><fmt:message key='config.default.auth.seq.name'/>:<span class="required">*</span></td>
                    <td>
                        <input style="width:50%" id="sequence-name" name="sequence-name" type="text"
                               value="<%=Encode.forHtmlAttribute(seqName)%>"
                               white-list-patterns="^[a-zA-Z0-9\s._-]*$" autofocus/>
                        <div class="sectionHelp"><fmt:message key='help.sequence.name'/></div>
                    </td>
                </tr>
                <tr>
                    <td style="width:15%" class="leftCol-med labelField"><fmt:message key='config.default.auth.seq.desc'/>:</td>
                    <td>
                        <textarea style="width:50%" type="text" name="sequence-description" id="sequence-description"
                                  class="text-box-big"><%=seqDesc != null ? Encode.forHtmlContent(seqDesc) : ""%></textarea>
                        <div class="sectionHelp"><fmt:message key='help.sequence.desc'/></div>
                    </td>
                </tr>
            </table>
            <div class="toggle_container sectionSub" id="editorRow">
                <div style="position: relative;">
                    <div class="sectionSub step_contents" id="codeMirror">
                        <textarea id="seqContent" name="seqContent" readonly="true"
                                  style="height: 500px;width: 100%; display: none;"><%out.print(Encode.forHtmlContent(seqContent));%></textarea>
                    </div>
                </div>
            </div>
            </br>
            <textarea hidden="hidden" name="default-authSeq-name" id="default-authSeq-name"><%=Encode.forHtmlContent(seqName)%></textarea>
            <div class="buttonRow">
                <input type="button" class="button" onclick="javascript:location.href='list-default-authSeq.jsp'"
                       value="<fmt:message key='button.cancel'/>"/>
            </div>
        </form>
    </div>
</fmt:bundle>
<script src="js/edit-default-authSeq.js"></script>
