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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This is to represent the tenant resource artifact category.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourceArtifactCategory")
public class ResourceArtifactCategory implements Serializable {

    @XmlTransient
    private int id;

    @XmlElement(name = "name", required = true)
    private String name;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "resourceArtifacts")
    private ResourceArtifact[] resourceArtifacts;

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
    public ResourceArtifact[] getResourceArtifacts() {

        return resourceArtifacts;
    }

    /**
     * Set list of resource artifacts belongs to the category.
     *
     * @param resourceArtifacts list of resource artifacts
     */
    public void setResourceArtifacts(ResourceArtifact[] resourceArtifacts) {

        this.resourceArtifacts = resourceArtifacts;
    }
}

