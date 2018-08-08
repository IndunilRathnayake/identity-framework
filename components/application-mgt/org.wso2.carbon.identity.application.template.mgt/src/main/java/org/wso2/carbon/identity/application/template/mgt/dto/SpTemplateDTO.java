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

package org.wso2.carbon.identity.application.template.mgt.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Data transfer representation for service provider template.
 */
public class SpTemplateDTO implements Serializable {

    private String spContent;
    private String name;
    private String description;
    /*private List<PlaceholderDTO> placeholders;*/

    public SpTemplateDTO(String spContent, String name, String description) {
        this.spContent = spContent;
        this.name = name;
        this.description = description;
    }

    /**
     * Get Service Provider content.
     *
     * @return SP content
     */
    public String getSpContent() {
        return spContent;
    }

    /**
     * Set Service Provider content.
     *
     * @param spContent SP content
     */
    public void setSpContent(String spContent) {
        this.spContent = spContent;
    }

    /**
     * Get Service Provider template name.
     *
     * @return SP template name
     */
    public String getName() {
        return name;
    }

    /**
     * Set Service Provider template name.
     *
     * @param name SP template name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Service Provider template description.
     *
     * @return SP template description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set Service Provider template description.
     *
     * @param description SP template description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
