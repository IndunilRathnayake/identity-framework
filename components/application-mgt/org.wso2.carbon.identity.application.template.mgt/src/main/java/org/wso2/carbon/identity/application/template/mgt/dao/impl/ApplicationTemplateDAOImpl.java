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
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.application.template.mgt.IdentityApplicationTemplateMgtException;
import org.wso2.carbon.identity.application.template.mgt.dao.ApplicationTemplateDAO;
import org.wso2.carbon.identity.application.template.mgt.dto.SpTemplateDTO;
import org.wso2.carbon.identity.application.template.mgt.util.JdbcUtils;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;

import static org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries.ADD_SP_TEMPLATE;
import static org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries.DELETE_SP_TEMPLATE_BY_NAME;
import static org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries.GET_ALL_SP_TEMPLATES_BASIC_INFO;
import static org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries.IS_SP_TEMPLATE_EXISTS;
import static org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries.LOAD_SP_TEMPLATE_CONTENT;
import static org.wso2.carbon.identity.application.template.mgt.ApplicationTemplateMgtDBQueries.UPDATE_SP_TEMPLATE_BY_NAME;

/**
 * Default implementation of {@link ApplicationTemplateDAO}. This handles {@link SpTemplateDTO} related db layer
 * operations.
 */
public class ApplicationTemplateDAOImpl implements ApplicationTemplateDAO {

    private static final Log log = LogFactory.getLog(ApplicationTemplateDAOImpl.class);

    public ApplicationTemplateDAOImpl() {
    }

    @Override
    public void createApplicationTemplate(SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating application template: %s in tenant: %s", spTemplateDTO.getName(),
                    tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);
        String templateName = spTemplateDTO.getName();
        String templateDescription = spTemplateDTO.getDescription();

        try {
            JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
            jdbcTemplate.executeInsert(ADD_SP_TEMPLATE, (preparedStatement -> {
                preparedStatement.setInt(1, tenantID);
                preparedStatement.setString(2, templateName);
                preparedStatement.setString(3, templateDescription);
                preparedStatement.setCharacterStream(4, new StringReader(spTemplateDTO.getSpContent()));
                try {
                    preparedStatement.setBinaryStream(4, setBlobObject(spTemplateDTO.getSpContent()));
                } catch (IOException e) {
                    throw new SQLException(String.format("Could not set application template: %s content as a Blob" +
                            "in tenant: %s.", templateName, tenantDomain), e);
                }
            }), null, true);
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error while creating application " +
                    "template: %s in tenant: %s", templateName, tenantDomain), e);
        }
    }

    @Override
    public SpTemplateDTO loadApplicationTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Loading application template: %s in tenant: %s", templateName, tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);
        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        SpTemplateDTO spTemplateDTO;

        try {
            spTemplateDTO = jdbcTemplate.fetchSingleRecord(LOAD_SP_TEMPLATE_CONTENT,
                    (resultSet, rowNumber) -> {
                        try {
                            return new SpTemplateDTO(templateName,
                                    null,
                                    getBlobObject(resultSet.getCharacterStream(1)));
                        } catch (IOException e) {
                            throw new SQLException(String.format("Could not get application template: %s content as " +
                                    "a Blob in tenant: %s.", templateName, tenantDomain), e);
                        }
                    },
                    preparedStatement -> {
                        preparedStatement.setString(1, templateName);
                        preparedStatement.setInt(2, tenantID);
                    });
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(
                    String.format("Could not read the template information for template: %s in tenant: %s" +
                            templateName, tenantDomain), e);
        }
        return spTemplateDTO;
    }

    @Override
    public boolean isExistingTemplate(String templateName, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Checking application template exists for name: %s in tenant: %s", templateName,
                    tenantDomain));
        }
        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        int tenantID = getTenantID(tenantDomain);
        int id;

        try {
            id = jdbcTemplate.fetchSingleRecord(IS_SP_TEMPLATE_EXISTS,
                    (resultSet, rowNumber) -> resultSet.getInt(1),
                    preparedStatement -> {
                        preparedStatement.setString(1, templateName);
                        preparedStatement.setInt(2, tenantID);
                    });
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error while checking existence of " +
                    "application template: %s", templateName), e);
        }

        if (id != 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<SpTemplateDTO> getAllApplicationTemplates(String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Getting all the application templates of tenant: %s", tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        List<SpTemplateDTO> spTemplateDTOList;
        try {
            spTemplateDTOList = jdbcTemplate.executeQuery(GET_ALL_SP_TEMPLATES_BASIC_INFO,
                    (resultSet, i) -> {
                        SpTemplateDTO spTemplateDTO = new SpTemplateDTO();
                        spTemplateDTO.setName(resultSet.getString(1));
                        spTemplateDTO.setDescription(resultSet.getString(2));
                        return spTemplateDTO;
                    },
                    preparedStatement -> preparedStatement.setInt(1, tenantID));
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error while Loading all the application " +
                    "templates of tenant: %s", tenantDomain), e);
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
        List<String> spTemplateNames;

        try {
            JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
            spTemplateNames = jdbcTemplate.executeQuery(GET_ALL_SP_TEMPLATES_BASIC_INFO,
                    (resultSet, i) -> resultSet.getString(1),
                    preparedStatement -> preparedStatement.setInt(1, tenantID));
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("Error while loading application template "
                    + "names of tenant: %s", tenantDomain), e);
        }
        return spTemplateNames;
    }

    @Override
    public void deleteApplicationTemplate(String templateName, String tenantDomain) throws
            IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Deleting application template: %s in tenant: %s", templateName, tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);
        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            jdbcTemplate.executeUpdate(DELETE_SP_TEMPLATE_BY_NAME,
                    preparedStatement -> {
                        preparedStatement.setString(1, templateName);
                        preparedStatement.setInt(2, tenantID);
                    });
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("An error occurred while deleting the " +
                    "application template : %s in tenant: %s", templateName, tenantDomain), e);
        }
    }

    @Override
    public void updateApplicationTemplate(String templateName, SpTemplateDTO spTemplateDTO, String tenantDomain)
            throws IdentityApplicationTemplateMgtException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Updating application template: %s in tenant: %s", spTemplateDTO.getName(),
                    tenantDomain));
        }

        int tenantID = getTenantID(tenantDomain);
        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            jdbcTemplate.executeUpdate(UPDATE_SP_TEMPLATE_BY_NAME,
                    preparedStatement -> {
                        preparedStatement.setString(1, spTemplateDTO.getName());
                        preparedStatement.setString(2, spTemplateDTO.getDescription());
                        preparedStatement.setCharacterStream(3, new StringReader(spTemplateDTO.getSpContent()));
                        preparedStatement.setString(4, templateName);
                        preparedStatement.setInt(5, tenantID);
                    });
        } catch (DataAccessException e) {
            throw new IdentityApplicationTemplateMgtException(String.format("An error occurred while updating the" +
                    " application template : %s in tenant: %s", spTemplateDTO.getName(), tenantDomain), e);
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

    private String getBlobObject(Reader reader) throws IOException {

        BufferedReader br = null;
        if (reader != null) {
            try {
                StringBuilder sb = new StringBuilder();
                br = new BufferedReader(reader);
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\r\n");
                }
                return sb.toString();
            } finally {
                reader.close();
                if (br != null) {
                    br.close();
                }
            }
        }
        return null;
    }

    private InputStream setBlobObject(String content) throws IOException {

        InputStream inputStream = null;
        try {
            if (content != null) {
                inputStream = new ByteArrayInputStream(content.getBytes());
            }
            return inputStream;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
