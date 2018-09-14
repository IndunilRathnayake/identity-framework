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

package org.wso2.carbon.identity.tenant.artifact.mgt.dao.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.database.utils.jdbc.exceptions.TransactionException;
import org.wso2.carbon.identity.tenant.artifact.mgt.IdentityArtifactManagementException;
import org.wso2.carbon.identity.tenant.artifact.mgt.dao.ArtifactMgtDAO;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifact;
import org.wso2.carbon.identity.tenant.artifact.mgt.dto.ResourceArtifactCategory;
import org.wso2.carbon.identity.tenant.artifact.mgt.util.JdbcUtils;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.ADD_ARTIFACT;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.ADD_ARTIFACT_CATEGORY;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.DELETE_ARTIFACT;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.GET_ALL_ARTIFACT_INFO;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.GET_ALL_ARTIFACT_OF_CATEGORY;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.GET_ARTIFACT;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.GET_ARTIFACT_CATEGORY;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.GET_ARTIFACT_CATEGORY_ID;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.UPDATE_ARTIFACT;
import static org.wso2.carbon.identity.tenant.artifact.mgt.ArtifactMgtDBQueries.UPDATE_ARTIFACT_CATEGORY;

/**
 * This class access the IDN_RESOURCE_ARTIFACT and IDN_RESOURCE_ARTIFACT_CATEGORY tables to manage tenant resource
 * artifacts and tenant resource artifact categories respectively.
 */
public class ArtifactMgtDAOImpl implements ArtifactMgtDAO {

    private static final String UTC = "UTC";

    public ArtifactMgtDAOImpl() {
    }

    @Override
    public void createArtifactCategory(ResourceArtifactCategory artifactCategory, String tenantDomain)
            throws IdentityArtifactManagementException {

        try {
            JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            doCreateCategory(artifactCategory, tenantDomain, jdbcTemplate);
            doCreateArtifactsOfCategory(artifactCategory, tenantDomain, timestamp, jdbcTemplate);
        } catch (DataAccessException | TransactionException e) {
            throw new IdentityArtifactManagementException(String.format("Error while creating resource artifact " +
                    "category: %s in tenant: %s", artifactCategory.getName(), tenantDomain), e);
        }
    }

    @Override
    public ResourceArtifactCategory getArtifactCategory(String artifactCategoryName, String tenantDomain)
            throws IdentityArtifactManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        ResourceArtifactCategory artifactCategory;
        try {
            artifactCategory = doGetCategory(artifactCategoryName, tenantDomain, jdbcTemplate);
            if (artifactCategory != null) {
                List<ResourceArtifact> resourceArtifactList = doGetArtifactsOfCategory(tenantDomain, jdbcTemplate,
                    artifactCategory);
                artifactCategory.setResourceArtifacts(resourceArtifactList.toArray(new ResourceArtifact[0]));
            }
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(
                    String.format("Could not retrieve the resource artifact category: %s in tenant: %s",
                            artifactCategoryName, tenantDomain), e);
        }
        return artifactCategory;
    }

    @Override
    public boolean isExistingArtifactCategory(String artifactCategoryName, String tenantDomain)
            throws IdentityArtifactManagementException {

        try {
            int categoryID = doGetCategoryID(artifactCategoryName, tenantDomain, null);
            if (categoryID != 0) {
                return true;
            }
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(
                    String.format("Could not check existence of resource artifact category: %s in tenant: %s",
                            artifactCategoryName, tenantDomain), e);
        }
        return false;
    }

    @Override
    public void updateArtifactCategory(String artifactCategoryName, ResourceArtifactCategory artifactCategory,
                                         String tenantDomain) throws IdentityArtifactManagementException {

        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
            int categoryID = doGetCategoryID(artifactCategoryName, tenantDomain, jdbcTemplate);
            if (categoryID == 0) {
                throw new IdentityArtifactManagementException(String.format("The resource artifact category: %s " +
                        "is not available in tenant: %s", artifactCategoryName, tenantDomain));
            }
            artifactCategory.setId(categoryID);

            if (categoryID == 0) {
                throw new IdentityArtifactManagementException(String.format("The resource artifact category: %s " +
                        "is not available in tenant: %s", artifactCategoryName, tenantDomain));
            }

            doUpdateCategory(artifactCategory, tenantDomain, jdbcTemplate);
            doCreateArtifactsOfCategory(artifactCategory, tenantDomain, timestamp, jdbcTemplate);
        } catch (DataAccessException | TransactionException e) {
            throw new IdentityArtifactManagementException(String.format("An error occurred while updating the" +
                    " resource artifact category: %s in tenant: %s", artifactCategoryName, tenantDomain), e);
        }
    }

    @Override
    public void createArtifact(String artifactCategoryName, ResourceArtifact artifact, String tenantDomain)
            throws IdentityArtifactManagementException {

        try {
            JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
            int categoryID = doGetCategoryID(artifactCategoryName, tenantDomain, jdbcTemplate);
            if (categoryID == 0) {
                throw new IdentityArtifactManagementException(String.format("The resource artifact category: %s " +
                        "is not available in tenant: %s", artifactCategoryName, tenantDomain));
            }
            doCreateArtifact(artifact, tenantDomain, jdbcTemplate, categoryID);
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(String.format("Error while creating resource artifact: %s " +
                    "in tenant: %s", artifact.getName(), tenantDomain), e);
        }
    }

    @Override
    public ResourceArtifact getArtifact(String artifactCategoryName, String artifactName, String tenantDomain)
            throws IdentityArtifactManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        ResourceArtifact resourceArtifact;
        try {

            int categoryID = doGetCategoryID(artifactCategoryName, tenantDomain, jdbcTemplate);
            if (categoryID == 0) {
                throw new IdentityArtifactManagementException(String.format("The resource artifact category: %s " +
                        "is not available in tenant: %s", artifactCategoryName, tenantDomain));
            }
            resourceArtifact = doGetArtifact(artifactName, tenantDomain, jdbcTemplate, categoryID);
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(
                    String.format("Could not retrieve the resource artifact: %s in tenant: %s" + artifactName,
                            tenantDomain), e);
        }
        return resourceArtifact;
    }

    @Override
    public List<ResourceArtifact> getAllArtifactInfo(String tenantDomain) throws IdentityArtifactManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        List<ResourceArtifact> resourceArtifactList;
        try {
            resourceArtifactList = doGetArtifactList(tenantDomain, jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(
                    String.format("Could not retrieve all the resource artifact info in tenant: %s", tenantDomain), e);
        }
        return resourceArtifactList;
    }

    @Override
    public void updateArtifact(String artifactCategoryName, String artifactName, ResourceArtifact artifact,
                               String tenantDomain)
            throws IdentityArtifactManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int categoryID = doGetCategoryID(artifactCategoryName, tenantDomain, jdbcTemplate);
            if (categoryID == 0) {
                throw new IdentityArtifactManagementException(String.format("The resource artifact category: %s " +
                        "is not available in tenant: %s", artifactCategoryName, tenantDomain));
            }
            doUpdateArtifact(artifactName, artifact, tenantDomain, jdbcTemplate, categoryID);
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(String.format("An error occurred while updating the" +
                    " resource artifact : %s in tenant: %s", artifactName, tenantDomain), e);
        }
    }

    @Override
    public void deleteArtifact(String artifactCategoryName, String artifactName, String tenantDomain)
            throws IdentityArtifactManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int categoryID = doGetCategoryID(artifactCategoryName, tenantDomain, jdbcTemplate);
            if (categoryID == 0) {
                throw new IdentityArtifactManagementException(String.format("The resource artifact category: %s " +
                        "is not available in tenant: %s", artifactCategoryName, tenantDomain));
            }
            doDeleteArtifact(artifactName, tenantDomain, jdbcTemplate, categoryID);
        } catch (DataAccessException e) {
            throw new IdentityArtifactManagementException(String.format("An error occurred while deleting the " +
                    "resource artifact : %s in tenant: %s", artifactName, tenantDomain), e);
        }

    }

    private void doCreateCategory(ResourceArtifactCategory artifactCategory, String tenantDomain,
                                    JdbcTemplate jdbcTemplate) throws DataAccessException {

        int categoryID = jdbcTemplate.executeInsert(ADD_ARTIFACT_CATEGORY, (preparedStatement -> {
            preparedStatement.setInt(1, getTenantID(tenantDomain));
            preparedStatement.setString(2, artifactCategory.getName());
            preparedStatement.setString(3, artifactCategory.getDescription());
        }), null, true);
        artifactCategory.setId(categoryID);
    }

    private void doCreateArtifactsOfCategory(ResourceArtifactCategory artifactCategory, String tenantDomain,
                                               Timestamp timestamp, JdbcTemplate jdbcTemplate)
            throws TransactionException {

        if (ArrayUtils.isNotEmpty(artifactCategory.getResourceArtifacts())) {
            jdbcTemplate.withTransaction(template -> {
                template.executeBatchInsert(ADD_ARTIFACT, (preparedStatement -> {
                    for (ResourceArtifact artifact : artifactCategory.getResourceArtifacts()) {
                        preparedStatement.setInt(1, getTenantID(tenantDomain));
                        preparedStatement.setInt(2, artifactCategory.getId());
                        preparedStatement.setString(3, artifact.getName());
                        preparedStatement.setString(4, artifact.getDescription());
                        try {
                            setBlobValue(artifact.getValue(), preparedStatement, 5);
                        } catch (IOException e) {
                            throw new SQLException(String.format("Could not set resource artifact: %s content as " +
                                    "a Blob in tenant: %s.", artifact.getName(), tenantDomain), e);
                        }
                        preparedStatement.setTimestamp(6, timestamp,
                                Calendar.getInstance(TimeZone.getTimeZone(UTC)));
                        preparedStatement.setTimestamp(7, timestamp,
                                Calendar.getInstance(TimeZone.getTimeZone(UTC)));
                        preparedStatement.addBatch();
                    }
                }), null);
                return null;
            });
        }
    }


    private ResourceArtifactCategory doGetCategory(String artifactCategoryName, String tenantDomain,
                                                   JdbcTemplate jdbcTemplate) throws DataAccessException {

        ResourceArtifactCategory artifactCategory;
        artifactCategory = jdbcTemplate.fetchSingleRecord(GET_ARTIFACT_CATEGORY,
                (resultSet, rowNumber) -> {
                    ResourceArtifactCategory category = new ResourceArtifactCategory();
                    category.setId(resultSet.getInt(1));
                    category.setName(artifactCategoryName);
                    category.setDescription(resultSet.getString(3));
                    return category;
                },
                (PreparedStatement preparedStatement) -> {
                    preparedStatement.setString(1, artifactCategoryName);
                    preparedStatement.setInt(2, getTenantID(tenantDomain));
                });
        return artifactCategory;
    }

    private List<ResourceArtifact> doGetArtifactsOfCategory(String tenantDomain, JdbcTemplate jdbcTemplate,
                                                            ResourceArtifactCategory artifactCategory)
            throws DataAccessException {

        return jdbcTemplate.executeQuery(GET_ALL_ARTIFACT_OF_CATEGORY,
                (resultSet, rowNumber) -> {
                    try {
                        ResourceArtifact artifact = new ResourceArtifact();
                        artifact.setName(resultSet.getString(1));
                        artifact.setDescription(resultSet.getString(2));
                        artifact.setValue(IOUtils.toString(resultSet.getBinaryStream(3),
                                StandardCharsets.UTF_8.name()));
                        artifact.setCreatedTime(resultSet.getTimestamp(4).getTime());
                        artifact.setLastUpdatedTime(resultSet.getTimestamp(5).getTime());
                        return artifact;
                    } catch (IOException e) {
                        throw new SQLException(String.format("Could not get resource artifact: %s content as " +
                                "a Blob in tenant: %s.", resultSet.getString(1), tenantDomain), e);
                    }
                },
                (PreparedStatement preparedStatement) -> {
                    preparedStatement.setInt(1, artifactCategory.getId());
                    preparedStatement.setInt(2, getTenantID(tenantDomain));
                });
    }

    private int doGetCategoryID(String artifactCategoryName, String tenantDomain, JdbcTemplate jdbcTemplate)
            throws DataAccessException, IdentityArtifactManagementException {

        if (jdbcTemplate == null) {
            jdbcTemplate = JdbcUtils.getNewTemplate();
        }

        int categoryID = 0;
        String categoryIDExists = jdbcTemplate.fetchSingleRecord(GET_ARTIFACT_CATEGORY_ID,
                (resultSet, rowNumber) -> Integer.toString(resultSet.getInt(1)),
                (PreparedStatement preparedStatement) -> {
                    preparedStatement.setString(1, artifactCategoryName);
                    preparedStatement.setInt(2, getTenantID(tenantDomain));
                });
        if (categoryIDExists != null) {
            categoryID = Integer.parseInt(categoryIDExists);
        }
        return categoryID;
    }

    private void doUpdateCategory(ResourceArtifactCategory artifactCategory, String tenantDomain,
                                  JdbcTemplate jdbcTemplate) throws DataAccessException {

        jdbcTemplate.executeUpdate(UPDATE_ARTIFACT_CATEGORY,
                preparedStatement -> {
                    preparedStatement.setString(1, artifactCategory.getName());
                    preparedStatement.setString(2, artifactCategory.getDescription());
                    preparedStatement.setInt(3, artifactCategory.getId());
                    preparedStatement.setInt(4, getTenantID(tenantDomain));
                });
    }

    private void doCreateArtifact(ResourceArtifact artifact, String tenantDomain, JdbcTemplate jdbcTemplate,
                                  int categoryID) throws DataAccessException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.executeInsert(ADD_ARTIFACT, (preparedStatement -> {
            preparedStatement.setInt(1, getTenantID(tenantDomain));
            preparedStatement.setInt(2, categoryID);
            preparedStatement.setString(3, artifact.getName());
            preparedStatement.setString(4, artifact.getDescription());
            try {
                setBlobValue(artifact.getValue(), preparedStatement, 5);
            } catch (IOException e) {
                throw new SQLException(String.format("Could not set resource artifact: %s content as " +
                        "a Blob in tenant: %s.", artifact.getName(), tenantDomain), e);
            }
            preparedStatement.setTimestamp(6, timestamp,
                    Calendar.getInstance(TimeZone.getTimeZone(UTC)));
            preparedStatement.setTimestamp(7, timestamp,
                    Calendar.getInstance(TimeZone.getTimeZone(UTC)));
        }), null, true);
    }

    private ResourceArtifact doGetArtifact(String artifactName, String tenantDomain, JdbcTemplate jdbcTemplate,
                                           int categoryID) throws DataAccessException {

        return jdbcTemplate.fetchSingleRecord(GET_ARTIFACT,
                (resultSet, rowNumber) -> {
                    try {
                        ResourceArtifact artifact = new ResourceArtifact();
                        artifact.setName(artifactName);
                        artifact.setDescription(resultSet.getString(2));
                        artifact.setValue(IOUtils.toString(resultSet.getBinaryStream(3),
                                StandardCharsets.UTF_8.name()));
                        artifact.setCreatedTime(resultSet.getTimestamp(4).getTime());
                        artifact.setLastUpdatedTime(resultSet.getTimestamp(5).getTime());
                        return artifact;
                    } catch (IOException e) {
                        throw new SQLException(String.format("Could not get resource artifact: %s content as " +
                                "a Blob in tenant: %s.", artifactName, tenantDomain), e);
                    }
                },
                (PreparedStatement preparedStatement) -> {
                    preparedStatement.setInt(1, categoryID);
                    preparedStatement.setString(2, artifactName);
                    preparedStatement.setInt(3, getTenantID(tenantDomain));
                });
    }

    private List<ResourceArtifact> doGetArtifactList(String tenantDomain, JdbcTemplate jdbcTemplate)
            throws DataAccessException {

        return jdbcTemplate.executeQuery(GET_ALL_ARTIFACT_INFO,
                (resultSet, rowNumber) -> {
                    ResourceArtifact artifact = new ResourceArtifact();
                    artifact.setName(resultSet.getString(1));
                    artifact.setDescription(resultSet.getString(2));
                    artifact.setCreatedTime(resultSet.getTimestamp(3).getTime());
                    artifact.setLastUpdatedTime(resultSet.getTimestamp(4).getTime());
                    return artifact;
                },
                (PreparedStatement preparedStatement) -> preparedStatement.setInt(1, getTenantID(tenantDomain)));
    }

    private void doUpdateArtifact(String artifactName, ResourceArtifact artifact, String tenantDomain,
                                  JdbcTemplate jdbcTemplate, int categoryID) throws DataAccessException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.executeUpdate(UPDATE_ARTIFACT,
                preparedStatement -> {
                    preparedStatement.setString(1, artifact.getName());
                    preparedStatement.setString(2, artifact.getDescription());
                    try {
                        setBlobValue(artifact.getValue(), preparedStatement, 3);
                    } catch (IOException e) {
                        throw new SQLException(String.format("Could not set resource artifact: %s content as " +
                                "a Blob in tenant: %s.", artifactName, tenantDomain), e);
                    }
                    preparedStatement.setTimestamp(4, timestamp,
                            Calendar.getInstance(TimeZone.getTimeZone(UTC)));
                    preparedStatement.setInt(5, categoryID);
                    preparedStatement.setString(6, artifactName);
                    preparedStatement.setInt(7, getTenantID(tenantDomain));
                });
    }

    private void doDeleteArtifact(String artifactName, String tenantDomain, JdbcTemplate jdbcTemplate, int categoryID)
            throws DataAccessException {

        jdbcTemplate.executeUpdate(DELETE_ARTIFACT,
                preparedStatement -> {
                    preparedStatement.setInt(1, categoryID);
                    preparedStatement.setString(2, artifactName);
                    preparedStatement.setInt(3, getTenantID(tenantDomain));
                });
    }

    /**
     * Set given string as Blob for the given index into the prepared-statement.
     *
     * @param value    string value to be converted to blob
     * @param prepStmt Prepared statement
     * @param index    column index
     * @throws SQLException
     * @throws IOException
     */
    private void setBlobValue(String value, PreparedStatement prepStmt, int index) throws SQLException,
            IOException {

        if (value != null) {
            InputStream inputStream = new ByteArrayInputStream(value.getBytes());
            prepStmt.setBinaryStream(index, inputStream, inputStream.available());
        } else {
            prepStmt.setBinaryStream(index, new ByteArrayInputStream(new byte[0]), 0);
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
