package com.extole.client.rest.impl.campaign.summary;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.CampaignLockType;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.client.rest.campaign.summary.CampaignSummaryEndpoints;
import com.extole.client.rest.campaign.summary.CampaignSummaryListQueryParams;
import com.extole.client.rest.campaign.summary.CampaignSummaryResponse;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.label.CampaignLabelRestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignSummary;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignSummaryQueryBuilder;
import com.extole.model.service.campaign.CampaignSummaryService;
import com.extole.model.service.campaign.CampaignVersionState;

@Provider
public class CampaignSummaryEndpointsImpl implements CampaignSummaryEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignLabelRestMapper campaignLabelRestMapper;
    private final CampaignSummaryService campaignSummaryService;

    @Inject
    public CampaignSummaryEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignSummaryService campaignSummaryService,
        CampaignLabelRestMapper campaignLabelRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.campaignSummaryService = campaignSummaryService;
        this.campaignLabelRestMapper = campaignLabelRestMapper;
    }

    @Override
    public List<CampaignSummaryResponse> getCampaignSummaries(String accessToken,
        CampaignSummaryListQueryParams queryParams, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            CampaignVersionState state =
                campaignProvider.getCampaignVersionState(queryParams.getVersion().orElse(null));
            CampaignSummaryQueryBuilder campaignSummaryQueryBuilder;
            campaignSummaryQueryBuilder = campaignSummaryService.createCampaignSummaryQueryBuilder(userAuthorization);

            campaignSummaryQueryBuilder.withCampaignVersionState(state);

            if (queryParams.getProgramType().isPresent()
                && !Strings.isNullOrEmpty(queryParams.getProgramType().get())) {
                campaignSummaryQueryBuilder.withProgramType(queryParams.getProgramType().get());
            }
            if (queryParams.getTags() != null && !queryParams.getTags().isEmpty()) {
                campaignSummaryQueryBuilder.withTags(queryParams.getTags());
            }
            if (queryParams.getLabels() != null && !queryParams.getLabels().isEmpty()) {
                campaignSummaryQueryBuilder.withLabels(queryParams.getLabels());
            }
            if (queryParams.getProgramLabel().isPresent()) {
                campaignSummaryQueryBuilder.withProgramLabel(queryParams.getProgramLabel().get());
            }
            if (queryParams.getStates() != null && !queryParams.getStates().isEmpty()) {
                campaignSummaryQueryBuilder.withStates(queryParams.getStates().stream()
                    .map(campaignState -> com.extole.model.entity.campaign.CampaignState.valueOf(campaignState.name()))
                    .collect(Collectors.toSet()));
            }
            if (queryParams.getIncludeArchived().isPresent()
                && Boolean.TRUE.equals(queryParams.getIncludeArchived().get())) {
                campaignSummaryQueryBuilder.includeArchived();
            }
            return campaignSummaryQueryBuilder
                .list()
                .stream()
                .map(campaignSummaryMappingFunction(timeZone))
                .collect(Collectors.toUnmodifiableList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public CampaignSummaryResponse getCampaignSummary(String accessToken, String campaignIdValue, String version,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Id<Campaign> campaignId = Id.valueOf(campaignIdValue);
        Optional<CampaignVersionState> versionState = campaignProvider.parseState(version);
        CampaignSummary campaignSummary;
        try {
            if (versionState.isPresent()) {
                campaignSummary =
                    campaignSummaryService.getCampaignSummaryByVersionStateIncludeArchived(authorization, campaignId,
                        versionState.get());
            } else {
                campaignSummary =
                    campaignSummaryService.getCampaignSummaryByVersionIncludeArchived(authorization, campaignId,
                        campaignProvider.getCampaignVersion(campaignId, version));
            }
        } catch (CampaignNotFoundException e) {
            throw newInvalidCampaignRestException(campaignIdValue, e);
        }

        return campaignSummaryMappingFunction(timeZone).apply(campaignSummary);
    }

    private Function<CampaignSummary, CampaignSummaryResponse> campaignSummaryMappingFunction(ZoneId timeZone) {

        return campaign -> {
            List<CampaignLabelResponse> labels = campaign.getLabels().stream()
                .map(label -> campaignLabelRestMapper.toCampaignLabelResponse(label, ZoneOffset.UTC))
                .sorted(Comparator.comparing(CampaignLabelResponse::getName))
                .collect(Collectors.toList());

            ZonedDateTime updatedDate = campaign.getUpdatedDate().atZone(timeZone);

            return new CampaignSummaryResponse(
                campaign.getId().getValue(),
                campaign.getName(),
                campaign.getDescription(),
                updatedDate,
                campaign.getLastPublishedDate().map(date -> date.atZone(timeZone)),
                campaign.getStartDate().map(date -> date.atZone(timeZone)),
                campaign.getStopDate().map(date -> date.atZone(timeZone)),
                campaign.getPausedAt().map(date -> date.atZone(timeZone)),
                campaign.getEndedAt().map(date -> date.atZone(timeZone)),
                !campaign.getIsDraft().booleanValue(),
                CampaignState.valueOf(campaign.getState().toString()),
                labels,
                campaign.getProgramLabel().getName(),
                campaign.getProgramType(),
                campaign.getThemeName(),
                campaign.getTags(),
                campaign.getLocks().stream()
                    .map(item -> CampaignLockType.valueOf(item.name())).collect(
                        Collectors.toList()),
                campaign.getVariants(),
                CampaignType.valueOf(campaign.getCampaignType() != null ? campaign.getCampaignType().name()
                    : CampaignType.MARKETING.name()));
        };
    }

    private CampaignRestException newInvalidCampaignRestException(String campaignId,
        Throwable cause) {
        RestExceptionBuilder<CampaignRestException> exceptionBuilder =
            RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(cause);
        return exceptionBuilder.build();
    }

}
