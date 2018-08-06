<%--
  ~ Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="org.wso2.carbon.identity.application.template.mgt.dto.xsd.SpTemplateDTO" %>
<%@ page
        import="org.wso2.carbon.identity.application.template.mgt.ui.client.ApplicationTemplateManagementServiceClient" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar"
           prefix="carbon" %>

<script type="text/javascript" src="extensions/js/vui.js"></script>
<script type="text/javascript" src="../extensions/core/js/vui.js"></script>
<script type="text/javascript" src="../admin/js/main.js"></script>

<jsp:include page="../dialog/display_messages.jsp"/>

<fmt:bundle
        basename="org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources">
    <carbon:breadcrumb label="application.mgt"
                       resourceBundle="org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources"
                       topPage="true" request="<%=request%>"/>

    <script type="text/javascript" src="../carbon/admin/js/breadcrumbs.js"></script>
    <script type="text/javascript" src="../carbon/admin/js/cookies.js"></script>
    <script type="text/javascript" src="../carbon/admin/js/main.js"></script>

    <div id="middle">

        <h2>
            <fmt:message key='title.list.service.provider.templates'/>
        </h2>

        <div id="workArea">

            <script type="text/javascript">

                function viewSPTemplate(templateName) {
                    location.href = "view-sp-template.jsp?templateName=" + templateName;

                }

                function editSPTemplate(templateName) {
                    location.href = "temp.jsp?templateName=" + templateName;
                }

                function removeSPTemplate(templateName) {
                    function doDelete() {
                        $.ajax({
                            type: 'POST',
                            url: 'remove-sp-template-finish-ajaxprocessor.jsp',
                            headers: {
                                Accept: "text/html"
                            },
                            data: 'templateName=' + templateName,
                            async: false,
                            success: function (responseText, status) {
                                if (status == "success") {
                                    location.assign("list-sp-templates.jsp");
                                }
                            }
                        });
                    }

                    CARBON.showConfirmationDialog('Are you sure you want to delete "' + templateName + '" SP Template information?',
                        doDelete, null);
                }

                /*function exportSPTemplate(templateName) {

                    function doInclude() {
                        document.getElementById('exportSecrets').value = "true";
                        location.href = "export-sp-template-finish-ajaxprocessor.jsp?templateName=" + templateName;
                    }
                    function doExclude() {
                        document.getElementById('exportSecrets').value = "false";
                        location.href = "export-sp-template-finish-ajaxprocessor.jsp?templateName=" + templateName;
                    }
                    document.getElementById('templateName').value = templateName;
                    CARBON.showConfirmationDialog('Do you want include the secret keys of "' + templateName + '"' +
                        ' export in the file ? (hashed or encrypted secret willn\'t be included)', doInclude, doExclude);
                }*/
            </script>

            <form id="spTemplateExportData" name="sp-template-export-data" method="post"
                  action="export-sp-template-finish-ajaxprocessor.jsp">
                <input hidden id="templateName" name="templateName"/>
                <input hidden id="exportSecrets" name="exportSecrets"/>
            </form>
            <%
                SpTemplateDTO[] spTemplateDTOS = null;

                String BUNDLE = "org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources";
                ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE, request.getLocale());
                SpTemplateDTO[] templatesToDisplay = new SpTemplateDTO[0];
                String paginationValue = "region=region1&item=sp_template_list";
                String pageNumber = request.getParameter("pageNumber");

                int pageNumberInt = 0;
                int numberOfPages = 0;
                int resultsPerPage = 10;

                String system_template = "system-default";

                if (pageNumber != null) {
                    try {
                        pageNumberInt = Integer.parseInt(pageNumber);
                    } catch (NumberFormatException ignored) {
                        //not needed here since it's defaulted to 0
                    }
                }

                try {
                    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
                    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
                    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);

                    ApplicationTemplateManagementServiceClient serviceClient = new
                            ApplicationTemplateManagementServiceClient(cookie, backendServerURL, configContext);
                    spTemplateDTOS = serviceClient.getAllApplicationTemplates();

                    if (spTemplateDTOS != null) {
                        numberOfPages = (int) Math.ceil((double) spTemplateDTOS.length / resultsPerPage);
                        int startIndex = pageNumberInt * resultsPerPage;
                        int endIndex = (pageNumberInt + 1) * resultsPerPage;
                        templatesToDisplay = new SpTemplateDTO[resultsPerPage];

                        for (int i = startIndex, j = 0; i < endIndex && i < spTemplateDTOS.length; i++, j++) {
                            templatesToDisplay[j] = spTemplateDTOS[i];
                        }
                    }
                } catch (Exception e) {
                    String message = resourceBundle.getString("error.while.reading.template.info") + " : " + e.getMessage();
                    CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request, e);
                }
            %>
            <br/>
            <table style="width: 100%" class="styledLeft">
                <tbody>
                <tr>
                    <td style="border:none !important">
                        <table class="styledLeft" width="100%" id="ServiceProviderTemplates">
                            <thead>
                            <tr style="white-space: nowrap">
                                <th class="leftCol-small"><fmt:message
                                        key="field.sp.template.name"/></th>
                                <th class="leftCol-big"><fmt:message
                                        key="field.sp.template.desc"/></th>
                                <th style="width: 30%"><fmt:message
                                        key="field.sp.template.action"/></th>
                            </tr>
                            </thead>
                            <%
                                boolean canView = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/identity/apptemplatemgt/view");
                                boolean canEdit = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/identity/apptemplatemgt/update");
                                boolean canDelete = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/identity/apptemplatemgt/delete");
                                if (spTemplateDTOS != null && spTemplateDTOS.length > 0) {
                            %>
                            <tbody>
                            <%
                                for (SpTemplateDTO template : templatesToDisplay) {
                                    if (template != null) {
                            %>
                            <tr>
                                <td><%=Encode.forHtml(template.getName())%>
                                </td>
                                <td><%=template.getDescription() != null ? Encode.forHtml(template.getDescription()) : ""%>
                                </td>
                                <td style="width: 100px; white-space: nowrap;">
                                    <%
                                        if (canView) {
                                    %>
                                    <a title="Edit Service Provider Template"
                                       onclick="viewSPTemplate('<%=Encode.forJavaScriptAttribute(template.getName())%>');return false;" href="#"
                                       class="icon-link"
                                       style="background-image: url(../application-template/images/edit.gif)">View
                                    </a>
                                    <%
                                        }
                                        if (canEdit && template.getName() != system_template) {
                                    %>
                                    <a title="Edit Service Provider Template"
                                       onclick="editSPTemplate('<%=Encode.forJavaScriptAttribute(template.getName())%>');return false;" href="#"
                                       class="icon-link"
                                       style="background-image: url(../application-template/images/edit.gif)">Edit
                                    </a>
                                    <%
                                        }
                                        if (canDelete && template.getName() != system_template) {
                                    %>
                                    <a title="Remove Service Provider Template"
                                       onclick="removeSPTemplate('<%=Encode.forJavaScriptAttribute(template.getName())%>');return false;" href="#"
                                       class="icon-link"
                                       style="background-image: url(../application-template/images/delete.gif)">Delete
                                    </a>
                                    <%
                                        }
                                    %>
                                    <%--<a title="Export Service Provider Template"
                                       onclick="exportSPTemplate('<%=Encode.forJavaScriptAttribute(template.getName())%>');return false;" href="#"
                                       class="icon-link"
                                       style="background-image: url(../application-template/images/publish.gif)">Export
                                    </a>--%>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                            </tbody>
                            <% } else { %>
                            <tbody>
                            <tr>
                                <td colspan="3"><i>No Service Provider Templates registered</i></td>
                            </tr>
                            </tbody>
                            <% } %>
                        </table>
                    </td>
                </tr>
                </tbody>
            </table>

            <carbon:paginator pageNumber="<%=pageNumberInt%>"
                              numberOfPages="<%=numberOfPages%>"
                              page="list-sp-templates.jsp"
                              pageNumberParameterName="pageNumber"
                              resourceBundle="org.wso2.carbon.identity.application.template.mgt.ui.i18n.Resources"
                              parameters="<%=paginationValue%>"
                              prevKey="prev" nextKey="next"/>
            <br/>
        </div>
    </div>
</fmt:bundle>