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

package org.wso2.carbon.identity.tenant.resource.mgt;

/**
 * This class contains default SQL queries for tenant resource management.
 */
public class ResourceMgtDBQueries {

    public static final String ADD_RESOURCE = "INSERT INTO IDN_RESOURCE (TENANT_ID, NAME, DESCRIPTION, " +
            "CREATED, LAST_UPDATED) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_RESOURCE = "UPDATE IDN_RESOURCE SET NAME = ?, DESCRIPTION = ?, LAST_UPDATED = ? " +
            "WHERE ID = ? AND TENANT_ID = ?";
    public static final String DELETE_RESOURCE = "DELETE FROM IDN_RESOURCE WHERE NAME = ? AND TENANT_ID = ?";
    public static final String GET_RESOURCE = "SELECT ID, NAME, DESCRIPTION, CREATED, LAST_UPDATED FROM " +
            "IDN_RESOURCE WHERE NAME = ? AND TENANT_ID = ?";
    public static final String GET_RESOURCE_ID = "SELECT ID FROM IDN_RESOURCE WHERE NAME = ? AND TENANT_ID = ?";
    public static final String GET_ALL_RESOURCE_INFO = "SELECT NAME, DESCRIPTION, CREATED, LAST_UPDATED FROM " +
            "IDN_RESOURCE WHERE TENANT_ID = ?";

    public static final String ADD_RESOURCE_CONTENT = "INSERT INTO IDN_RESOURCE_CONTENT (RESOURCE_ID, CONTENT, " +
            "CONTENT_TYPE) VALUES (?, ?, ?)";
    public static final String GET_CONTENT_OF_RESOURCE = "SELECT CONTENT, CONTENT_TYPE FROM IDN_RESOURCE_CONTENT "
            + "WHERE RESOURCE_ID = ?";
    public static final String GET_CONTENT_ID = "SELECT ID FROM IDN_RESOURCE_CONTENT WHERE RESOURCE_ID = ?";
    public static final String UPDATE_CONTENT_OF_RESOURCE = "UPDATE IDN_RESOURCE_CONTENT SET CONTENT = ?, " +
            "CONTENT_TYPE = ? WHERE RESOURCE_ID = ?";
    public static final String DELETE_CONTENT_OF_RESOURCE = "DELETE FROM IDN_RESOURCE_CONTENT WHERE RESOURCE_ID = ?";
}
