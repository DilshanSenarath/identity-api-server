package org.wso2.carbon.identity.api.server.application.management.v1.core.functions.application.moderator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.wso2.carbon.identity.api.server.application.management.v1.ApplicationModel;
import org.wso2.carbon.identity.api.server.application.management.v1.ApplicationPatchModel;
import org.wso2.carbon.identity.api.server.application.management.v1.core.functions.UpdateFunction;
import org.wso2.carbon.identity.api.server.application.management.v1.core.functions.Utils;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.common.model.ServiceProviderProperty;

import java.util.Arrays;

/**
 * UpdateApplicationPatchModelUsingUITemplate
 */
public class UpdateApplicationPatchModelUsingUITemplate implements UpdateFunction<ApplicationPatchModel, JSONObject> {

    private final ServiceProvider serviceProvider;

    public UpdateApplicationPatchModelUsingUITemplate(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void apply(ApplicationPatchModel applicationPatchModel, JSONObject uiTemplate) {
        if (uiTemplate != null) {
            String uiTemplateCategory = uiTemplate.getString("category");
            JSONObject uiTemplatePayload = uiTemplate.getJSONObject("payload");

            if (Utils.isValidToBeApplicationManagedByIDP(uiTemplateCategory)) {
                if (applicationPatchModel.getAdvancedConfigurations() != null) {
                    updateAllowIDPManagedApplicationProperties(
                            applicationPatchModel.getAdvancedConfigurations()
                                    .getAllowIDPManagedApplicationProperties());
                }

                if (Utils.isApplicationManagedByIDP(serviceProvider.getSpProperties())) {
                    try {
                        ApplicationModel templateApplication = new ObjectMapper()
                                .readValue(uiTemplatePayload.toString(), ApplicationModel.class);

                        updateApplicationPatchModel(applicationPatchModel, templateApplication);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }

                return;
            }
        }

        updateAllowIDPManagedApplicationProperties(false);
    }

    private void updateApplicationPatchModel(
            ApplicationPatchModel applicationPatchModel, ApplicationModel uiTemplateApplication) {
        applicationPatchModel.setTemplateId(uiTemplateApplication.getTemplateId());
    }

    private void updateAllowIDPManagedApplicationProperties(Boolean allowIDPManagedApplicationProperties) {
        if (allowIDPManagedApplicationProperties == null) {
            return;
        }

        ServiceProviderProperty[] filteredProperties = Arrays.stream(
                serviceProvider.getSpProperties()).filter(property -> property.getName()
                .equals("allowIDPManagedApplicationProperties")).toArray(ServiceProviderProperty[]::new);
        if (filteredProperties.length > 0) {
            filteredProperties[0].setValue(Boolean.toString(allowIDPManagedApplicationProperties));
        } else {
            ServiceProviderProperty property = new ServiceProviderProperty();
            property.setName("allowIDPManagedApplicationProperties");
            property.setDisplayName("Allow IDP Managed Application Properties");
            property.setValue(Boolean.toString(allowIDPManagedApplicationProperties));

            ServiceProviderProperty[] properties = serviceProvider.getSpProperties();
            ServiceProviderProperty[] newProperties = new ServiceProviderProperty[properties.length + 1];
            System.arraycopy(properties, 0, newProperties, 0, properties.length);

            newProperties[properties.length] = property;
            serviceProvider.setSpProperties(newProperties);
        }
    }
}
