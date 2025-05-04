package com.extole.api.impl.service;

import java.time.ZoneId;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.api.Language;
import com.extole.api.impl.LanguageImpl;
import com.extole.api.service.BigDecimalService;
import com.extole.api.service.CouponService;
import com.extole.api.service.DateService;
import com.extole.api.service.DoubleService;
import com.extole.api.service.EmailVerificationService;
import com.extole.api.service.GlobalServices;
import com.extole.api.service.IntegerService;
import com.extole.api.service.JwtService;
import com.extole.api.service.MonthDayService;
import com.extole.api.service.NotificationService;
import com.extole.api.service.PublicClientDomainService;
import com.extole.api.service.RandomService;
import com.extole.api.service.RewardSupplierService;
import com.extole.api.service.ShareService;
import com.extole.api.service.UnicodeService;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.consumer.event.service.ConsumerEventSender;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.event.client.ClientEventService;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.id.Id;
import com.extole.key.provider.service.KeyProviderService;
import com.extole.model.service.blocks.BlockService;
import com.extole.model.shared.blocklist.BlockEvaluationCache;
import com.extole.model.shared.client.security.key.jwt.JwtClientKeyCache;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.model.shared.reward.supplier.BuiltRewardSupplierCache;
import com.extole.model.shared.user.UserCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.reward.PersonRewardService;
import com.extole.person.service.share.PersonShareService;
import com.extole.person.service.shareable.ShareableService;
import com.extole.sandbox.Sandbox;
import com.extole.sandbox.SandboxModel;
import com.extole.sandbox.SandboxService;
import com.extole.security.backend.BackendAuthorizationProvider;

@Component
public class GlobalServicesFactory {

    private final SandboxService sandboxService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final PersonService personService;
    private final ProgramDomainCache programDomainCache;
    private final VerifiedEmailService verifiedEmailService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final PersonRewardService personRewardService;
    private final ClientEventService clientEventService;
    private final JwtClientKeyCache jwtClientKeyCache;
    private final KeyProviderService keyProviderService;
    private final ShareableService shareableService;
    private final PersonShareService shareService;
    private final UserCache userCache;
    private final BuiltRewardSupplierCache builtRewardSupplierCache;
    private final BlockEvaluationCache blockEvaluation;
    private final BlockService blockService;

    @Autowired
    public GlobalServicesFactory(
        SandboxService sandboxService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        PersonService personService,
        ProgramDomainCache programDomainCache,
        VerifiedEmailService verifiedEmailService,
        ConsumerEventSenderService consumerEventSenderService,
        PersonRewardService personRewardService,
        ClientEventService clientEventService,
        @Lazy KeyProviderService keyProviderService,
        JwtClientKeyCache jwtClientKeyCache,
        ShareableService shareableService,
        PersonShareService shareService,
        UserCache userCache,
        @Lazy BuiltRewardSupplierCache builtRewardSupplierCache,
        BlockEvaluationCache blockEvaluation,
        BlockService blockService) {
        this.sandboxService = sandboxService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.personService = personService;
        this.programDomainCache = programDomainCache;
        this.verifiedEmailService = verifiedEmailService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.personRewardService = personRewardService;
        this.clientEventService = clientEventService;
        this.keyProviderService = keyProviderService;
        this.jwtClientKeyCache = jwtClientKeyCache;
        this.shareableService = shareableService;
        this.shareService = shareService;
        this.userCache = userCache;
        this.builtRewardSupplierCache = builtRewardSupplierCache;
        this.blockEvaluation = blockEvaluation;
        this.blockService = blockService;
    }

    public GlobalServicesBuilder createBuilder(Id<ClientHandle> clientId, String contextObjectName,
        Id<?> contextObjectId, ZoneId clientTimeZone) {
        return new GlobalServicesBuilder(clientId, contextObjectName, contextObjectId, clientTimeZone);
    }

    public GlobalServices initializeNew(ConsumerEvent consumerEvent, String contextObjectName,
        Id<?> contextObjectId) {
        return new GlobalServicesBuilder(consumerEvent, contextObjectName, contextObjectId).build();
    }

    private GlobalServices initializeNew(Id<ClientHandle> clientId, Supplier<PersonOperations> personOperationsSupplier,
        String contextObjectName, Id<?> contextObjectId, Optional<Id<ProgramHandle>> clientDomainId,
        ZoneId clientTimeZone) {
        LazyLoadingSupplier<PublicClientDomainService> publicClientDomainServiceSupplier =
            new LazyLoadingSupplier<>(() -> new PublicClientDomainServiceImpl(clientId, programDomainCache));
        LazyLoadingSupplier<EmailVerificationService> emailVerificationServiceSupplier =
            new LazyLoadingSupplier<>(() -> new EmailVerificationServiceImpl(verifiedEmailService));
        LazyLoadingSupplier<com.extole.api.service.PersonService> contextPersonServiceSupplier =
            new LazyLoadingSupplier<>(
                () -> new PersonServiceImpl(sandboxService, clientId, personService, backendAuthorizationProvider,
                    verifiedEmailService, programDomainCache, personOperationsSupplier, clientDomainId));
        LazyLoadingSupplier<NotificationService> notificationServiceSupplier = new LazyLoadingSupplier<>(
            () -> new NotificationServiceImpl(clientId, clientEventService, contextObjectName, contextObjectId));
        LazyLoadingSupplier<CouponService> couponServiceSupplier =
            new LazyLoadingSupplier<>(() -> new CouponServiceImpl(clientId, personRewardService));
        LazyLoadingSupplier<JwtService> jwtServiceSupplier =
            new LazyLoadingSupplier<>(() -> new JwtServiceImpl(clientId, jwtClientKeyCache, keyProviderService));
        LazyLoadingSupplier<RewardSupplierService> rewardSupplierServiceSupplier =
            new LazyLoadingSupplier<>(
                () -> new RewardSupplierServiceImpl(clientId, builtRewardSupplierCache));
        LazyLoadingSupplier<DateService> dateServiceSupplier =
            new LazyLoadingSupplier<>(() -> new DateServiceImpl(clientTimeZone));
        LazyLoadingSupplier<com.extole.api.service.ShareableService> apiShareableServiceSupplier =
            new LazyLoadingSupplier<>(() -> new ShareableServiceImpl(clientId, shareableService));
        LazyLoadingSupplier<Language> languageSupplier = new LazyLoadingSupplier<>(() -> new LanguageImpl());
        LazyLoadingSupplier<UnicodeService> unicodeServiceSupplier =
            new LazyLoadingSupplier<>(() -> new UnicodeServiceImpl());
        LazyLoadingSupplier<IntegerService> integerServiceSupplier =
            new LazyLoadingSupplier<>(() -> new IntegerServiceImpl());
        LazyLoadingSupplier<DoubleService> doubleServiceSupplier =
            new LazyLoadingSupplier<>(() -> new DoubleServiceImpl());
        LazyLoadingSupplier<BigDecimalService> bigDecimalServiceSupplier =
            new LazyLoadingSupplier<>(() -> new BigDecimalServiceImpl());
        LazyLoadingSupplier<MonthDayService> monthDayServiceSupplier =
            new LazyLoadingSupplier<>(() -> new MonthDayServiceImpl());
        LazyLoadingSupplier<RandomService> randomServiceSupplier =
            new LazyLoadingSupplier<>(() -> new RandomServiceImpl());
        LazyLoadingSupplier<ShareService> shareSupplier =
            new LazyLoadingSupplier<>(() -> new ShareServiceImpl(clientId, shareService));
        LazyLoadingSupplier<com.extole.api.service.UserService> userServiceSupplier =
            new LazyLoadingSupplier<>(() -> new UserServiceImpl(clientId, backendAuthorizationProvider, userCache));
        LazyLoadingSupplier<com.extole.api.service.BlockService> blockServiceSupplier =
            new LazyLoadingSupplier<>(() -> new BlockServiceImpl(clientId, blockEvaluation, blockService));
        LazyLoadingSupplier<com.extole.api.service.UrlService> urlServiceSupplier =
            new LazyLoadingSupplier<>(() -> new UrlServiceImpl());

        return new GlobalServicesImpl(
            randomServiceSupplier,
            unicodeServiceSupplier,
            integerServiceSupplier,
            doubleServiceSupplier,
            bigDecimalServiceSupplier,
            monthDayServiceSupplier,
            publicClientDomainServiceSupplier,
            emailVerificationServiceSupplier,
            contextPersonServiceSupplier,
            notificationServiceSupplier,
            couponServiceSupplier,
            jwtServiceSupplier,
            dateServiceSupplier,
            apiShareableServiceSupplier,
            rewardSupplierServiceSupplier,
            languageSupplier,
            shareSupplier,
            userServiceSupplier,
            blockServiceSupplier,
            urlServiceSupplier);
    }

    public final class GlobalServicesBuilder {

        private final Id<ClientHandle> clientId;
        private final String contextObjectName;
        private final Id<?> contextObjectId;
        private final ZoneId clientTimeZone;

        private Optional<Pair<String, Id<ProgramHandle>>> clientDomain = Optional.empty();
        private Optional<SandboxModel> sandbox = Optional.empty();
        private Optional<ConsumerEvent> consumerEvent = Optional.empty();
        private Optional<Integer> causeEventSequence = Optional.empty();

        private GlobalServicesBuilder(Id<ClientHandle> clientId, String contextObjectName, Id<?> contextObjectId,
            ZoneId clientTimeZone) {
            this.clientId = clientId;
            this.contextObjectName = contextObjectName;
            this.contextObjectId = contextObjectId;
            this.clientTimeZone = clientTimeZone;
        }

        private GlobalServicesBuilder(ConsumerEvent consumerEvent, String contextObjectName, Id<?> contextObjectId) {
            this.consumerEvent = Optional.of(consumerEvent);
            this.clientId = consumerEvent.getClientContext().getClientId();
            this.contextObjectName = contextObjectName;
            this.contextObjectId = contextObjectId;
            this.clientTimeZone = consumerEvent.getClientContext().getClientTimeZone();
        }

        public GlobalServicesBuilder withClientDomain(String clientDomain, Id<ProgramHandle> clientDomainId) {
            this.clientDomain = Optional.of(Pair.of(clientDomain, clientDomainId));
            return this;
        }

        public GlobalServicesBuilder withSandbox(SandboxModel sandbox) {
            this.sandbox = Optional.of(sandbox);
            return this;
        }

        public GlobalServicesBuilder withCauseEventSequence(Integer causeEventSequence) {
            this.causeEventSequence = Optional.of(causeEventSequence);
            return this;
        }

        public GlobalServices build() {
            Supplier<PersonOperations> personOperationsSupplier =
                () -> {
                    ConsumerEventSender consumerEventSender;
                    if (consumerEvent.isPresent()) {
                        consumerEventSender = consumerEventSenderService.createConsumerEventSender(consumerEvent.get());
                    } else {
                        consumerEventSender = consumerEventSenderService.createConsumerEventSender();
                    }

                    clientDomain.ifPresent(value -> consumerEventSender
                        .withClientDomainContext(new ClientDomainContext(value.getKey(), value.getRight())));
                    sandbox.ifPresent(value -> consumerEventSender
                        .withSandbox(Sandbox.getSandbox(value.getId().getValue(), value.getContainer())));
                    causeEventSequence.ifPresent(value -> consumerEventSender.withCauseEventSequence(value));

                    return consumerEventSender;
                };

            Optional<Id<ProgramHandle>> clientDomainId = clientDomain.map(value -> value.getRight())
                .or(() -> consumerEvent.map(value -> value.getClientDomainContext().getClientDomainId()));

            return initializeNew(clientId, personOperationsSupplier, contextObjectName, contextObjectId,
                clientDomainId, clientTimeZone);
        }

    }

}
