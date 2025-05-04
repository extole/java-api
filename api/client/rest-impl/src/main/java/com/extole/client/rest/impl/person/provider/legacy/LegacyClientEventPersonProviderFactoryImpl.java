package com.extole.client.rest.impl.person.provider.legacy;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.impl.person.provider.legacy.LegacyClientEventPersonLookupService.LegacyClientEventPersonLookupBuilder;
import com.extole.common.lang.JsonMap;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.log.execution.ExecutionLoggerFactory;
import com.extole.consumer.event.service.processor.EventProcessorCandidateProvider;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.EventProcessorPrehandler;
import com.extole.consumer.event.service.processor.EventProcessorPrehandlerContext;
import com.extole.consumer.event.service.processor.EventProcessorPrehandlerResult;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.event.service.processor.ProcessedRawEventProducer;
import com.extole.consumer.event.service.processor.exception.EventProcessorPersonNotFoundException;
import com.extole.event.consumer.ApiType;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.running.service.partner.PartnerProfileKeyService;

@Deprecated // TODO completely remove in ENG-12938
@Component
public class LegacyClientEventPersonProviderFactoryImpl implements LegacyClientEventPersonProviderFactory {

    private static final String PERSON_PREHANDLER_NAME =
        LegacyClientConsumerEventPersonPrehandler.class.getSimpleName();

    private static final String DATA_EMAIL = "email";
    private static final String DATA_PARTNER_USER_ID = "partner_user_id";
    private static final String DATA_PARTNER_CONVERSION_ID = "partner_conversion_id";
    private static final String DATA_PERSON_ID = "person_id";

    private final PartnerProfileKeyService partnerProfileKeyService;
    private final LegacyClientEventPersonLookupService personLookupService;
    private final ProcessedRawEventProducer processedRawEventProducer;

    @Autowired
    public LegacyClientEventPersonProviderFactoryImpl(
        PartnerProfileKeyService partnerProfileKeyService,
        LegacyClientEventPersonLookupService personLookupService,
        ProcessedRawEventProducer processedRawEventProducer) {
        this.partnerProfileKeyService = partnerProfileKeyService;
        this.personLookupService = personLookupService;
        this.processedRawEventProducer = processedRawEventProducer;
    }

    @Deprecated // TODO remove together with ClientConsumerEventV4Endpoints in ENG-10566
    @Override
    public EventProcessorCandidateProvider newPersonProvider(ClientAuthorization authorization,
        boolean dynamicPersonKeysEnabled) {
        return new LegacyClientConsumerEventPersonProvider(authorization, dynamicPersonKeysEnabled);
    }

    @Deprecated // TODO remove together with ClientConsumerEventV4Endpoints in ENG-10566
    @Override
    public EventProcessorPrehandler newPersonPrehandler(ClientAuthorization authorization,
        boolean dynamicPersonKeysEnabled) {
        return new LegacyClientConsumerEventPersonPrehandler(authorization, dynamicPersonKeysEnabled);
    }

    private LegacyClientEventPersonLookupBuilder createNewPersonLookupBuilder(ClientAuthorization authorization,
        ProcessedRawEvent processedRawEvent, boolean dynamicPersonKeysEnabled) {
        JsonMap data = JsonMap.valueOf(processedRawEvent.getData());
        LegacyClientEventPersonLookupBuilder builder = personLookupService.newLookup(authorization)
            .withPersonId(data.getValueAsString(DATA_PERSON_ID).orElse(null))
            .withPartnerUserId(data.getValueAsString(DATA_PARTNER_USER_ID).orElse(null))
            .withPartnerConversionId(
                data.getValueAsString(DATA_PARTNER_CONVERSION_ID).orElse(null))
            .withEmail(data.getValueAsString(DATA_EMAIL).orElse(null))
            .withClientDomain(processedRawEvent.getClientDomain());
        if (dynamicPersonKeysEnabled) {
            builder.withPersonKeys(getDynamicPersonKeys(processedRawEvent));
        }
        return builder;
    }

    private Set<PersonKey> getDynamicPersonKeys(ProcessedRawEvent processedRawEvent) {
        Id<ClientHandle> clientId = processedRawEvent.getClientContext().getClientId();
        Set<String> recognizedDynamicPersonKeyTypes =
            partnerProfileKeyService.getPartnerProfileKeyNames(clientId).stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return processedRawEvent.getData().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .filter(entry -> recognizedDynamicPersonKeyTypes.contains(PersonKey.sanitizeType(entry.getKey())))
            .map(entry -> PersonKey.ofType(entry.getKey(), entry.getValue().toString()))
            .collect(Collectors.toSet());
    }

    private final class LegacyClientConsumerEventPersonProvider implements EventProcessorCandidateProvider {

        private final ClientAuthorization authorization;
        private final boolean dynamicPersonKeysEnabled;

        LegacyClientConsumerEventPersonProvider(ClientAuthorization authorization, boolean dynamicPersonKeysEnabled) {
            this.authorization = authorization;
            this.dynamicPersonKeysEnabled = dynamicPersonKeysEnabled;
        }

        @Override
        public Optional<Person> getCandidatePerson(ProcessedRawEvent processedRawEvent, ExecutionLogger logger)
            throws AuthorizationException {
            try {
                LegacyClientEventPersonLookupResult result =
                    createNewPersonLookupBuilder(authorization, processedRawEvent, dynamicPersonKeysEnabled).lookup();
                logger.log(result.getLogMessages());
                return result.getPerson();
            } catch (PersonNotFoundException e) {
                logger.log("unable to provide candidate person due to: " + e.toString());
                return Optional.empty();
            }
        }
    }

    private final class LegacyClientConsumerEventPersonPrehandler implements EventProcessorPrehandler {

        private final ClientAuthorization authorization;
        private final boolean dynamicPersonKeysEnabled;

        LegacyClientConsumerEventPersonPrehandler(ClientAuthorization authorization, boolean dynamicPersonKeysEnabled) {
            this.authorization = authorization;
            this.dynamicPersonKeysEnabled = dynamicPersonKeysEnabled;
        }

        @Override
        public String getName() {
            return PERSON_PREHANDLER_NAME;
        }

        @Override
        public int getOrder() {
            return PRIORITY_ORDER_PROFILE;
        }

        @Override
        public EventProcessorPrehandlerResult execute(EventProcessorPrehandlerContext context)
            throws EventProcessorException {
            ProcessedRawEvent processedRawEvent = context.getProcessedRawEvent();
            if (processedRawEvent.getRawEvent().getApiType() == ApiType.CONSUMER) {
                return EventProcessorPrehandlerResult.unqualified(processedRawEvent);
            }
            if (context.getCandidateProfile().getPerson().isPresent()) {
                return EventProcessorPrehandlerResult.unqualified(processedRawEvent);
            }
            try {
                ExecutionLogger logger = ExecutionLoggerFactory.newInstance().withMessagePrefix(PERSON_PREHANDLER_NAME);
                LegacyClientEventPersonLookupOrCreateResult result =
                    createNewPersonLookupBuilder(authorization, processedRawEvent, dynamicPersonKeysEnabled)
                        .lookupOrCreate();
                logger.log(result.getLogMessages());
                context.getCandidateProfile().replaceWithExisting(result.getPerson());
                return EventProcessorPrehandlerResult.qualified(
                    processedRawEventProducer.createBuilder(processedRawEvent)
                        .addLogMessages(logger.getLogMessages())
                        .build());
            } catch (PersonNotFoundException e) {
                throw new EventProcessorPersonNotFoundException(e.getClientId(), e.getPersonId(), e);
            } catch (AuthorizationException e) {
                throw new EventProcessorException("Unable to lookup or create person", e);
            }
        }
    }

}
