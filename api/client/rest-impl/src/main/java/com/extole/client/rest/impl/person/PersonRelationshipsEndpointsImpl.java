package com.extole.client.rest.impl.person;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PersonReferralRole;
import com.extole.client.rest.person.PersonRelationshipDataResponse;
import com.extole.client.rest.person.PersonRelationshipResponse;
import com.extole.client.rest.person.PersonRelationshipsEndpoints;
import com.extole.client.rest.person.PersonRelationshipsListRequest;
import com.extole.client.rest.person.PersonRelationshipsListRestException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonRelationshipQueryBuilder;
import com.extole.person.service.profile.journey.Container;
import com.extole.person.service.profile.referral.PersonReferral;

@Provider
public class PersonRelationshipsEndpointsImpl implements PersonRelationshipsEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final FullPersonService fullPersonService;

    @Autowired
    public PersonRelationshipsEndpointsImpl(
        FullPersonService fullPersonService,
        ClientAuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
        this.fullPersonService = fullPersonService;
    }

    @Override
    public List<PersonRelationshipResponse> list(String accessToken, String personId,
        PersonRelationshipsListRequest listRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException,
        PersonRelationshipsListRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Multimap<String, Object> dataValues = parseDataValues(listRequest.getDataValues());
            PersonRelationshipQueryBuilder builder = fullPersonService.createRelationshipQueryBuilder(
                userAuthorization, Id.valueOf(personId))
                .withMyRoles(listRequest.getMyRoles().stream()
                    .map(PersonReferralRole::name)
                    .map(PersonReferral.Side::valueOf)
                    .collect(Collectors.toList()))
                .withContainers(listRequest.getContainers().stream()
                    .map(Container::new)
                    .collect(Collectors.toList()))
                .withPrograms(listRequest.getPrograms())
                .withCampaignIds(listRequest.getCampaignIds().stream()
                    .map(id -> Id.<CampaignHandle>valueOf(id))
                    .collect(Collectors.toList()))
                .withDataKeys(listRequest.getDataKeys())
                .withDataValues(dataValues)
                .withExcludeAnonymous(listRequest.isExcludeAnonymous())
                .withIncludeSelfReferrals(listRequest.isIncludeSelfReferrals())
                .withIncludeDuplicateIdentities(listRequest.isIncludeDuplicateIdentities())
                .withOffset(listRequest.getOffset())
                .withLimit(listRequest.getLimit());

            return builder.list().stream()
                .map(step -> mapToResponse(step, timeZone))
                .collect(Collectors.toList());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    private PersonRelationshipResponse mapToResponse(PersonReferral personReferral, ZoneId timeZone) {
        Map<String, PersonRelationshipDataResponse> data = Maps.newHashMap();
        addData(data, () -> personReferral.getData());
        data.put(PersonRelationshipResponse.DATA_NAME_REASON,
            new PersonRelationshipDataResponse(PersonRelationshipResponse.DATA_NAME_REASON,
                personReferral.getReason()));
        return PersonRelationshipResponse.builder()
            .withMyRole(PersonReferralRole.valueOf(personReferral.getMySide().name()))
            .withOtherPersonId(personReferral.getOtherPersonId().getValue())
            .withContainer(personReferral.getContainer().getName())
            .withProgram(personReferral.getProgramLabel())
            .withCampaignId(personReferral.getCampaignId().map(Id::getValue))
            .withRootEventId(personReferral.getRootEventId().getValue())
            .withCauseEventId(personReferral.getCauseEventId().getValue())
            .withData(data)
            .withCreatedDate(personReferral.getCreatedDate().atZone(timeZone))
            .withUpdatedDate(personReferral.getUpdatedDate().atZone(timeZone))
            .build();
    }

    private void addData(Map<String, PersonRelationshipDataResponse> data, Supplier<Map<String, Object>> dataSupplier) {
        dataSupplier.get().forEach((name, value) -> data.put(name, new PersonRelationshipDataResponse(name, value)));
    }

    private Multimap<String, Object> parseDataValues(List<String> dataValues)
        throws PersonRelationshipsListRestException {
        Multimap<String, Object> result = ArrayListMultimap.create();
        for (String dataValue : dataValues) {
            int delimiterLocation = dataValue.indexOf(":");
            if (delimiterLocation > 0) {
                result.put(dataValue.substring(0, delimiterLocation), dataValue.substring(delimiterLocation + 1));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonRelationshipsListRestException.class)
                    .withErrorCode(PersonRelationshipsListRestException.INVALID_DATA_VALUE)
                    .addParameter("data_values", dataValue)
                    .build();
            }
        }

        return result;
    }

}
