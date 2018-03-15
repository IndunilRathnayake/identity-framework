/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.application.authentication.framework.internal;

import org.osgi.framework.BundleContext;
import org.wso2.carbon.consent.mgt.core.ConsentManager;
import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticationDataPublisher;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticationMethodNameTranslator;
import org.wso2.carbon.identity.application.authentication.framework.config.loader.SequenceLoader;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilderFactory;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.ClaimFilter;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl.DefaultClaimFilter;
import org.wso2.carbon.identity.application.authentication.framework.handler.request.PostAuthenticationHandler;
import org.wso2.carbon.identity.application.authentication.framework.handler.request.impl.consent.SSOConsentService;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityRequestFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponseFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityProcessor;
import org.wso2.carbon.identity.application.authentication.framework.services.PostAuthenticationMgtService;
import org.wso2.carbon.identity.claim.metadata.mgt.ClaimMetadataManagementService;
import org.wso2.carbon.identity.core.handler.HandlerComparator;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.ArrayList;
import java.util.List;

public class FrameworkServiceDataHolder {

    private static FrameworkServiceDataHolder instance = new FrameworkServiceDataHolder();

    private BundleContext bundleContext = null;
    private RealmService realmService = null;
    private RegistryService registryService = null;
    private List<ApplicationAuthenticator> authenticators = new ArrayList<>();
    private long nanoTimeReference = 0;
    private long unixTimeReference = 0;
    private List<IdentityProcessor> identityProcessors = new ArrayList<IdentityProcessor>();
    private List<HttpIdentityRequestFactory> httpIdentityRequestFactories = new ArrayList<HttpIdentityRequestFactory>();
    private List<HttpIdentityResponseFactory> httpIdentityResponseFactories = new ArrayList<>();
    private AuthenticationDataPublisher authnDataPublisherProxy = null;
    private SequenceLoader sequenceLoader = null;
    private JsGraphBuilderFactory JsGraphBuilderFactory;
    private AuthenticationMethodNameTranslator authenticationMethodNameTranslator;
    private List<PostAuthenticationHandler> postAuthenticationHandlers = new ArrayList<>();
    private PostAuthenticationMgtService postAuthenticationMgtService = null;
    private ConsentManager consentManager = null;
    private ClaimMetadataManagementService claimMetadataManagementService = null;
    private List<ClaimFilter> claimFilters = new ArrayList<>();
    private SSOConsentService ssoConsentService;

    private FrameworkServiceDataHolder() {
        setNanoTimeReference(System.nanoTime());
        setUnixTimeReference(System.currentTimeMillis());
    }

    public static FrameworkServiceDataHolder getInstance() {
        return instance;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    /**
     *
     * @return
     * @throws FrameworkException
     * @Deprecated The usage of bundle context outside of the component should never be needed. Component should
     * provide necessary wiring for any place which require the BundleContext.
     */
    @Deprecated
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public List<ApplicationAuthenticator> getAuthenticators() {
        return authenticators;
    }

    public long getNanoTimeReference() {
        return nanoTimeReference;
    }

    private void setNanoTimeReference(long nanoTimeReference) {
        this.nanoTimeReference = nanoTimeReference;
    }

    public long getUnixTimeReference() {
        return unixTimeReference;
    }

    private void setUnixTimeReference(long unixTimeReference) {
        this.unixTimeReference = unixTimeReference;
    }

    public List<HttpIdentityRequestFactory> getHttpIdentityRequestFactories() {
        return httpIdentityRequestFactories;
    }

    public List<IdentityProcessor> getIdentityProcessors() {
        return identityProcessors;
    }

    public List<HttpIdentityResponseFactory> getHttpIdentityResponseFactories() {
        return httpIdentityResponseFactories;
    }

    public AuthenticationDataPublisher getAuthnDataPublisherProxy() {
        return authnDataPublisherProxy;
    }

    public void setAuthnDataPublisherProxy(AuthenticationDataPublisher authnDataPublisherProxy) {
        this.authnDataPublisherProxy = authnDataPublisherProxy;
    }

    public SequenceLoader getSequenceLoader() {
        return sequenceLoader;
    }

    public void setSequenceLoader(SequenceLoader sequenceLoader) {
        this.sequenceLoader = sequenceLoader;
    }

    public AuthenticationMethodNameTranslator getAuthenticationMethodNameTranslator() {
        return authenticationMethodNameTranslator;
    }

    public void setAuthenticationMethodNameTranslator(
            AuthenticationMethodNameTranslator authenticationMethodNameTranslator) {
        this.authenticationMethodNameTranslator = authenticationMethodNameTranslator;
    }

    public org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilderFactory getJsGraphBuilderFactory() {
        return JsGraphBuilderFactory;
    }

    public void setJsGraphBuilderFactory(
            org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilderFactory jsGraphBuilderFactory) {
        JsGraphBuilderFactory = jsGraphBuilderFactory;
    }

    /**
     * Adds a post authentication handler.
     * @param postAuthenticationHandler Post authentication handler implementation.
     */
    public void addPostAuthenticationHandler(PostAuthenticationHandler postAuthenticationHandler) {

        synchronized (postAuthenticationHandlers) {
            this.postAuthenticationHandlers.add(postAuthenticationHandler);
            postAuthenticationHandlers.sort(new HandlerComparator());
        }
    }

    /**
     * Get set of post authentication handlers registered via OSGI services.
     * @return List of Post Authentication handlers.
     */
    public List<PostAuthenticationHandler> getPostAuthenticationHandlers() {

        return this.postAuthenticationHandlers;
    }

    /**
     * Set post authentication management service.
     * @param postAuthenticationMgtService Post authentication management service.
     */
    public void setPostAuthenticationMgtService(PostAuthenticationMgtService postAuthenticationMgtService) {

        this.postAuthenticationMgtService = postAuthenticationMgtService;
    }

    /**
     * Get post authentication management service.
     * @return Post authentication management service.
     */
    public PostAuthenticationMgtService getPostAuthenticationMgtService() {

        return this.postAuthenticationMgtService;
    }

    /**
     * Get {@link ConsentManager} service.
     * @return Consent manager service
     */
    public ConsentManager getConsentManager() {
        return consentManager;
    }

    /**
     * Set {@link ConsentManager} service.
     * @param consentManager Instance of {@link ConsentManager} service.
     */
    public void setConsentManager(ConsentManager consentManager) {
        this.consentManager = consentManager;
    }

    /**
     * Get {@link ClaimMetadataManagementService}.
     * @return ClaimMetadataManagementService.
     */
    public ClaimMetadataManagementService getClaimMetadataManagementService() {

        return claimMetadataManagementService;
    }

    /**
     * Set {@link ClaimMetadataManagementService}.
     * @param claimMetadataManagementService Instance of {@link ClaimMetadataManagementService}.
     */
    public void setClaimMetadataManagementService (ClaimMetadataManagementService claimMetadataManagementService) {

        this.claimMetadataManagementService = claimMetadataManagementService;
    }

    /**
     * Get {@link SSOConsentService}.
     * @return SSOConsentService.
     */
    public SSOConsentService getSSOConsentService() {
        return ssoConsentService;
    }

    /**
     * Set {@link SSOConsentService}.
     * @param ssoConsentService Instance of {@link SSOConsentService}.
     */
    public void setSSOConsentService(SSOConsentService ssoConsentService) {
        this.ssoConsentService = ssoConsentService;
    }


    /**
     *
     * @return The Claim Filter with the highest priority.
     */
    public ClaimFilter getHighestPriorityClaimFilter() {
        if (claimFilters.isEmpty()) {
            throw new RuntimeException("No Claim Filters available.");
        }
        return claimFilters.get(0);
    }

    public List<ClaimFilter> getClaimFilters() {
        return claimFilters;
    }

    public void setClaimFilters(List<ClaimFilter> claimFilters) {
        this.claimFilters = claimFilters;
    }
}
