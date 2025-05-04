package com.extole.reporting.rest.impl.posthandler;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.report.posthandler.ReportPostHandler;
import com.extole.reporting.rest.posthandler.ReportPostHandlerActionValidationRestException;
import com.extole.reporting.rest.posthandler.ReportPostHandlerConditionValidationRestException;
import com.extole.reporting.rest.posthandler.ReportPostHandlerEndpoints;
import com.extole.reporting.rest.posthandler.ReportPostHandlerRequest;
import com.extole.reporting.rest.posthandler.ReportPostHandlerResponse;
import com.extole.reporting.rest.posthandler.ReportPostHandlerRestException;
import com.extole.reporting.rest.posthandler.ReportPostHandlerValidationRestException;
import com.extole.reporting.rest.posthandler.action.ReportPostHandlerActionRequest;
import com.extole.reporting.rest.posthandler.action.ReportPostHandlerActionResponse;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionRequest;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionResponse;
import com.extole.reporting.service.posthandler.ReportPostHandlerBuilder;
import com.extole.reporting.service.posthandler.ReportPostHandlerEmptyActionsException;
import com.extole.reporting.service.posthandler.ReportPostHandlerEmptyConditionsException;
import com.extole.reporting.service.posthandler.ReportPostHandlerMissingNameException;
import com.extole.reporting.service.posthandler.ReportPostHandlerNameAlreadyExistsException;
import com.extole.reporting.service.posthandler.ReportPostHandlerNotFoundException;
import com.extole.reporting.service.posthandler.ReportPostHandlerService;

@Provider
public class ReportPostHandlerEndpointsImpl implements ReportPostHandlerEndpoints {

    private final ReportPostHandlerService reportPostHandlerService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportPostHandlerResponseMappersRepository mappersRepository;
    private final ReportPostHandlerRequestMappersRepository uploadersRepository;

    @Inject
    public ReportPostHandlerEndpointsImpl(ReportPostHandlerService reportPostHandlerService,
        ClientAuthorizationProvider authorizationProvider, ReportPostHandlerResponseMappersRepository mappersRepository,
        ReportPostHandlerRequestMappersRepository uploadersRepository) {
        this.reportPostHandlerService = reportPostHandlerService;
        this.authorizationProvider = authorizationProvider;
        this.mappersRepository = mappersRepository;
        this.uploadersRepository = uploadersRepository;
    }

    @Override
    public ReportPostHandlerResponse create(String accessToken, ReportPostHandlerRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, ReportPostHandlerValidationRestException,
        ReportPostHandlerActionValidationRestException, ReportPostHandlerConditionValidationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportPostHandlerBuilder postHandlerBuilder =
                reportPostHandlerService.create(userAuthorization);

            if (!Strings.isNullOrEmpty(request.getName())) {
                postHandlerBuilder.withName(request.getName());
            }
            if (request.getActions() != null) {
                for (ReportPostHandlerActionRequest action : request.getActions()) {
                    uploadersRepository.getActionMapper(action.getType()).upload(postHandlerBuilder, action);
                }
            }
            if (request.getConditions() != null) {
                for (ReportPostHandlerConditionRequest condition : request.getConditions()) {
                    uploadersRepository.getConditionMapper(condition.getType()).upload(postHandlerBuilder,
                        condition);
                }
            }
            if (request.isEnabled() != null) {
                postHandlerBuilder.withEnabled(request.isEnabled());
            }
            return toReportPostHandler(postHandlerBuilder.save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportPostHandlerEmptyConditionsException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.EMPTY_CONDITIONS)
                .withCause(e).build();
        } catch (ReportPostHandlerEmptyActionsException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.EMPTY_ACTIONS)
                .withCause(e).build();
        } catch (ReportPostHandlerNameAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.NAME_DUPLICATED)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (ReportPostHandlerMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.NAME_MISSING)
                .withCause(e).build();
        }
    }

    @Override
    public ReportPostHandlerResponse get(String accessToken, String handlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportPostHandlerRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportPostHandler reportPostHandler =
                reportPostHandlerService.getById(userAuthorization, Id.valueOf(handlerId));

            return toReportPostHandler(reportPostHandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ReportPostHandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerRestException.class)
                .withErrorCode(ReportPostHandlerRestException.NOT_FOUND)
                .addParameter("id", handlerId)
                .withCause(e).build();
        }
    }

    @Override
    public List<ReportPostHandlerResponse> list(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<ReportPostHandler> reportPostHandlers =
                reportPostHandlerService.getAll(userAuthorization);

            return reportPostHandlers.stream().map(item -> toReportPostHandler(item, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportPostHandlerResponse update(String accessToken, String handlerId,
        ReportPostHandlerRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportPostHandlerValidationRestException,
        ReportPostHandlerRestException, ReportPostHandlerActionValidationRestException,
        ReportPostHandlerConditionValidationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportPostHandlerBuilder postHandlerBuilder =
                reportPostHandlerService.update(userAuthorization, Id.valueOf(handlerId));

            if (!Strings.isNullOrEmpty(request.getName())) {
                postHandlerBuilder.withName(request.getName());
            }
            if (request.getActions() != null) {
                postHandlerBuilder.removeActions();
                for (ReportPostHandlerActionRequest action : request.getActions()) {
                    uploadersRepository.getActionMapper(action.getType()).upload(postHandlerBuilder, action);
                }
            }
            if (request.getConditions() != null) {
                postHandlerBuilder.removeConditions();
                for (ReportPostHandlerConditionRequest condition : request.getConditions()) {
                    uploadersRepository.getConditionMapper(condition.getType()).upload(postHandlerBuilder,
                        condition);
                }
            }
            if (request.isEnabled() != null) {
                postHandlerBuilder.withEnabled(request.isEnabled());
            }

            return toReportPostHandler(postHandlerBuilder.save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportPostHandlerEmptyConditionsException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.EMPTY_CONDITIONS)
                .withCause(e).build();
        } catch (ReportPostHandlerEmptyActionsException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.EMPTY_ACTIONS)
                .withCause(e).build();
        } catch (ReportPostHandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerRestException.class)
                .withErrorCode(ReportPostHandlerRestException.NOT_FOUND)
                .addParameter("id", handlerId)
                .withCause(e).build();
        } catch (ReportPostHandlerNameAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.NAME_DUPLICATED)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (ReportPostHandlerMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerValidationRestException.class)
                .withErrorCode(ReportPostHandlerValidationRestException.NAME_MISSING)
                .withCause(e).build();
        }
    }

    @Override
    public ReportPostHandlerResponse delete(String accessToken, String handlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportPostHandlerRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportPostHandler reportPostHandler =
                reportPostHandlerService.delete(userAuthorization, Id.valueOf(handlerId));

            return toReportPostHandler(reportPostHandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportPostHandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportPostHandlerRestException.class)
                .withErrorCode(ReportPostHandlerRestException.NOT_FOUND)
                .addParameter("id", handlerId)
                .withCause(e).build();
        }
    }

    private ReportPostHandlerResponse toReportPostHandler(ReportPostHandler postHandler, ZoneId timeZone) {
        return new ReportPostHandlerResponse(postHandler.getId().getValue(),
            postHandler.getName(),
            postHandler.isEnabled(),
            postHandler.getCreatedDate().atZone(timeZone),
            mapToActionsResponse(postHandler),
            mapToConditionsResponse(postHandler));
    }

    private List<ReportPostHandlerConditionResponse> mapToConditionsResponse(ReportPostHandler postHandler) {
        return postHandler.getConditions().stream()
            .map(item -> mappersRepository.getConditionMapper(item.getType()).toReponse(item)).collect(
                Collectors.toList());
    }

    private List<ReportPostHandlerActionResponse> mapToActionsResponse(ReportPostHandler postHandler) {
        return postHandler.getActions().stream()
            .map(item -> mappersRepository.getActionMapper(item.getType()).toReponse(item))
            .collect(Collectors.toList());
    }
}
