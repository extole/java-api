package com.extole.client.rest.impl.campaign.incentive.quality.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.incentive.CampaignIncentiveRestException;
import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.client.rest.campaign.incentive.quality.rule.CampaignQualityRuleEndpoints;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleRequest;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleResponse;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleRestException;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleType;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.QualityRule;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.quality.rule.QualityRuleBuilder;
import com.extole.model.service.campaign.quality.rule.QualityRuleNotFoundException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceIllegalCharacterInKeyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceIllegalCharacterInValueException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidBooleanPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidCountryPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidDomainPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidExpressionTypeException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidIntegerPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidLongPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidRegexpPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidSubnetPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidTemporalUnitPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidTimeUnitPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceKeyLengthException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServicePropertyNotSupportedException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServicePropertyValueCountException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceValueLengthException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignQualityRuleEndpointsImpl implements CampaignQualityRuleEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;

    @Inject
    public CampaignQualityRuleEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignService campaignService,
        CampaignProvider campaignProvider) {
        this.authorizationProvider = authorizationProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
    }

    @Override
    public List<QualityRuleResponse> list(String accessToken, String campaignId)
        throws UserAuthorizationRestException, CampaignIncentiveRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignService.getPublishedOrDraftAnyStateCampaign(userAuthorization,
                Id.valueOf(campaignId));
            return campaign.getQualityRules().stream().map(this::toQualityRuleResponse).collect(Collectors.toList());
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        }
    }

    @Override
    public QualityRuleResponse get(String accessToken, String campaignId, String qualityRuleId)
        throws UserAuthorizationRestException, QualityRuleRestException, CampaignIncentiveRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignService.getPublishedOrDraftAnyStateCampaign(userAuthorization,
                Id.valueOf(campaignId));
            QualityRule qualityRule = campaign.getQualityRules().stream()
                .filter(incentiveQualityRule -> incentiveQualityRule.getId().equals(Id.valueOf(qualityRuleId)))
                .findFirst().orElseThrow(() -> RestExceptionBuilder.newBuilder(QualityRuleRestException.class)
                    .withErrorCode(QualityRuleRestException.QUALITY_RULE_NOT_FOUND)
                    .addParameter("quality_rule_id", qualityRuleId).build());
            return toQualityRuleResponse(qualityRule);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        }
    }

    @Override
    public QualityRuleResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String qualityRuleId,
        QualityRuleRequest request)
        throws UserAuthorizationRestException, CampaignRestException, QualityRuleRestException,
        QualityRuleValidationRestException, CampaignIncentiveRestException, BuildCampaignRestException,
        CampaignUpdateRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.editCampaign(userAuthorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            QualityRuleBuilder qualityRuleBuilder = campaignBuilder
                .updateQualityRule(Id.valueOf(qualityRuleId));

            if (request.getEnabled() != null) {
                qualityRuleBuilder.withEnabled(request.getEnabled().booleanValue());
            }
            if (request.getProperties() != null) {
                Map<String, List<String>> properties = request.getProperties();
                for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                    qualityRuleBuilder.setProperty(entry.getKey(), entry.getValue());
                }
            }
            if (request.getActionTypes() != null) {
                Set<com.extole.model.entity.campaign.RuleActionType> modelRuleActionTypes =
                    new HashSet<>();
                for (RuleActionType actionType : request.getActionTypes()) {
                    modelRuleActionTypes
                        .add(com.extole.model.entity.campaign.RuleActionType.valueOf(actionType.name()));
                }
                qualityRuleBuilder.withActionTypes(modelRuleActionTypes);
            }
            return toQualityRuleResponse(qualityRuleBuilder.save());
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
        } catch (QualityRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleRestException.class)
                .withErrorCode(QualityRuleRestException.QUALITY_RULE_NOT_FOUND).withCause(e)
                .addParameter("quality_rule_id", qualityRuleId).build();
        } catch (QualityRuleServiceKeyLengthException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_KEY_INVALID_LENGTH).withCause(e)
                .addParameter("key", e.getKey())
                .build();
        } catch (QualityRuleServiceIllegalCharacterInKeyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_KEY_INVALID_CHARACTER)
                .addParameter("key", e.getKey())
                .withCause(e)
                .build();
        } catch (QualityRuleServiceValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_LENGTH)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceIllegalCharacterInValueException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_CHARACTER)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidIntegerPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_NUMBER)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidBooleanPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_BOOLEAN)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidCountryPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_COUNTRY)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidLongPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_NUMBER)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidTimeUnitPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_TIME_UNIT)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServicePropertyNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_UNSUPPORTED)
                .addParameter("key", e.getKey())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidDomainPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_DOMAIN)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServicePropertyValueCountException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_INVALID_VALUE_COUNT)
                .addParameter("key", e.getKey())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidTemporalUnitPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_TEMPORAL_UNIT)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidRegexpPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_INVALID_REGEXP)
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidSubnetPropertyException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_INVALID_SUBNET)
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (QualityRuleServiceInvalidExpressionTypeException e) {
            throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_INVALID_EXPRESSION_TYPE)
                .addParameter("expression_type", e.getExpressionType())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    private QualityRuleResponse toQualityRuleResponse(QualityRule qualityRule) {
        Set<RuleActionType> restRuleActionTypes = new HashSet<>();
        for (com.extole.model.entity.campaign.RuleActionType actionType : qualityRule.getActionTypes()) {
            restRuleActionTypes.add(RuleActionType.valueOf(actionType.name()));
        }
        return new QualityRuleResponse(qualityRule.getId().getValue(), Boolean.valueOf(qualityRule.getEnabled()),
            QualityRuleType.valueOf(qualityRule.getRuleType().name()),
            restRuleActionTypes, qualityRule.getProperties());
    }

    @Override
    public List<QualityRuleResponse> disableAllQualityRules(String accessToken,
        String campaignId,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            return campaignBuilder
                .withDisabledQualityRules()
                .save()
                .getQualityRules().stream()
                .map(this::toQualityRuleResponse)
                .collect(Collectors.toList());
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
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException
            | CreativeArchiveJavascriptException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

}
