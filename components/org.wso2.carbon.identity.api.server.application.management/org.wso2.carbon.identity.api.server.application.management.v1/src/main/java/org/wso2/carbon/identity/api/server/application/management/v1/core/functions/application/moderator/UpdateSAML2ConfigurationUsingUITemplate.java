package org.wso2.carbon.identity.api.server.application.management.v1.core.functions.application.moderator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.wso2.carbon.identity.api.server.application.management.v1.SAML2Configuration;
import org.wso2.carbon.identity.api.server.application.management.v1.SAML2ServiceProvider;
import org.wso2.carbon.identity.api.server.application.management.v1.SingleLogoutProfile;
import org.wso2.carbon.identity.api.server.application.management.v1.core.functions.UpdateFunction;
import org.wso2.carbon.identity.api.server.application.management.v1.core.functions.Utils;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;

/**
 * UpdateSAML2ConfigurationUsingUITemplate
 */
public class UpdateSAML2ConfigurationUsingUITemplate implements UpdateFunction<SAML2Configuration, JSONObject> {

    private final ServiceProvider serviceProvider;

    public UpdateSAML2ConfigurationUsingUITemplate(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void apply(SAML2Configuration saml2Configuration, JSONObject uiTemplate) {
        if (uiTemplate != null) {
            String uiTemplateCategory = uiTemplate.getString("category");
            JSONObject uiTemplateSAMLInboundConfiguration = null;

            if (uiTemplate.getJSONObject("payload") != null) {
                if (uiTemplate.getJSONObject("payload")
                        .getJSONObject("inboundProtocolConfiguration") != null) {
                    uiTemplateSAMLInboundConfiguration = uiTemplate.getJSONObject("payload")
                            .getJSONObject("inboundProtocolConfiguration").getJSONObject("saml");
                }
            }

            if (uiTemplateSAMLInboundConfiguration != null
                    && Utils.isValidToBeApplicationManagedByIDP(uiTemplateCategory)) {
                if (Utils.isApplicationManagedByIDP(serviceProvider.getSpProperties())) {
                    try {
                        SAML2Configuration templateSAML2Configuration = new ObjectMapper()
                                .readValue(uiTemplateSAMLInboundConfiguration.toString(), SAML2Configuration.class);

                        // metadata url and file update not supported.
                        saml2Configuration.setMetadataFile(null);
                        saml2Configuration.setMetadataURL(null);

                        updateSAML2Configuration(saml2Configuration.getManualConfiguration(),
                                templateSAML2Configuration.getManualConfiguration());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void updateSAML2Configuration(
            SAML2ServiceProvider applicationSAML2Configuration,
            SAML2ServiceProvider uiTemplateSAML2ManualConfiguration) {
        if (uiTemplateSAML2ManualConfiguration != null) {
            applicationSAML2Configuration.setAssertionConsumerUrls(
                    uiTemplateSAML2ManualConfiguration.getAssertionConsumerUrls());
            applicationSAML2Configuration.setDefaultAssertionConsumerUrl(
                    uiTemplateSAML2ManualConfiguration.getDefaultAssertionConsumerUrl());
            updateSingleLogoutProfile(applicationSAML2Configuration.getSingleLogoutProfile(),
                    uiTemplateSAML2ManualConfiguration.getSingleLogoutProfile());
        }
    }

    private void updateSingleLogoutProfile(
            SingleLogoutProfile applicationSingleLogoutProfile,
            SingleLogoutProfile uiTemplateSingleLogoutProfile
    ) {
        if (uiTemplateSingleLogoutProfile != null) {
            applicationSingleLogoutProfile.setLogoutRequestUrl(uiTemplateSingleLogoutProfile.getLogoutRequestUrl());
            applicationSingleLogoutProfile.setLogoutResponseUrl(uiTemplateSingleLogoutProfile.getLogoutResponseUrl());
        }
    }
}
