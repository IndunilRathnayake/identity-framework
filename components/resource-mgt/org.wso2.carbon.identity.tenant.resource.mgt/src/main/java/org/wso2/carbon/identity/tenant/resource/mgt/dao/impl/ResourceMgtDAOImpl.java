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

package org.wso2.carbon.identity.tenant.resource.mgt.dao.impl;

import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.tenant.resource.mgt.IdentityResourceManagementException;
import org.wso2.carbon.identity.tenant.resource.mgt.dao.ResourceMgtDAO;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.Resource;
import org.wso2.carbon.identity.tenant.resource.mgt.dto.ResourceContent;
import org.wso2.carbon.identity.tenant.resource.mgt.util.JdbcUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.ADD_RESOURCE;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.ADD_RESOURCE_CONTENT;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.DELETE_CONTENT_OF_RESOURCE;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.DELETE_RESOURCE;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.GET_CONTENT_OF_RESOURCE;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.GET_RESOURCE;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.GET_RESOURCE_ID;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.UPDATE_CONTENT_OF_RESOURCE;
import static org.wso2.carbon.identity.tenant.resource.mgt.ResourceMgtDBQueries.UPDATE_RESOURCE;

/**
 * This class access the IDN_RESOURCE and IDN_RESOURCE_CONTENT tables to manage tenant resources.
 */
public class ResourceMgtDAOImpl implements ResourceMgtDAO {

    private static final String UTC = "UTC";

    public ResourceMgtDAOImpl() {
    }

    @Override
    public void createResource(Resource resource, String tenantDomain) throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int resourceID = 0;
        try {
            if (isExistingResource(resource.getName(), tenantDomain)) {
                throw new IdentityResourceManagementException(String.format("Resource with name: %s is already " +
                        "exists in tenant: %s", resource.getName(), tenantDomain));
            }
            resourceID = doCreateResource(resource, tenantDomain, timestamp, jdbcTemplate);
            doCreateContentOfResource(resourceID, resource.getResourceContent(), jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("Error while creating resource: %s in " +
                    "tenant: %s", resource.getName(), tenantDomain), e);
        }
    }

    @Override
    public Resource getResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        Resource resource;
        try {
            resource = doGetResource(resourceName, tenantDomain, jdbcTemplate);
            if (resource != null) {
                ResourceContent resourceContent = doGetContentOfResource(resource.getId(), jdbcTemplate);
                resource.setResourceContent(resourceContent);
            }
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(
                    String.format("Could not retrieve the resource: %s in tenant: %s", resourceName, tenantDomain), e);
        }
        return resource;
    }

    @Override
    public boolean isExistingResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException {

        try {
            int resourceID = doGetResourceID(resourceName, tenantDomain, null);
            if (resourceID != 0) {
                return true;
            }
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(
                    String.format("Could not check existence of resource: %s in tenant: %s", resourceName,
                            tenantDomain), e);
        }
        return false;
    }

    @Override
    public void updateResource(String resourceName, Resource resource, String tenantDomain)
            throws IdentityResourceManagementException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int resourceID = doGetResourceID(resourceName, tenantDomain, jdbcTemplate);
            if (resourceID == 0) {
                throw new IdentityResourceManagementException(String.format("The resource: %s is not available in " +
                        "tenant: %s", resourceName, tenantDomain));
            }
            resource.setId(resourceID);

            if (resourceID == 0) {
                throw new IdentityResourceManagementException(String.format("The resource: %s is not available in " +
                        "tenant: %s", resourceName, tenantDomain));
            }

            doUpdateResource(resource, tenantDomain, timestamp, jdbcTemplate);
            doDeleteContentOfResource(resourceID, jdbcTemplate);
            doCreateContentOfResource(resourceID, resource.getResourceContent(), jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("An error occurred while updating the" +
                    " resource: %s in tenant: %s", resource.getName(), tenantDomain), e);
        }

    }

    @Override
    public void deleteResource(String resourceName, String tenantDomain) throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            doDeleteResource(resourceName, tenantDomain, jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("An error occurred while deleting the" +
                    " resource: %s in tenant: %s", resourceName, tenantDomain), e);
        }
    }

    @Override
    public void createResourceContent(String resourceName, ResourceContent resourceContent, String tenantDomain)
            throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int resourceID = doGetResourceID(resourceName, tenantDomain, jdbcTemplate);
            if (resourceID == 0) {
                throw new IdentityResourceManagementException(String.format("The resource: %s is not available in " +
                        "tenant: %s", resourceName, tenantDomain));
            }
            doCreateContentOfResource(resourceID, resourceContent, jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("An error occurred while deleting the" +
                    " resource: %s in tenant: %s", resourceName, tenantDomain), e);
        }
    }

    @Override
    public ResourceContent getContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int resourceID = doGetResourceID(resourceName, tenantDomain, jdbcTemplate);
            if (resourceID == 0) {
                throw new IdentityResourceManagementException(String.format("The resource: %s is not available in " +
                        "tenant: %s", resourceName, tenantDomain));
            }
            return doGetContentOfResource(resourceID, jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("An error occurred while retrieving content of" +
                    " the resource: %s in tenant: %s", resourceName, tenantDomain), e);
        }
    }

    @Override
    public void updateContentOfResource(String resourceName, ResourceContent resourceContent, String tenantDomain)
            throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int resourceID = doGetResourceID(resourceName, tenantDomain, jdbcTemplate);
            if (resourceID == 0) {
                throw new IdentityResourceManagementException(String.format("The resource: %s is not available in " +
                        "tenant: %s", resourceName, tenantDomain));
            }
            doUpdateContentOfResource(resourceID, resourceContent, jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("An error occurred while updating the" +
                    " content of resource : %s in tenant: %s", resourceName, tenantDomain), e);
        }
    }

    @Override
    public void deleteContentOfResource(String resourceName, String tenantDomain)
            throws IdentityResourceManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int resourceID = doGetResourceID(resourceName, tenantDomain, jdbcTemplate);
            if (resourceID == 0) {
                throw new IdentityResourceManagementException(String.format("The resource: %s is not available in " +
                        "tenant: %s", resourceName, tenantDomain));
            }
            doDeleteContentOfResource(resourceID, jdbcTemplate);
        } catch (DataAccessException e) {
            throw new IdentityResourceManagementException(String.format("An error occurred while deleting the " +
                    "content of resource : %s in tenant: %s", resourceName, tenantDomain), e);
        }

    }

    private int doCreateResource(Resource resource, String tenantDomain, Timestamp timestamp,
                                 JdbcTemplate jdbcTemplate) throws DataAccessException {

        return jdbcTemplate.executeInsert(ADD_RESOURCE, (preparedStatement -> {
            preparedStatement.setInt(1, getTenantID(tenantDomain));
            preparedStatement.setString(2, resource.getName());
            preparedStatement.setString(3, resource.getDescription());
            preparedStatement.setTimestamp(4, timestamp,
                    Calendar.getInstance(TimeZone.getTimeZone(UTC)));
            preparedStatement.setTimestamp(5, timestamp,
                    Calendar.getInstance(TimeZone.getTimeZone(UTC)));
        }), null, true);
    }


    private Resource doGetResource(String resourceName, String tenantDomain, JdbcTemplate jdbcTemplate)
            throws DataAccessException {

        return jdbcTemplate.fetchSingleRecord(GET_RESOURCE,
                (resultSet, rowNumber) -> {
                    Resource resource = new Resource();
                    resource.setId(resultSet.getInt(1));
                    resource.setName(resourceName);
                    resource.setDescription(resultSet.getString(3));
                    resource.setCreatedTime(resultSet.getTimestamp(4).getTime());
                    resource.setCreatedTime(resultSet.getTimestamp(5).getTime());
                    return resource;
                },
                (PreparedStatement preparedStatement) -> {
                    preparedStatement.setString(1, resourceName);
                    preparedStatement.setInt(2, getTenantID(tenantDomain));
                });
    }

    private int doGetResourceID(String resourceName, String tenantDomain, JdbcTemplate jdbcTemplate)
            throws DataAccessException {

        if (jdbcTemplate == null) {
            jdbcTemplate = JdbcUtils.getNewTemplate();
        }

        int resourceID = 0;
        String resourceIDExists = null;
        resourceIDExists = jdbcTemplate.fetchSingleRecord(GET_RESOURCE_ID,
                (resultSet, rowNumber) -> Integer.toString(resultSet.getInt(1)),
                (PreparedStatement preparedStatement) -> {
                    preparedStatement.setString(1, resourceName);
                    preparedStatement.setInt(2, getTenantID(tenantDomain));
                });

        if (resourceIDExists != null) {
            resourceID = Integer.parseInt(resourceIDExists);
        }
        return resourceID;
    }

    private void doUpdateResource(Resource resource, String tenantDomain, Timestamp timestamp,
                                  JdbcTemplate jdbcTemplate) throws DataAccessException {

        jdbcTemplate.executeUpdate(UPDATE_RESOURCE,
                preparedStatement -> {
                    preparedStatement.setString(1, resource.getName());
                    preparedStatement.setString(2, resource.getDescription());
                    preparedStatement.setTimestamp(3, timestamp,
                            Calendar.getInstance(TimeZone.getTimeZone(UTC)));
                    preparedStatement.setInt(4, resource.getId());
                    preparedStatement.setInt(5, getTenantID(tenantDomain));
                });
    }

    private void doDeleteResource(String resourceName, String tenantDomain,
                                  JdbcTemplate jdbcTemplate) throws DataAccessException {

        jdbcTemplate.executeUpdate(DELETE_RESOURCE,
                preparedStatement -> {
                    preparedStatement.setString(1, resourceName);
                    preparedStatement.setInt(2, getTenantID(tenantDomain));
                });
    }

    private void doCreateContentOfResource(int resourceID, ResourceContent resourceContent,
                                           JdbcTemplate jdbcTemplate) throws DataAccessException {

        if (resourceContent != null && resourceContent.getContent() != null) {
            jdbcTemplate.executeInsert(ADD_RESOURCE_CONTENT, (preparedStatement -> {
                preparedStatement.setInt(1, resourceID);
                try {
                    setBlobValue(resourceContent.getContent(), preparedStatement, 2);
                } catch (IOException e) {
                    throw new SQLException("Could not set resource content as a Blob.", e);
                }
                preparedStatement.setString(3, resourceContent.getContentType());
            }), null, true);
        }
    }

    private ResourceContent doGetContentOfResource(int resourceID, JdbcTemplate jdbcTemplate)
            throws DataAccessException {

        return jdbcTemplate.fetchSingleRecord(GET_CONTENT_OF_RESOURCE,
                (resultSet, rowNumber) -> {
                    ResourceContent content = new ResourceContent();
                    try {
                        byte[] requestBytes = resultSet.getBytes(1);
                        ByteArrayInputStream bais = new ByteArrayInputStream(requestBytes);
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        Object objectRead = ois.readObject();
                        content.setContent(objectRead);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new SQLException(String.format("Could not get content of resource: %s as a Blob.",
                                resourceID), e);
                    }
                    content.setContentType(resultSet.getString(2));
                    return content;
                }, preparedStatement -> preparedStatement.setInt(1, resourceID));
    }

    private void doUpdateContentOfResource(int resourceID, ResourceContent resourceContent, JdbcTemplate jdbcTemplate)
            throws DataAccessException {

        jdbcTemplate.executeUpdate(UPDATE_CONTENT_OF_RESOURCE,
                preparedStatement -> {
                    try {
                        setBlobValue(resourceContent.getContent(), preparedStatement, 1);
                    } catch (IOException e) {
                        throw new SQLException("Could not set resource content as a Blob.", e);
                    }
                    preparedStatement.setString(2, resourceContent.getContentType());
                    preparedStatement.setInt(3, resourceID);
                });
    }

    private void doDeleteContentOfResource(int resourceID, JdbcTemplate jdbcTemplate) throws DataAccessException {

        jdbcTemplate.executeUpdate(DELETE_CONTENT_OF_RESOURCE,
                preparedStatement -> {
                    preparedStatement.setInt(1, resourceID);
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
    private void setBlobValue(Object value, PreparedStatement prepStmt, int index) throws SQLException,
            IOException {

        if (value != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.close();
            prepStmt.setBytes(index, baos.toByteArray());
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
