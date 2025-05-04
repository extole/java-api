package com.extole.client.rest.impl.prehandler.v2;

import static com.extole.client.rest.prehandler.v2.PrehandlerValidationV2RestException.JSON_ACTION;
import static com.extole.client.rest.prehandler.v2.PrehandlerValidationV2RestException.JSON_ACTION_TYPE;
import static com.extole.client.rest.prehandler.v2.PrehandlerValidationV2RestException.JSON_CONDITION;
import static com.extole.client.rest.prehandler.v2.PrehandlerValidationV2RestException.JSON_CONDITION_TYPE;
import static com.extole.client.rest.prehandler.v2.PrehandlerValidationV2RestException.JSON_ERRORS;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.EnumUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.prehandler.v2.PrehandlerCreateV2Request;
import com.extole.client.rest.prehandler.v2.PrehandlerUpdateV2Request;
import com.extole.client.rest.prehandler.v2.PrehandlerV2ActionType;
import com.extole.client.rest.prehandler.v2.PrehandlerV2ConditionType;
import com.extole.client.rest.prehandler.v2.PrehandlerV2Endpoints;
import com.extole.client.rest.prehandler.v2.PrehandlerV2Response;
import com.extole.client.rest.prehandler.v2.PrehandlerV2RestException;
import com.extole.client.rest.prehandler.v2.PrehandlerValidationV2RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.prehandler_legacy.LegacyPrehandler;
import com.extole.model.entity.prehandler_legacy.LegacyPrehandlerActionType;
import com.extole.model.entity.prehandler_legacy.LegacyPrehandlerConditionType;
import com.extole.model.service.prehandler_legacy.LegacyPrehandlerBuilder;
import com.extole.model.service.prehandler_legacy.LegacyPrehandlerNotFoundException;
import com.extole.model.service.prehandler_legacy.LegacyPrehandlerService;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerActionLengthException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerConditionLengthException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerEventNameLengthException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerEventNameMissingException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerIllegalCharacterInEventNameException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerIllegalCharacterInParameterNameException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerInvalidActionException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerInvalidConditionException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerMissingActionException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerMissingActionTypeException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerMissingConditionException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerMissingConditionTypeException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerParameterNameLengthException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerParameterNameMissingException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerParameterValueLengthException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerParameterValueMissingException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerUnknownActionTypeException;
import com.extole.model.service.prehandler_legacy.exceptions.PrehandlerUnknownConditionTypeException;

@Deprecated // TODO to be removed in ENG-13399
@Provider
public class PrehandlerV2EndpointsImpl implements PrehandlerV2Endpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final LegacyPrehandlerService prehandlerService;

    @Inject
    public PrehandlerV2EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        LegacyPrehandlerService prehandlerService) {
        this.authorizationProvider = authorizationProvider;
        this.prehandlerService = prehandlerService;
    }

    @Override
    public List<PrehandlerV2Response> list(String accessToken, @Nullable String conditionType,
        @Nullable String condition, @Nullable String actionType, @Nullable String action)
        throws UserAuthorizationRestException, PrehandlerValidationV2RestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return prehandlerService.createPrehandlerQueryBuilder(authorization).withAction(action)
                .withActionType(mapActionType(actionType)).withCondition(condition)
                .withConditionType(mapConditionType(conditionType)).list()
                .stream().map(this::toPrehandlerResponse).collect(toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerUnknownActionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.UNKNOWN_ACTION_TYPE)
                .addParameter(JSON_ACTION_TYPE, actionType).withCause(e).build();
        } catch (PrehandlerUnknownConditionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.UNKNOWN_CONDITION_TYPE)
                .addParameter(JSON_CONDITION_TYPE, conditionType).withCause(e).build();
        }
    }

    @Override
    public PrehandlerV2Response get(String accessToken, String prehandlerId)
        throws UserAuthorizationRestException, PrehandlerV2RestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return toPrehandlerResponse(prehandlerService.get(authorization, Id.valueOf(prehandlerId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (LegacyPrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerV2RestException.class)
                .withErrorCode(PrehandlerV2RestException.INVALID_PREHANDLER_ID)
                .addParameter("prehandler_id", prehandlerId)
                .addParameter("client_id", authorization.getClientId()).withCause(e).build();
        }
    }

    @Override
    public PrehandlerV2Response create(String accessToken, PrehandlerCreateV2Request request)
        throws UserAuthorizationRestException, PrehandlerValidationV2RestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        String condition = request.getCondition();
        String action = request.getAction();
        try {
            LegacyPrehandlerBuilder builder = prehandlerService.create(authorization);
            builder.withConditionAndConditionType(condition, mapConditionType(request.getConditionType().name()))
                .withActionAndActionType(action, mapActionType(request.getActionType().name()));
            return toPrehandlerResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerConditionLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.CONDITION_LENGTH_OUT_OF_RANGE)
                .addParameter(JSON_CONDITION, condition).withCause(e).build();
        } catch (PrehandlerActionLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.ACTION_LENGTH_OUT_OF_RANGE)
                .addParameter(JSON_ACTION, action).withCause(e).build();
        } catch (PrehandlerMissingActionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_ACTION).withCause(e).build();
        } catch (PrehandlerMissingActionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_ACTION_TYPE).withCause(e).build();
        } catch (PrehandlerMissingConditionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_CONDITION).withCause(e).build();
        } catch (PrehandlerMissingConditionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_CONDITION_TYPE).withCause(e).build();
        } catch (PrehandlerInvalidConditionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.INVALID_CONDITION)
                .addParameter(JSON_CONDITION_TYPE, request.getConditionType())
                .addParameter(JSON_CONDITION, condition)
                .addParameter(JSON_ERRORS, e.getErrors().isEmpty() ? e.getMessage() : e.getErrors()).withCause(e)
                .build();
        } catch (PrehandlerUnknownActionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.UNKNOWN_ACTION_TYPE)
                .addParameter(JSON_ACTION_TYPE, request.getActionType()).withCause(e).build();
        } catch (PrehandlerInvalidActionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.INVALID_ACTION)
                .addParameter(JSON_ACTION_TYPE, request.getActionType())
                .addParameter(JSON_ACTION, action)
                .addParameter(JSON_ERRORS, e.getErrors().isEmpty() ? e.getMessage() : e.getErrors()).withCause(e)
                .build();
        } catch (PrehandlerUnknownConditionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.UNKNOWN_CONDITION_TYPE)
                .addParameter(JSON_CONDITION_TYPE, request.getConditionType().name()).withCause(e).build();
        } catch (PrehandlerIllegalCharacterInParameterNameException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("parameter_name", condition).withCause(e).build();
        } catch (PrehandlerParameterNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("parameter_name", e.getParameterName()).withCause(e).build();
        } catch (PrehandlerParameterValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("parameter_value", e.getParameterValue()).withCause(e).build();
        } catch (PrehandlerIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name", condition).withCause(e).build();
        } catch (PrehandlerEventNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", e.getEventName()).withCause(e).build();
        } catch (PrehandlerEventNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.EVENT_NAME_MISSING).withCause(e).build();
        } catch (PrehandlerParameterValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_VALUE_MISSING).withCause(e).build();
        } catch (PrehandlerParameterNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_NAME_MISSING).withCause(e).build();
        }
    }

    @Override
    public PrehandlerV2Response update(String accessToken, String prehandlerId,
        PrehandlerUpdateV2Request updateRequest) throws UserAuthorizationRestException,
        PrehandlerValidationV2RestException, PrehandlerV2RestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        String condition = updateRequest.getCondition();
        try {
            LegacyPrehandlerBuilder builder = prehandlerService.update(authorization, Id.valueOf(prehandlerId));
            builder.withConditionAndConditionType(updateRequest.getCondition(),
                getConditionTypeForUpdate(updateRequest));
            builder.withActionAndActionType(updateRequest.getAction(), getActionTypeForUpdate(updateRequest));
            return toPrehandlerResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (LegacyPrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerV2RestException.class)
                .withErrorCode(PrehandlerV2RestException.INVALID_PREHANDLER_ID)
                .addParameter("prehandler_id", prehandlerId)
                .addParameter("client_id", authorization.getClientId()).withCause(e).build();
        } catch (PrehandlerConditionLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.CONDITION_LENGTH_OUT_OF_RANGE)
                .addParameter(JSON_CONDITION, updateRequest.getCondition()).withCause(e).build();
        } catch (PrehandlerActionLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.ACTION_LENGTH_OUT_OF_RANGE)
                .addParameter(JSON_ACTION, updateRequest.getAction()).withCause(e).build();
        } catch (PrehandlerMissingConditionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_CONDITION).withCause(e).build();
        } catch (PrehandlerMissingActionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_ACTION).withCause(e).build();
        } catch (PrehandlerInvalidConditionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.INVALID_CONDITION)
                .addParameter(JSON_CONDITION_TYPE, updateRequest.getConditionType())
                .addParameter(JSON_CONDITION, updateRequest.getCondition())
                .addParameter(JSON_ERRORS, e.getErrors().isEmpty() ? e.getMessage() : e.getErrors()).withCause(e)
                .build();
        } catch (PrehandlerUnknownActionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.UNKNOWN_ACTION_TYPE)
                .addParameter(JSON_ACTION_TYPE, updateRequest.getActionType()).withCause(e).build();
        } catch (PrehandlerInvalidActionException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.INVALID_ACTION)
                .addParameter(JSON_ACTION_TYPE, updateRequest.getActionType())
                .addParameter(JSON_ACTION, updateRequest.getAction()).withCause(e).build();
        } catch (PrehandlerIllegalCharacterInParameterNameException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("parameter_name", condition).withCause(e).build();
        } catch (PrehandlerParameterNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("parameter_name", e.getParameterName()).withCause(e).build();
        } catch (PrehandlerParameterValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("parameter_value", e.getParameterValue()).withCause(e).build();
        } catch (PrehandlerIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name", condition).withCause(e).build();
        } catch (PrehandlerEventNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", e.getEventName()).withCause(e).build();
        } catch (PrehandlerEventNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.EVENT_NAME_MISSING).withCause(e).build();
        } catch (PrehandlerParameterValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_VALUE_MISSING).withCause(e).build();
        } catch (PrehandlerParameterNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.PARAMETER_NAME_MISSING).withCause(e).build();
        } catch (PrehandlerMissingActionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_ACTION_TYPE).withCause(e).build();
        } catch (PrehandlerMissingConditionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.MISSING_CONDITION_TYPE).withCause(e).build();
        } catch (PrehandlerUnknownConditionTypeException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerValidationV2RestException.class)
                .withErrorCode(PrehandlerValidationV2RestException.UNKNOWN_CONDITION_TYPE)
                .addParameter(JSON_CONDITION_TYPE, updateRequest.getConditionType()).withCause(e).build();
        }
    }

    @Override
    public PrehandlerV2Response delete(String accessToken, String prehandlerId)
        throws UserAuthorizationRestException,
        PrehandlerV2RestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return toPrehandlerResponse(prehandlerService.delete(authorization, Id.valueOf(prehandlerId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (LegacyPrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerV2RestException.class)
                .withErrorCode(PrehandlerV2RestException.INVALID_PREHANDLER_ID)
                .addParameter("prehandler_id", prehandlerId)
                .addParameter("client_id", authorization.getClientId()).withCause(e).build();
        }
    }

    private LegacyPrehandlerActionType
        getActionTypeForUpdate(PrehandlerUpdateV2Request updateRequest) throws PrehandlerUnknownActionTypeException {
        return updateRequest.getActionType() != null ? mapActionType(updateRequest.getActionType().name()) : null;
    }

    private LegacyPrehandlerConditionType getConditionTypeForUpdate(
        PrehandlerUpdateV2Request updateRequest) throws PrehandlerUnknownConditionTypeException {
        return updateRequest.getConditionType() != null ? mapConditionType(updateRequest.getConditionType().name())
            : null;
    }

    private PrehandlerV2Response toPrehandlerResponse(LegacyPrehandler prehandler) {
        return new PrehandlerV2Response(
            prehandler.getId().getValue(),
            prehandler.getCondition(),
            prehandler.getConditionType() != null
                ? PrehandlerV2ConditionType.valueOf(prehandler.getConditionType().name())
                : null,
            prehandler.getAction(),
            prehandler.getActionType() != null ? PrehandlerV2ActionType.valueOf(prehandler.getActionType().name())
                : null);
    }

    private LegacyPrehandlerConditionType
        mapConditionType(@Nullable String conditionType) throws PrehandlerUnknownConditionTypeException {
        if (Objects.isNull(conditionType)) {
            return null;
        }
        if (!EnumUtils.isValidEnum(PrehandlerV2ConditionType.class, conditionType)) {
            throw new PrehandlerUnknownConditionTypeException("Invalid condition type: " + conditionType);
        }
        return LegacyPrehandlerConditionType.valueOf(conditionType);
    }

    private LegacyPrehandlerActionType
        mapActionType(@Nullable String actionType) throws PrehandlerUnknownActionTypeException {
        if (Objects.isNull(actionType)) {
            return null;
        }
        if (!EnumUtils.isValidEnum(PrehandlerV2ActionType.class, actionType)) {
            throw new PrehandlerUnknownActionTypeException("Invalid action type: " + actionType);
        }
        return LegacyPrehandlerActionType.valueOf(actionType);
    }
}
