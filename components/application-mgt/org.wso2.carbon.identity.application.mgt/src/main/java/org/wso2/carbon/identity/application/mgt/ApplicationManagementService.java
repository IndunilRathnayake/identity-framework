/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.application.mgt;

import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.ApplicationBasicInfo;
import org.wso2.carbon.identity.application.common.model.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.SpFileContent;
import org.wso2.carbon.identity.application.common.model.ImportResponse;
import org.wso2.carbon.identity.application.common.model.LocalAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.RequestPathAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.common.model.SpFileStream;
import org.wso2.carbon.identity.application.mgt.dto.SpTemplateDTO;

import java.util.List;
import java.util.Map;

/**
 * Application management service abstract class.
 */
public abstract class ApplicationManagementService {

    /**
     * Get ApplicationManagementService instance.
     *
     * @return ApplicationManagementService instance
     */
    public static ApplicationManagementService getInstance() {
        return ApplicationManagementServiceImpl.getInstance();
    }

    /**
     * Creates a service provider with basic information.First we need to create
     * a role with the
     * application name. Only the users in this role will be able to edit/update
     * the application.The
     * user will assigned to the created role.Internal roles used.
     * @param serviceProvider Service Provider Name
     * @param tenantDomain Tenant Domain
     * @param username User Name
     * @param spTemplateContent SP template XML content
     * @return
     * @throws IdentityApplicationManagementException
     * @deprecated  This method is replaced by {@link #addApplication}
     */
    @Deprecated
    public abstract void createApplication(ServiceProvider serviceProvider, String tenantDomain, String username,
                                           String spTemplateContent)
            throws IdentityApplicationManagementException;

    /**
     * Creates a service provider with basic information and returns the created service provider. First we need to
     * create an internal role with the application name. Only the users in this role will be able to edit/update
     * the application.Then the user will assigned to the created role.
     * @param serviceProvider Service Provider Name
     * @param tenantDomain Tenant Domain
     * @param username User Name
     * @param spTemplateContent SP template XML content
     * @return created service provider
     * @throws IdentityApplicationManagementException
     */
    public abstract ServiceProvider addApplication(ServiceProvider serviceProvider, String tenantDomain,
                                                   String username, String spTemplateContent)
            throws IdentityApplicationManagementException;

    /**
     * Get Application for given application name
     *
     * @param applicationName Application Name
     * @param tenantDomain Tenant Domain
     * @return ServiceProvider
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract ServiceProvider getApplicationExcludingFileBasedSPs(String applicationName, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get All Application Basic Information
     *
     * @param tenantDomain Tenant Domain
     * @param username User Name
     * @return ApplicationBasicInfo[]
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract ApplicationBasicInfo[] getAllApplicationBasicInfo(String tenantDomain, String username)
            throws IdentityApplicationManagementException;

    /**
     * Update Application
     *
     * @param tenantDomain Tenant Domain
     * @param serviceProvider Service Provider
     * @param username User Name
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract void updateApplication(ServiceProvider serviceProvider, String tenantDomain, String username)
            throws IdentityApplicationManagementException;

    /**
     * Delete Application
     *
     * @param tenantDomain Tenant Domain
     * @param applicationName Application name
     * @param username User Name
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract void deleteApplication(String applicationName, String tenantDomain, String username)
            throws IdentityApplicationManagementException;

    /**
     * Get Identity Provider
     *
     * @param tenantDomain Tenant Domain
     * @param federatedIdPName Federated identity provider name
     * @return IdentityProvider
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract IdentityProvider getIdentityProvider(String federatedIdPName, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get All Identity Providers
     *
     * @param tenantDomain Tenant Domain
     * @return IdentityProvider[]
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract IdentityProvider[] getAllIdentityProviders(String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get All Local Authenticators
     *
     * @param tenantDomain Tenant Domain
     * @return LocalAuthenticatorConfig[]
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract LocalAuthenticatorConfig[] getAllLocalAuthenticators(String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get All Request Path Authenticators
     *
     * @param tenantDomain Tenant Domain
     * @return RequestPathAuthenticatorConfig[]
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract RequestPathAuthenticatorConfig[] getAllRequestPathAuthenticators(String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get All local claim uris
     *
     * @param tenantDomain Tenant Domain
     * @return String[] All Local Claim Uris
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract String[] getAllLocalClaimUris(String tenantDomain) throws IdentityApplicationManagementException;

    public abstract String getServiceProviderNameByClientIdExcludingFileBasedSPs(String clientId, String type, String
            tenantDomain)
            throws IdentityApplicationManagementException;

    public abstract Map<String, String> getServiceProviderToLocalIdPClaimMapping(String serviceProviderName,
                                                                          String tenantDomain)
            throws IdentityApplicationManagementException;

    public abstract Map<String, String> getLocalIdPToServiceProviderClaimMapping(String serviceProviderName,
                                                                          String tenantDomain)
            throws IdentityApplicationManagementException;

    public abstract List<String> getAllRequestedClaimsByServiceProvider(String serviceProviderName,
                                                                 String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get application data for given client Id and type
     *
     * @param clientId Client Id
     * @param type     Type
     * @param tenantDomain Tenant Domain
     * @return ServiceProvider
     * @throws org.wso2.carbon.identity.application.common.IdentityApplicationManagementException
     */
    public abstract String getServiceProviderNameByClientId(String clientId, String type, String tenantDomain)
            throws IdentityApplicationManagementException;

    public abstract ServiceProvider getServiceProvider(String serviceProviderName, String tenantDomain)
            throws IdentityApplicationManagementException;

    public abstract ServiceProvider getServiceProvider(int appId) throws IdentityApplicationManagementException;

    public abstract ServiceProvider getServiceProviderByClientId(String clientId, String clientType,
                                                                 String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Export Service Provider application.
     *
     * @param applicationName name of the SP
     * @param exportSecrets   is export the secrets
     * @param tenantDomain    tenant Domain
     * @return xml string of the SP
     * @throws IdentityApplicationManagementException Identity Application Management Exception
     */
    public abstract String exportSPApplication(String applicationName, boolean exportSecrets, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Import Service Provider application from file.
     *
     * @param spFileContent xml string of the SP and file name
     * @param tenantDomain  tenant Domain
     * @param username      username
     * @param isUpdate      isUpdate
     * @return ImportResponse
     * @throws IdentityApplicationManagementException Identity Application Management Exception
     */
    public abstract ImportResponse importSPApplication(SpFileContent spFileContent, String tenantDomain, String
            username, boolean isUpdate) throws IdentityApplicationManagementException;

    /**
     * Import Service Provider application from object.
     *
     * @param serviceProvider
     * @param tenantDomain
     * @param username
     * @param isUpdate
     * @return ImportResponse
     * @throws IdentityApplicationManagementException
     */
    public abstract ImportResponse importSPApplication(ServiceProvider serviceProvider, String tenantDomain, String
            username, boolean isUpdate) throws IdentityApplicationManagementException;

    /**
     * Add configured service provider as a template.
     *
     * @param serviceProvider Service provider to be configured as a template
     * @param spTemplateDTO   service provider template basic info
     * @throws IdentityApplicationManagementException
     */
    public abstract void createServiceProviderAsTemplate(ServiceProvider serviceProvider, SpTemplateDTO spTemplateDTO,
                                                         String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Import Service provider template.
     *
     * @param spTemplateDTO service provider template info
     * @param tenantDomain  tenant domain
     * @throws IdentityApplicationManagementException
     */
    public abstract void importApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Load Service provider template.
     *
     * @param templateName template name
     * @param tenantDomain tenant domain
     * @return service provider template info
     * @throws IdentityApplicationManagementException
     */
    public abstract SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Check existence of a application template.
     *
     * @param templateName template name
     * @param tenantDomain tenant domain
     * @return true if a template with the specified name exists
     * @throws IdentityApplicationManagementException
     */
    public abstract boolean isExistingTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get all service provider templates.
     *
     * @param tenantDomain tenant domain
     * @return list of all application template info
     * @throws IdentityApplicationManagementException
     */
    public abstract List<SpTemplateDTO> getAllApplicationTemplates(String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Get all Service provider template names.
     *
     * @param tenantDomain tenant domain
     * @return list of application template names
     * @throws IdentityApplicationManagementException
     */
    public abstract List<String> getAllApplicationTemplateNames(String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Delete a application template.
     *
     * @param templateName name of the template
     * @param tenantDomain tenant domain
     * @throws IdentityApplicationManagementException
     */
    public abstract void deleteApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationManagementException;

    /**
     * Update an application template.
     *
     * @param spTemplateDTO SP template info to be updated
     * @param tenantDomain  tenant domain
     * @throws IdentityApplicationManagementException
     */
    public abstract void updateApplicationTemplate(String templateName, SpTemplateDTO spTemplateDTO,
                                                   String tenantDomain) throws IdentityApplicationManagementException;

    /**
     * Export a application template.
     *
     * @param templateName  name of the template
     * @param tenantDomain  tenant domain
     * @param exportSecrets is export the secrets
     * @return XML string of the template content
     * @throws IdentityApplicationManagementException
     */
    public abstract String exportApplicationTemplate(String templateName, boolean exportSecrets, String tenantDomain)
            throws IdentityApplicationManagementException;
}
