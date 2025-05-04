package com.extole.reporting.rest.impl.audience.stats;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.audience.membership.service.AudienceMembershipService;
import com.extole.audience.membership.service.AudienceStats;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.audience.Audience;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.model.service.audience.AudienceService;
import com.extole.reporting.rest.audience.member.AudienceMemberRestException;
import com.extole.reporting.rest.audience.stats.AudienceStatsEndpoints;
import com.extole.reporting.rest.audience.stats.AudienceStatsResponse;

@Provider
public class AudienceStatsEndpointsImpl implements AudienceStatsEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceService audienceService;
    private final AudienceMembershipService audienceMembershipService;

    @Autowired
    public AudienceStatsEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AudienceService audienceService,
        AudienceMembershipService audienceMembershipService) {
        this.authorizationProvider = authorizationProvider;
        this.audienceService = audienceService;
        this.audienceMembershipService = audienceMembershipService;
    }

    @Override
    public AudienceStatsResponse getStats(String accessToken,
        Id<com.extole.api.audience.Audience> audienceId)
        throws UserAuthorizationRestException, AudienceMemberRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));
            AudienceStats audienceStats = audienceMembershipService.getStats(
                authorizationProvider.getClientAuthorization(accessToken), audience.getId());
            return new AudienceStatsResponse(audienceStats.getActiveMembersCount());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberRestException.class)
                .withErrorCode(AudienceMemberRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }
}
