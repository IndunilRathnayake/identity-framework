package org.wso2.carbon.identity.application.template.mgt.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.ApplicationMgtUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

public class ApplicationTemplateMgtUtil {

    private static Log log = LogFactory.getLog(ApplicationTemplateMgtUtil.class);

    /*public static boolean isUserAuthorized(String templateName, String username, String permission)
            throws IdentityApplicationManagementException {

        String applicationRoleName = getAppRoleName(templateName);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Checking whether user: " + username + " has permission : " + permission;
            }

            UserStoreManager userStoreManager = CarbonContext.getThreadLocalCarbonContext().getUserRealm()
                    .getUserStoreManager();
            if (userStoreManager instanceof AbstractUserStoreManager) {
                return ((AbstractUserStoreManager) userStoreManager);
            }

            String[] userRoles = userStoreManager.getRoleListOfUser(username);
            for (String userRole : userRoles) {
                if (applicationRoleName.equals(userRole)) {
                    return true;
                }
            }
        } catch (UserStoreException e) {
            throw new IdentityApplicationManagementException("Error while checking authorization for user: " +
                    username + " for application: " + templateName, e);
        }
        return false;
    }
*/
}
