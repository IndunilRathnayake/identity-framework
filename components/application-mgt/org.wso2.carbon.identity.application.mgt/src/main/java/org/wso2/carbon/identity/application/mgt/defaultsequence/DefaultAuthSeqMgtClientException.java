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

package org.wso2.carbon.identity.application.mgt.defaultsequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exception class for client side errors in tenant default authentication sequence management.
 */
public class DefaultAuthSeqMgtClientException extends DefaultAuthSeqMgtException {

    private String message;
    private String[] messages;

    private static final long serialVersionUID = -1982152066401023165L;

    public DefaultAuthSeqMgtClientException(String message) {

        super(message);
        this.message = message;
    }

    public DefaultAuthSeqMgtClientException(String[] messages) {

        super(Arrays.toString(messages));
        if (messages == null) {
            return;
        }
        List<String> msgList = new ArrayList<>();
        for (String msg: messages) {
            if (!msg.trim().isEmpty()) {
                msgList.add(msg);
            }
        }
        this.messages = msgList.toArray(new String[0]);
    }

    public DefaultAuthSeqMgtClientException(String message, Throwable e) {

        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {

        return message;
    }

    public String[] getMessages() {

        return messages;
    }
}