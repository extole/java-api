package com.extole.client.rest.impl.share;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.impl.person.PersonShareRestMapper;
import com.extole.client.rest.person.v4.PersonShareV4Response;
import com.extole.client.rest.share.ClientShareEndpoints;
import com.extole.client.rest.share.ClientShareRestException;
import com.extole.client.rest.share.ClientShareUnconstrainedRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.share.PersonShareNotFoundException;
import com.extole.person.service.share.PersonShareService;

@Provider
public class ClientShareEndpointsImpl implements ClientShareEndpoints {

    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonShareService personShareService;
    private final PersonShareRestMapper personShareRestMapper;

    @Autowired
    public ClientShareEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonShareService personShareService,
        PersonShareRestMapper personShareRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.personShareService = personShareService;
        this.personShareRestMapper = personShareRestMapper;
    }

    @Override
    public PersonShareV4Response getShare(String accessToken, String shareId, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientShareRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        PersonShare share;
        try {
            share = personShareService.getShare(authorization, Id.valueOf(shareId));
        } catch (PersonShareNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareRestException.class)
                .withErrorCode(ClientShareRestException.SHARE_NOT_FOUND)
                .addParameter("share_id", shareId)
                .withCause(e)
                .build();
        }

        return personShareRestMapper.toPersonShareResponse(authorization, share, timeZone);
    }

    @Override
    public List<PersonShareV4Response> getShares(String accessToken, @Nullable String partnerShareId,
        @Nullable String partnerId, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientShareUnconstrainedRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (partnerShareId != null) {
            PartnerEventId partnerEventId = PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId);

            Optional<PersonShare> shareByPartnerShareId = getShareByPartnerId(authorization, partnerEventId);
            if (shareByPartnerShareId.isPresent()) {
                return Collections.singletonList(
                    personShareRestMapper.toPersonShareResponse(authorization, shareByPartnerShareId.get(), timeZone));
            } else {
                return Collections.emptyList();
            }
        }

        if (partnerId != null) {
            String partnerEventIdName = StringUtils.substringBefore(partnerId, ":");
            String partnerEventIdValue = StringUtils.substringAfter(partnerId, ":");
            if (StringUtils.isEmpty(partnerEventIdName) || StringUtils.isEmpty(partnerEventIdValue)) {
                return Collections.emptyList();
            }

            PartnerEventId partnerEventId = PartnerEventId.of(partnerEventIdName, partnerEventIdValue);

            Optional<PersonShare> shareByPartnerId = getShareByPartnerId(authorization, partnerEventId);
            if (shareByPartnerId.isPresent()) {
                return Collections.singletonList(
                    personShareRestMapper.toPersonShareResponse(authorization, shareByPartnerId.get(), timeZone));
            } else {
                return Collections.emptyList();
            }
        }

        throw RestExceptionBuilder.newBuilder(ClientShareUnconstrainedRestException.class)
            .withErrorCode(ClientShareUnconstrainedRestException.PARTNER_ID_MISSING)
            .build();
    }

    private Optional<PersonShare> getShareByPartnerId(Authorization authorization, PartnerEventId partnerShareId) {
        try {
            return Optional.of(personShareService.getShare(authorization, partnerShareId));
        } catch (PersonShareNotFoundException e) {
            return Optional.empty();
        }
    }

}
