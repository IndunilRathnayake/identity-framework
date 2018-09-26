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

<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.DefaultAuthenticationSequence" %>
<%@ page import="org.wso2.carbon.idp.mgt.ui.client.DefaultAuthenticationSeqMgtServiceClient" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page
        import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar"
           prefix="carbon" %>
<script type="text/javascript" src="extensions/js/vui.js"></script>
<script type="text/javascript" src="../extensions/core/js/vui.js"></script>
<script type="text/javascript" src="../admin/js/main.js"></script>
<jsp:include page="../dialog/display_messages.jsp"/>
<fmt:bundle
        basename="org.wso2.carbon.idp.mgt.ui.i18n.Resources">
    <carbon:breadcrumb label="breadcrumb.artifact.mgt"
                       resourceBundle="org.wso2.carbon.idp.mgt.ui.i18n.Resources"
                       topPage="true" request="<%=request%>"/>
    <script type="text/javascript" src="../carbon/admin/js/breadcrumbs.js"></script>
    <script type="text/javascript" src="../carbon/admin/js/cookies.js"></script>
    <script type="text/javascript" src="../carbon/admin/js/main.js"></script>
    <script>
        function exportSequence() {
            jQuery('#defaultAuthSeqExportData').submit();
            jQuery(this).dialog("close");
        }

        function closeExportSequence() {
            jQuery(this).dialog("close");
        }

        $(function () {
            $("#exportDefaultAuthSeqMsgDialog").dialog({
                autoOpen: false,
                buttons: {
                    OK: exportSequence,
                    Cancel: closeExportSequence
                },
                height: 160,
                width: 450,
                minHeight: 160,
                minWidth: 330,
                modal: true
            });
        });
    </script>
    <div id="middle">
        <h2>
            <fmt:message key='title.list.default.auth.seq'/>
        </h2>
        <div id="workArea">
            <script type="text/javascript">
                function removeDefaultAuthSeq() {
                    function doDelete() {
                        $.ajax({
                            type: 'POST',
                            url: 'remove-default-authSeq-finish-ajaxprocessor.jsp',
                            headers: {
                                Accept: "text/html"
                            },
                            async: false,
                            success: function (responseText, status) {
                                if (status == "success") {
                                    location.assign("list-default-authSeq.jsp");
                                }
                            }
                        });
                    }

                    CARBON.showConfirmationDialog('Are you sure you want to delete default authentication sequence ' + seqName + ' ?',
                        doDelete, null);
                }

                function exportDefaultAuthSeq() {
                    $('#exportDefaultAuthSeqMsgDialog').dialog("open");
                }
            </script>
            <%
                String BUNDLE = "org.wso2.carbon.idp.mgt.ui.i18n.Resources";
                ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE, request.getLocale());
                DefaultAuthenticationSequence sequence = null;
                try {
                    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
                    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
                    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
                    DefaultAuthenticationSeqMgtServiceClient serviceClient = new
                            DefaultAuthenticationSeqMgtServiceClient(cookie, backendServerURL, configContext);
                    sequence = serviceClient.getDefaultAuthenticationSeq();
                } catch (Exception e) {
                    String message = resourceBundle.getString("error.read.artifact.info") + " : " + e.getMessage();
                    CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request, e);
                }
            %>
            <br/>
            <table style="width: 100%" class="styledLeft">
                <div style="height:30px;">
                    <%
                        if (sequence == null || StringUtils.isBlank(sequence.getContentXml())) {
                    %>
                    <a href="javascript:document.location.href='add-default-authSeq.jsp'" class="icon-link"
                       style="background-image:url(../admin/images/add.gif);"><fmt:message
                            key="default.auth.seq.add.link"/></a>
                    <%
                    } else {
                    %>
                    <a href="javascript:document.location.href='add-default-authSeq.jsp'" class="icon-link isDisabled"
                       style="background-image:url(../admin/images/add.gif);"><fmt:message
                            key="default.auth.seq.add.link"/></a>
                    <%
                        }
                    %>
                </div>

                <tbody>
                <tr>
                    <td style="border:none !important">
                        <table class="styledLeft" width="100%" id="defaultAuthnSequences">
                            <thead>
                            <tr style="white-space: nowrap">
                                <th class="leftCol-big"><fmt:message
                                        key="field.default.auth.seq.desc"/></th>
                                <th style="width: 30%"><fmt:message
                                        key="field.default.auth.seq.action"/></th>
                            </tr>
                            </thead>
                            <%
                                if (sequence != null && StringUtils.isNotBlank(sequence.getContentXml())) {
                            %>
                            <tbody>
                            <tr>
                                <td><%=sequence.getDescription() != null ? Encode.forHtml(sequence.getDescription()) : ""%>
                                </td>
                                <td style="width: 100px; white-space: nowrap;">
                                    <a title="Edit Default Authentication Sequence"
                                       onclick="javascript:location.href='edit-default-authSeq.jsp%>'"
                                       class="icon-link"
                                       style="background-image: url(../application/images/edit.gif)"><fmt:message
                                            key="field.default.auth.seq.edit"/>
                                    </a>
                                    <a title="Export Default Authentication Sequence"
                                       onclick="exportDefaultAuthSeq();"
                                       class="icon-link"
                                       style="background-image: url(../application/images/publish.gif)">
                                        <fmt:message key="field.default.auth.seq.export"/>
                                    </a>
                                    <a title="Remove Default Authentication Sequence"
                                       onclick="removeDefaultAuthSeq();"
                                       class="icon-link"
                                       style="background-image: url(../application/images/delete.gif)"><fmt:message
                                            key="field.default.auth.seq.delete"/>
                                    </a>
                                </td>
                            </tr>
                            </tbody>
                            <% } else { %>
                            <tbody>
                            <tr>
                                <td colspan="3"><i><fmt:message key="default.auth.seq.not.registered"/></i></td>
                            </tr>
                            </tbody>
                            <% } %>
                        </table>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    </div>
    <div id='exportDefaultAuthSeqMsgDialog' title='WSO2 Carbon'>
        <div id='messagebox-confirm'>
            <p><fmt:message key="default.auth.seq.export.para"/></p><br>
            <form id="defaultAuthSeqExportData" name="default-authSeq-export-data" method="post"
                  action="export-default-authSeq-finish-ajaxprocessor.jsp">
            </form>
        </div>
    </div>
</fmt:bundle>
