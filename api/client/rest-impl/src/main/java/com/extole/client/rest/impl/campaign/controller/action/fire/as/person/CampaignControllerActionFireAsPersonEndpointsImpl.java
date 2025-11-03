package com.extole.client.rest.impl.campaign.controller.action.fire.as.person;

import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE;

import java.time.ZoneOffset;
import java.util.List;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.controller.action.fire.as.person.BuiltCampaignControllerActionFireAsPersonResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonCreateRequest;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonEndpoints;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonResponse;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonUpdateRequest;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonJourney;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.BuiltCampaignControllerActionFireAsPersonResponseMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.client.rest.impl.campaign.controller.action.fire.as.person.request.FireAsPersonIdentificationRequestMapperRepository;
import com.extole.common.journey.JourneyName;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerActionFireAsPerson;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionFireAsPerson;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonDataNameLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonDataValueInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonDuplicateDataEntryNameException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonEventNameInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonEventNameLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonEventNameMissingException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIllegalCharacterInEventNameException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonLabelInvalidCharactersException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonLabelLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.DoublePersonIdentificationFireAsPersonActionException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationMissingException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationValueLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyFieldInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyFieldLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyMissingDataForReferralException;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyMissingJourneyNameException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.person.service.profile.referral.PersonReferralReason;

@Provider
public class CampaignControllerActionFireAsPersonEndpointsImpl
    implements CampaignControllerActionFireAsPersonEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionFireAsPersonResponseMapper responseMapper;
    private final BuiltCampaignControllerActionFireAsPersonResponseMapper builtResponseMapper;
    private final CampaignProvider campaignProvider;
    private final FireAsPersonIdentificationRequestMapperRepository fireAsPersonIdentificationRequestMapperRepository;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionFireAsPersonEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignProvider campaignProvider,
        CampaignControllerActionFireAsPersonResponseMapper responseMapper,
        BuiltCampaignControllerActionFireAsPersonResponseMapper builtResponseMapper,
        FireAsPersonIdentificationRequestMapperRepository fireAsPersonIdentificationRequestMapperRepository,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignService = campaignService;
        this.responseMapper = responseMapper;
        this.builtResponseMapper = builtResponseMapper;
        this.campaignProvider = campaignProvider;
        this.fireAsPersonIdentificationRequestMapperRepository = fireAsPersonIdentificationRequestMapperRepository;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CampaignControllerActionFireAsPersonResponse create(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionFireAsPersonCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionFireAsPersonValidationRestException,
        CampaignControllerActionFireAsPersonIdentificationValidationRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            CampaignControllerActionFireAsPersonBuilder actionBuilder =
                campaignBuilder.updateController(controller)
                    .addAction(CampaignControllerActionType.FIRE_AS_PERSON);

            request.getQuality().ifPresent(
                quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));

            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getAsPersonIdentification().ifPresent(asPersonIdentification -> {
                FireAsPersonIdentificationBuilder asPersonIdentificationBuilder =
                    actionBuilder.withAsPersonIdentification(FireAsPersonIdenticationType
                        .valueOf(asPersonIdentification.getPersonIdentificationType().name()));
                fireAsPersonIdentificationRequestMapperRepository
                    .getMapper(asPersonIdentification.getPersonIdentificationType())
                    .upload(asPersonIdentification, asPersonIdentificationBuilder);
            });
            request.getAsPersonJourney().ifPresent(asPersonJourney -> {
                if (asPersonJourney.isPresent()) {
                    addAsPersonJourney(actionBuilder, asPersonJourney.get());
                }
            });
            request.getData().ifPresent(data -> actionBuilder.withData(data));
            request.getEventName()
                .ifPresent(eventName -> eventName.ifDefined((value) -> actionBuilder.withEventName(value)));
            request.getLabels().ifPresent(labels -> actionBuilder.withLabels(labels));
            request.getPersonId().ifPresent(personId -> actionBuilder.withPersonId(personId));
            request.getExtraData().ifPresent(extraData -> actionBuilder.withExtraData(extraData));

            return responseMapper.toResponse(actionBuilder.save(), ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (FireAsPersonIdentificationMissingException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_IDENTIFICATION_MISSING)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyMissingJourneyNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.JOURNEY_NAME_MISSING)
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyMissingDataForReferralException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.JOURNEY_MISSING_REFERRAL_DATA)
                .addParameter("data_name", e.getMissingJourneyDataName())
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyFieldLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_JOURNEY_FIELD_OUT_OF_RANGE)
                .addParameter("field_name", e.getFieldName())
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyFieldInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_JOURNEY_FIELD_INVALID)
                .addParameter("field_name", e.getFieldName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonDataValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DATA_VALUE_INVALID)
                .addParameter("data_name", e.getDataName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonDataNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DATA_NAME_OUT_OF_RANGE)
                .addParameter("data_name", e.getDataName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonDuplicateDataEntryNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DUPLICATE_DATA_ENTRY_NAME)
                .addParameter("evaluated_data_entry_name", e.getDataName())
                .addParameter("first_data_entry_name_evaluatable", e.getFirstDataEntryNameEvaluatable())
                .addParameter("second_data_entry_name_evaluatable", e.getSecondDataEntryNameEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonEventNameMissingException
            | CampaignControllerActionFireAsPersonEventNameInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.MISSING_EVENT_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonEventNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonLabelLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.LABEL_OUT_OF_RANGE)
                .addParameter("label", e.getLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonLabelInvalidCharactersException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.LABEL_ILLEGAL_CHARACTERS)
                .addParameter("label", e.getLabel())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "action")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (DoublePersonIdentificationFireAsPersonActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DOUBLE_PERSON_IDENTIFICATION)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionFireAsPersonResponse update(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionFireAsPersonUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionFireAsPersonValidationRestException,
        CampaignControllerActionFireAsPersonIdentificationValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignControllerActionFireAsPerson action =
                campaignStepProvider.getFireAsPersonControllerAction(campaign, controllerId, actionId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            CampaignControllerActionFireAsPersonBuilder actionBuilder =
                campaignBuilder.updateController(controller)
                    .updateAction(action);

            request.getQuality().ifPresent(
                quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getAsPersonIdentification().ifPresent(asPersonIdentification -> {
                if (asPersonIdentification.isPresent()) {
                    FireAsPersonIdentificationBuilder asPersonIdentificationBuilder =
                        actionBuilder.withAsPersonIdentification(FireAsPersonIdenticationType
                            .valueOf(asPersonIdentification.get().getPersonIdentificationType().name()));
                    fireAsPersonIdentificationRequestMapperRepository
                        .getMapper(asPersonIdentification.get().getPersonIdentificationType())
                        .upload(asPersonIdentification.get(), asPersonIdentificationBuilder);
                } else {
                    actionBuilder.clearAsPersonIdentification();
                }
            });
            request.getAsPersonJourney().ifPresent(asPersonJourney -> {
                if (asPersonJourney.isPresent()) {
                    addAsPersonJourney(actionBuilder, asPersonJourney.get());
                } else {
                    actionBuilder.clearAsPersonJourney();
                }
            });
            request.getData().ifPresent(data -> actionBuilder.withData(data));
            request.getEventName()
                .ifPresent(eventName -> eventName.ifDefined((value) -> actionBuilder.withEventName(value)));
            request.getLabels().ifPresent(labels -> actionBuilder.withLabels(labels));
            request.getPersonId().ifPresent(personId -> actionBuilder.withPersonId(personId));
            request.getExtraData().ifPresent(extraData -> actionBuilder.withExtraData(extraData));

            return responseMapper.toResponse(actionBuilder.save(), ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (FireAsPersonIdentificationValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyFieldLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_JOURNEY_FIELD_OUT_OF_RANGE)
                .addParameter("field_name", e.getFieldName())
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyFieldInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_JOURNEY_FIELD_INVALID)
                .addParameter("field_name", e.getFieldName())
                .withCause(e)
                .build();
        } catch (FireAsPersonJourneyMissingDataForReferralException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.JOURNEY_MISSING_REFERRAL_DATA)
                .addParameter("data_name", e.getMissingJourneyDataName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonDataValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DATA_VALUE_INVALID)
                .addParameter("data_name", e.getDataName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonDataNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DATA_NAME_OUT_OF_RANGE)
                .addParameter("data_name", e.getDataName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonDuplicateDataEntryNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DUPLICATE_DATA_ENTRY_NAME)
                .addParameter("evaluated_data_entry_name", e.getDataName())
                .addParameter("first_data_entry_name_evaluatable", e.getFirstDataEntryNameEvaluatable())
                .addParameter("second_data_entry_name_evaluatable", e.getSecondDataEntryNameEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonEventNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonLabelLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.LABEL_OUT_OF_RANGE)
                .addParameter("label", e.getLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonLabelInvalidCharactersException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.LABEL_ILLEGAL_CHARACTERS)
                .addParameter("label", e.getLabel())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "action")
                .addParameter("referencing_entity", actionId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (DoublePersonIdentificationFireAsPersonActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.DOUBLE_PERSON_IDENTIFICATION)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationMissingException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_IDENTIFICATION_MISSING)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionFireAsPersonResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignControllerActionFireAsPerson action =
                campaignStepProvider.getFireAsPersonControllerAction(campaign, controllerId, actionId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            campaignBuilder.updateController(controller)
                .removeAction(action)
                .save();

            return responseMapper.toResponse(action, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException
            | InvalidComponentReferenceException | StepDataBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionFireAsPersonResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionFireAsPerson action =
            campaignStepProvider.getFireAsPersonControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    @Override
    public BuiltCampaignControllerActionFireAsPersonResponse getBuilt(String accessToken, String campaignId,
        String version, String controllerId, String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);

        BuiltCampaignControllerActionFireAsPerson action =
            campaignStepProvider.getFireAsPersonBuiltControllerAction(campaign, controllerId, actionId);

        return builtResponseMapper.toResponse(action, ZoneOffset.UTC);
    }

    private void addAsPersonJourney(CampaignControllerActionFireAsPersonBuilder actionBuilder,
        FireAsPersonJourney asPersonJourney)
        throws FireAsPersonJourneyFieldInvalidException, FireAsPersonJourneyFieldLengthException {
        FireAsPersonJourneyBuilder asPersonJourneyBuilder = actionBuilder.withAsPersonJourney();

        if (asPersonJourney.getAdvocateCode().isPresent()) {
            asPersonJourneyBuilder.withAdvocateCode(asPersonJourney.getAdvocateCode().get());
        }
        if (asPersonJourney.getAdvocatePartnerUserId().isPresent()) {
            asPersonJourneyBuilder.withAdvocatePartnerUserId(asPersonJourney.getAdvocatePartnerUserId().get());
        }
        if (asPersonJourney.getCampaignId().isPresent()) {
            asPersonJourneyBuilder.withCampaignId(asPersonJourney.getCampaignId().get());
        }
        if (asPersonJourney.getContainer().isPresent()) {
            asPersonJourneyBuilder.withContainer(asPersonJourney.getContainer().get());
        }
        if (asPersonJourney.getCouponCode().isPresent()) {
            asPersonJourneyBuilder.withCouponCode(asPersonJourney.getCouponCode().get());
        }
        if (asPersonJourney.getJourneyName().isPresent()) {
            asPersonJourneyBuilder.withJourneyName(JourneyName.valueOf(asPersonJourney.getJourneyName().get()));
        }
        if (asPersonJourney.getLabel().isPresent()) {
            asPersonJourneyBuilder.withLabel(asPersonJourney.getLabel().get());
        }
        if (asPersonJourney.getPromotableCode().isPresent()) {
            asPersonJourneyBuilder.withPromotableCode(asPersonJourney.getPromotableCode().get());
        }
        if (asPersonJourney.getReason().isPresent()) {
            asPersonJourneyBuilder.withReason(asPersonJourney.getReason().get());
        }
        if (asPersonJourney.getReferralReason().isPresent()) {
            asPersonJourneyBuilder
                .withReferralReason(PersonReferralReason.valueOf(asPersonJourney.getReferralReason().get().name()));
        }
        if (asPersonJourney.getShareableId().isPresent()) {
            asPersonJourneyBuilder.withShareableId(asPersonJourney.getShareableId().get());
        }
        if (asPersonJourney.getShareId().isPresent()) {
            asPersonJourneyBuilder.withShareId(asPersonJourney.getShareId().get());
        }
        if (asPersonJourney.getZone().isPresent()) {
            asPersonJourneyBuilder.withZone(asPersonJourney.getZone().get());
        }

        asPersonJourneyBuilder.done();
    }

    private CampaignBuilder getCampaignBuilder(String campaignId, Authorization authorization,
        String expectedCurrentVersion)
        throws CampaignRestException {
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }
        return campaignBuilder;
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
