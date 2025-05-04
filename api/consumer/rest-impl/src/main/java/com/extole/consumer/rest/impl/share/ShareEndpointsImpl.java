package com.extole.consumer.rest.impl.share;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.PartnerEventIdResponse;
import com.extole.consumer.rest.share.PublicShareResponse;
import com.extole.consumer.rest.share.ShareEndpoints;
import com.extole.consumer.rest.share.ShareRestException;
import com.extole.consumer.rest.share.ShareUnconstrainedRestException;
import com.extole.id.Id;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonPublicShare;
import com.extole.person.service.share.PersonShareNotFoundException;
import com.extole.person.service.share.PersonShareService;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Provider
public class ShareEndpointsImpl implements ShareEndpoints {

    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";

    private final PersonShareService personShareService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ClientShareableService clientShareableService;
    private final HttpServletRequest servletRequest;

    @Autowired
    public ShareEndpointsImpl(PersonShareService personShareService,
        ConsumerRequestContextService consumerRequestContextService,
        ClientShareableService clientShareableService,
        @Context HttpServletRequest servletRequest) {
        this.personShareService = personShareService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.clientShareableService = clientShareableService;
        this.servletRequest = servletRequest;
    }

    @Override
    public PublicShareResponse getShare(String accessToken, String shareId)
        throws AuthorizationRestException, ShareRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();

        try {
            PersonPublicShare publicShare =
                personShareService.getPublicShare(authorization.getClientId(), Id.valueOf(shareId));
            return mapShareToResponse(authorization, publicShare);
        } catch (PersonShareNotFoundException | ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareRestException.class)
                .withErrorCode(ShareRestException.SHARE_NOT_FOUND)
                .addParameter("share_id", shareId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<PublicShareResponse> getShares(String accessToken, @Nullable String partnerShareId,
        @Nullable String partnerId)
        throws AuthorizationRestException, ShareUnconstrainedRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();

        if (partnerShareId != null) {
            PartnerEventId partnerEventId = PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId);

            Optional<PersonPublicShare> shareByPartnerId = getPublicShareByPartnerId(authorization, partnerEventId);
            try {
                if (shareByPartnerId.isPresent()) {
                    return Collections.singletonList(mapShareToResponse(authorization, shareByPartnerId.get()));
                } else {
                    return Collections.emptyList();
                }
            } catch (ShareableNotFoundException e) {
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
            Optional<PersonPublicShare> shareByPartnerId = getPublicShareByPartnerId(authorization, partnerEventId);

            try {
                if (shareByPartnerId.isPresent()) {
                    return Collections.singletonList(mapShareToResponse(authorization, shareByPartnerId.get()));
                } else {
                    return Collections.emptyList();
                }
            } catch (ShareableNotFoundException e) {
                return Collections.emptyList();
            }
        }

        throw RestExceptionBuilder.newBuilder(ShareUnconstrainedRestException.class)
            .withErrorCode(ShareUnconstrainedRestException.PARTNER_ID_MISSING)
            .build();
    }

    private Optional<PersonPublicShare> getPublicShareByPartnerId(Authorization authorization,
        PartnerEventId partnerEventId) {
        try {
            return Optional.of(personShareService.getPublicShare(authorization.getClientId(), partnerEventId));
        } catch (PersonShareNotFoundException e) {
            return Optional.empty();
        }
    }

    private PublicShareResponse mapShareToResponse(Authorization authorization, PersonPublicShare publicShare)
        throws ShareableNotFoundException {
        ClientShareable shareable =
            clientShareableService.get(authorization, Id.valueOf(publicShare.getShareableId().getValue()));
        URI shareableLink = shareable.getLink();
        String shareChannel = publicShare.getChannel().map(channel -> channel.getName()).orElse(null);
        PartnerEventIdResponse sharePartnerId = publicShare.getPartnerId()
            .map(partnerId -> new PartnerEventIdResponse(partnerId.getName(), partnerId.getValue())).orElse(null);

        return new PublicShareResponse(
            publicShare.getId().getValue(),
            publicShare.getCampaignId().getValue(),
            shareChannel,
            publicShare.getShareDate().toString(),
            shareableLink.toString(),
            sharePartnerId,
            shareable.getId().getValue());
    }

}
