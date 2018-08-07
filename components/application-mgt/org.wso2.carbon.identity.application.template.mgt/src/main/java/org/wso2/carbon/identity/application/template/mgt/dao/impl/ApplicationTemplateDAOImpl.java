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

package org.wso2.carbon.identity.application.template.mgt.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries;
import org.wso2.carbon.identity.application.template.mgt.IdentityApplicationTemplateMgtException;
import org.wso2.carbon.identity.application.template.mgt.dao.ApplicationTemplateDAO;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.utils.DBUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to access the data storage to retrieve, store, delete and update service provider templates.
 */
public class ApplicationTemplateDAOImpl implements ApplicationTemplateDAO {

    private static final Log log = LogFactory.getLog(ApplicationTemplateDAOImpl.class);

    public ApplicationTemplateDAOImpl() {
    }

    @Override
    public void createApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating application template: %s in tenant %s", spTemplateDTO.getName(),
                    tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);
        String templateName = spTemplateDTO.getName();
        String templateDescription = spTemplateDTO.getDescription();

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement storeAppTemplatePrepStmt = null;
        ResultSet results = null;

        try {
            String dbProductName = connection.getMetaData().getDatabaseProductName();
            storeAppTemplatePrepStmt = connection.prepareStatement(
                    ApplicationTemplateMgtDBQueries.ADD_SP_TEMPLATE, new String[]{
                            DBUtils.getConvertedAutoGeneratedColumnName(dbProductName, "ID")});

            storeAppTemplatePrepStmt.setInt(1, tenantID);
            storeAppTemplatePrepStmt.setString(2, templateName);
            storeAppTemplatePrepStmt.setString(3, templateDescription);
            storeAppTemplatePrepStmt.setCharacterStream(4, new StringReader(spTemplateDTO.getSpContent()));
            storeAppTemplatePrepStmt.execute();

            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            if (log.isDebugEnabled()) {
                log.debug("Application Template Stored successfully with name " + templateName);
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sql) {
                throw new IdentityApplicationTemplateMgtException(
                        "Error while Creating Application Template", sql);
            }
            throw new IdentityApplicationTemplateMgtException("Error while Creating Application Template", e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, results, storeAppTemplatePrepStmt);
        }
    }

    @Override
    public SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Loading application template: %s of tenant %s", templateName, tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement loadAppTemplatePrepStmt = null;
        SpTemplateDTO spTemplateDTO = null;
        ResultSet results = null;

        try {
            loadAppTemplatePrepStmt = connection.prepareStatement(
                    ApplicationTemplateMgtDBQueries.LOAD_SP_TEMPLATE_CONTENT);

            loadAppTemplatePrepStmt.setString(1, templateName);
            loadAppTemplatePrepStmt.setInt(2, tenantID);

            try (ResultSet applicationTemplateResultSet = loadAppTemplatePrepStmt
                    .executeQuery()) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
                if (applicationTemplateResultSet.next()) {
                    spTemplateDTO = new SpTemplateDTO();

                    try {
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br = new BufferedReader(
                                applicationTemplateResultSet.getCharacterStream(1));
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\r\n");
                        }
                        String spContent = sb.toString();
                        spTemplateDTO.setName(templateName);
                        spTemplateDTO.setSpContent(spContent);
                    } catch (IOException e) {
                        throw new IdentityApplicationTemplateMgtException(
                                "Could not read the template information for : " + templateName, e);
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Application Template Loaded successfully with name " + templateName);
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sql) {
                throw new IdentityApplicationTemplateMgtException(
                        "Error while Loading Application Template", sql);
            }
            throw new IdentityApplicationTemplateMgtException("Error while Loading Application Template", e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, results, loadAppTemplatePrepStmt);
        }
        return spTemplateDTO;
    }

    @Override
    public boolean isExistingTemplate(String templateName, String tenantDomain) throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Checking application template exists for name: %s in tenant: %s", templateName,
                    tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement isAppTemplateExistsPrepStmt = null;
        boolean isTemplateExists = false;
        ResultSet results = null;

        try {
            isAppTemplateExistsPrepStmt = connection.prepareStatement(
                    ApplicationTemplateMgtDBQueries.IS_SP_TEMPLATE_EXISTS);

            isAppTemplateExistsPrepStmt.setString(1, templateName);
            isAppTemplateExistsPrepStmt.setInt(2, tenantID);

            try (ResultSet isTemplateExistsResultSet = isAppTemplateExistsPrepStmt
                    .executeQuery()) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
                if (isTemplateExistsResultSet.next()) {
                    isTemplateExists = true;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Application Template Loaded successfully with name " + templateName);
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sql) {
                throw new IdentityApplicationTemplateMgtException(
                        "Error while checking existence of Application Template", sql);
            }
            throw new IdentityApplicationTemplateMgtException("Error while checking existence of Application Template",
                    e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, results, isAppTemplateExistsPrepStmt);
        }
        return isTemplateExists;
    }

    @Override
    public List<SpTemplateDTO> getAllApplicationTemplates(String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Getting all the application templates of tenant: %s", tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement getAllAppTemplatePrepStmt = null;
        List<SpTemplateDTO> spTemplateDTOList = new ArrayList<>();
        ResultSet results = null;

        try {
            getAllAppTemplatePrepStmt = connection.prepareStatement(
                    ApplicationTemplateMgtDBQueries.GET_ALL_SP_TEMPLATES);

            getAllAppTemplatePrepStmt.setInt(1, tenantID);

            try (ResultSet getAllAppTemplateResultSet = getAllAppTemplatePrepStmt
                    .executeQuery()) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }

                while (getAllAppTemplateResultSet.next()) {
                    SpTemplateDTO spTemplateDTO = new SpTemplateDTO();
                    spTemplateDTO.setName(getAllAppTemplateResultSet.getString(1));
                    spTemplateDTO.setDescription(getAllAppTemplateResultSet.getString(2));
                    spTemplateDTO.setSpContent(getAllAppTemplateResultSet.getString(3));
                    spTemplateDTOList.add(spTemplateDTO);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("All Application Templates retrieved successfully for tenant domain " + tenantDomain);
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sql) {
                throw new IdentityApplicationTemplateMgtException("Error while Loading Application Templates", sql);
            }
            throw new IdentityApplicationTemplateMgtException("Error while Loading Application Templates", e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, results, getAllAppTemplatePrepStmt);
        }
        return spTemplateDTOList;
    }

    @Override
    public List<String> getAllApplicationTemplateNames(String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Getting all the application template names of tenant: %s", tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement getAllAppTemplateNamesPrepStmt = null;
        List<String> spTemplateNames = new ArrayList<>();
        ResultSet results = null;

        try {
            getAllAppTemplateNamesPrepStmt = connection.prepareStatement(
                    ApplicationTemplateMgtDBQueries.GET_ALL_SP_TEMPLATE_NAMES);

            getAllAppTemplateNamesPrepStmt.setInt(1, tenantID);

            try (ResultSet getAllAppTemplateNamesResultSet = getAllAppTemplateNamesPrepStmt
                    .executeQuery()) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }

                while (getAllAppTemplateNamesResultSet.next()) {
                    spTemplateNames.add(getAllAppTemplateNamesResultSet.getString(1));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("All Application Template names retrieved successfully for tenant domain " + tenantDomain);
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sql) {
                throw new IdentityApplicationTemplateMgtException("Error while Loading Application Template names",
                        sql);
            }
            throw new IdentityApplicationTemplateMgtException("Error while Loading Application Template names", e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, results, getAllAppTemplateNamesPrepStmt);
        }
        return spTemplateNames;
    }

    @Override
    public void deleteApplicationTemplate(String templateName, String tenantDomain) throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Deleting Application Template: %s", templateName));
        }

        int tenantID = getTenantID(tenantDomain);

        Connection connection = IdentityDatabaseUtil.getDBConnection();

        // Now, delete the application
        PreparedStatement deleteTemplatePrepStmt = null;
        try {
            deleteTemplatePrepStmt = connection
                    .prepareStatement(ApplicationTemplateMgtDBQueries.DELETE_SP_TEMPLATE_BY_NAME);
            deleteTemplatePrepStmt.setString(1, templateName);
            deleteTemplatePrepStmt.setInt(2, tenantID);
            deleteTemplatePrepStmt.execute();

            if (!connection.getAutoCommit()) {
                connection.commit();
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignore) {
                }
            }
            String errorMessege = "An error occurred while delete the application template : " + templateName;
            log.error(errorMessege, e);
            throw new IdentityApplicationTemplateMgtException(errorMessege, e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, null, deleteTemplatePrepStmt);
        }
    }

    @Override
    public void updateApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Updating Application Template: %s", spTemplateDTO.getName()));
        }

        int tenantID = getTenantID(tenantDomain);

        Connection connection = IdentityDatabaseUtil.getDBConnection();

        // Now, delete the application
        PreparedStatement updateTemplatePrepStmt = null;
        try {
            updateTemplatePrepStmt = connection
                    .prepareStatement(ApplicationTemplateMgtDBQueries.UPDATE_SP_TEMPLATE_BY_NAME);
            updateTemplatePrepStmt.setCharacterStream(1, new StringReader(spTemplateDTO.getSpContent()));
            updateTemplatePrepStmt.setString(2, spTemplateDTO.getName());
            updateTemplatePrepStmt.setInt(3, tenantID);
            updateTemplatePrepStmt.execute();

            if (!connection.getAutoCommit()) {
                connection.commit();
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignore) {
                }
            }
            String errorMessege = "An error occurred while update the application template : " + spTemplateDTO.getName();
            log.error(errorMessege, e);
            throw new IdentityApplicationTemplateMgtException(errorMessege, e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, null, updateTemplatePrepStmt);
        }
    }

    private int getTenantID(String tenantDomain) {

        // get logged-in users tenant identifier.
        int tenantID = MultitenantConstants.INVALID_TENANT_ID;

        if (tenantDomain != null) {
            tenantID = IdentityTenantUtil.getTenantId(tenantDomain);
        }
        return tenantID;
    }
}
