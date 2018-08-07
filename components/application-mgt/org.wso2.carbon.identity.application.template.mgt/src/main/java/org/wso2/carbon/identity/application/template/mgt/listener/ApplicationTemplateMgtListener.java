/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.identity.application.template.mgt.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtUtil;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;
import org.wso2.carbon.stratos.common.exception.StratosException;
import org.wso2.carbon.stratos.common.listeners.TenantMgtListener;

public class ApplicationTemplateMgtListener implements TenantMgtListener {
    private static final int EXEC_ORDER = 50;
    private static final Log log = LogFactory.getLog(ApplicationTemplateMgtListener.class);

    /**
     * Add the global SP template when a new tenant is registered.
     *
     * @param tenantInfo Information about the newly created tenant
     */
    @Override
    public void onTenantCreate(TenantInfoBean tenantInfo) throws StratosException {
        try {
            PrivilegedCarbonContext.getThreadLocalCarbonContext().startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantInfo.getTenantDomain());
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantInfo.getTenantId());
            ApplicationTemplateMgtUtil.setGlobalSPTemplateConfiguration();
        } finally {
            PrivilegedCarbonContext.getThreadLocalCarbonContext().endTenantFlow();
        }
    }

    @Override
    public void onTenantUpdate(TenantInfoBean tenantInfoBean) throws StratosException {

    }

    @Override
    public void onTenantDelete(int i) {

    }

    @Override
    public void onTenantRename(int i, String s, String s1) throws StratosException {

    }

    @Override
    public void onTenantInitialActivation(int i) throws StratosException {

    }

    @Override
    public void onTenantActivation(int i) throws StratosException {

    }

    @Override
    public void onTenantDeactivation(int i) throws StratosException {

    }

    @Override
    public void onSubscriptionPlanChange(int i, String s, String s1) throws StratosException {

    }

    @Override
    public int getListenerOrder() {
        return EXEC_ORDER;
    }

    @Override
    public void onPreDelete(int i) throws StratosException {

    }
}
