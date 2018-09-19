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
 * This is to represent the tenant resource.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Resource")
public class Resource implements Serializable {

    @XmlTransient
    private int id;

    @XmlElement(name = "name", required = true)
    private String name;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "resourceContent")
    private ResourceContent resourceContent;

    @XmlElement(name = "createdTime")
    private long createdTime;

    @XmlElement(name = "lastUpdatedTime")
    private long lastUpdatedTime;

    /**
     * Get resource artifact category ID.
     *
     * @return resource artifact category ID
     */
    public int getId() {

        return id;
    }

    /**
     * Set resource artifact category ID.
     *
     * @param id resource artifact category ID
     */
    public void setId(int id) {

        this.id = id;
    }

    /**
     * Get resource artifact category name.
     *
     * @return resource artifact category name
     */
    public String getName() {

        return name;
    }

    /**
     * Set resource artifact category name.
     *
     * @param name resource artifact category name
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * Get resource artifact category description.
     *
     * @return resource artifact category description
     */
    public String getDescription() {

        return description;
    }

    /**
     * Set resource artifact category description.
     *
     * @param description resource artifact category description
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Get list of resource artifacts belongs to the category.
     *
     * @return list of resource artifacts
     */
    public ResourceContent getResourceContent() {

        return resourceContent;
    }

    /**
     * Set list of resource artifacts belongs to the category.
     *
     * @param resourceContent resource artifacts
     */
    public void setResourceContent(ResourceContent resourceContent) {

        this.resourceContent = resourceContent;
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
}

