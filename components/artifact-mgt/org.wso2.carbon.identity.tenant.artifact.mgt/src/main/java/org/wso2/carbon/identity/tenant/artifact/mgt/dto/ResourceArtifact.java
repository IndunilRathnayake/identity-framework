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

package org.wso2.carbon.identity.tenant.artifact.mgt.dto;

import java.io.Serializable;
import java.util.Properties;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This is to represent the tenant resource artifact object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourceArtifact")
public class ResourceArtifact implements Serializable {

    @XmlTransient
    private int id;

    @XmlElement(name = "name", required = true)
    private String name;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "value", required = true)
    private String value;

    @XmlElement(name = "properties")
    private Properties properties;

    @XmlElement(name = "createdTime")
    private long createdTime;

    @XmlElement(name = "lastUpdatedTime")
    private long lastUpdatedTime;

    public ResourceArtifact() {}

    public ResourceArtifact(String name, String description, String value) {

        this.name = name;
        this.description = description;
        this.value = value;
    }

    public ResourceArtifact(String name, String description, String value, long createdTime, long lastUpdatedTime) {

        this.name = name;
        this.description = description;
        this.value = value;
        this.createdTime = createdTime;
        this.lastUpdatedTime = lastUpdatedTime;
    }

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
     * Get resource artifact name.
     *
     * @return resource artifact name
     */
    public String getName() {

        return name;
    }

    /**
     * Set resource artifact name.
     *
     * @param name resource artifact name
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * Get resource artifact description.
     *
     * @return resource artifact description
     */
    public String getDescription() {

        return description;
    }

    /**
     * Set resource artifact description.
     *
     * @param description resource artifact description
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Get resource artifact value.
     *
     * @return resource artifact value
     */
    public String getValue() {

        return value;
    }

    /**
     * Set resource artifact value.
     *
     * @param value resource artifact value
     */
    public void setValue(String value) {

        this.value = value;
    }

    /**
     * Get created time of the resource artifact.
     *
     * @return created time of the resource artifact
     */
    public long getCreatedTime() {

        return createdTime;
    }

    /**
     * Set created time of the resource artifact.
     *
     * @param createdTime created time of the resource artifact
     */
    public void setCreatedTime(long createdTime) {

        this.createdTime = createdTime;
    }

    /**
     * Get last updated time of the resource artifact.
     *
     * @return last updated time of the resource artifact
     */
    public long getLastUpdatedTime() {

        return lastUpdatedTime;
    }

    /**
     * Set last updated time of the resource artifact.
     *
     * @param lastUpdatedTime last updated time of the resource artifact
     */
    public void setLastUpdatedTime(long lastUpdatedTime) {

        this.lastUpdatedTime = lastUpdatedTime;
    }

    /**
     * Get resource artifact properties.
     *
     * @return properties resource artifact properties
     */
    public Properties getProperties() {

        return properties;
    }

    /**
     * Set resource artifact properties.
     *
     * @param properties resource artifact properties
     */
    public void setProperties(Properties properties) {

        this.properties = properties;
    }
}
