/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.identity.tenant.resource.mgt.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This is to represent the tenant resource content.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourceContent")
public class ResourceContent implements Serializable {

    @XmlTransient
    private int id;

    @XmlElement(name = "content", required = true)
    private Object content;

    @XmlElement(name = "contentType")
    private String contentType;

    /**
     * Get resource artifact ID.
     *
     * @return resource artifact ID
     */
    public int getId() {

        return id;
    }

    /**
     * Set resource artifact ID.
     *
     * @param id resource artifact ID
     */
    public void setId(int id) {

        this.id = id;
    }

    /**
     * Get resource content type.
     *
     * @return resource content type
     */
    public String getContentType() {

        return contentType;
    }

    /**
     * Set resource content type.
     *
     * @param contentType resource content type
     */
    public void setContentType(String contentType) {

        this.contentType = contentType;
    }

    /**
     * Get resource artifact value.
     *
     * @return resource artifact value
     */
    public Object getContent() {

        return content;
    }

    /**
     * Set resource artifact value.
     *
     * @param content resource artifact value
     */
    public void setContent(Object content) {

        this.content = content;
    }
}
