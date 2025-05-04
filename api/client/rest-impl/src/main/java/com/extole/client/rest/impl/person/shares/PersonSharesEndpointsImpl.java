package com.extole.client.rest.impl.person.shares;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.share.PersonShareDataResponse;
import com.extole.client.rest.person.share.PersonShareResponse;
import com.extole.client.rest.person.share.PersonShareRestException;
import com.extole.client.rest.person.share.PersonSharesEndpoints;
import com.extole.client.rest.person.share.PersonSharesListRequest;
import com.extole.client.rest.person.share.PersonSharesListRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignLabel;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.model.shared.campaign.built.BuiltCampaignCache;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.PersonShareQueryBuilder;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.Channel;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Provider
public class PersonSharesEndpointsImpl implements PersonSharesEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(PersonSharesEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final FullPersonService fullPersonService;
    private final ClientShareableService clientShareableService;
    private final BuiltCampaignCache builtCampaignCache;

    @Autowired
    public PersonSharesEndpointsImpl(PersonService personService,
        FullPersonService fullPersonService,
        ClientShareableService clientShareableService,
        BuiltCampaignCache builtCampaignCache,
        ClientAuthorizationProvider authorizationProvider) {
        this.personService = personService;
        this.fullPersonService = fullPersonService;
        this.clientShareableService = clientShareableService;
        this.builtCampaignCache = builtCampaignCache;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public PersonShareResponse get(String accessToken, String personId, String shareId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<PersonShare> personShare =
                personService.getPerson(userAuthorization, Id.valueOf(personId))
                    .getShares().stream()
                    .filter(share -> share.getId().getValue().equals(shareId))
                    .findFirst();

            if (personShare.isEmpty()) {
                personShare = fullPersonService.createShareQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withIds(List.of(Id.valueOf(shareId))).withLimit(1)
                    .list().stream()
                    .findFirst();
            }

            if (personShare.isPresent()) {
                return toPersonShareResponse(userAuthorization, personShare.get(), timeZone);
            } else {
                throw RestExceptionBuilder.newBuilder(PersonShareRestException.class)
                    .withErrorCode(PersonShareRestException.SHARE_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("share_id", shareId)
                    .build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<PersonShareResponse> list(String accessToken, String personId,
        PersonSharesListRequest personShareListRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonSharesListRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Multimap<String, Object> dataValues = parseDataValues(personShareListRequest.getDataValues());
            List<PartnerEventId> partnerIds = parsePartnerIds(personShareListRequest.getPartnerIds());
            PersonShareQueryBuilder builder =
                fullPersonService.createShareQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withPrograms(personShareListRequest.getPrograms())
                    .withCampaignIds(personShareListRequest.getCampaignIds().stream().map(Id::<CampaignHandle>valueOf)
                        .collect(Collectors.toList()))
                    .withPartnerIds(partnerIds)
                    .withDataKeys(personShareListRequest.getDataKeys())
                    .withDataValues(dataValues)
                    .withOffset(personShareListRequest.getOffset())
                    .withLimit(personShareListRequest.getLimit());
            List<PersonShareResponse> responses = Lists.newArrayList();
            for (PersonShare share : builder.list()) {
                responses.add(toPersonShareResponse(userAuthorization, share, timeZone));
            }
            return responses;
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private PersonShareResponse toPersonShareResponse(Authorization authorization, PersonShare share, ZoneId timeZone) {
        Optional<ClientShareable> shareable = Optional.empty();
        String shareableId = share.getShareableId().getValue();
        try {
            shareable = Optional.of(clientShareableService.get(authorization, Id.valueOf(shareableId)));
        } catch (ShareableNotFoundException e) {
            LOG.warn("Can not find shareable {} for share {}", share.getShareableId(), share.getId());
        }
        Optional<BuiltCampaign> campaign = builtCampaignCache.getCampaignLatestVersion(authorization.getClientId(),
            Id.valueOf(share.getCampaignId().getValue()));
        return PersonShareResponse.builder()
            .withId(share.getId().getValue())
            .withProgram(campaign.map(BuiltCampaign::getProgramLabel).map(BuiltCampaignLabel::getName).orElse(null))
            .withCampaignId(share.getCampaignId().getValue())
            .withShareableCode(shareable.map(ClientShareable::getCode).orElse(null))
            .withLink(shareable.map(ClientShareable::getLink).map(URI::toString).orElse(null))
            .withChannel(share.getChannel().map(Channel::getName).orElse(null))
            .withMessage(share.getMessage().orElse(null))
            .withPartnerId(share.getPartnerId()
                .map(partnerEventId -> new PartnerEventIdResponse(partnerEventId.getName(), partnerEventId.getValue()))
                .orElse(null))
            .withSubject(share.getSubject().orElse(null))
            .withData(share.getData().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> new PersonShareDataResponse(entry.getKey(), entry.getValue()))))
            .withCreatedDate(ZonedDateTime.ofInstant(share.getShareDate(), timeZone))
            .build();
    }

    private Multimap<String, Object> parseDataValues(List<String> dataValues) throws PersonSharesListRestException {
        Multimap<String, Object> result = ArrayListMultimap.create();
        for (String dataValue : dataValues) {
            int delimiterLocation = dataValue.indexOf(":");
            if (delimiterLocation > 0) {
                result.put(dataValue.substring(0, delimiterLocation), dataValue.substring(delimiterLocation + 1));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonSharesListRestException.class)
                    .withErrorCode(PersonSharesListRestException.INVALID_DATA_VALUE)
                    .addParameter("data_values", dataValue)
                    .build();
            }
        }

        return result;
    }

    private List<PartnerEventId> parsePartnerIds(List<String> partnerIds) throws PersonSharesListRestException {
        List<PartnerEventId> result = Lists.newArrayList();
        for (String partnerId : partnerIds) {
            int delimiterLocation = partnerId.indexOf(":");
            if (delimiterLocation > 0) {
                result.add(PartnerEventId.of(partnerId.substring(0, delimiterLocation),
                    partnerId.substring(delimiterLocation + 1)));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonSharesListRestException.class)
                    .withErrorCode(PersonSharesListRestException.INVALID_PARTNER_IDS)
                    .addParameter("partner_ids", partnerIds)
                    .build();
            }
        }
        return result;
    }
}
