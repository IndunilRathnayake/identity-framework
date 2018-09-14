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
 * Constants for Artifact Management Service.
 */
public class ArtifactMgtConstants {

    private ArtifactMgtConstants() {

    }

    public static final String MY_SQL = "MySQL";
    public static final String POSTGRES_SQL = "PostgreSQL";
    public static final String DB2 = "DB2";
    public static final String MICROSOFT = "Microsoft";
    public static final String S_MICROSOFT = "microsoft";
    public static final String INFORMIX = "Informix";
    public static final String H2 = "H2";

    public static final String DEFAULT_AUTH_SEQ_COLLECTION_NAME = "default_auth_seq";
    public static final String DEFAULT_AUTH_SEQ_COLLECTION_DESC = "This is the collection of all default " +
            "authentication sequences registered for a tenant.";
}
