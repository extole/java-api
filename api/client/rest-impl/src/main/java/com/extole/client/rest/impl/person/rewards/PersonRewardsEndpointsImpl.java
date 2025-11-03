package com.extole.client.rest.impl.person.rewards;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.rewards.PersonRewardDataResponse;
import com.extole.client.rest.person.rewards.PersonRewardResponse;
import com.extole.client.rest.person.rewards.PersonRewardResponse.Builder;
import com.extole.client.rest.person.rewards.PersonRewardRestException;
import com.extole.client.rest.person.rewards.PersonRewardsEndpoints;
import com.extole.client.rest.person.rewards.PersonRewardsListRequest;
import com.extole.client.rest.person.rewards.PersonRewardsListRestException;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.common.journey.JourneyName;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.shared.reward.supplier.ArchivedRewardSupplierCache;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonRewardQueryBuilder;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.reward.PersonReward;
import com.extole.person.service.profile.reward.PersonRewardState;
import com.extole.person.service.profile.reward.PersonRewardSupplierType;
import com.extole.sandbox.Container;
import com.extole.sandbox.SandboxNotFoundException;
import com.extole.sandbox.SandboxService;

@Provider
public class PersonRewardsEndpointsImpl implements PersonRewardsEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PersonRewardsEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final FullPersonService fullPersonService;
    private final SandboxService sandboxService;
    private final ArchivedRewardSupplierCache archivedRewardSupplierCache;

    @Autowired
    public PersonRewardsEndpointsImpl(PersonService personService,
        FullPersonService fullPersonService,
        SandboxService sandboxService,
        ArchivedRewardSupplierCache archivedRewardSupplierCache,
        ClientAuthorizationProvider authorizationProvider) {
        this.personService = personService;
        this.fullPersonService = fullPersonService;
        this.sandboxService = sandboxService;
        this.archivedRewardSupplierCache = archivedRewardSupplierCache;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public PersonRewardResponse get(String accessToken, String personId, String rewardId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRewardRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<PersonReward> personReward =
                personService.getPerson(userAuthorization, Id.valueOf(personId))
                    .getRewards().stream()
                    .filter(reward -> reward.getRewardId().getValue().equals(rewardId))
                    .findFirst();

            if (personReward.isEmpty()) {
                personReward = fullPersonService.createRewardQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withIds(List.of(Id.valueOf(rewardId))).withLimit(1)
                    .list().stream()
                    .findFirst();
            }

            if (personReward.isPresent()) {
                return toPersonRewardResponse(userAuthorization.getClientId(), personReward.get(), timeZone);
            } else {
                throw RestExceptionBuilder.newBuilder(PersonRewardRestException.class)
                    .withErrorCode(PersonRewardRestException.REWARD_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("reward_id", rewardId)
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
        } catch (SandboxNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public List<PersonRewardResponse> list(String accessToken, String personId,
        PersonRewardsListRequest personRewardsListRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRewardsListRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Multimap<String, Object> dataValues =
                parseKeyValuePairs(personRewardsListRequest.getDataValues(), "data_values");
            Multimap<String, String> journeyKeyValues =
                parseKeyValuePairs(personRewardsListRequest.getJourneyKeyValues(), "journey_key_values").entries()
                    .stream()
                    .collect(toImmutableListMultimap(entry -> entry.getKey(), pair -> pair.getValue().toString()));

            List<JourneyName> journeyNames = personRewardsListRequest.getJourneyNames().stream()
                .filter(journeyName -> StringUtils.isNotBlank(journeyName))
                .map(journeName -> JourneyName.valueOf(journeName))
                .collect(Collectors.toUnmodifiableList());

            PersonRewardQueryBuilder builder =
                fullPersonService.createRewardQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withProgramLabels(personRewardsListRequest.getPrograms())
                    .withCampaignIds(personRewardsListRequest.getCampaignIds().stream().map(Id::<CampaignHandle>valueOf)
                        .collect(Collectors.toList()))
                    .withContainers(personRewardsListRequest.getContainers().stream().map(Container::new)
                        .collect(Collectors.toList()))
                    .withDataKeys(personRewardsListRequest.getDataKeys())
                    .withDataValues(dataValues)
                    .withRewardTypes(personRewardsListRequest.getRewardTypes()
                        .stream().map(Enum::name).map(PersonRewardSupplierType::valueOf).collect(Collectors.toList()))
                    .withRewardStates(personRewardsListRequest.getRewardStates()
                        .stream().map(Enum::name).map(PersonRewardState::valueOf).collect(Collectors.toList()))
                    .withJourneyNames(journeyNames)
                    .withJourneyKeyNames(personRewardsListRequest.getJourneyKeyNames())
                    .withJourneyKeyValues(journeyKeyValues);

            builder.withOffset(personRewardsListRequest.getOffset())
                .withLimit(personRewardsListRequest.getLimit());
            List<PersonRewardResponse> responses = Lists.newArrayList();
            for (PersonReward reward : builder.list()) {
                responses.add(toPersonRewardResponse(userAuthorization.getClientId(), reward, timeZone));
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
        } catch (SandboxNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private PersonRewardResponse toPersonRewardResponse(Id<ClientHandle> clientId, PersonReward reward,
        ZoneId timeZone) throws SandboxNotFoundException {
        Optional<String> partnerRewardSupplierId = Optional.empty();
        try {
            RewardSupplier rewardSupplier = archivedRewardSupplierCache.getRewardSupplier(clientId,
                Id.valueOf(reward.getRewardSupplierId().getValue()));
            partnerRewardSupplierId = rewardSupplier.getPartnerRewardSupplierId();
        } catch (RewardSupplierNotFoundException e) {
            LOG.warn("Unable to find rewardSupplierById for client_id = {}, rewardSupplierId = {}",
                clientId, reward.getRewardSupplierId(), e);
        }
        com.extole.sandbox.Container container =
            sandboxService.getById(clientId, Id.valueOf(reward.getSandbox())).getContainer();

        Builder responseBuilder = PersonRewardResponse.builder()
            .withId(reward.getRewardId().getValue())
            .withProgram(reward.getProgramLabel())
            .withCampaignId(reward.getCampaignId().getValue())
            .withContainer(container.getName())
            .withSandbox(reward.getSandbox())
            .withRewardSupplierId(reward.getRewardSupplierId().getValue())
            .withFaceValue(reward.getFaceValue())
            .withFaceValueType(FaceValueType.valueOf(reward.getFaceValueType().name()))
            .withPartnerRewardId(reward.getPartnerRewardId().orElse(null))
            .withPartnerRewardSupplierId(partnerRewardSupplierId.orElse(null))
            .withName(reward.getRewardName().orElse(null))
            .withValueOfRewardedEvent(reward.getValueOfRewardedEvent())
            .withState(RewardState.valueOf(reward.getState().name()))
            .withTags(ImmutableList.copyOf(reward.getRewardSlots()))
            .withData(reward.getData().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> new PersonRewardDataResponse(entry.getKey(), entry.getValue()))))
            .withCreatedDate(ZonedDateTime.ofInstant(reward.getRewardedDate(), timeZone))
            .withUpdatedDate(ZonedDateTime.ofInstant(reward.getUpdatedDate(), timeZone))
            .withEarnedDate(ZonedDateTime.ofInstant(reward.getRewardedDate(), timeZone))
            .withRedeemedDate(reward.getRedeemedDate()
                .map(redeemedDate -> ZonedDateTime.ofInstant(redeemedDate, timeZone)).orElse(null))
            .withExpiryDate(
                reward.getExpiryDate().map(expiryDate -> ZonedDateTime.ofInstant(expiryDate, timeZone)).orElse(null))
            .withJourneyName(reward.getJourneyName().getValue());

        reward.getJourneyKey()
            .ifPresent(value -> responseBuilder.withJourneyKey(new JourneyKey(value.getName(), value.getValue())));

        return responseBuilder.build();
    }

    private Multimap<String, Object> parseKeyValuePairs(List<String> valuesToParse, String parameterName)
        throws PersonRewardsListRestException {
        ImmutableListMultimap.Builder<String, Object> resultBuilder = ImmutableListMultimap.<String, Object>builder();

        for (String valueToParse : valuesToParse) {
            int delimiterLocation = valueToParse.indexOf(":");
            if (delimiterLocation > 0) {
                resultBuilder.put(valueToParse.substring(0, delimiterLocation),
                    valueToParse.substring(delimiterLocation + 1));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonRewardsListRestException.class)
                    .withErrorCode(PersonRewardsListRestException.VALUE_DOES_NOT_FOLLOW_PATTERN)
                    .addParameter("parameter", parameterName)
                    .addParameter("value", valueToParse)
                    .build();
            }
        }

        return resultBuilder.build();
    }

}
