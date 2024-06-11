package org.wso2.carbon.identity.api.server.application.management.common.factory;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.extension.mgt.ExtensionManager;
import org.wso2.carbon.identity.extension.mgt.exception.ExtensionManagementException;

/**
 * Factory Beans serves as a factory for creating other beans within the IOC container. This factory bean is used to
 * instantiate the ApplicationManagementService type of object inside the container.
 */
public class ExtensionMgtOSGIServiceFactory extends AbstractFactoryBean<ExtensionManager> {
    private ExtensionManager extensionManager;

    @Override
    public Class<?> getObjectType() {

        return Object.class;
    }

    @Override
    protected ExtensionManager createInstance() throws Exception {

        if (this.extensionManager == null) {
            ExtensionManager extensionManager = (ExtensionManager) PrivilegedCarbonContext.
                    getThreadLocalCarbonContext().getOSGiService(ExtensionManager.class, null);
            if (extensionManager != null) {
                this.extensionManager = extensionManager;
            } else {
                throw new ExtensionManagementException("Unable to retrieve extensionManager service.");
            }
        }
        return this.extensionManager;
    }
}
