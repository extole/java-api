package com.extole.client.rest.impl.campaign.incentive.reward.rule;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.incentive.reward.rule.CampaignRewardRuleEndpoints;
import com.extole.client.rest.campaign.incentive.reward.rule.ExpressionType;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleCreateRequest;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleExpression;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleResponse;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleRestException;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleUpdateRequest;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.RewardRule;
import com.extole.model.entity.campaign.Rewardee;
import com.extole.model.entity.campaign.RuleActionType;
import com.extole.model.entity.campaign.RuleDataMatcherType;
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
import com.extole.model.service.campaign.reward.rule.DataAttributeMatcherMissingException;
import com.extole.model.service.campaign.reward.rule.DataAttributeNameOutOfRangeException;
import com.extole.model.service.campaign.reward.rule.DataAttributeValueOutOfRangeException;
import com.extole.model.service.campaign.reward.rule.ExpressionInvalidException;
import com.extole.model.service.campaign.reward.rule.ExpressionLengthException;
import com.extole.model.service.campaign.reward.rule.ExpressionMissingException;
import com.extole.model.service.campaign.reward.rule.ExpressionTypeMissingException;
import com.extole.model.service.campaign.reward.rule.ExpressionTypeNotSupportedException;
import com.extole.model.service.campaign.reward.rule.IllegalRegexpInDataAttributeValueException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInMinCartValueException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInReferralsPerRewardException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardCountLimitException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardCountSinceDaysException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardCountSinceMonthException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardEveryXFriendActionsException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardValueLimitException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardValueSinceDaysException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardValueSinceMonthException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.reward.rule.RewardRuleBuilder;
import com.extole.model.service.campaign.reward.rule.RewardRuleNotFoundException;
import com.extole.model.service.campaign.reward.rule.RewardRuleServiceException;
import com.extole.model.service.campaign.reward.rule.RewardSlotInvalidNameException;
import com.extole.model.service.campaign.reward.rule.RewardSlotLengthOutOfRangeException;
import com.extole.model.service.campaign.reward.rule.RewardSlotsLengthException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;

@Provider
public class CampaignRewardRuleEndpointsImpl implements CampaignRewardRuleEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;

    @Autowired
    public CampaignRewardRuleEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignService campaignService, CampaignProvider campaignProvider) {
        this.authorizationProvider = authorizationProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
    }

    @Override
    public List<RewardRuleResponse> list(String accessToken, String campaignId)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        List<RewardRuleResponse> rewardRuleResponses = new ArrayList<>();
        for (RewardRule rewardRule : campaign.getRewardRules()) {
            rewardRuleResponses.add(rewardRuleToResponse(rewardRule));
        }
        return rewardRuleResponses;

    }

    @Override
    public RewardRuleResponse get(String accessToken, String campaignId, String rewardRuleId)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignProvider.getLatestCampaign(userAuthorization, Id.valueOf(campaignId));
            RewardRule rewardRule = campaign.getRewardRules().stream()
                .filter(incentiveRewardRule -> incentiveRewardRule.getId().getValue().equals(rewardRuleId)).findFirst()
                .orElseThrow(() -> new RewardRuleNotFoundException(
                    "Campaign '" + campaignId + "' has no reward rule with id '" + rewardRuleId + "'."));
            return rewardRuleToResponse(rewardRule);
        } catch (RewardRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleRestException.class)
                .withErrorCode(RewardRuleRestException.REWARD_RULE_NOT_FOUND).addParameter("campaign_id", campaignId)
                .addParameter("reward_rule_id", rewardRuleId).withCause(e).build();
        }
    }

    @Override
    public RewardRuleResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        RewardRuleCreateRequest rewardRule)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleValidationRestException,
        RewardSupplierRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder;
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            if (Strings.isNullOrEmpty(rewardRule.getRewardSupplierId())) {
                throw new RewardSupplierNotFoundException("reward supplier id cannot be null or empty: " +
                    rewardRule.getRewardSupplierId(),
                    Id.valueOf(Strings.nullToEmpty(rewardRule.getRewardSupplierId())));
            }

            RewardRuleBuilder rewardRuleBuilder =
                campaignBuilder.addRewardRule((Id.valueOf(rewardRule.getRewardSupplierId())));
            populateBuilderFromCreateRequest(rewardRule, rewardRuleBuilder);
            return rewardRuleToResponse(rewardRuleBuilder.save());
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
        } catch (IllegalValueInRewardEveryXFriendActionsException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_EVERY_X_FRIEND_ACTIONS)
                .addParameter("reward_every_x_friend_actions", rewardRule.getRewardEveryXFriendActions())
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardRule.getRewardSupplierId()).withCause(e).build();
        } catch (IllegalValueInReferralsPerRewardException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.ACTION_COUNT_INVALID)
                .addParameter("action_count", rewardRule.getReferralsPerReward()).withCause(e).build();
        } catch (IllegalValueInMinCartValueException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.MIN_CART_VALUE_INVALID)
                .addParameter("min_cart_value", rewardRule.getReferralsPerReward()).withCause(e).build();
        } catch (IllegalValueInRewardCountLimitException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_COUNT_LIMIT_INVALID)
                .addParameter("reward_count_limit", rewardRule.getRewardCountLimit()).withCause(e).build();
        } catch (IllegalValueInRewardCountSinceMonthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_COUNT_SINCE_MONTH_INVALID)
                .addParameter("reward_count_since_month", rewardRule.getRewardCountSinceMonth()).withCause(e).build();
        } catch (IllegalValueInRewardCountSinceDaysException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_COUNT_SINCE_DAYS_INVALID)
                .addParameter("reward_count_since_days", rewardRule.getRewardCountSinceDays()).withCause(e).build();
        } catch (IllegalValueInRewardValueLimitException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_VALUE_LIMIT_INVALID)
                .addParameter("reward_value_limit", rewardRule.getRewardValueLimit()).withCause(e).build();
        } catch (IllegalValueInRewardValueSinceMonthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_VALUE_SINCE_MONTH_INVALID)
                .addParameter("reward_value_since_month", rewardRule.getRewardValueSinceMonth()).withCause(e).build();
        } catch (IllegalValueInRewardValueSinceDaysException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_VALUE_SINCE_DAYS_INVALID)
                .addParameter("reward_value_since_days", rewardRule.getRewardValueSinceDays()).withCause(e).build();
        } catch (IllegalRegexpInDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_REGEXP_INVALID)
                .addParameter("data_attribute_value", rewardRule.getDataAttributeValue()).withCause(e).build();
        } catch (DataAttributeMatcherMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_VALUE_TYPE_REQUIRED)
                .withCause(e).build();
        } catch (DataAttributeNameOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .withCause(e).build();
        } catch (DataAttributeValueOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .withCause(e).build();
        } catch (RewardSlotLengthOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_SLOT_LENGTH_OUT_OF_RANGE)
                .addParameter("reward_slots", rewardRule.getRewardSlots())
                .withCause(e)
                .build();
        } catch (RewardSlotsLengthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_SLOTS_CONCATENATED_LENGTH_EXCEPTION)
                .addParameter("reward_slots", rewardRule.getRewardSlots())
                .withCause(e)
                .build();
        } catch (RewardSlotInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_SLOT_INVALID_NAME)
                .addParameter("reward_slots", rewardRule.getRewardSlots())
                .withCause(e)
                .build();
        } catch (ExpressionInvalidException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_VALUE_INVALID)
                .addParameter("expression_value", rewardRule.getExpression().getValue().getValue())
                .addParameter("expression_type", rewardRule.getExpression().getValue().getType())
                .withCause(e)
                .build();
        } catch (ExpressionTypeNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_INVALID_TYPE)
                .addParameter("expression_type", rewardRule.getExpression().getValue().getType())
                .withCause(e)
                .build();
        } catch (ExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_VALUE_INVALID_LENGTH)
                .addParameter("expression_value", rewardRule.getExpression().getValue().getValue())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (ExpressionMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_MISSING_VALUE)
                .withCause(e)
                .build();
        } catch (ExpressionTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_MISSING_TYPE)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder
                .newBuilder(RewardRuleValidationRestException.class)
                .withCause(e)
                .withErrorCode(RewardRuleValidationRestException.IDENTITY_KEY_INCOMPATIBLE_USAGE)
                .build();
        } catch (RewardRuleServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public RewardRuleResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String rewardRuleId,
        RewardRuleUpdateRequest rewardRule)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleRestException,
        RewardRuleValidationRestException, RewardSupplierRestException, BuildCampaignRestException,
        CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            RewardRuleBuilder rewardRuleBuilder =
                campaignBuilder.updateRewardRule(Id.valueOf(rewardRuleId));
            populateBuilderFromUpdateRequest(rewardRule, authorization, rewardRuleBuilder);
            return rewardRuleToResponse(rewardRuleBuilder.save());
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
        } catch (ExpressionMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_MISSING_VALUE)
                .withCause(e)
                .build();
        } catch (ExpressionTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_MISSING_TYPE)
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (RewardRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleRestException.class)
                .withErrorCode(RewardRuleRestException.REWARD_RULE_NOT_FOUND).addParameter("campaign_id", campaignId)
                .addParameter("reward_rule_id", rewardRuleId).withCause(e).build();
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardRule.getRewardSupplierId()).withCause(e).build();
        } catch (IllegalValueInReferralsPerRewardException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.ACTION_COUNT_INVALID)
                .addParameter("action_count", rewardRule.getReferralsPerReward()).withCause(e).build();
        } catch (IllegalValueInMinCartValueException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.MIN_CART_VALUE_INVALID)
                .addParameter("min_cart_value", rewardRule.getReferralsPerReward()).withCause(e).build();
        } catch (IllegalValueInRewardCountLimitException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_COUNT_LIMIT_INVALID)
                .addParameter("reward_count_limit", rewardRule.getRewardCountLimit()).withCause(e).build();
        } catch (IllegalValueInRewardCountSinceMonthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_COUNT_SINCE_MONTH_INVALID)
                .addParameter("reward_count_since_month", rewardRule.getRewardCountSinceMonth()).withCause(e).build();
        } catch (IllegalValueInRewardCountSinceDaysException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_COUNT_SINCE_DAYS_INVALID)
                .addParameter("reward_count_since_days", rewardRule.getRewardCountSinceDays()).withCause(e).build();
        } catch (IllegalValueInRewardValueLimitException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_VALUE_LIMIT_INVALID)
                .addParameter("reward_value_limit", rewardRule.getRewardValueLimit()).withCause(e).build();
        } catch (IllegalValueInRewardValueSinceMonthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_VALUE_SINCE_MONTH_INVALID)
                .addParameter("reward_value_since_month", rewardRule.getRewardValueSinceMonth()).withCause(e).build();
        } catch (IllegalValueInRewardValueSinceDaysException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_VALUE_SINCE_DAYS_INVALID)
                .addParameter("reward_value_since_days", rewardRule.getRewardValueSinceDays()).withCause(e).build();
        } catch (IllegalRegexpInDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_REGEXP_INVALID)
                .addParameter("data_attribute_value", rewardRule.getDataAttributeValue()).withCause(e).build();
        } catch (DataAttributeMatcherMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_VALUE_TYPE_REQUIRED)
                .withCause(e).build();
        } catch (DataAttributeNameOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .withCause(e).build();
        } catch (DataAttributeValueOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .withCause(e).build();
        } catch (RewardSlotLengthOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_SLOT_LENGTH_OUT_OF_RANGE)
                .addParameter("reward_slots", rewardRule.getRewardSlots())
                .withCause(e)
                .build();
        } catch (RewardSlotInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_SLOT_INVALID_NAME)
                .addParameter("reward_slots", rewardRule.getRewardSlots())
                .withCause(e)
                .build();
        } catch (RewardSlotsLengthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_SLOTS_CONCATENATED_LENGTH_EXCEPTION)
                .addParameter("reward_slots", rewardRule.getRewardSlots())
                .withCause(e)
                .build();
        } catch (ExpressionInvalidException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_VALUE_INVALID)
                .addParameter("expression_value", rewardRule.getExpression().getValue().getValue())
                .addParameter("expression_type", rewardRule.getExpression().getValue().getType())
                .withCause(e)
                .build();
        } catch (IllegalValueInRewardEveryXFriendActionsException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.REWARD_EVERY_X_FRIEND_ACTIONS)
                .addParameter("reward_every_x_friend_actions", rewardRule.getRewardEveryXFriendActions())
                .withCause(e)
                .build();
        } catch (ExpressionTypeNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_INVALID_TYPE)
                .addParameter("expression_type", rewardRule.getExpression().getValue().getType())
                .withCause(e)
                .build();
        } catch (ExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                .withErrorCode(RewardRuleValidationRestException.EXPRESSION_VALUE_INVALID_LENGTH)
                .addParameter("expression_value", rewardRule.getExpression().getValue().getValue())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder
                .newBuilder(RewardRuleValidationRestException.class)
                .withCause(e)
                .withErrorCode(RewardRuleValidationRestException.IDENTITY_KEY_INCOMPATIBLE_USAGE)
                .build();
        } catch (RewardRuleServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private void populateBuilderFromCreateRequest(RewardRuleCreateRequest rewardRule,
        RewardRuleBuilder rewardRuleBuilder) throws IllegalValueInReferralsPerRewardException,
        IllegalValueInRewardCountLimitException, IllegalValueInRewardCountSinceMonthException,
        IllegalValueInRewardCountSinceDaysException, IllegalValueInRewardValueLimitException,
        IllegalValueInRewardValueSinceMonthException, IllegalValueInRewardValueSinceDaysException,
        RewardRuleServiceException, IllegalValueInMinCartValueException, DataAttributeMatcherMissingException,
        IllegalRegexpInDataAttributeValueException, DataAttributeNameOutOfRangeException,
        DataAttributeValueOutOfRangeException, IllegalValueInRewardEveryXFriendActionsException {
        rewardRule.getRewardee()
            .ifPresent(rewardee -> rewardRuleBuilder.withRewardee(Rewardee.valueOf(rewardee.name())));
        rewardRule.getReferralsPerReward().ifPresent(rewardRuleBuilder::withReferralsPerReward);
        rewardRule.getRewardCountLimit().ifPresent(rewardRuleBuilder::withRewardCountLimit);
        rewardRule.getRewardCountSinceMonth().ifPresent(rewardRuleBuilder::withRewardCountSinceMonth);
        rewardRule.getRewardCountSinceDays().ifPresent(rewardRuleBuilder::withRewardCountSinceDays);
        rewardRule.getRewardValueLimit().ifPresent(rewardRuleBuilder::withRewardValueLimit);
        rewardRule.getRewardValueSinceMonth().ifPresent(rewardRuleBuilder::withRewardValueSinceMonth);
        rewardRule.getRewardValueSinceDays().ifPresent(rewardRuleBuilder::withRewardValueSinceDays);
        rewardRule.getRewardSlots().ifPresent(rewardRuleBuilder::withRewardSlots);
        rewardRule.getUniqueFriendRequired()
            .ifPresent(value -> rewardRuleBuilder.withUniqueFriendRequired(value.booleanValue()));
        rewardRule.isReferralLoopAllowed()
            .ifPresent(value -> rewardRuleBuilder.withReferralLoopAllowed(value.booleanValue()));
        rewardRule.isEmailRequired().ifPresent(value -> rewardRuleBuilder.withEmailRequired(value.booleanValue()));
        rewardRule.getMinCartValue().ifPresent(rewardRuleBuilder::withMinCartValue);
        rewardRule.getDataAttributeName()
            .ifPresent(dataAttributeName -> rewardRule.getDataAttributeValue().ifPresent(dataAttributeValue -> {
                RuleDataMatcherType matcherType = rewardRule.getDataAttributeMatcherType()
                    .map(dataAttributeMatcherType -> RuleDataMatcherType.valueOf(dataAttributeMatcherType.name()))
                    .orElse(null);
                rewardRuleBuilder.withDataAttribute(dataAttributeName, matcherType, dataAttributeValue);
            }));
        rewardRule.getRewardEveryXFriendActions().ifPresent(rewardRuleBuilder::withRewardEveryXFriendActions);
        rewardRule.getRuleActionType()
            .ifPresent(value -> rewardRuleBuilder.withRuleActionType(RuleActionType.valueOf(value.name())));
        rewardRule.getExpression()
            .ifPresent(value -> rewardRuleBuilder.withExpression(toInternalRewardRuleExpression(value)));
        rewardRule.getCountRewardsBasedOnPartnerUserId()
            .ifPresent(rewardRuleBuilder::withCountRewardsBasedOnPartnerUserId);
    }

    private void populateBuilderFromUpdateRequest(RewardRuleUpdateRequest rewardRule, Authorization userAuthorization,
        RewardRuleBuilder rewardRuleBuilder) throws RewardSupplierNotFoundException,
        IllegalValueInReferralsPerRewardException, IllegalValueInRewardCountLimitException,
        IllegalValueInRewardCountSinceMonthException, IllegalValueInRewardCountSinceDaysException,
        IllegalValueInRewardValueLimitException, IllegalValueInRewardValueSinceMonthException,
        IllegalValueInRewardValueSinceDaysException, RewardRuleServiceException,
        IllegalValueInMinCartValueException, IllegalValueInRewardEveryXFriendActionsException {
        rewardRule.getRewardee()
            .ifPresent(rewardee -> rewardRuleBuilder.withRewardee(Rewardee.valueOf(rewardee.name())));
        rewardRule.getRewardSupplierId().ifPresent(
            value -> rewardRuleBuilder.withRewardSupplierId(userAuthorization.getClientId(), Id.valueOf(value)));
        rewardRule.getReferralsPerReward().ifPresent(rewardRuleBuilder::withReferralsPerReward);
        rewardRule.getRewardCountLimit().ifPresent(rewardRuleBuilder::withRewardCountLimit);
        rewardRule.getRewardCountSinceMonth().ifPresent(rewardRuleBuilder::withRewardCountSinceMonth);
        rewardRule.getRewardCountSinceDays().ifPresent(rewardRuleBuilder::withRewardCountSinceDays);
        rewardRule.getRewardValueLimit().ifPresent(rewardRuleBuilder::withRewardValueLimit);
        rewardRule.getRewardValueSinceMonth().ifPresent(rewardRuleBuilder::withRewardValueSinceMonth);
        rewardRule.getRewardValueSinceDays().ifPresent(rewardRuleBuilder::withRewardValueSinceDays);
        rewardRule.getRewardSlots().ifPresent(rewardRuleBuilder::withRewardSlots);
        rewardRule.getUniqueFriendRequired()
            .ifPresent(value -> rewardRuleBuilder.withUniqueFriendRequired(value.booleanValue()));
        rewardRule.isReferralLoopAllowed()
            .ifPresent(value -> rewardRuleBuilder.withReferralLoopAllowed(value.booleanValue()));
        rewardRule.isEmailRequired().ifPresent(value -> rewardRuleBuilder.withEmailRequired(value.booleanValue()));
        rewardRule.getMinCartValue().ifPresent(rewardRuleBuilder::withMinCartValue);
        rewardRule.getDataAttributeName()
            .ifPresent(dataAttributeName -> rewardRule.getDataAttributeValue().ifPresent(dataAttributeValue -> {
                RuleDataMatcherType matcherType = rewardRule.getDataAttributeMatcherType()
                    .map(dataAttributeMatcherType -> RuleDataMatcherType.valueOf(dataAttributeMatcherType.name()))
                    .orElse(null);
                rewardRuleBuilder.withDataAttribute(dataAttributeName, matcherType, dataAttributeValue);
            }));
        rewardRule.getRewardEveryXFriendActions().ifPresent(rewardRuleBuilder::withRewardEveryXFriendActions);
        rewardRule.getRuleActionType()
            .ifPresent(value -> rewardRuleBuilder.withRuleActionType(RuleActionType.valueOf(value.name())));
        rewardRule.getExpression()
            .ifPresent(value -> rewardRuleBuilder.withExpression(toInternalRewardRuleExpression(value)));
        rewardRule.getCountRewardsBasedOnPartnerUserId()
            .ifPresent(rewardRuleBuilder::withCountRewardsBasedOnPartnerUserId);
    }

    @Override
    public RewardRuleResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String rewardRuleId)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardRule rewardRuleToDelete =
                campaignProvider
                    .getLatestCampaign(authorization, Id.valueOf(campaignId))
                    .getRewardRules().stream().filter(rule -> rule.getId().getValue().equals(rewardRuleId)).findFirst()
                    .orElseThrow(() -> new RewardRuleNotFoundException(rewardRuleId));
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            campaignBuilder.removeRewardRule(Id.valueOf(rewardRuleId)).save();

            return rewardRuleToResponse(rewardRuleToDelete);
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
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (RewardRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRuleRestException.class)
                .withErrorCode(RewardRuleRestException.REWARD_RULE_NOT_FOUND).addParameter("campaign_id", campaignId)
                .addParameter("reward_rule_id", rewardRuleId).withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException
            | CreativeArchiveJavascriptException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentFacetsNotFoundException e) {
            // TODO move the handling of these exceptions in the proper builder as part of ENG-19069
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private RewardRuleResponse rewardRuleToResponse(RewardRule rewardRule) {
        com.extole.client.rest.campaign.incentive.reward.rule.RuleDataMatcherType ruleDataAttribute = null;
        if (rewardRule.getDataAttributeMatcherType() != null) {
            ruleDataAttribute = com.extole.client.rest.campaign.incentive.reward.rule.RuleDataMatcherType.valueOf(
                rewardRule.getDataAttributeMatcherType().name());
        }
        return new RewardRuleResponse(rewardRule.getId().getValue(),
            com.extole.client.rest.campaign.incentive.reward.rule.Rewardee.valueOf(rewardRule.getRewardee().name()),
            rewardRule.getRewardSupplierId().getValue(), rewardRule.getReferralsPerReward(),
            rewardRule.getRewardCountLimit(), rewardRule.getRewardCountSinceMonth(),
            rewardRule.getRewardCountSinceDays(), rewardRule.getRewardValueLimit(),
            rewardRule.getRewardValueSinceMonth(), rewardRule.getRewardValueSinceDays(),
            Boolean.valueOf(rewardRule.isUniqueFriendRequired()),
            Boolean.valueOf(rewardRule.isReferralLoopAllowed()),
            rewardRule.getRewardSlots(),
            rewardRule.getMinCartValue(),
            com.extole.client.rest.campaign.incentive.RuleActionType
                .valueOf(rewardRule.getRuleActionType().name()),
            Boolean.valueOf(rewardRule.isEmailRequired()),
            rewardRule.getDataAttributeName(),
            rewardRule.getDataAttributeValue(),
            ruleDataAttribute,
            rewardRule.getExpression().map(rule -> new RewardRuleExpression(rule.getValue(),
                ExpressionType.valueOf(rule.getType().name()))).orElse(null),
            rewardRule.getRewardEveryXFriendActions(),
            Boolean.valueOf(rewardRule.isRewardCountingBasedOnPartnerUserId()));
    }

    private com.extole.model.entity.campaign.RewardRuleExpression toInternalRewardRuleExpression(
        RewardRuleExpression expression) throws ExpressionTypeMissingException {
        if (expression.getType() == null) {
            throw new ExpressionTypeMissingException("Missing expression type");
        }
        return new com.extole.model.entity.campaign.RewardRuleExpression(expression.getValue(),
            com.extole.model.entity.campaign.ExpressionType.valueOf(expression.getType().name()));
    }
}
