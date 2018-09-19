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

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="carbon" uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar"%>

<carbon:breadcrumb label="breadcrumb.artifact.mgt"
                   resourceBundle="org.wso2.carbon.identity.application.defaultauth.seq.mgt.ui.i18n.Resources"
                   topPage="true"
                   request="<%=request%>" />
<jsp:include page="../dialog/display_messages.jsp"/>
<script type="text/javascript" src="extensions/js/vui.js"></script>
<script type="text/javascript" src="../extensions/core/js/vui.js"></script>
<script type="text/javascript" src="../admin/js/main.js"></script>
<script type="text/javascript" src="../identity/validation/js/identity-validate.js"></script>
<jsp:include page="../dialog/display_messages.jsp" />

<script type="text/javascript">
    var openFile = function (event) {
        var input = event.target;
        var reader = new FileReader();
        reader.onload = function () {
            var data = reader.result;
            document.getElementById('sequence-file-content').value = data;
        };
        document.getElementById('sequence-file-name').value = input.files[0].name;
        reader.readAsText(input.files[0]);
    };

    function validateTextForIllegal(fld) {
        var isValid = doValidateInput(fld, "Provided Default Authentication Sequence name is invalid.");
        if (isValid) {
            return true;
        } else {
            return false;
        }
    }

    function importDefaultAuthnSeq() {
        var seqName = $.trim(document.getElementById('sequence-name').value);
        var seqContent = $.trim(document.getElementById('sequence-file-content').value);
        if (seqName === null || 0 === seqName.length) {
            CARBON.showWarningDialog('Please specify default authentication sequence name.');
            location.href = '#';
            return false;
        } else if (seqContent === null || 0 === seqContent.length) {
            CARBON.showWarningDialog('Please specify default authentication sequence configuration file.');
            location.href = '#';
            return false;
        } else if (!validateTextForIllegal(document.getElementById("sequence-name"))) {
            return false;
        } else {
            $("#add-default-auth-seq-form").submit();
            return true;
        }
    }
</script>
<fmt:bundle basename="org.wso2.carbon.identity.application.defaultauth.seq.mgt.ui.i18n.Resources">
    <div id="middle">
        <h2>
            <fmt:message key='title.default.auth.sequence.add'/>
        </h2>
        <div id="workArea">
            <form id="add-default-auth-seq-form" name="add-default-auth-seq-form" method="post"
                  action="add-default-authSeq-finish-ajaxprocessor.jsp">
                <div class="sectionSeperator togglebleTitle"><fmt:message key='upload.default.auth.seq.file'/></div>
                <div class="sectionSub">
                    <table class="carbonFormTable">
                        <tr>
                            <td style="width:15%" class="leftCol-med labelField"><fmt:message key='config.default.auth.seq.name'/>:<span class="required">*</span></td>
                            <td>
                                <input id="sequence-name" name="sequence-name" type="text" value="" white-list-patterns="^[a-zA-Z0-9\s._-]*$" autofocus/>
                                <div class="sectionHelp">
                                    <fmt:message key='help.sequence.name'/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:15%" class="leftCol-med labelField"><fmt:message key='config.default.auth.seq.desc'/>:</td>
                            <td>
                                <textarea style="width:50%" type="text" name="sequence-description" id="sequence-description" class="text-box-big"></textarea>
                                <div class="sectionHelp">
                                    <fmt:message key='help.sequence.desc'/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:15%" class="leftCol-med labelField"><fmt:message key='config.default.auth.seq.file.location'/>:</td>
                            <td>
                                <input type="file" class="button" id="sp_file" name="sp_file" onchange='openFile(event)'/>
                                <textarea hidden="hidden" name="sequence-file-content" id="sequence-file-content"></textarea>
                                <textarea hidden="hidden" name="sequence-file-name" id="sequence-file-name"></textarea>
                                <div class="sectionHelp">
                                    <fmt:message key='help.sequence.content'/>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="buttonRow">
                    <input type="button" class="button"  value="<fmt:message key='button.import.default.auth.seq'/>" onclick="importDefaultAuthnSeq();"/>
                    <input type="button" class="button" onclick="javascript:location.href='list-sp-templates.jsp'" value="<fmt:message key='button.cancel'/>"/>
                </div>
            </form>
        </div>
    </div>
</fmt:bundle>
