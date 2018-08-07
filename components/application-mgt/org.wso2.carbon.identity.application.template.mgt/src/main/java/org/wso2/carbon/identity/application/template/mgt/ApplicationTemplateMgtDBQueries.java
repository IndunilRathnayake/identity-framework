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

/**
 * This class contains default SQL queries.
 */
public class ApplicationTemplateMgtDBQueries {

    public static final String ADD_SP_TEMPLATE = "INSERT INTO SP_TEMPLATE (TENANT_ID, NAME, DESCRIPTION, " +
            "CONTENT) VALUES (?, ?, ?, ?)";

    public static final String LOAD_SP_TEMPLATE_CONTENT = "SELECT CONTENT FROM SP_TEMPLATE WHERE NAME = ? " +
            "AND TENANT_ID = ?";

    public static final String IS_SP_TEMPLATE_EXISTS = "SELECT ID FROM SP_TEMPLATE WHERE NAME = ? " +
            "AND TENANT_ID = ?";

    public static final String GET_ALL_SP_TEMPLATES = "SELECT NAME, DESCRIPTION,CONTENT FROM SP_TEMPLATE WHERE " +
            "TENANT_ID = ?";

    public static final String GET_ALL_SP_TEMPLATE_NAMES = "SELECT NAME FROM SP_TEMPLATE WHERE " +
            "TENANT_ID = ?";

    public static final String DELETE_SP_TEMPLATE_BY_NAME = "DELETE FROM SP_TEMPLATE WHERE NAME = ? AND TENANT_ID= ?";

    public static final String UPDATE_SP_TEMPLATE_BY_NAME = "UPDATE SP_TEMPLATE SET CONTENT= ? WHERE NAME = ? " +
            "AND TENANT_ID = ?";
}