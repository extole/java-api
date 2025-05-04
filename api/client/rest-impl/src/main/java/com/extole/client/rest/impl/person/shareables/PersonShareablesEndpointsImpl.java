package com.extole.client.rest.impl.person.shareables;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.impl.person.PersonShareableRestMapper;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.shareables.PersonShareableContentRequest;
import com.extole.client.rest.person.shareables.PersonShareableCreateRequest;
import com.extole.client.rest.person.shareables.PersonShareableResponse;
import com.extole.client.rest.person.shareables.PersonShareableRestException;
import com.extole.client.rest.person.shareables.PersonShareableUpdateRequest;
import com.extole.client.rest.person.shareables.PersonShareableValidationRestException;
import com.extole.client.rest.person.shareables.PersonShareablesEndpoints;
import com.extole.client.rest.person.shareables.PersonShareablesListRequest;
import com.extole.client.rest.person.shareables.PersonShareablesListRestException;
import com.extole.client.rest.program.ProgramRestException;
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
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.PersonShareableQueryBuilder;
import com.extole.person.service.shareable.PersonNotRewardableException;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
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
import com.extole.person.service.shareable.ShareableServiceException;

@Provider
public class PersonShareablesEndpointsImpl implements PersonShareablesEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(PersonShareablesEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final FullPersonService fullPersonService;
    private final ClientShareableService clientShareableService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final ProgramDomainCache programCache;
    private final PersonShareableRestMapper personShareableRestMapper;

    @Autowired
    public PersonShareablesEndpointsImpl(PersonService personService,
        FullPersonService fullPersonService,
        ClientShareableService clientShareableService,
        ConsumerEventSenderService consumerEventSenderService,
        ProgramDomainCache programCache,
        PersonShareableRestMapper personShareableRestMapper,
        ClientAuthorizationProvider authorizationProvider) {
        this.personService = personService;
        this.fullPersonService = fullPersonService;
        this.clientShareableService = clientShareableService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.programCache = programCache;
        this.personShareableRestMapper = personShareableRestMapper;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public PersonShareableResponse get(String accessToken, String personId, String code, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareableRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<Shareable> personShareable =
                personService.getPerson(userAuthorization, Id.valueOf(personId))
                    .getShareables().stream()
                    .filter(shareable -> shareable.getCode().equalsIgnoreCase(code))
                    .findFirst();

            if (personShareable.isEmpty()) {
                personShareable = fullPersonService.createShareableQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withCodes(List.of(code)).withLimit(1)
                    .list().stream()
                    .findFirst();
            }

            return personShareable.flatMap(shareable -> {
                try {
                    return Optional.of(personShareableRestMapper.toPersonShareableResponse(shareable, timeZone));
                } catch (ProgramNotFoundException e) {
                    LOG.warn("Unable to find program domain for shareable: {}", shareable, e);
                    return Optional.empty();
                }
            }).orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonShareableRestException.class)
                .withErrorCode(PersonShareableRestException.SHAREABLE_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("code", code)
                .build());
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
    public List<PersonShareableResponse> list(String accessToken, String personId, PersonShareablesListRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareablesListRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Multimap<String, Object> dataValues = parseDataValues(request.getDataValues());
            PersonShareableQueryBuilder builder =
                fullPersonService.createShareableQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withLabels(request.getLabels())
                    .withDataKeys(request.getDataKeys())
                    .withDataValues(dataValues)
                    .withOffset(request.getOffset())
                    .withLimit(request.getLimit());
            return builder.list().stream().flatMap(shareable -> {
                try {
                    return Stream.of(personShareableRestMapper.toPersonShareableResponse(shareable, timeZone));
                } catch (ProgramNotFoundException e) {
                    LOG.warn("Unable to find program domain for shareable: {}", shareable, e);
                    return Stream.empty();
                }
            }).collect(Collectors.toList());
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

    @Override
    public PersonShareableResponse create(String accessToken, String personId, PersonShareableCreateRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, ProgramRestException, PersonRestException, PersonShareableRestException,
        PersonShareableValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        PublicProgram program = getProgram(authorization, request.getProgramUrl().getValue());

        if (Strings.isNullOrEmpty(request.getLabel())) {
            throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                .withErrorCode(PersonShareableValidationRestException.LABEL_IS_MISSING).build();
        }
        try {
            ClientShareable shareable =
                personService.updatePerson(authorization, Id.valueOf(personId),
                    new LockDescription("person-shareable-endpoint-create"),
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
                        } catch (PersonShareableValidationRestException | ShareableServiceException e) {
                            throw new LockClosureException(e);
                        }
                    }, createConsumerEventSender(program));
            return personShareableRestMapper.toPersonShareableResponse(shareable, timeZone);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ShareableFieldLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.CODE_LENGTH_OUT_OF_RANGE)
                    .addParameter("code", request.getCode()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldValueException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.CODE_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("code", request.getCode()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonNotRewardableException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.PERSON_NOT_REWARDABLE)
                    .addParameter("person_id", ((PersonNotRewardableException) cause).getPersonId())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.CODE_TAKEN)
                    .addParameter("code", ((ShareableCodeTakenException) cause).getCode())
                    .addParameter("code_suggestions", ((ShareableCodeTakenException) cause).getSuggestions())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenByPromotionException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.CODE_TAKEN_BY_PROMOTION)
                    .addParameter("code", ((ShareableCodeTakenByPromotionException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass()
                .isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label",
                        ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeReservedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.SHAREABLE_CODE_CONTAINS_RESERVED_WORD)
                    .addParameter("reserved_word",
                        ((ShareableCodeReservedException) cause).getReservedWord())
                    .addParameter("code", ((ShareableCodeReservedException) cause).getCode())
                    .withCause(cause)
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.KEY_TAKEN)
                    .addParameter("code", ((ShareableKeyTakenException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonShareableValidationRestException.class)) {
                throw (PersonShareableValidationRestException) cause;
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public PersonShareableResponse update(String accessToken, String personId, String code,
        PersonShareableUpdateRequest request, ZoneId timezone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareableRestException,
        PersonShareableValidationRestException, ProgramRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            ClientShareable clientShareable = clientShareableService.getByCode(authorization, code);
            if (!personService.isSamePerson(authorization.getClientId(), person.getId(),
                clientShareable.getPersonId())) {
                throw RestExceptionBuilder.newBuilder(PersonShareableRestException.class)
                    .withErrorCode(PersonShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code)
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
                new LockDescription("person-shareable-endpoint-update"),
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
                    } catch (PersonShareableValidationRestException | ShareableServiceException e) {
                        throw new LockClosureException(e);
                    }
                }, createConsumerEventSender(clientShareable.getProgramId(), clientShareable.getClientId()));
            return personShareableRestMapper.toPersonShareableResponse(shareable, timezone);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonShareableValidationRestException.class)) {
                throw (PersonShareableValidationRestException) cause;
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.KEY_TAKEN)
                    .addParameter("code", ((ShareableKeyTakenException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause)
                        .getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonShareableRestException.class)
                    .withErrorCode(PersonShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(AuthorizationException.class)) {
                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                    .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonShareableRestException.class)
                .withErrorCode(PersonShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private void addData(ClientShareableBuilder builder, Map<String, String> data)
        throws PersonShareableValidationRestException {
        builder.removeExistingData();
        for (Map.Entry<String, String> dataEntry : data.entrySet()) {
            addData(builder, dataEntry.getKey(), dataEntry.getValue());
        }
    }

    private void addData(ClientShareableBuilder builder, String dataAttributeName, String dataAttributeValue)
        throws PersonShareableValidationRestException {
        try {
            builder.addData(dataAttributeName, dataAttributeValue);
        } catch (ShareableDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                .withErrorCode(PersonShareableValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", dataAttributeName).withCause(e).build();
        } catch (ShareableDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                .withErrorCode(PersonShareableValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", dataAttributeName).withCause(e).build();
        } catch (ShareableDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                .withErrorCode(PersonShareableValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName).withCause(e).build();
        } catch (ShareableDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                .withErrorCode(PersonShareableValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName).withCause(e).build();
        }
    }

    private void addContent(ClientShareableBuilder builder, PersonShareableContentRequest content)
        throws PersonShareableValidationRestException {

        content.getImageUrl().ifPresent(contentImageUrl -> {
            URI imageUrl = null;
            if (contentImageUrl.isPresent()) {
                imageUrl = contentImageUrl.map(PersonShareablesEndpointsImpl::parseUrl)
                    .orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                        .withErrorCode(PersonShareableValidationRestException.CONTENT_IMAGE_URL_INVALID)
                        .addParameter("image_url", contentImageUrl).build());
            }
            builder.getContentBuilder().withImageUrl(imageUrl);
        });

        content.getUrl().ifPresent(contentUrl -> {
            URI url = null;
            if (contentUrl.isPresent()) {
                url = contentUrl.map(PersonShareablesEndpointsImpl::parseUrl)
                    .orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                        .withErrorCode(PersonShareableValidationRestException.CONTENT_URL_INVALID)
                        .addParameter("url", contentUrl.get()).build());
            }
            try {
                builder.getContentBuilder().withUrl(url);
            } catch (ShareableBlockedUrlException e) {
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.CONTENT_URL_BLOCKED)
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
                throw RestExceptionBuilder.newBuilder(PersonShareableValidationRestException.class)
                    .withErrorCode(PersonShareableValidationRestException.CONTENT_DESCRIPTION_LENGTH_EXCEEDED)
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

    private Multimap<String, Object> parseDataValues(List<String> dataValues) throws PersonShareablesListRestException {
        Multimap<String, Object> result = ArrayListMultimap.create();
        for (String dataValue : dataValues) {
            int delimiterLocation = dataValue.indexOf(":");
            if (delimiterLocation > 0) {
                result.put(dataValue.substring(0, delimiterLocation), dataValue.substring(delimiterLocation + 1));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonShareablesListRestException.class)
                    .withErrorCode(PersonShareablesListRestException.INVALID_DATA_VALUE)
                    .addParameter("data_values", dataValue)
                    .build();
            }
        }

        return result;
    }
}
