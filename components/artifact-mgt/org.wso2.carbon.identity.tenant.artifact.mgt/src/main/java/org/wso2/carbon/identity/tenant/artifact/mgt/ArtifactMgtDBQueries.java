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

package org.wso2.carbon.identity.tenant.artifact.mgt;

/**
 * This class contains default SQL queries for tenant resource artifact management.
 */
public class ArtifactMgtDBQueries {

    public static final String ADD_ARTIFACT_CATEGORY = "INSERT INTO IDN_RESOURCE_ARTIFACT_CATEGORY (TENANT_ID, " +
            "NAME, DESCRIPTION) VALUES (?, ?, ?)";
    public static final String UPDATE_ARTIFACT_CATEGORY = "UPDATE IDN_RESOURCE_ARTIFACT_CATEGORY SET NAME = ?," +
            "DESCRIPTION = ? WHERE ID = ? AND TENANT_ID = ?";
    public static final String GET_ARTIFACT_CATEGORY = "SELECT ID, NAME, DESCRIPTION FROM " +
            "IDN_RESOURCE_ARTIFACT_CATEGORY WHERE NAME = ? AND TENANT_ID = ?";
    public static final String GET_ARTIFACT_CATEGORY_ID = "SELECT ID FROM IDN_RESOURCE_ARTIFACT_CATEGORY " +
            "WHERE NAME = ? AND TENANT_ID = ?";

    public static final String ADD_ARTIFACT = "INSERT INTO IDN_RESOURCE_ARTIFACT (TENANT_ID, CATEGORY_ID, NAME, " +
            "DESCRIPTION, VALUE, CREATED, LAST_UPDATED) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String GET_ARTIFACT = "SELECT NAME, DESCRIPTION, VALUE, CREATED, LAST_UPDATED FROM " +
            "IDN_RESOURCE_ARTIFACT WHERE CATEGORY_ID = ? AND NAME = ? AND TENANT_ID = ?";
    public static final String GET_ALL_ARTIFACT_OF_CATEGORY = "SELECT NAME, DESCRIPTION, VALUE, CREATED, " +
            "LAST_UPDATED FROM IDN_RESOURCE_ARTIFACT WHERE CATEGORY_ID = ? AND TENANT_ID = ?";
    public static final String GET_ALL_ARTIFACT_INFO = "SELECT NAME, DESCRIPTION, CREATED, LAST_UPDATED FROM " +
            "IDN_RESOURCE_ARTIFACT WHERE TENANT_ID = ?";
    public static final String UPDATE_ARTIFACT = "UPDATE IDN_RESOURCE_ARTIFACT SET NAME = ?,DESCRIPTION = ?," +
            "VALUE = ?,LAST_UPDATED = ? WHERE CATEGORY_ID = ? AND NAME = ? AND TENANT_ID = ?";
    public static final String DELETE_ARTIFACT = "DELETE FROM IDN_RESOURCE_ARTIFACT WHERE CATEGORY_ID = ? AND " +
            "NAME = ? AND TENANT_ID= ?";
}
