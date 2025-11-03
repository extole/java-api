package com.extole.client.rest.impl.flow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.flow.FlowEndpoints;
import com.extole.client.rest.flow.FlowQueryParams;
import com.extole.client.rest.flow.FlowRestException;
import com.extole.client.rest.flow.FlowStepResponse;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.flow.FlowFilter;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignVersionState;
import com.extole.model.service.campaign.program.CampaignProgramLabelNotFoundException;
import com.extole.model.shared.flow.FlowQueryBuilder;
import com.extole.model.shared.flow.FlowService;
import com.extole.model.shared.flow.MixedCampaignIdAndProgramLabelFiltersFlowQueryBuilderException;
import com.extole.model.shared.flow.MixedStepIncludeAndExcludeFiltersFlowQueryBuilderException;

@Provider
public class FlowEndpointsImpl implements FlowEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final FlowRestMapper flowRestMapper;
    private final FlowService flowService;

    @Inject
    public FlowEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        FlowRestMapper flowRestMapper,
        FlowService flowService) {
        this.authorizationProvider = authorizationProvider;
        this.flowRestMapper = flowRestMapper;
        this.flowService = flowService;
    }

    @Override
    public List<FlowStepResponse> getFlow(
        String accessToken,
        FlowQueryParams flowQueryParams)
        throws UserAuthorizationRestException, CampaignRestException, FlowRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            FlowQueryBuilder flowQueryBuilder = flowService.createFlowQueryBuilder(authorization);

            flowQueryParams.getFlowPaths().filter(flowPaths -> !flowPaths.isEmpty())
                .ifPresent(flowQueryBuilder::withFlowPathsToInclude);
            flowQueryParams.getFlowPathsToInclude().filter(flowPathsToInclude -> !flowPathsToInclude.isEmpty())
                .ifPresent(flowQueryBuilder::withFlowPathsToInclude);
            flowQueryParams.getFlowPathsToExclude().filter(stepsToInclude -> !stepsToInclude.isEmpty())
                .ifPresent(flowQueryBuilder::withFlowPathsToExclude);
            flowQueryParams.getCampaignId().filter(StringUtils::isNotBlank).ifPresent(campaignId -> flowQueryBuilder
                .withCampaignIds(Sets.newHashSet(Id.valueOf(StringUtils.trim(campaignId)))));
            flowQueryParams.getCampaignIds()
                .filter(campaignIds -> !campaignIds.isEmpty())
                .ifPresent(campaignIds -> flowQueryBuilder.withCampaignIds(campaignIds.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .map(Id::<Campaign>valueOf)
                    .collect(Collectors.toSet())));
            flowQueryParams.getProgramLabel().filter(StringUtils::isNotBlank)
                .ifPresent(programLabel -> flowQueryBuilder
                    .withProgramLabels(Sets.newHashSet(StringUtils.trim(programLabel))));
            flowQueryParams.getProgramLabels()
                .filter(programLabels -> !programLabels.isEmpty())
                .ifPresent(programLabels -> flowQueryBuilder.withProgramLabels(programLabels.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .collect(Collectors.toSet())));

            flowQueryParams.getStepsToInclude().filter(stepsToInclude -> !stepsToInclude.isEmpty())
                .ifPresent(flowQueryBuilder::withStepsToInclude);
            flowQueryParams.getTagsToInclude().filter(tagsToInclude -> !tagsToInclude.isEmpty())
                .ifPresent(flowQueryBuilder::withTagsToInclude);
            flowQueryParams.getMetricTagsToInclude().filter(metricTagsToInclude -> !metricTagsToInclude.isEmpty())
                .ifPresent(flowQueryBuilder::withMetricTagsToInclude);
            flowQueryParams.getStepsToExclude().filter(stepsToExclude -> !stepsToExclude.isEmpty())
                .ifPresent(flowQueryBuilder::withStepsToExclude);
            flowQueryParams.getTagsToExclude().filter(tagsToExclude -> !tagsToExclude.isEmpty())
                .ifPresent(flowQueryBuilder::withTagsToExclude);
            flowQueryParams.getMetricTagsToExclude().filter(metricTagsToExclude -> !metricTagsToExclude.isEmpty())
                .ifPresent(flowQueryBuilder::withMetricTagsToExclude);
            if (flowQueryParams.getCampaignVersionState().isPresent()) {
                if (StringUtils.isNotBlank(flowQueryParams.getCampaignVersionState().get())) {
                    CampaignVersionState campaignVersionState = Arrays.stream(CampaignVersionState.values())
                        .filter(state -> StringUtils.equalsIgnoreCase(state.name(),
                            flowQueryParams.getCampaignVersionState().get()))
                        .findFirst()
                        .orElseThrow(() -> RestExceptionBuilder.newBuilder(FlowRestException.class)
                            .withErrorCode(FlowRestException.CAMPAIGN_STATE_INVALID)
                            .addParameter("campaign_version_state", flowQueryParams.getCampaignVersionState())
                            .build());
                    flowQueryBuilder.withCampaignVersionState(campaignVersionState);
                }
            }
            if (flowQueryParams.getFlowFilter().isPresent()) {
                FlowFilter flowFilter =
                    FlowFilter.valueOf(flowQueryParams.getFlowFilter().get().name());
                flowQueryBuilder.withFlowFilter(flowFilter);
            }
            flowQueryParams.getSimpleTrigger().ifPresent(flowQueryBuilder::withSimpleTrigger);

            return flowQueryBuilder.list().stream()
                .map(flowStep -> flowRestMapper.toFlowStepResponse(flowStep))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (CampaignProgramLabelNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FlowRestException.class)
                .withErrorCode(FlowRestException.PROGRAM_LABEL_NOT_FOUND)
                .addParameter("program_label", e.getProgramLabel())
                .withCause(e)
                .build();
        } catch (MixedStepIncludeAndExcludeFiltersFlowQueryBuilderException e) {
            throw RestExceptionBuilder.newBuilder(FlowRestException.class)
                .withErrorCode(FlowRestException.MIXED_STEP_INCLUDE_AND_EXCLUDE_FILTERS)
                .withCause(e)
                .build();
        } catch (MixedCampaignIdAndProgramLabelFiltersFlowQueryBuilderException e) {
            throw RestExceptionBuilder.newBuilder(FlowRestException.class)
                .withErrorCode(FlowRestException.MIXED_CAMPAIGN_ID_AND_PROGRAM_LABEL_FILTERS)
                .withCause(e)
                .build();
        }
    }

}
