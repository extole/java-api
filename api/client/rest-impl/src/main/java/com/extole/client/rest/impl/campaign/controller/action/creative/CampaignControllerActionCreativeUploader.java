package com.extole.client.rest.impl.campaign.controller.action.creative;

import java.time.ZoneId;
import java.util.Map;

import com.google.common.io.ByteSource;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreativeConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeValidationRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.Classification;
import com.extole.model.entity.campaign.CreativeArchiveApiVersion;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveIncompatibleApiVersionException;

@Component
class CampaignControllerActionCreativeUploader
    implements CampaignControllerActionUploader<CampaignControllerActionCreativeConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionCreativeConfiguration action, ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException,
        CreativeArchiveRestException {
        CampaignControllerActionCreativeBuilder actionBuilder = context.get(step, action);

        if (action.getQuality() != null) {
            actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
        }
        if (action.getThemeVersion() != null) {
            actionBuilder.withThemeVersion(action.getThemeVersion());
        }
        if (action.getClassification() != null) {
            actionBuilder.withClassification(Classification.valueOf(action.getClassification().name()));
        }
        if (action.getApiVersion() != null) {
            actionBuilder
                .withCreativeArchiveApiVersion(CreativeArchiveApiVersion.fromVersion(action.getApiVersion()));
        }
        action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
        actionBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : action.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                actionBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }

        addCreativeToCreativeActionIfNeeded(context.getCreatives(), actionBuilder, action);
    }

    private static CampaignControllerActionCreativeBuilder addCreativeToCreativeActionIfNeeded(
        Map<String, ByteSource> creatives,
        CampaignControllerActionCreativeBuilder actionCreativeBuilder,
        CampaignControllerActionCreativeConfiguration creativeAction)
        throws CampaignControllerActionCreativeValidationRestException, CreativeArchiveRestException {

        if (creativeAction.getCreativeArchiveId().isPresent() &&
            creatives.containsKey(creativeAction.getCreativeArchiveId().get())) {
            ByteSource creativeArchiveByteSource = creatives.get(creativeAction.getCreativeArchiveId().get());
            try {
                actionCreativeBuilder.addCreativeArchive().withData(creativeArchiveByteSource);
            } catch (CreativeArchiveIncompatibleApiVersionException e) {
                throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                    .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_INCOMPATIBLE_API_VERSION)
                    .addParameter("archive_id", e.getArchiveId())
                    .addParameter("api_version", e.getApiVersion())
                    .withCause(e).build();
            } catch (CreativeArchiveBuilderException e) {
                throw RestExceptionBuilder.newBuilder(CampaignControllerActionCreativeValidationRestException.class)
                    .withErrorCode(CampaignControllerActionCreativeValidationRestException.INVALID_ARCHIVE)
                    .addParameter("archive_id", creativeAction.getCreativeArchiveId().get())
                    .withCause(e)
                    .build();
            }
        }
        return actionCreativeBuilder;
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.CREATIVE;
    }

}
