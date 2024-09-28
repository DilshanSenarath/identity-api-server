/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
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

package org.wso2.carbon.identity.api.server.branding.preference.management.common.factory;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.branding.preference.management.core.ai.BrandingAIPreferenceManager;


/**
 * Factory Beans serve as a factory for creating other beans within the IOC container. This factory bean is used to
 * instantiate the AIBrandingPreferenceManager type of object inside the container.
 */
public class BrandingPreferenceAIMgtOSGiServiceFactory extends AbstractFactoryBean<BrandingAIPreferenceManager> {

    private BrandingAIPreferenceManager brandingAiPreferenceManager;

    @Override
    public Class<?> getObjectType() {

        return Object.class;
    }

    @Override
    protected BrandingAIPreferenceManager createInstance() throws Exception {

        if (this.brandingAiPreferenceManager == null) {
            BrandingAIPreferenceManager taskOperationService = (BrandingAIPreferenceManager) PrivilegedCarbonContext.
                    getThreadLocalCarbonContext().getOSGiService(BrandingAIPreferenceManager.class, null);

            if (taskOperationService != null) {
                this.brandingAiPreferenceManager = taskOperationService;
            } else {
                throw new Exception("Unable to retrieve ConfigurationManager service.");
            }
        }
        return this.brandingAiPreferenceManager;
    }
}
