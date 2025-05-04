package com.extole.client.rest.impl.campaign.component;

import static java.util.stream.Collectors.toList;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentAssetResponse;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentResponse;
import com.extole.client.rest.campaign.built.component.BuiltComponentResponse;
import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.CampaignComponentResponse;
import com.extole.client.rest.campaign.component.ComponentOwner;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.anchor.AnchorDetailsResponse;
import com.extole.client.rest.campaign.component.anchor.SourceElementType;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetResponse;
import com.extole.client.rest.campaign.configuration.CampaignComponentAssetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentAsset;
import com.extole.model.entity.campaign.CampaignComponentReference;
import com.extole.model.entity.campaign.Component;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;
import com.extole.model.entity.campaign.built.BuiltCampaignComponentAsset;
import com.extole.model.entity.campaign.built.BuiltComponent;
import com.extole.model.service.campaign.component.anchor.AnchorDetails;

@org.springframework.stereotype.Component
public class CampaignComponentRestMapper {

    private final CampaignComponentSettingRestMapper componentSettingRestMapper;

    @Autowired
    public CampaignComponentRestMapper(CampaignComponentSettingRestMapper componentSettingRestMapper) {
        this.componentSettingRestMapper = componentSettingRestMapper;
    }

    public CampaignComponentResponse toComponentResponse(CampaignComponent campaignComponent, ZoneId timeZone) {
        return new CampaignComponentResponse(campaignComponent.getId().getValue(),
            campaignComponent.getComponentVersion(),
            campaignComponent.getName(),
            campaignComponent.getDisplayName(),
            campaignComponent.getType(),
            campaignComponent.getDescription().orElse(null),
            campaignComponent.getInstalledIntoSocket(),
            campaignComponent.getInstall(),
            campaignComponent.getTags(),
            campaignComponent.getSettings().stream()
                .map(setting -> componentSettingRestMapper.toSettingResponse(setting))
                .collect(toList()),
            campaignComponent.getAssets().stream().map(asset -> toAssetResponse(asset)).collect(toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            campaignComponent.getCreatedDate().atZone(timeZone),
            campaignComponent.getUpdatedDate().atZone(timeZone));
    }

    public CampaignComponentConfiguration toComponentConfiguration(CampaignComponent campaignComponent, ZoneId timeZone,
        Map<Id<CampaignComponent>, List<String>> absoluteNames) {
        return new CampaignComponentConfiguration(Omissible.of(Id.valueOf(campaignComponent.getId().getValue())),
            campaignComponent.getComponentVersion(),
            absoluteNames.get(campaignComponent.getId()),
            campaignComponent.getName(),
            campaignComponent.getDisplayName(),
            campaignComponent.getType(),
            campaignComponent.getDescription().orElse(null),
            campaignComponent.getInstalledIntoSocket(),
            campaignComponent.getInstall(),
            campaignComponent.getTags(),
            campaignComponent.getSettings().stream().map(setting -> componentSettingRestMapper
                .toSettingConfiguration(setting)).collect(toList()),
            campaignComponent.getAssets().stream().map(asset -> toAssetConfiguration(asset)).collect(toList()),
            campaignComponent.getCampaignComponentReferences().stream()
                .map(componentReference -> toComponentReferenceConfiguration(componentReference,
                    reference -> absoluteNames.get(reference.getComponentId()).get(0)))
                .collect(Collectors.toList()),
            campaignComponent.getCreatedDate().atZone(timeZone),
            campaignComponent.getUpdatedDate().atZone(timeZone));
    }

    public BuiltCampaignComponentResponse toBuiltComponentResponse(BuiltCampaignComponent campaignComponent,
        ZoneId timeZone,
        BuiltCampaign campaign) {
        return new BuiltCampaignComponentResponse(campaignComponent.getId().getValue(),
            campaignComponent.getComponentVersion(),
            campaignComponent.getName(),
            campaignComponent.getDisplayName(),
            campaignComponent.getType(),
            campaignComponent.getDescription().orElse(null),
            campaignComponent.getInstalledIntoSocket(),
            campaignComponent.getInstall(),
            campaignComponent.getTags(),
            campaignComponent.getSettings().stream().map(setting -> componentSettingRestMapper
                .toBuiltSettingResponse(campaign, campaignComponent.getId().getValue(), setting))
                .collect(toList()),
            campaignComponent.getAssets().stream().map(asset -> toBuiltAssetResponse(asset)).collect(toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            campaignComponent.getCreatedDate().atZone(timeZone),
            campaignComponent.getUpdatedDate().atZone(timeZone));
    }

    public ComponentResponse toComponentResponse(Component component, ZoneId timeZone) {
        CampaignComponent campaignComponent = component.getCampaignComponent();
        return new ComponentResponse(campaignComponent.getId().getValue(),
            component.getCampaign().getId().getValue(),
            component.getCampaign().getState().name(),
            ComponentOwner.valueOf(component.getOwner().name()),
            campaignComponent.getComponentVersion(),
            campaignComponent.getName(),
            campaignComponent.getDisplayName(),
            campaignComponent.getType(),
            campaignComponent.getDescription().orElse(null),
            campaignComponent.getInstalledIntoSocket(),
            campaignComponent.getTags(),
            campaignComponent.getCampaignComponentReferences().stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            campaignComponent.getSettings().stream()
                .map(setting -> componentSettingRestMapper.toSettingResponse(setting))
                .collect(toList()),
            campaignComponent.getCreatedDate().atZone(timeZone),
            campaignComponent.getUpdatedDate().atZone(timeZone));
    }

    public BuiltComponentResponse toBuiltComponentResponse(Component component,
        BuiltCampaignComponent builtCampaignComponent, ZoneId timeZone,
        BuiltCampaign campaign) {
        CampaignComponent campaignComponent = component.getCampaignComponent();
        return new BuiltComponentResponse(campaignComponent.getId().getValue(),
            component.getCampaign().getId().getValue(),
            component.getCampaign().getState().name(),
            ComponentOwner.valueOf(component.getOwner().name()),
            campaignComponent.getComponentVersion(),
            campaignComponent.getName(),
            campaignComponent.getDisplayName(),
            campaignComponent.getType(),
            campaignComponent.getDescription().orElse(null),
            campaignComponent.getInstalledIntoSocket(),
            campaignComponent.getTags(),
            builtCampaignComponent.getSettings().stream()
                .map(setting -> componentSettingRestMapper.toBuiltSettingResponse(campaign,
                    campaignComponent.getId().getValue(), setting))
                .collect(toList()),
            builtCampaignComponent
                .getAssets().stream().map(asset -> toBuiltAssetResponse(asset)).collect(toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            campaignComponent.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            campaignComponent.getCreatedDate().atZone(timeZone),
            campaignComponent.getUpdatedDate().atZone(timeZone));
    }

    public BuiltComponentResponse toBuiltComponentResponse(BuiltComponent builtComponent, ZoneId timeZone) {
        BuiltCampaignComponent campaignComponent = builtComponent.getCampaignComponent();
        BuiltCampaign campaign = builtComponent.getCampaign();

        List<BuiltCampaignComponentSettingResponse> settings = campaignComponent.getSettings().stream()
            .map(setting -> componentSettingRestMapper.toBuiltSettingResponse(campaign,
                campaignComponent.getId().getValue(), setting))
            .collect(Collectors.toUnmodifiableList());
        List<BuiltCampaignComponentAssetResponse> assets = campaignComponent.getAssets().stream()
            .map(asset -> toBuiltAssetResponse(asset))
            .collect(Collectors.toUnmodifiableList());
        List<Id<ComponentResponse>> componentIds = campaignComponent.getCampaignComponentReferences().stream()
            .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
            .collect(Collectors.toUnmodifiableList());
        List<ComponentReferenceResponse> componentReferences =
            campaignComponent.getCampaignComponentReferences().stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toUnmodifiableList());

        return new BuiltComponentResponse(campaignComponent.getId().getValue(),
            campaign.getId().getValue(),
            campaign.getState().name(),
            ComponentOwner.valueOf(builtComponent.getOwner().name()),
            campaignComponent.getComponentVersion(),
            campaignComponent.getName(),
            campaignComponent.getDisplayName(),
            campaignComponent.getType(),
            campaignComponent.getDescription().orElse(null),
            campaignComponent.getInstalledIntoSocket(),
            campaignComponent.getTags(),
            settings,
            assets,
            componentIds,
            componentReferences,
            campaignComponent.getCreatedDate().atZone(timeZone),
            campaignComponent.getUpdatedDate().atZone(timeZone));
    }

    public AnchorDetailsResponse toAnchorDetails(AnchorDetails anchorDetails) {
        return new AnchorDetailsResponse(anchorDetails.getSourceElementId(),
            SourceElementType.valueOf(anchorDetails.getSourceElementType().name()));
    }

    public CampaignComponentReferenceConfiguration toComponentReferenceConfiguration(
        CampaignComponentReference reference,
        Function<CampaignComponentReference, String> absoluteNameProvider) {
        return new CampaignComponentReferenceConfiguration(absoluteNameProvider.apply(reference), reference.getTags(),
            reference.getSocketNames());
    }

    public CampaignComponentAssetResponse toAssetResponse(CampaignComponentAsset asset) {
        return new CampaignComponentAssetResponse(asset.getId().getValue(),
            asset.getName(),
            asset.getFilename(),
            asset.getTags(),
            asset.getDescription());
    }

    public CampaignComponentAssetConfiguration toAssetConfiguration(CampaignComponentAsset asset) {
        return new CampaignComponentAssetConfiguration(Omissible.of(Id.valueOf(asset.getId().getValue())),
            asset.getName(),
            asset.getFilename(),
            asset.getTags(),
            asset.getDescription());
    }

    public BuiltCampaignComponentAssetResponse toBuiltAssetResponse(BuiltCampaignComponentAsset asset) {
        return new BuiltCampaignComponentAssetResponse(asset.getId().getValue(),
            asset.getName(),
            asset.getFilename(),
            asset.getTags(),
            asset.getDescription());
    }

}
