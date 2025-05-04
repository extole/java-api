package com.extole.consumer.rest.impl.request.context;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.AuthorizationException;
import com.extole.client.identity.IdentityKey;
import com.extole.common.lang.JsonMap;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.log.execution.ExecutionLoggerFactory;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.EventProcessorPrehandler;
import com.extole.consumer.event.service.processor.EventProcessorPrehandlerContext;
import com.extole.consumer.event.service.processor.EventProcessorPrehandlerResult;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.event.service.processor.ProcessedRawEventProducer;
import com.extole.event.consumer.ApiType;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.identity.IdentityKeyValueNormalizer;
import com.extole.person.service.identity.InvalidIdentityKeyValueException;
import com.extole.person.service.identity.NormalizedIdentityKeyValue;
import com.extole.person.service.profile.IdentityKeyValueAlreadyTakenException;
import com.extole.person.service.profile.IdentityKeyValueUnauthorizedUpdateException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonDisplacedAlreadyDefinedException;
import com.extole.person.service.profile.PersonDisplacedIdentityException;
import com.extole.person.service.profile.PersonDisplacedMissingIdentityException;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonSelfDisplacingException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.referral.PersonReferral;

@Component
public class ConsumerDisplacePrehandler implements EventProcessorPrehandler {
    private static final String PREHANDLER_NAME = ConsumerDisplacePrehandler.class.getSimpleName();
    private static final String PARAMETER_EMAIL_DEPRECATED = "e";

    private final ConsumerEventSenderService consumerEventSenderService;
    private final PersonService personService;
    private final ProcessedRawEventProducer processedRawEventProducer;
    private final IdentityKeyValueNormalizer identityKeyValueNormalizer;

    @Autowired
    public ConsumerDisplacePrehandler(ConsumerEventSenderService consumerEventSenderService,
        PersonService personService,
        ProcessedRawEventProducer processedRawEventProducer,
        IdentityKeyValueNormalizer identityKeyValueNormalizer) {
        this.consumerEventSenderService = consumerEventSenderService;
        this.personService = personService;
        this.processedRawEventProducer = processedRawEventProducer;
        this.identityKeyValueNormalizer = identityKeyValueNormalizer;
    }

    @Override
    public String getName() {
        return PREHANDLER_NAME;
    }

    @Override
    public int getOrder() {
        return PRIORITY_ORDER_PROFILE;
    }

    @Override
    public EventProcessorPrehandlerResult execute(EventProcessorPrehandlerContext context)
        throws EventProcessorException {
        ProcessedRawEvent processedRawEvent = context.getProcessedRawEvent();
        if (processedRawEvent.getRawEvent().getApiType() != ApiType.CONSUMER) {
            return EventProcessorPrehandlerResult.unqualified(processedRawEvent);
        }
        if (!context.getCandidateProfile().isInitialCandidate()) {
            return EventProcessorPrehandlerResult.unqualified(processedRawEvent);
        }
        Person candidatePerson = context.getCandidateProfile().getPerson().orElse(null);
        if (candidatePerson == null) {
            return EventProcessorPrehandlerResult.unqualified(processedRawEvent);
        }

        IdentityKey identityKey = candidatePerson.getIdentityKey();
        Optional<String> candidatePersonIdentityKeyValue = candidatePerson.getIdentityKeyValue();

        JsonMap data = JsonMap.valueOf(context.getProcessedRawEvent().getData());
        Optional<String> rawIdentityKeyValue = getRawIdentityKeyValue(identityKey, data);

        if (rawIdentityKeyValue.isEmpty()
            || candidatePersonIdentityKeyValue.isEmpty()
            || rawIdentityKeyValue.equals(candidatePersonIdentityKeyValue)) {
            return EventProcessorPrehandlerResult.unqualified(processedRawEvent);
        }

        ExecutionLogger logger = ExecutionLoggerFactory.newInstance().withMessagePrefix(PREHANDLER_NAME);

        Optional<NormalizedIdentityKeyValue> normalizedIdentityKeyValue =
            normalizeIdentityKeyValue(identityKey, rawIdentityKeyValue.get(), logger);

        if (normalizedIdentityKeyValue.isPresent()
            && !normalizedIdentityKeyValue.map(NormalizedIdentityKeyValue::getNormalizedValue)
                .equals(candidatePersonIdentityKeyValue)) {
            Person newPerson =
                createNewPersonWithDisplace(context, candidatePerson, normalizedIdentityKeyValue.get(), logger);
            context.getCandidateProfile().replaceWithExisting(newPerson);
        }

        return EventProcessorPrehandlerResult.qualified(
            processedRawEventProducer.createBuilder(processedRawEvent)
                .addLogMessages(logger.getLogMessages())
                .build());
    }

    private Person createNewPersonWithDisplace(EventProcessorPrehandlerContext context, Person candidatePerson,
        NormalizedIdentityKeyValue newNormalizedIdentityKeyValue, ExecutionLogger logger)
        throws EventProcessorException {

        String newIdentityKeyValue = newNormalizedIdentityKeyValue.getNormalizedValue();
        logger.log("displacing the candidate person: " + candidatePerson.getId()
            + " with a new person with identity_key_value: " + newIdentityKeyValue);

        PublicProgram clientDomain = context.getProcessedRawEvent().getClientDomain();

        // TODO ENG-11433 add the information about displacement to the upcoming INPUT event
        PersonOperations personOperations = consumerEventSenderService.createConsumerEventSender()
            .withClientDomainContext(
                new ClientDomainContext(clientDomain.getProgramDomain().toString(), clientDomain.getId()))
            .log("Person created as a result of a displace event. Displacing identity_key_value: "
                + newIdentityKeyValue + ", original person identity_key_value: "
                + candidatePerson.getIdentityKeyValue());

        // TODO ENG-9033 only copy person data since last identify
        try {
            return personService.newPerson(candidatePerson.getClientId(),
                new LockDescription("displace-identify"), personBuilder -> {
                    try {
                        personBuilder.withIdentityKeyValue(newNormalizedIdentityKeyValue);
                        return personBuilder
                            .withDisplacedPerson(candidatePerson.getId())
                            .withDisplaceRecentReferral(PersonReferral.Side.FRIEND)
                            .withDisplaceRecentReferral(PersonReferral.Side.ADVOCATE)
                            .withDisplaceMostRecentJourney()
                            .done()
                            .save();
                    } catch (PersonDisplacedAlreadyDefinedException | PersonSelfDisplacingException
                        | AuthorizationException | PersonDisplacedIdentityException
                        | PersonDisplacedMissingIdentityException | IdentityKeyValueUnauthorizedUpdateException
                        | IdentityKeyValueAlreadyTakenException | InvalidIdentityKeyValueException e) {
                        throw new LockClosureException(e);
                    }
                }, personOperations);
        } catch (LockClosureException e) {
            throw new EventProcessorException(String.format("Unable to displace candidate person: %s " +
                "for client: %s " +
                "with a new person with identity_key_value: %s",
                candidatePerson.getId(), candidatePerson.getClientId(), newIdentityKeyValue), e);
        }
    }

    private Optional<String> getRawIdentityKeyValue(IdentityKey identityKey, JsonMap data) {
        Optional<String> identityKeyValue = data.getValueAsString(identityKey.getName());
        if (IdentityKey.EMAIL_IDENTITY_KEY.equals(identityKey) && identityKeyValue.isEmpty()) {
            return data.getValueAsString(PARAMETER_EMAIL_DEPRECATED);
        }
        return identityKeyValue;
    }

    private Optional<NormalizedIdentityKeyValue> normalizeIdentityKeyValue(IdentityKey identityKey,
        String identityKeyValue, ExecutionLogger logger) {
        try {
            return Optional.of(identityKeyValueNormalizer.normalize(identityKey, identityKeyValue));
        } catch (InvalidIdentityKeyValueException e) {
            logger.log("invalid identity_key_value data parameter: " + identityKeyValue + " due to: " + e);
            return Optional.empty();
        }
    }
}
