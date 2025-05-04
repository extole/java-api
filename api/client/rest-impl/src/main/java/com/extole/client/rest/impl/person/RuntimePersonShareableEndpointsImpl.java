package com.extole.client.rest.impl.person;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.RuntimePersonShareableEndpoints;
import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.client.rest.program.ProgramRestException;
import com.extole.client.rest.shareable.ClientShareableContentRequest;
import com.extole.client.rest.shareable.ClientShareableCreateRestException;
import com.extole.client.rest.shareable.ClientShareableRestException;
import com.extole.client.rest.shareable.ClientShareableValidationRestException;
import com.extole.client.rest.shareable.CreateClientShareableRequest;
import com.extole.client.rest.shareable.ShareableRestException;
import com.extole.client.rest.shareable.UpdateClientShareableRequest;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.security.HashAlgorithm;
import com.extole.consumer.event.service.ConsumerEventSender;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableBuilder;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.shareable.PersonNotRewardableException;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
import com.extole.person.service.shareable.ShareableByCodeNotFoundException;
import com.extole.person.service.shareable.ShareableCodeReservedException;
import com.extole.person.service.shareable.ShareableCodeTakenByPromotionException;
import com.extole.person.service.shareable.ShareableCodeTakenException;
import com.extole.person.service.shareable.ShareableContentDescriptionTooLongException;
import com.extole.person.service.shareable.ShareableDataAttributeNameInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeNameLengthException;
import com.extole.person.service.shareable.ShareableDataAttributeValueInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeValueLengthException;
import com.extole.person.service.shareable.ShareableFieldLengthException;
import com.extole.person.service.shareable.ShareableFieldValueException;
import com.extole.person.service.shareable.ShareableKeyTakenException;
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;
import com.extole.person.service.shareable.ShareableServiceException;

@Provider
public class RuntimePersonShareableEndpointsImpl implements RuntimePersonShareableEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimePersonShareableEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final ClientShareableService clientShareableService;
    private final PersonShareableRestMapper shareableRestMapper;
    private final ProgramDomainCache programCache;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final PersonShareableRestMapper personShareableRestMapper;
    private final ShareableService shareableService;

    @Autowired
    public RuntimePersonShareableEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        ClientShareableService clientShareableService,
        PersonShareableRestMapper shareableRestMapper,
        ProgramDomainCache programCache,
        ConsumerEventSenderService consumerEventSenderService,
        PersonShareableRestMapper personShareableRestMapper,
        ShareableService shareableService) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
        this.clientShareableService = clientShareableService;
        this.shareableRestMapper = shareableRestMapper;
        this.programCache = programCache;
        this.consumerEventSenderService = consumerEventSenderService;
        this.personShareableRestMapper = personShareableRestMapper;
        this.shareableService = shareableService;
    }

    @Override
    public List<PersonShareableV4Response> getShareables(String accessToken, String personId)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<PersonShareableV4Response> response = new ArrayList<>();
            for (Shareable shareable : shareableService.getAll(authorization, Id.valueOf(personId))) {
                try {
                    response.add(personShareableRestMapper.toPersonShareableV4Response(shareable));
                } catch (ProgramNotFoundException e) {
                    LOG.warn("Unable to find program domain for shareable: {}", shareable, e);
                }
            }
            return response;
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

    @Override
    public PersonShareableV4Response getShareableByCode(String accessToken, String personId, String code)
        throws UserAuthorizationRestException, PersonRestException, ShareableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<PersonShareableV4Response> response = new ArrayList<>();
            for (Shareable shareable : shareableService.getAll(authorization, Id.valueOf(personId)).stream()
                .filter(shareable -> shareable.getCode().equalsIgnoreCase(code)).collect(Collectors.toList())) {
                try {
                    response.add(personShareableRestMapper.toPersonShareableV4Response(shareable));
                } catch (ProgramNotFoundException e) {
                    LOG.warn("Unable to find program domain for shareable: {}", shareable, e);
                }
            }

            if (!response.isEmpty()) {
                return response.get(0);
            }
            throw new ShareableByCodeNotFoundException(authorization.getClientId(), code);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ShareableByCodeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableRestException.class)
                .withErrorCode(ShareableRestException.SHAREABLE_NOT_FOUND)
                .addParameter("code", e.getCode())
                .withCause(e).build();
        }
    }

    @Override
    public PersonShareableV4Response create(String accessToken, String personId, CreateClientShareableRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ClientShareableCreateRestException,
        ClientShareableValidationRestException, ClientShareableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        PublicProgram program = getProgram(authorization, request.getProgramUrl().getValue());

        if (Strings.isNullOrEmpty(request.getLabel())) {
            throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                .withErrorCode(ClientShareableCreateRestException.LABEL_IS_MISSING).build();
        }
        try {
            ClientShareable shareable =
                personService.updatePerson(authorization, Id.valueOf(personId),
                    new LockDescription("client-person-shareable-endpoint-create"),
                    (personBuilder, originalPersonProfile) -> {
                        try {
                            ClientShareableBuilder builder =
                                clientShareableService.create(authorization.getClientId(), program.getId(),
                                    personBuilder);
                            if (authorization.isAuthorized(authorization.getClientId(),
                                Authorization.Scope.USER_SUPPORT)) {
                                builder.withIgnoringNaughtyWords();
                            }
                            builder.withLabel(request.getLabel());
                            request.getCode().ifPresent(builder::withCode);
                            request.getKey().ifPresent(builder::withKey);
                            request.getContent().ifPresent(content -> addContent(builder, content));
                            request.getData().ifPresent(data -> addData(builder, data));

                            return builder.save();
                        } catch (ClientShareableValidationRestException | ShareableServiceException e) {
                            throw new LockClosureException(e);
                        }
                    }, createConsumerEventSender(program));
            return shareableRestMapper.toPersonShareableV4Response(shareable);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ShareableFieldLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                    .withErrorCode(ClientShareableCreateRestException.CODE_LENGTH_OUT_OF_RANGE)
                    .addParameter("code", request.getCode()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldValueException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                    .withErrorCode(ClientShareableCreateRestException.CODE_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("code", request.getCode()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonNotRewardableException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                    .withErrorCode(ClientShareableCreateRestException.PERSON_NOT_REWARDABLE)
                    .addParameter("person_id", ((PersonNotRewardableException) cause).getPersonId())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                    .withErrorCode(ClientShareableCreateRestException.CODE_TAKEN)
                    .addParameter("code", ((ShareableCodeTakenException) cause).getCode())
                    .addParameter("code_suggestions", ((ShareableCodeTakenException) cause).getSuggestions())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenByPromotionException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                    .withErrorCode(ClientShareableCreateRestException.CODE_TAKEN_BY_PROMOTION)
                    .addParameter("code", ((ShareableCodeTakenByPromotionException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass()
                .isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label",
                        ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeReservedException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateRestException.class)
                    .withErrorCode(ClientShareableCreateRestException.SHAREABLE_CODE_CONTAINS_RESERVED_WORD)
                    .addParameter("reserved_word",
                        ((ShareableCodeReservedException) cause).getReservedWord())
                    .addParameter("code", ((ShareableCodeReservedException) cause).getCode())
                    .withCause(cause)
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.KEY_TAKEN)
                    .addParameter("code", ((ShareableKeyTakenException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ClientShareableValidationRestException.class)) {
                throw (ClientShareableValidationRestException) cause;
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableRestException.class)
                .withErrorCode(ClientShareableRestException.PERSON_NOT_FOUND).addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public PersonShareableV4Response update(String accessToken, String personId, String code,
        UpdateClientShareableRequest request)
        throws UserAuthorizationRestException, ClientShareableRestException, ClientShareableValidationRestException,
        ProgramRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            ClientShareable clientShareable = clientShareableService.getByCode(authorization, code);
            if (!personService.isSamePerson(authorization.getClientId(), person.getId(),
                clientShareable.getPersonId())) {
                throw RestExceptionBuilder.newBuilder(ClientShareableRestException.class)
                    .withErrorCode(ClientShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code)
                    .build();
            }
            if (!personService.isAuthorized(authorization, clientShareable.getPersonId())) {
                throw new AuthorizationException(
                    "Not authorized to edit shareable with code " + clientShareable.getCode()
                        + " for person " + clientShareable.getPersonId()
                        + " with token " + HashAlgorithm.SHA1.hashString(authorization.getAccessToken()));
            }

            PublicProgram program = null;
            if (request.getProgramUrl().isPresent()) {
                program = getProgram(authorization, request.getProgramUrl().getValue());
            }
            Id<ProgramHandle> programId = program != null ? program.getId() : null;

            ClientShareable shareable = personService.updatePerson(authorization, clientShareable.getPersonId(),
                new LockDescription("client-person-shareable-endpoint-update"),
                (personBuilder, originalPersonProfile) -> {
                    try {
                        ClientShareableBuilder builder =
                            clientShareableService.edit(authorization.getClientId(), clientShareable, personBuilder);
                        if (programId != null) {
                            builder.withProgramId(programId);
                        }
                        request.getLabel().ifPresent(label -> {
                            if (label.isPresent()) {
                                builder.withLabel(label.get());
                            } else {
                                builder.clearLabel();
                            }
                        });
                        request.getKey().ifPresent(key -> {
                            if (key.isPresent()) {
                                builder.withKey(key.get());
                            } else {
                                builder.clearKey();
                            }
                        });
                        request.getContent().ifPresent(content -> addContent(builder, content));
                        request.getData().ifPresent(data -> addData(builder, data));

                        return builder.save();
                    } catch (ClientShareableValidationRestException | ShareableServiceException e) {
                        throw new LockClosureException(e);
                    }
                }, createConsumerEventSender(clientShareable.getProgramId(), clientShareable.getClientId()));
            return shareableRestMapper.toPersonShareableV4Response(shareable);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ClientShareableValidationRestException.class)) {
                throw (ClientShareableValidationRestException) cause;
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.KEY_TAKEN)
                    .addParameter("code", ((ShareableKeyTakenException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause)
                        .getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableRestException.class)
                    .withErrorCode(ClientShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(AuthorizationException.class)) {
                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                    .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableRestException.class)
                .withErrorCode(ClientShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableRestException.class)
                .withErrorCode(ClientShareableRestException.PERSON_NOT_FOUND).addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    private PublicProgram getProgram(Authorization authorization, String domain) throws ProgramRestException {
        if (Strings.isNullOrEmpty(domain)) {
            return programCache.getDefaultProgram(authorization.getClientId())
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build());
        }
        if (!InternetDomainName.isValid(domain)) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM)
                .build();
        }

        try {
            return programCache.getByProgramDomain(InternetDomainName.from(domain));
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.UNKNOWN_PROGRAM)
                .addParameter("program_url", domain)
                .withCause(e)
                .build();
        }
    }

    private ConsumerEventSender createConsumerEventSender(PublicProgram program) {
        return consumerEventSenderService.createConsumerEventSender()
            .withClientDomainContext(new ClientDomainContext(program.getProgramDomain().toString(), program.getId()));
    }

    private ConsumerEventSender createConsumerEventSender(Id<ProgramHandle> programId, Id<ClientHandle> clientId) {
        try {
            PublicProgram program = programCache.getById(programId, clientId);
            return createConsumerEventSender(program);
        } catch (ProgramNotFoundException e) {
            LOG.error("Could not find program {} for client {}", programId, clientId, e);
            return consumerEventSenderService.createConsumerEventSender();
        }
    }

    private void addContent(ClientShareableBuilder builder, ClientShareableContentRequest content)
        throws ClientShareableValidationRestException {

        content.getImageUrl().ifPresent(contentImageUrl -> {
            URI imageUrl = null;
            if (contentImageUrl.isPresent()) {
                imageUrl = contentImageUrl.map(RuntimePersonShareableEndpointsImpl::parseUrl)
                    .orElseThrow(() -> RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                        .withErrorCode(ClientShareableValidationRestException.CONTENT_IMAGE_URL_INVALID)
                        .addParameter("image_url", contentImageUrl).build());
            }
            builder.getContentBuilder().withImageUrl(imageUrl);
        });

        content.getUrl().ifPresent(contentUrl -> {
            URI url = null;
            if (contentUrl.isPresent()) {
                url = contentUrl.map(RuntimePersonShareableEndpointsImpl::parseUrl)
                    .orElseThrow(() -> RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                        .withErrorCode(ClientShareableValidationRestException.CONTENT_URL_INVALID)
                        .addParameter("url", contentUrl.get()).build());
            }
            try {
                builder.getContentBuilder().withUrl(url);
            } catch (ShareableBlockedUrlException e) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.CONTENT_URL_BLOCKED)
                    .addParameter("url", contentUrl)
                    .withCause(e).build();
            }
        });
        content.getDescription().ifPresent(contentDescription -> {
            String description = null;
            if (contentDescription.isPresent()) {
                description = contentDescription.get();
            }
            try {
                builder.getContentBuilder().withDescription(description);
            } catch (ShareableContentDescriptionTooLongException e) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                    .withErrorCode(ClientShareableValidationRestException.CONTENT_DESCRIPTION_LENGTH_EXCEEDED)
                    .addParameter("description", contentDescription)
                    .withCause(e).build();
            }
        });
        content.getTitle().ifPresent(contentTitle -> {
            String title = null;
            if (contentTitle.isPresent()) {
                title = contentTitle.get();
            }
            builder.getContentBuilder().withTitle(title);
        });
        content.getPartnerContentId().ifPresent(contentPartnerContentId -> {
            String partnerContentId = null;
            if (contentPartnerContentId.isPresent()) {
                partnerContentId = contentPartnerContentId.get();
            }
            builder.getContentBuilder().withPartnerContentId(partnerContentId);
        });
    }

    private void addData(ClientShareableBuilder builder, Map<String, String> data)
        throws ClientShareableValidationRestException {
        builder.removeExistingData();
        for (Map.Entry<String, String> dataEntry : data.entrySet()) {
            addData(builder, dataEntry.getKey(), dataEntry.getValue());
        }
    }

    private void addData(ClientShareableBuilder builder, String dataAttributeName, String dataAttributeValue)
        throws ClientShareableValidationRestException {
        try {
            builder.addData(dataAttributeName, dataAttributeValue);
        } catch (ShareableDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                .withErrorCode(ClientShareableValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", dataAttributeName).withCause(e).build();
        } catch (ShareableDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                .withErrorCode(ClientShareableValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", dataAttributeName).withCause(e).build();
        } catch (ShareableDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                .withErrorCode(ClientShareableValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName).withCause(e).build();
        } catch (ShareableDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationRestException.class)
                .withErrorCode(ClientShareableValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName).withCause(e).build();
        }
    }

    @Nullable
    private static URI parseUrl(String urlValue) {
        URI result = null;
        if (urlValue != null) {
            try {
                new URL(urlValue);
                result = new URI(urlValue);
            } catch (MalformedURLException | URISyntaxException ignored) {
                // ignore this exception, will return null
            }
        }
        return result;
    }
}
