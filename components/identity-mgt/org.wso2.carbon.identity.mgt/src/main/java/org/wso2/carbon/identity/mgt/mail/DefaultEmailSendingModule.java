/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.mgt.mail;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.transport.base.BaseConstants;
import org.apache.axis2.transport.mail.MailConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.CarbonConfigurationContextFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.event.output.adapter.core.*;
import org.wso2.carbon.event.output.adapter.core.exception.OutputEventAdapterException;
import org.wso2.carbon.event.output.adapter.core.internal.util.EventAdapterConfigHelper;
import org.wso2.carbon.event.output.adapter.email.EmailEventAdapter;
import org.wso2.carbon.event.output.adapter.email.EmailEventAdapterFactory;
import org.wso2.carbon.identity.mgt.constants.IdentityMgtConstants;
import org.wso2.carbon.utils.CarbonUtils;

import javax.swing.text.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Default email sending implementation
 */
public class DefaultEmailSendingModule extends AbstractEmailSendingModule {

    public static final String CONF_STRING = "confirmation";
    private static Log log = LogFactory.getLog(DefaultEmailSendingModule.class);
    private Notification notification;

    /**
     * Replace the {user-parameters} in the config file with the respective
     * values
     *
     * @param text           the initial text
     * @param userParameters mapping of the key and its value
     * @return the final text to be sent in the email
     */
    public static String replacePlaceHolders(String text, Map<String, String> userParameters) {
        if (userParameters != null) {
            for (Map.Entry<String, String> entry : userParameters.entrySet()) {
                String key = entry.getKey();
                if (key != null && entry.getValue() != null) {
                    text = text.replaceAll("\\{" + key + "\\}", entry.getValue());
                }
            }
        }
        return text;
    }

    @Override
    public void sendEmail() {

        Map<String, String> headerMap = new HashMap<String, String>();

        try {
            if (this.notification == null) {
                throw new IllegalStateException("Notification not set. " +
                        "Please set the notification before sending messages");
            }
            PrivilegedCarbonContext.startTenantFlow();
            if (notificationData != null) {
                String tenantDomain = notificationData.getDomainName();
                PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                carbonContext.setTenantDomain(tenantDomain, true);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("notification data not found. Tenant might not be loaded correctly");
                }
            }

            if (notification.getType() != null && notification.getType().equalsIgnoreCase("text/html")) {
                sendEmailWithHtmlTemplate();
            } else {
                headerMap.put(MailConstants.MAIL_HEADER_SUBJECT, this.notification.getSubject());

                OMElement payload = OMAbstractFactory.getOMFactory().createOMElement(
                        BaseConstants.DEFAULT_TEXT_WRAPPER, null);
                StringBuilder contents = new StringBuilder();
                contents.append(this.notification.getBody())
                        .append(System.getProperty("line.separator"))
                        .append(System.getProperty("line.separator"))
                        .append(this.notification.getFooter());
                payload.setText(contents.toString());
                ServiceClient serviceClient;
                ConfigurationContext configContext = CarbonConfigurationContextFactory
                        .getConfigurationContext();
                if (configContext != null) {
                    serviceClient = new ServiceClient(configContext, null);
                } else {
                    serviceClient = new ServiceClient();
                }
                Options options = new Options();
                options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
                options.setProperty(MessageContext.TRANSPORT_HEADERS, headerMap);
                options.setProperty(MailConstants.TRANSPORT_MAIL_FORMAT,
                        MailConstants.TRANSPORT_FORMAT_TEXT);
                options.setTo(new EndpointReference("mailto:" + this.notification.getSendTo()));
                serviceClient.setOptions(options);
                log.info("Sending " + "user credentials configuration mail to " + this.notification.getSendTo());
                serviceClient.fireAndForget(payload);

                if (log.isDebugEnabled()) {
                    log.debug("Email content : " + this.notification.getBody());
                }
            }
            log.info("User credentials configuration mail has been sent to " + this.notification.getSendTo());
        } catch (AxisFault axisFault) {
            log.error("Failed Sending Email", axisFault);
        } catch (OutputEventAdapterException e) {
            e.printStackTrace();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    public void sendEmailWithHtmlTemplate() throws OutputEventAdapterException {
/*        EventAdapterConfigHelper eventAdapterConfigHelper = new EventAdapterConfigHelper();
        Map<String, String> globalProperties =
        eventAdapterConfigHelper.loadGlobalConfigs().
                getAdapterConfig("email").getGlobalPropertiesAsMap();*/

        /*Map<String, String> globalProperties = new HashMap<String, String>();
        globalProperties.put()

        smtpFrom = props.getProperty(MailConstants.MAIL_SMTP_FROM);
        smtpHost = props.getProperty(EmailEventAdapterConstants.MAIL_SMTP_HOST);
        smtpPort = props.getProperty(EmailEventAdapterConstants.MAIL_SMTP_PORT);

        //Retrieving username and password of SMTP server.
        smtpUsername = props.getProperty(MailConstants.MAIL_SMTP_USERNAME);
        smtpPassword = props.getProperty(MailConstants.MAIL_SMTP_PASSWORD);*/

        // read parameter from axis2.xml
        AxisConfiguration axisConfiguration =
                CarbonConfigurationContextFactory.getConfigurationContext()
                        .getAxisConfiguration();
        ArrayList<Parameter> axis_mailParams = axisConfiguration.getTransportOut("mailto").getParameters();
        Map<String, String> globalProperties = new HashMap<String, String>();
        for(Parameter parameter : axis_mailParams) {
            globalProperties.put(parameter.getName(), (String) parameter.getValue());
        }

        EmailEventAdapterFactory emailEventAdapterFactory = new EmailEventAdapterFactory();
        OutputEventAdapter emailEventAdapter = (EmailEventAdapter) emailEventAdapterFactory.createEventAdapter(null, globalProperties);

        //get dynamic properties
        Map<String, String> dynamicProperties = new HashMap<>();
        String emailSubject = notification.getSubject();
        dynamicProperties.put("email.subject", emailSubject);
        String emailType = notification.getType();
        dynamicProperties.put("email.type", emailType);
        String emailAdd = notification.getSendTo();
        dynamicProperties.put("email.address", emailAdd);

        StringBuilder contents = new StringBuilder();
        contents.append(this.notification.getBody())
                .append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"))
                .append(this.notification.getFooter());
        String emailBody = contents.toString();

        emailEventAdapter.init();
        emailEventAdapter.connect();
        emailEventAdapter.publish(emailBody, dynamicProperties);

        if (log.isDebugEnabled()) {
            log.debug("Email content : " + emailBody);
        }
        log.info("User credentials configuration mail has been sent to " + this.notification.getSendTo());

    }

    public String getRequestMessage(EmailConfig emailConfig) {

        StringBuilder msg;
        String targetEpr = emailConfig.getTargetEpr();
        if (emailConfig.getEmailBody().length() == 0) {
            msg = new StringBuilder(EmailConfig.DEFAULT_VALUE_MESSAGE);
            msg.append("\n");
            if (notificationData.getNotificationCode() != null) {

                msg.append(targetEpr).append("?").append(CONF_STRING).append(notificationData
                        .getNotificationCode()).append("\n");
            }
        } else {
            msg = new StringBuilder(emailConfig.getEmailBody());
            msg.append("\n");
        }
        if (emailConfig.getEmailFooter() != null) {
            msg.append("\n").append(emailConfig.getEmailFooter());
        }
        return msg.toString();
    }

    @Override
    public Notification getNotification() {
        return this.notification;
    }

    @Override
    public void setNotification(Notification notification) {
        this.notification = notification;
    }


  /* public static void secureResolveDocument(Document doc)
            throws OutputEventAdapterException {
        Element element = doc.getDocumentElement();
        if (element != null) {
            try {
                secureLoadElement(element);
            } catch (CryptoException e) {
                throw new OutputEventAdapterException("Error in secure load of global output event adapter properties: " +
                        e.getMessage(), e);
            }
        }
    }

    public static Document convertToDocument(File file) throws OutputEventAdapterException {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setNamespaceAware(true);
        try {
            return fac.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            throw new OutputEventAdapterException("Error in creating an XML document from file: " +
                    e.getMessage(), e);
        }
    }

    private static void secureLoadElement(Element element)
            throws CryptoException {

        Attr secureAttr = element.getAttributeNodeNS(EventAdapterConstants.SECURE_VAULT_NS,
                EventAdapterConstants.SECRET_ALIAS_ATTR_NAME);
        if (secureAttr != null) {
            element.setTextContent(loadFromSecureVault(secureAttr.getValue()));
            element.removeAttributeNode(secureAttr);
        }
        NodeList childNodes = element.getChildNodes();
        int count = childNodes.getLength();
        Node tmpNode;
        for (int i = 0; i < count; i++) {
            tmpNode = childNodes.item(i);
            if (tmpNode instanceof Element) {
                secureLoadElement((Element) tmpNode);
            }
        }
    }*/

    public static Map<String, String> loadGlobalConfigs() {

       /* String path = CarbonUtils.getCarbonConfigDirPath() + File.separator + "conf" + File.separator + IdentityMgtConstants.GLOBAL_CONFIG_FILE_NAME;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DefaultEmailSendingModule.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            File configFile = new File(path);
            if (!configFile.exists()) {
                log.warn(IdentityMgtConstants.GLOBAL_CONFIG_FILE_NAME + " can not found in " + path + "," +
                        " hence Output Event Adapters will be running with default global configs.");
            }
            Document globalConfigDoc = convertToDocument(configFile);
            secureResolveDocument(globalConfigDoc);
            return (Map<String, String>) unmarshaller.unmarshal(globalConfigDoc);
        } catch (JAXBException e) {
            log.error("Error in loading " + IdentityMgtConstants.GLOBAL_CONFIG_FILE_NAME + " from " + path + "," +
                    " hence Output Event Adapters will be running with default global configs.");
        } catch (OutputEventAdapterException e) {
            log.error("Error in converting " + IdentityMgtConstants.GLOBAL_CONFIG_FILE_NAME + " to parsed document," +
                    " hence Output Event Adapters will be running with default global configs.");
        }*/
        return new HashMap<>();

        /*// read parameter from axis2.xml
        AxisConfiguration axisConfiguration =
                CarbonConfigurationContextFactory.getConfigurationContext()
                        .getAxisConfiguration();*/

    }


    /*
    public static void secureResolveDocument(Document doc)
            throws OutputEventAdapterException {
        Element element = doc.getDocumentElement();
        if (element != null) {
            try {
                secureLoadElement(element);
            } catch (CryptoException e) {
                throw new OutputEventAdapterException("Error in secure load of global output event adapter properties: " +
                        e.getMessage(), e);
            }
        }
    }

    public static Document convertToDocument(File file) throws OutputEventAdapterException {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setNamespaceAware(true);
        try {
            return fac.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            throw new OutputEventAdapterException("Error in creating an XML document from file: " +
                    e.getMessage(), e);
        }
    }

    private static void secureLoadElement(Element element)
            throws CryptoException {

        Attr secureAttr = element.getAttributeNodeNS(EventAdapterConstants.SECURE_VAULT_NS,
                EventAdapterConstants.SECRET_ALIAS_ATTR_NAME);
        if (secureAttr != null) {
            element.setTextContent(loadFromSecureVault(secureAttr.getValue()));
            element.removeAttributeNode(secureAttr);
        }
        NodeList childNodes = element.getChildNodes();
        int count = childNodes.getLength();
        Node tmpNode;
        for (int i = 0; i < count; i++) {
            tmpNode = childNodes.item(i);
            if (tmpNode instanceof Element) {
                secureLoadElement((Element) tmpNode);
            }
        }
    }

    public static AdapterConfigs loadGlobalConfigs() {

        String path = CarbonUtils.getCarbonConfigDirPath() + File.separator + EventAdapterConstants.GLOBAL_CONFIG_FILE_NAME;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AdapterConfigs.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            File configFile = new File(path);
            if (!configFile.exists()) {
                log.warn(EventAdapterConstants.GLOBAL_CONFIG_FILE_NAME + " can not found in " + path + "," +
                        " hence Output Event Adapters will be running with default global configs.");
            }
            Document globalConfigDoc = convertToDocument(configFile);
            secureResolveDocument(globalConfigDoc);
            return (AdapterConfigs) unmarshaller.unmarshal(globalConfigDoc);
        } catch (JAXBException e) {
            log.error("Error in loading " + EventAdapterConstants.GLOBAL_CONFIG_FILE_NAME + " from " + path + "," +
                    " hence Output Event Adapters will be running with default global configs.");
        } catch (OutputEventAdapterException e) {
            log.error("Error in converting " + EventAdapterConstants.GLOBAL_CONFIG_FILE_NAME + " to parsed document," +
                    " hence Output Event Adapters will be running with default global configs.");
        }
        return new AdapterConfigs();
    }
*/


}
