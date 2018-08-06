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

package org.wso2.carbon.identity.application.template.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationTemplateMgtUtil {

    private static final Log log = LogFactory.getLog(ApplicationTemplateMgtUtil.class);
    private static String globalSpTemplatePath;

    /**
     * Read the global SP template and loaded in to the database.
     */
    public static void setGlobalSPTemplateConfiguration() {

        Path configFile = globalSpTemplatePath != null ? Paths.get(globalSpTemplatePath) :
                Paths.get(IdentityUtil.getIdentityConfigDirPath(),
                        ApplicationTemplateMgtConstants.SYSTEM_DEFAULT_SP_TEMPLATE_CONFIG);

        try (InputStream inputStream = Files.newInputStream(configFile)) {
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            SpTemplateDTO spTemplateDTO = new SpTemplateDTO();
            spTemplateDTO.setName(ApplicationTemplateMgtConstants.SYSTEM_DEFAULT_SP_TEMPLATE_NAME);
            spTemplateDTO.setDescription(ApplicationTemplateMgtConstants.SYSTEM_DEFAULT_SP_TEMPLATE_DESC);
            spTemplateDTO.setSpContent(textBuilder.toString());
            String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
            ApplicationTemplateManagementService templateMgtService = ApplicationTemplateManagementServiceImpl
                    .getInstance();
            boolean isExists = templateMgtService.isExistingTemplate(ApplicationTemplateMgtConstants.SYSTEM_DEFAULT_SP_TEMPLATE_NAME,
                    tenantDomain);
            if (isExists) {
               // templateMgtService.(spTemplateDTO, tenantDomain);
            } else {
                templateMgtService.importApplicationTemplate(spTemplateDTO, tenantDomain);
            }
        } catch (FileNotFoundException e) {
            log.error(ApplicationTemplateMgtConstants.SYSTEM_DEFAULT_SP_TEMPLATE_CONFIG + " file is not available", e);
        } catch (IOException | IdentityApplicationTemplateMgtException e) {
            log.error("Error adding global SP template configurations.", e);
        }
    }

    public static void setGlobalSpTemplatePath(String globalSpTemplatePath) {
        ApplicationTemplateMgtUtil.globalSpTemplatePath = globalSpTemplatePath;
    }

    public static String getGlobalSpTemplatePath() {
        return globalSpTemplatePath;
    }
}
