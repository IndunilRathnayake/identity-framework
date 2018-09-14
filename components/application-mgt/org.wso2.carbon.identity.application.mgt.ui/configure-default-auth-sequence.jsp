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

<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.AuthenticationStep" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.LocalAuthenticatorConfig" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.IdentityProvider" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.FederatedAuthenticatorConfig" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.xsd.LocalAndOutboundAuthenticationConfig" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.ui.util.ApplicationMgtUIUtil" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.ui.ApplicationBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.wso2.carbon.identity.application.common.model.script.xsd.AuthenticationScriptConfig" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.identity.application.mgt.ui.client.IdpManagementServiceClient" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>

<%
    String httpMethod = request.getMethod();
    if (!"post".equalsIgnoreCase(httpMethod)) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return;
    }

    String spName = request.getParameter("spName");
    ApplicationBean appBean = ApplicationMgtUIUtil.getApplicationBeanFromSession(session, spName);
    LocalAuthenticatorConfig[] localAuthenticatorConfigs = appBean.getLocalAuthenticatorConfigs();
    Map<String, IdentityProvider> federatedIdentityProvidersMap = appBean.getFederatedIdentityProviderMap();
    LocalAndOutboundAuthenticationConfig localAndOutboundAuthenticationConfig = new LocalAndOutboundAuthenticationConfig();

    String[] authSteps = request.getParameterValues("auth_step");

    if (authSteps != null && authSteps.length > 0) {
        List<AuthenticationStep> authStepList = new ArrayList<AuthenticationStep>();

        for (String authstep : authSteps) {
            AuthenticationStep authStep = new AuthenticationStep();
            authStep.setStepOrder(Integer.parseInt(authstep));

            boolean isSubjectStep = request.getParameter("subject_step_" + authstep) != null
                    && "on".equals(request.getParameter("subject_step_" + authstep)) ? true
                    : false;
            authStep.setSubjectStep(isSubjectStep);

            boolean isAttributeStep = request.getParameter("attribute_step_" + authstep) != null
                    && "on".equals(request.getParameter("attribute_step_" + authstep)) ? true
                    : false;
            authStep.setAttributeStep(isAttributeStep);

            String[] localAuthenticatorNames = request.getParameterValues("step_" + authstep
                    + "_local_auth");

            if (localAuthenticatorNames != null && localAuthenticatorNames.length > 0) {
                List<LocalAuthenticatorConfig> localAuthList = new ArrayList<LocalAuthenticatorConfig>();
                for (String name : localAuthenticatorNames) {
                    if (name != null) {
                        LocalAuthenticatorConfig localAuth = new LocalAuthenticatorConfig();
                        localAuth.setName(name);
                        if (localAuthenticatorConfigs != null) {
                            for (LocalAuthenticatorConfig config1 : localAuthenticatorConfigs) {
                                if (config1.getName().equals(name)) {
                                    localAuth.setDisplayName(config1.getDisplayName());
                                    break;
                                }
                            }
                        }
                        localAuthList.add(localAuth);
                    }
                }

                if (localAuthList != null && !localAuthList.isEmpty()) {
                    authStep.setLocalAuthenticatorConfigs(localAuthList
                            .toArray(new LocalAuthenticatorConfig[localAuthList.size()]));
                }

            }

            String[] federatedIdpNames = request.getParameterValues("step_" + authstep
                    + "_fed_auth");

            if (federatedIdpNames != null && federatedIdpNames.length > 0) {
                List<IdentityProvider> fedIdpList = new ArrayList<IdentityProvider>();
                for (String name : federatedIdpNames) {
                    if (StringUtils.isNotBlank(name)) {
                        IdentityProvider idp = new IdentityProvider();
                        idp.setIdentityProviderName(name);
                        IdentityProvider referringIdP = federatedIdentityProvidersMap.get(name);
                        String authenticatorName = request.getParameter("step_" + authstep + "_idp_" + name +
                                "_fed_authenticator");
                        if (StringUtils.isNotBlank(authenticatorName)) {
                            String authenticatorDisplayName = null;

                            for (FederatedAuthenticatorConfig config2 : referringIdP
                                    .getFederatedAuthenticatorConfigs()) {
                                if (authenticatorName.equals(config2.getName())) {
                                    authenticatorDisplayName = config2.getDisplayName();
                                    break;
                                }
                            }

                            FederatedAuthenticatorConfig authenticator = new FederatedAuthenticatorConfig();
                            authenticator.setName(authenticatorName);
                            authenticator.setDisplayName(authenticatorDisplayName);
                            idp.setDefaultAuthenticatorConfig(authenticator);
                            idp.setFederatedAuthenticatorConfigs(new FederatedAuthenticatorConfig[]{authenticator});
                            fedIdpList.add(idp);
                        }
                    }
                }

                if (fedIdpList != null && !fedIdpList.isEmpty()) {
                    authStep.setFederatedIdentityProviders(fedIdpList
                            .toArray(new IdentityProvider[fedIdpList.size()]));
                }
            }

            if ((authStep.getFederatedIdentityProviders() != null && authStep
                    .getFederatedIdentityProviders().length > 0)
                    || (authStep.getLocalAuthenticatorConfigs() != null && authStep
                    .getLocalAuthenticatorConfigs().length > 0)) {
                authStepList.add(authStep);
            }

        }

        if (CollectionUtils.isNotEmpty(authStepList)) {
            localAndOutboundAuthenticationConfig.setAuthenticationSteps(authStepList.toArray(new AuthenticationStep[authStepList.size()]));
        }
    }

    AuthenticationScriptConfig authenticationScriptConfig = new AuthenticationScriptConfig();
    String flawByScript = request.getParameter("scriptTextArea");

    if (StringUtils.isBlank(flawByScript)) {
        authenticationScriptConfig.setEnabled(false);
    } else {
        if ("true".equalsIgnoreCase(request.getParameter("enableScript"))) {
            authenticationScriptConfig.setEnabled(true);
        } else {
            authenticationScriptConfig.setEnabled(false);
        }
    }

    authenticationScriptConfig.setContent(flawByScript);
    localAndOutboundAuthenticationConfig.setAuthenticationScriptConfig(authenticationScriptConfig);

    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    IdpManagementServiceClient serviceClient = new IdpManagementServiceClient(cookie,
            backendServerURL, configContext);
    serviceClient.addDefaultAuthenticationSequence(localAndOutboundAuthenticationConfig);

    %>

