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

package org.wso2.carbon.identity.application.common.model;

import org.apache.axiom.om.OMElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DefaultAuthenticationSequence")
public class DefaultAuthenticationSequence {

    @XmlElement(name = "sequenceName", required = true)
    private String sequenceName;

    @XmlElement(name = "sequenceDescription")
    private String sequenceDescription;

    @XmlElement(name = "sequenceContent")
    private String sequenceContent;

    public static DefaultAuthenticationSequence build(OMElement defaultAuthSeqOM) {
        DefaultAuthenticationSequence authenticationSequence = new DefaultAuthenticationSequence();

        Iterator<?> iter = defaultAuthSeqOM.getChildElements();


        while (iter.hasNext()) {
            OMElement member = (OMElement) iter.next();
            if ("name".equals(member.getLocalName())) {
                authenticationSequence.setSequenceName(member.getText());
            } else if ("description".equals(member.getLocalName())) {
                if (StringUtils.isNotBlank(member.getText())) {
                    authenticationSequence.setSequenceDescription(member.getText());
                }
            }
        }
        return authenticationSequence;
    }

    public String getSequenceName() {

        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {

        this.sequenceName = sequenceName;
    }

    public String getSequenceDescription() {

        return sequenceDescription;
    }

    public void setSequenceDescription(String sequenceDescription) {

        this.sequenceDescription = sequenceDescription;
    }

    public String getSequenceContent() {

        return sequenceContent;
    }

    public void setSequenceContent(String sequenceContent) {

        this.sequenceContent = sequenceContent;
    }
}
