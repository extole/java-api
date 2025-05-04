package com.extole.client.rest.impl.support;

import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.support.SupportEndpoints;
import com.extole.client.rest.support.SupportRequest;
import com.extole.client.rest.support.SupportResponse;
import com.extole.client.rest.support.SupportRestException;
import com.extole.client.rest.support.SupportValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.support.Support;
import com.extole.model.service.client.support.InvalidSlackChannelException;
import com.extole.model.service.client.support.SupportBuilder;
import com.extole.model.service.client.support.SupportFieldTooLongException;
import com.extole.model.service.client.support.SupportFieldValidationException;
import com.extole.model.service.client.support.SupportService;
import com.extole.model.service.user.UserNotFoundException;

@Provider
public class SupportEndpointsImpl implements SupportEndpoints {
    private final SupportService supportService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public SupportEndpointsImpl(ClientAuthorizationProvider authorizationProvider, SupportService supportService) {
        this.supportService = supportService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public SupportResponse get(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Support support = supportService.get(authorization);

            return toResponse(support);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        }
    }

    @Override
    public SupportResponse update(String accessToken, SupportRequest request)
        throws UserAuthorizationRestException, SupportValidationRestException, SupportRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SupportBuilder builder = supportService.update(authorization);

            request.getCsmUserId().ifPresent(csmUserId -> {
                if (csmUserId.isEmpty() || csmUserId.get().isEmpty()) {
                    builder.clearCsmUserId();
                } else {
                    builder.withCsmUserId(Id.valueOf(csmUserId.get()));
                }
            });
            request.getSupportUserId().ifPresent(supportUserId -> {
                if (supportUserId.isEmpty() || supportUserId.get().isEmpty()) {
                    builder.clearSupportUserId();
                } else {
                    builder.withSupportUserId(Id.valueOf(supportUserId.get()));
                }
            });
            request.getExternalSlackChannelName().ifPresent(externalSlackChannelName -> {
                if (externalSlackChannelName.isEmpty() || externalSlackChannelName.get().isEmpty()) {
                    builder.clearExternalSlackChannelName();
                } else {
                    builder.withExternalSlackChannelName(externalSlackChannelName.get());
                }
            });
            request.getSlackChannelName().ifPresent(slackChannelName -> {
                if (slackChannelName.isEmpty() || slackChannelName.get().isEmpty()) {
                    builder.clearSlackChannelName();
                } else {
                    builder.withSlackChannelName(slackChannelName.get());
                }
            });
            request.getSalesforceAccountId().ifPresent(salesforceAccountId -> {
                if (salesforceAccountId.isEmpty() || salesforceAccountId.get().isEmpty()) {
                    builder.clearSalesforceAccountId();
                } else {
                    builder.withSalesforceAccountId(salesforceAccountId.get());
                }
            });
            request.getNotes().ifPresent(notes -> {
                if (notes.isEmpty() || notes.get().isEmpty()) {
                    builder.clearNotes();
                } else {
                    builder.withNotes(notes.get());
                }
            });
            return toResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        } catch (SupportFieldTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SupportValidationRestException.class)
                .withErrorCode(SupportValidationRestException.FIELD_LENGTH_EXCEEDED)
                .addParameter("name", e.getFieldName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (InvalidSlackChannelException e) {
            throw RestExceptionBuilder.newBuilder(SupportValidationRestException.class)
                .withErrorCode(SupportValidationRestException.INVALID_SLACK_CHANNEL)
                .addParameter("name", request.getSlackChannelName())
                .withCause(e)
                .build();
        } catch (SupportFieldValidationException e) {
            throw RestExceptionBuilder.newBuilder(SupportValidationRestException.class)
                .withErrorCode(SupportValidationRestException.INVALID_SUPPORT_FIELD)
                .withCause(e)
                .addParameter("name", e.getFieldName())
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SupportRestException.class)
                .withErrorCode(SupportRestException.USER_NOT_FOUND)
                .addParameter("user_id", e.getUserId().map(Id::getValue).orElse(StringUtils.EMPTY))
                .withCause(e)
                .build();
        } catch (Exception e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private SupportResponse toResponse(Support settings) {
        return new SupportResponse(settings.getCsmUserId().map(Id::getValue),
            settings.getSupportUserId().map(Id::getValue),
            settings.getSlackChannelName(),
            settings.getExternalSlackChannelName(),
            settings.getSalesforceAccountId(),
            settings.getNotes());
    }
}
