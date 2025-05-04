package com.extole.client.rest.impl.person;

import java.math.BigDecimal;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PersonNetworkStatsEndpoints;
import com.extole.client.rest.person.PersonNetworkStatsResponse;
import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.FullPersonStatsService;
import com.extole.person.service.profile.PersonNetworkStats;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.journey.Container;

@Provider
public class PersonNetworkStatsEndpointsImpl implements PersonNetworkStatsEndpoints {

    private static final String ALL_CONTAINERS = "*";

    private final ClientAuthorizationProvider authorizationProvider;
    private final FullPersonStatsService fullPersonStatsService;

    @Autowired
    public PersonNetworkStatsEndpointsImpl(FullPersonStatsService fullPersonStatsService,
        ClientAuthorizationProvider authorizationProvider) {
        this.fullPersonStatsService = fullPersonStatsService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public PersonNetworkStatsResponse getNetworkStats(String accessToken, String personId, String container,
        Boolean excludeAnonymous) throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonNetworkStats stats;
            if (!ALL_CONTAINERS.equalsIgnoreCase(container)) {
                stats = fullPersonStatsService.getNetworkStats(authorization, Id.valueOf(personId),
                    !Strings.isNullOrEmpty(container) ? new Container(container) : Container.DEFAULT,
                    excludeAnonymous == null || excludeAnonymous.booleanValue());
            } else {
                stats = fullPersonStatsService.getNetworkStats(authorization, Id.valueOf(personId),
                    excludeAnonymous == null || excludeAnonymous.booleanValue());
            }
            return toPersonNetworkStatsResponse(stats);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId).withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    private PersonNetworkStatsResponse toPersonNetworkStatsResponse(PersonNetworkStats stats) {
        return new PersonNetworkStatsResponse(
            formatFaceValue(stats.getAov()),
            formatFaceValue(stats.getLtv()),
            stats.getActivities(),
            stats.getTransactions(),
            stats.getConversions());
    }

    private String formatFaceValue(BigDecimal faceValue) {
        return faceValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
