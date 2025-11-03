package com.extole.client.rest.impl.campaign.component;

import static com.extole.model.entity.campaign.CampaignComponent.ROOT_REFERENCE;

import java.util.List;
import java.util.Objects;

import com.google.common.io.ByteSource;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentAssetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentFacetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.client.rest.impl.campaign.component.asset.UploadedAssetId;
import com.extole.client.rest.impl.campaign.component.setting.SettingUploaderRegistry;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.CampaignComponentDescriptionLengthException;
import com.extole.model.service.campaign.component.CampaignComponentDisplayNameLengthException;
import com.extole.model.service.campaign.component.CampaignComponentIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.component.CampaignComponentIllegalCharacterInNameException;
import com.extole.model.service.campaign.component.CampaignComponentNameLengthException;
import com.extole.model.service.campaign.component.CampaignComponentRootRenameException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentSizeTooBigException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetDescriptionLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameLengthException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetBuilder;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;

@Component
public class CampaignComponentUploader {

    private final SettingUploaderRegistry settingUploaderRegistry;

    public CampaignComponentUploader(SettingUploaderRegistry settingUploaderRegistry) {
        this.settingUploaderRegistry = settingUploaderRegistry;
    }

    @SuppressWarnings("unchecked")
    public void uploadComponent(CampaignUploadContext context, CampaignComponentConfiguration component)
        throws CampaignComponentIllegalCharacterInNameException, CampaignComponentNameLengthException,
        CampaignComponentDescriptionLengthException, SettingNameLengthException, SettingInvalidNameException,
        VariableValueKeyLengthException, CampaignComponentValidationRestException,
        CampaignComponentAssetNameInvalidException, CampaignComponentAssetNameLengthException,
        CampaignComponentAssetDescriptionLengthException, CampaignComponentAssetFilenameInvalidException,
        CampaignComponentAssetFilenameLengthException, CampaignComponentAssetContentMissingException,
        CampaignComponentAssetContentSizeTooBigException, SettingTagLengthException,
        SettingDisplayNameLengthException, SettingIllegalCharacterInDisplayNameException,
        CampaignComponentRootRenameException, ComponentTypeNotFoundException,
        CampaignComponentIllegalCharacterInDisplayNameException, CampaignComponentDisplayNameLengthException {
        CampaignComponentBuilder campaignComponentBuilder = context.get(component);

        if (component.getDisplayName().isPresent()) {
            campaignComponentBuilder.withDisplayName(component.getDisplayName().get());
        }

        if (component.getName() != null) {
            campaignComponentBuilder.withName(component.getName());
        }

        if (component.getType().isPresent()) {
            campaignComponentBuilder.withTypes(List.of(component.getType().get()));
        }

        if (!component.getTypes().isEmpty()) {
            campaignComponentBuilder.withTypes(component.getTypes());
        }

        if (component.getDescription() != null) {
            campaignComponentBuilder.withDescription(component.getDescription());
        }

        if (component.getInstalledIntoSocket().isPresent()) {
            campaignComponentBuilder.withInstalledIntoSocket(component.getInstalledIntoSocket().get());
        }

        if (component.getInstall().isPresent()) {
            campaignComponentBuilder.withInstall(component.getInstall().get());
        }

        if (component.getTags() != null) {
            if (component.getTags().isEmpty()) {
                campaignComponentBuilder.clearTags();
            } else {
                campaignComponentBuilder.withTags(component.getTags());
            }
        }
        if (component.getSettings() != null) {
            for (CampaignComponentSettingConfiguration variable : component.getSettings().stream()
                .filter(variableRequest -> Objects.nonNull(variableRequest)).toList()) {
                settingUploaderRegistry.getUploader(variable.getType())
                    .orElse(settingUploaderRegistry.getDefaultUploader())
                    .upload(settingUploaderRegistry, context, component, variable);
            }
        }

        if (component.getUploadVersion().isPresent()) {
            campaignComponentBuilder.withUploadVersion(component.getUploadVersion().get());
        }

        campaignComponentBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : component.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder componentReferenceBuilder =
                campaignComponentBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            componentReferenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            componentReferenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }

        if (component.getFacets() != null) {
            for (CampaignComponentFacetConfiguration facet : component.getFacets()
                .stream()
                .filter(facet -> Objects.nonNull(facet))
                .toList()) {
                CampaignComponentFacetBuilder facetBuilder = context.get(component, facet);
                facetBuilder.withName(facet.getName());
                facetBuilder.withValue(facet.getValue());
            }
        }

        if (component.getAssets() != null) {
            for (CampaignComponentAssetConfiguration asset : component.getAssets().stream()
                .filter(asset -> Objects.nonNull(asset)).toList()) {
                CampaignComponentAssetBuilder assetBuilder = context.get(component, asset);

                if (asset.getName() != null) {
                    assetBuilder.withName(asset.getName());
                }

                if (asset.getTags() != null) {
                    assetBuilder.withTags(asset.getTags());
                }

                if (asset.getDescription().isPresent()) {
                    assetBuilder.withDescription(asset.getDescription().get());
                }

                if (asset.getFilename() != null) {
                    assetBuilder.withFilename(asset.getFilename());
                }

                UploadedAssetId uploadedAssetId;
                if (component.getName().equalsIgnoreCase(CampaignComponent.ROOT)) {
                    uploadedAssetId = new UploadedAssetId(ROOT_REFERENCE, asset.getName());
                } else if (!component.getComponentReferences().isEmpty()) {
                    String parentAbsoluteName = component.getComponentReferences().get(0).getAbsoluteName();
                    if (parentAbsoluteName.equals(ROOT_REFERENCE)) {
                        uploadedAssetId = new UploadedAssetId(ROOT_REFERENCE + component.getName(), asset.getName());
                    } else {
                        String componentAbsoluteName =
                            parentAbsoluteName + CampaignComponent.PATH_DELIMITER + component.getName();
                        uploadedAssetId = new UploadedAssetId(componentAbsoluteName, asset.getName());
                    }
                } else {
                    uploadedAssetId = new UploadedAssetId(ROOT_REFERENCE + component.getName(), asset.getName());
                }

                ByteSource binary = context.getAssets().get(uploadedAssetId);
                if (binary != null) {
                    assetBuilder.withContent(binary);
                }
            }
        }

    }

}
