package com.extole.client.rest.impl.promotion;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.promotion.PromotionLinkContentRequest;
import com.extole.client.rest.promotion.PromotionLinkContentResponse;
import com.extole.client.rest.promotion.PromotionLinkCreateRequest;
import com.extole.client.rest.promotion.PromotionLinkCreateRestException;
import com.extole.client.rest.promotion.PromotionLinkEndpoints;
import com.extole.client.rest.promotion.PromotionLinkResponse;
import com.extole.client.rest.promotion.PromotionLinkRestException;
import com.extole.client.rest.promotion.PromotionLinkUpdateRequest;
import com.extole.client.rest.promotion.PromotionLinkValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.entity.promotion.PromotionLink;
import com.extole.model.service.label.LabelIllegalCharacterInNameException;
import com.extole.model.service.label.LabelNameLengthException;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.promotion.PromotionLinkBuilder;
import com.extole.model.service.promotion.PromotionLinkCodeAlreadyDefinedException;
import com.extole.model.service.promotion.PromotionLinkCodeDuplicateException;
import com.extole.model.service.promotion.PromotionLinkCodeInvalidException;
import com.extole.model.service.promotion.PromotionLinkCodeLengthException;
import com.extole.model.service.promotion.PromotionLinkCodeMissingException;
import com.extole.model.service.promotion.PromotionLinkCodeReservedException;
import com.extole.model.service.promotion.PromotionLinkContentBuilder;
import com.extole.model.service.promotion.PromotionLinkDataAttributeNameInvalidException;
import com.extole.model.service.promotion.PromotionLinkDataAttributeNameLengthException;
import com.extole.model.service.promotion.PromotionLinkDataAttributeValueInvalidException;
import com.extole.model.service.promotion.PromotionLinkDataAttributeValueLengthException;
import com.extole.model.service.promotion.PromotionLinkDescriptionLengthOutOfRangeException;
import com.extole.model.service.promotion.PromotionLinkNotFoundException;
import com.extole.model.service.promotion.PromotionLinkService;
import com.extole.model.shared.program.ProgramDomainCache;

@Provider
public class PromotionLinkEndpointsImpl implements PromotionLinkEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(PromotionLinkEndpointsImpl.class);

    private static final String PARAMETER_DATA_PREFIX = "data.";

    private final UriInfo uriInfo;
    private final ClientAuthorizationProvider authorizationProvider;
    private final PromotionLinkService promotionLinkService;
    private final ProgramDomainCache programDomainCache;

    @Autowired
    public PromotionLinkEndpointsImpl(
        @Context UriInfo uriInfo,
        PromotionLinkService promotionLinkService,
        ClientAuthorizationProvider authorizationProvider,
        ProgramDomainCache programDomainCache) {
        this.uriInfo = uriInfo;
        this.promotionLinkService = promotionLinkService;
        this.authorizationProvider = authorizationProvider;
        this.programDomainCache = programDomainCache;
    }

    @Override
    public List<PromotionLinkResponse> getPromotionLinks(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Map<String, String> data = uriInfo.getQueryParameters().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(PARAMETER_DATA_PREFIX))
            .collect(Collectors.toMap(entry -> StringUtils.removeStart(entry.getKey(), PARAMETER_DATA_PREFIX),
                entry -> Iterables.getFirst(entry.getValue(), null)));

        List<PromotionLink> promotionLinks;
        try {
            if (data.isEmpty()) {
                promotionLinks = promotionLinkService.getAll(authorization);
            } else {
                promotionLinks = promotionLinkService.getByData(authorization, data);
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        List<PromotionLinkResponse> result = new ArrayList<>(promotionLinks.size());
        for (PromotionLink promotionLink : promotionLinks) {
            try {
                result.add(toPromotionLinkResponse(promotionLink));
            } catch (ProgramNotFoundException e) {
                LOG.warn("Unable to find program for promotion link: {}", promotionLink, e);
            }
        }
        return result;
    }

    @Override
    public PromotionLinkResponse getPromotionLink(String accessToken, String code)
        throws UserAuthorizationRestException, PromotionLinkRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PromotionLink promotionLink = promotionLinkService.getByCode(authorization, code);
            return toPromotionLinkResponse(promotionLink);
        } catch (PromotionLinkNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkRestException.class)
                .withErrorCode(PromotionLinkRestException.PROMOTION_LINK_NOT_FOUND)
                .addParameter("code", code)
                .withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PromotionLinkResponse create(String accessToken, PromotionLinkCreateRequest request)
        throws UserAuthorizationRestException, PromotionLinkCreateRestException, PromotionLinkValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PromotionLinkBuilder builder = promotionLinkService.createPromotionLink(authorization)
                .withCode(request.getCode())
                .withKey(request.getKey());
            addProgramUrl(builder, request.getProgramUrl());
            request.getContent().ifPresent(content -> addContent(builder, content));
            request.getData().ifPresent(data -> addData(builder, data));
            if (!request.getLabel().isOmitted() && !Strings.isNullOrEmpty(request.getLabel().getValue())) {
                builder.withLabel(request.getLabel().getValue());
            }
            if (request.getDescription().isPresent()) {
                builder.withDescription(request.getDescription().getValue());
            }
            PromotionLink promotionLink = builder.save();
            return toPromotionLinkResponse(promotionLink);
        } catch (PromotionLinkCodeMissingException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkCreateRestException.class)
                .withErrorCode(PromotionLinkCreateRestException.CODE_MISSING)
                .addParameter("code", request.getCode())
                .withCause(e).build();
        } catch (PromotionLinkCodeReservedException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.CODE_CONTAINS_RESERVED_WORD)
                .addParameter("code", request.getCode())
                .addParameter("reserved_word", e.getReservedWord())
                .withCause(e).build();
        } catch (PromotionLinkCodeDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.CODE_TAKEN)
                .addParameter("code", request.getCode())
                .withCause(e).build();
        } catch (PromotionLinkCodeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.CODE_INVALID)
                .addParameter("code", request.getCode())
                .withCause(e).build();
        } catch (PromotionLinkCodeLengthException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.CODE_LENGTH_OUT_OF_RANGE)
                .addParameter("code", request.getCode())
                .withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.PROGRAM_NOT_FOUND)
                .addParameter("program_url", request.getProgramUrl())
                .addParameter("client_id", authorization.getClientId())
                .withCause(e).build();
        } catch (PromotionLinkCodeAlreadyDefinedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        } catch (LabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.LABEL_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("label", e.getLabelName())
                .withCause(e).build();
        } catch (LabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.PROMOTION_LABEL_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PromotionLinkDescriptionLengthOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        }
    }

    @Override
    public PromotionLinkResponse update(String accessToken, String code, PromotionLinkUpdateRequest request)
        throws UserAuthorizationRestException, PromotionLinkRestException, PromotionLinkValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PromotionLinkBuilder builder = promotionLinkService.updatePromotionLink(authorization, code);
            request.getKey().ifPresent(key -> {
                if (!Strings.isNullOrEmpty(key)) {
                    builder.withKey(key);
                }
            });
            request.getContent().ifPresent(content -> {
                if (content.isPresent()) {
                    addContent(builder, content.get());
                } else {
                    builder.withContent().clear();
                }
            });
            request.getData().ifPresent(data -> {
                if (data.isPresent()) {
                    addData(builder, data.get());
                } else {
                    builder.clearData();
                }
            });

            if (!request.getProgramUrl().isOmitted()) {
                addProgramUrl(builder, request.getProgramUrl().getValue());
            }

            if (!request.getLabel().isOmitted()) {
                Optional<String> label = request.getLabel().getValue();
                if (label.isPresent()) {
                    builder.withLabel(label.get());
                } else {
                    builder.clearLabel();
                }
            }

            if (request.getDescription().isPresent()) {
                Optional<String> description = request.getDescription().getValue();
                if (description.isPresent()) {
                    builder.withDescription(description.get());
                } else {
                    builder.clearDescription();
                }
            }
            return toPromotionLinkResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PromotionLinkNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkRestException.class)
                .withErrorCode(PromotionLinkRestException.PROMOTION_LINK_NOT_FOUND)
                .addParameter("code", code)
                .withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.PROGRAM_NOT_FOUND)
                .addParameter("program_url", request.getProgramUrl())
                .addParameter("client_id", authorization.getClientId())
                .withCause(e).build();
        } catch (LabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.LABEL_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("label", e.getLabelName())
                .withCause(e).build();
        } catch (LabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.PROMOTION_LABEL_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        } catch (PromotionLinkCodeDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.CODE_TAKEN)
                .addParameter("code", code)
                .withCause(e).build();
        } catch (PromotionLinkDescriptionLengthOutOfRangeException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        }
    }

    private void addProgramUrl(PromotionLinkBuilder builder, String programUrl)
        throws ProgramNotFoundException, PromotionLinkValidationRestException {
        if (Strings.isNullOrEmpty(programUrl)) {
            return;
        }
        if (!InternetDomainName.isValid(programUrl)) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.PROGRAM_URL_INVALID)
                .addParameter("program_url", programUrl)
                .build();
        }
        builder.withProgramUrl(InternetDomainName.from(programUrl));
    }

    private void addContent(PromotionLinkBuilder builder, PromotionLinkContentRequest content)
        throws PromotionLinkValidationRestException {
        PromotionLinkContentBuilder contentBuilder = builder.withContent();
        content.getContentId().ifPresent(contentId -> {
            if (contentId.isPresent()) {
                contentBuilder.withContentId(contentId.get());
            } else {
                contentBuilder.clearContentId();
            }
        });

        content.getTitle().ifPresent(title -> {
            if (title.isPresent()) {
                contentBuilder.withTitle(title.get());
            } else {
                contentBuilder.clearTitle();
            }
        });

        content.getDescription().ifPresent(description -> {
            if (description.isPresent()) {
                contentBuilder.withDescription(description.get());
            } else {
                contentBuilder.clearDescription();
            }
        });

        content.getUrl().ifPresent(url -> {
            if (url.isPresent()) {
                try {
                    contentBuilder.withUrl(new URI(url.get()));
                } catch (URISyntaxException e) {
                    throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                        .withErrorCode(PromotionLinkValidationRestException.CONTENT_URL_INVALID)
                        .addParameter("url", content.getUrl())
                        .withCause(e).build();
                }
            } else {
                contentBuilder.clearUrl();
            }
        });

        content.getImageUrl().ifPresent(imageUrl -> {
            if (imageUrl.isPresent()) {
                try {
                    contentBuilder.withImageUrl(new URI(imageUrl.get()));
                } catch (URISyntaxException e) {
                    throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                        .withErrorCode(PromotionLinkValidationRestException.CONTENT_IMAGE_URL_INVALID)
                        .addParameter("url", content.getImageUrl())
                        .withCause(e).build();
                }
            } else {
                contentBuilder.clearImageUrl();
            }
        });
    }

    private void addData(PromotionLinkBuilder builder, Map<String, String> data)
        throws PromotionLinkValidationRestException {
        builder.clearData();
        for (Entry<String, String> dataEntry : data.entrySet()) {
            addData(builder, dataEntry.getKey(), dataEntry.getValue());
        }
    }

    private void addData(PromotionLinkBuilder builder, String dataAttributeName, String dataAttributeValue)
        throws PromotionLinkValidationRestException {
        try {
            builder.addData(dataAttributeName, dataAttributeValue);
        } catch (PromotionLinkDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (PromotionLinkDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (PromotionLinkDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (PromotionLinkDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(PromotionLinkValidationRestException.class)
                .withErrorCode(PromotionLinkValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        }
    }

    private PromotionLinkResponse toPromotionLinkResponse(PromotionLink promotionLink) throws ProgramNotFoundException {
        PublicProgram program =
            programDomainCache.getForwardedById(promotionLink.getProgramId(), promotionLink.getClientId());
        String pathParameter =
            promotionLink.getCode().equals(PromotionLink.BLANK_CODE) ? "" : promotionLink.getCode();
        URI link = URI.create(
            String.format("%s://%s/%s", program.getScheme(), program.getProgramDomain(), pathParameter));
        return new PromotionLinkResponse(promotionLink.getCode(), promotionLink.getKey(),
            program.getProgramDomain().toString(), link.toString(), toPromotionLinkContent(promotionLink.getContent()),
            promotionLink.getData(), promotionLink.getLabel(), promotionLink.getDescription());
    }

    private PromotionLinkContentResponse
        toPromotionLinkContent(com.extole.model.entity.promotion.PromotionLinkContent content) {
        return PromotionLinkContentResponse.builder().withContentId(content.getContentId())
            .withTitle(content.getTitle())
            .withDescription(content.getDescription())
            .withUrl(Optional.ofNullable(content.getUrl()).map(URI::toString).orElse(null))
            .withImageUrl(Optional.ofNullable(content.getImageUrl()).map(URI::toString).orElse(null)).build();
    }

}
