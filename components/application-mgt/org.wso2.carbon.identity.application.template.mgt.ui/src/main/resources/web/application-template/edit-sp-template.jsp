<!--
~ Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
~ KIND, either express or implied. See the License for the
~ specific language governing permissions and limitations
~ under the License.
-->
<link rel="stylesheet" href="codemirror/lib/codemirror.css">
<link rel="stylesheet" href="codemirror/theme/mdn-like.css">
<link rel="stylesheet" href="codemirror/addon/dialog/dialog.css">
<link rel="stylesheet" href="codemirror/addon/display/fullscreen.css">
<link rel="stylesheet" href="codemirror/addon/fold/foldgutter.css">
<link rel="stylesheet" href="codemirror/addon/hint/show-hint.css">
<link rel="stylesheet" href="codemirror/addon/lint/lint.css">

<link rel="stylesheet" href="css/idpmgt.css">
<link rel="stylesheet" href="css/conditional-authentication.css">

<script src="codemirror/lib/codemirror.js"></script>
<script src="codemirror/keymap/sublime.js"></script>
<script src="codemirror/mode/javascript/javascript.js"></script>

<script src="codemirror/addon/lint/jshint.min.js"></script>
<script src="codemirror/addon/lint/lint.js"></script>
<script src="codemirror/addon/lint/javascript-lint.js"></script>
<script src="codemirror/addon/hint/anyword-hint.js"></script>
<script src="codemirror/addon/hint/show-hint.js"></script>
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


<%@ page import="com.google.gson.JsonArray" %>

<%@ page import="com.google.gson.JsonPrimitive" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="carbon" uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.wso2.carbon.identity.application.template.mgt.dto.xsd.SpTemplateDTO" %>
<%@ page
        import="org.wso2.carbon.identity.application.template.mgt.ui.client.ApplicationTemplateManagementServiceClient" %>

<carbon:breadcrumb label="breadcrumb.advanced.auth.step.config"
                   resourceBundle="org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources"
                   topPage="true" request="<%=request%>"/>
<jsp:include page="../dialog/display_messages.jsp"/>

<%
    String templatesJson = null;
    String availableJsFunctionsJson = null;

    StringBuilder localAuthTypes = new StringBuilder();
    String startOption = "<option value=\"";
    String middleOption = "\">";
    String endOption = "</option>";
    String policyText = "";


    try {
        String templateName = request.getParameter("templateName");

        String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
        ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);

        ApplicationTemplateManagementServiceClient serviceClient = new
                ApplicationTemplateManagementServiceClient(cookie, backendServerURL, configContext);
        SpTemplateDTO spTemplateDTO = serviceClient.loadSpFromApplicationTemplate(templateName);
        policyText = spTemplateDTO.getSpContent();
        JsonArray jsonArray = new JsonArray();
        availableJsFunctionsJson = jsonArray.toString();
    } catch (Exception e) {
        CarbonUIMessage.sendCarbonUIMessage("Error occurred while loading SP advanced outbound authentication " +
                "configuration", CarbonUIMessage.ERROR, request, e);
    }
    if (templatesJson == null) {
        templatesJson = "";
    }
    templatesJson = StringEscapeUtils.escapeJavaScript(templatesJson);

%>

<%

    StringBuilder idpType = new StringBuilder();
    StringBuilder enabledIdpType = new StringBuilder();
    Map<String, String> idpAuthenticators = new HashMap<String, String>();
    Map<String, String> enabledIdpAuthenticators = new HashMap<String, String>();
    Map<String, Boolean> idpEnableStatus = new HashMap<String, Boolean>();
    Map<String, Boolean> idpAuthenticatorsStatus = new HashMap<String, Boolean>();

%>

<fmt:bundle basename="org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources">
    <div id="middle">
        <h2>
            <fmt:message key='breadcrumb.advanced.auth.step.config.for'/>
        </h2>
        <div id="workArea">
            <form id="configure-auth-flow-form" method="post" name="configure-auth-flow-form" method="post"
                  action="configure-authentication-flow-finish-ajaxprocessor.jsp">
                <input type=hidden name=spName value=''/>


                <h2 id="authentication_step_config_head1" class="sectionSeperator trigger">
                    <a href="#"><fmt:message key="title.config.authentication.steps"/></a>
                </h2>

                <div class="toggle_container sectionSub" style="margin-bottom:10px;" id="stepsConfRow">
                    <table>
                        <tr>
                            <td><a id="stepsAddLink" class="icon-link"
                                   style="background-image:url(images/add.gif);margin-left:0" href="#"><fmt:message
                                    key='button.add.step'/></a></td>
                        </tr>
                    </table>

                    <div class="script-select-container" style="display: none;">
                        <label class="noselect">
                            <input id="enableScript" name="enableScript" type="checkbox" value="true"
                                   checked="checked"/> Enable Script Based Adaptive Authentication
                        </label>
                    </div>
                </div>

                <div style="clear:both"></div>
                <!-- sectionSub Div -->
                <br/>
                <h2 id="authentication_step_config_head2" class="sectionSeperator trigger active">
                    <a href="#">Script Based Adaptive Authentication</a>
                </h2>

                <div class="toggle_container sectionSub" id="editorRow">
                    <div class="err_warn_container">
                        <div class="disable_status">
                            <img src="images/disabled.png"><span class="disable_text">Disabled</span>
                            <span class="show_errors_toggle_buttons">
                                <a href="#">[+] See Errors</a>
                                <a href="#" style="display: none;">[-] Hide Errors</a>
                            </span>
                        </div>
                        <div class="err_warn_content">
                            <div class="err_container">
                                <img src="images/error.gif" class="editor_err_img"/> <span class="err_head">Errors</span>
                                <ul class="err_list"></ul>
                            </div>
                            <div class="warn_container">
                                <img src="images/warning.gif" class="editor_warn_img"/><span class="err_head">Warnings</span>
                                <ul class="warn_list"></ul>
                            </div>
                        </div>
                        <div class="instruction">Correct errors and update to enable the script.</div>
                    </div>
                    <div style="position: relative;">
                        <div class="sectionSub step_contents" id="codeMirror">
<textarea id="scriptTextArea" name="scriptTextArea"
          placeholder="Write custom JavaScript or select from templates that match a scenario..."
          style="height: 500px;width: 100%; display: none;"><%
    out.print(policyText);%></textarea>
                        </div>
                        <div id="codeMirrorTemplate" class="step_contents">
                            <div class="add-template-container vertical-text">
                                <a id="addTemplate" class="icon-link noselect">Templates</a>
                            </div>
                            <div class="template-list-container">
                                <ul id="template_list"></ul>
                            </div>
                        </div>
                    </div>
                </div>
                <div style="clear:both"></div>
                <div class="buttonRow" style=" margin-top: 10px;">
                    <input id="createApp" type="button" value="<fmt:message key='button.update.service.provider'/>"/>
                    <input type="button" value="<fmt:message key='button.cancel'/>"
                           onclick="javascript:location.href='configure-service-provider.jsp?display=auth_config&spName='"/>
                </div>
            </form>
        </div>
    </div>
    <div class="editor-error-warn-container">
        <div class="err_warn_text"></div>
        <div class="editor-error-content">
            <div class="messagebox-error-custom">
                <ul class="errorListContainer"></ul>
                <ul class="stepErrorListContainer"></ul>
            </div>
        </div>
        <div class="editor-warning-content">
            <div class="messagebox-warning-custom">
                <ul class="warningListContainer"></ul>
                <ul class="stepWarningListContainer"></ul>
            </div>
        </div>
    </div>
</fmt:bundle>

<script id="template-info" type="text/x-handlebars-template">
    <div id="template-replace-warn" class="error-msg">
        <p>The template code will replace the existing scripts in the editor. Any of your current
            changes will be lost. Click "OK" to continue.</p>
    </div>
    <div id='messagebox-template-summary' class="messagebox-info-custom">
        <h2>{{title}}</h2>
        <br/>
        {{#if summary}}
        <p>{{summary}}</p>
        <br/>
        {{/if}}
        {{#if preRequisites}}
        <h3>Prerequisites</h3>
        <ul>
            {{#each preRequisites}}
            <li>{{this}}</li>
            {{/each}}
        </ul>
        <br/>
        {{/if}}
        {{#if parametersDescription}}
        <h3>Parameters</h3>
        <table>
            <tbody>
            {{#each parametersDescription}}
            <tr>
                <td><i>{{@key}}</i></td>
                <td>{{this}}</td>
            </tr>
            {{/each}}
            </tbody>
        </table>
        <br/>
        {{/if}}
        {{#if defaultStepsDescription}}
        <h3>Default Steps</h3>
        <ul>
            {{#each defaultStepsDescription}}
            <li>{{@key}} : {{this}}</li>
            {{/each}}
        </ul>
        <br/>
        {{/if}}
        {{#if helpLink}}
        <h3>Help/Reference</h3>
        <a href="{{helpLink}}">{{helpLink}}</a>
        {{/if}}
    </div>
</script>
<script>
    var authMap = {};
    var conditionalAuthFunctions = $.parseJSON('<%=availableJsFunctionsJson%>');
    var localAuthenticators = [];

    var stepOrder = 0;

    var stepOrder = 0;
    var img = "";

    var templates = $.parseJSON('<%=templatesJson%>');

    function addNewUIStep(){
        stepOrder++;
        jQuery('#stepsConfRow .steps').append(jQuery('<h2 id="step_head_' + stepOrder +
            '" class="sectionSeperator trigger active step_heads" style="background-color: beige; clear: both;"><input type="hidden" value="' + stepOrder + '" name="auth_step" id="auth_step"><a class="step_order_header" href="#">Step ' + stepOrder + '</a><a href="#" class="delete_step icon-link" data-step-no="' + stepOrder + '" style="background-image: url(images/delete.gif);float:right;width: 9px;"></a></h2><div class="toggle_container sectionSub step_contents step_body" style="margin-bottom:10px;" id="step_dev_' + stepOrder + '"> <div style="padding-bottom: 5px"><table class="carbonFormTable"><tr><td><input type="checkbox" style="vertical-align: middle;" id="subject_step_' + stepOrder + '" name="subject_step_' + stepOrder + '" class="subject_steps" onclick="setSubjectStep(this)"><label for="subject_step_' + stepOrder + '" style="cursor: pointer;">Use subject identifier from this step</label></td></tr><tr><td><input type="checkbox" style="vertical-align: middle;" id="attribute_step_' + stepOrder + '" name="attribute_step_' + stepOrder + '" class="attribute_steps" onclick="setAttributeStep(this)" ><label for="attribute_step_' + stepOrder + '" style="cursor: pointer;">Use attributes from this step</label></td></tr></table></div><h2 id="local_auth_head_' + stepOrder + '" class="sectionSeperator trigger active" style="background-color: floralwhite;"><a href="#">Local Authenticators</a></h2><div class="toggle_container sectionSub" style="margin-bottom:10px;" id="local_auth_head_dev_' + stepOrder + '"><table class="styledLeft auth_table" width="100%" id="local_auth_table_' + stepOrder + '"><thead><tr><td><select name="step_' + stepOrder + '_local_oauth_select" style="float: left; min-width: 150px;font-size:13px;"><%=localAuthTypes.toString()%></select><a id="localOptionAddLinkStep_' + stepOrder + '" onclick="addLocalRow(this,' + stepOrder + ');return false;" class="icon-link claimMappingAddLinkss claimMappingAddLinkssLocal" style="background-image:url(images/add.gif);">Add Authenticator</a></td></tr></thead></table> </div><%if (enabledIdpType.length() > 0) { %> <h2 id="fed_auth_head_' + stepOrder + '" class="sectionSeperator trigger active" style="background-color: floralwhite;"><a href="#">Federated Authenticators</a></h2><div class="toggle_container sectionSub" style="margin-bottom:10px;" id="fed_auth_head_dev_' + stepOrder + '"><table class="styledLeft auth_table" width="100%" id="fed_auth_table_' + stepOrder + '"><thead> <tr><td><select name="idpAuthType_' + stepOrder + '" style="float: left; min-width: 150px;font-size:13px;"><%=enabledIdpType.toString()%></select><a id="claimMappingAddLinkss" onclick="addIDPRow(this,' + stepOrder + ');return false;" class="icon-link claimMappingAddLinkssIdp" style="background-image:url(images/add.gif);">Add Authenticator</a></td></tr></thead></table></div><%}%></div>'));
        if (!$('#stepsConfRow').is(":visible")) {
            $(jQuery('#stepsConfRow')).toggle();
        }
        if (stepOrder == 1) {
            $('#subject_step_' + stepOrder).attr('checked', true);
            $('#attribute_step_' + stepOrder).attr('checked', true);
        }
    }
</script>
<script src="./js/configure-authentication-flow.js"></script>