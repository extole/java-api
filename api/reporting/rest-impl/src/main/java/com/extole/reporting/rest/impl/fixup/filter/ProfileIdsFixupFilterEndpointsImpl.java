package com.extole.reporting.rest.impl.fixup.filter;

import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.profile.ProfileHandle;
import com.extole.reporting.rest.fixup.FixupRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterUpdateRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterValidationRestException;
import com.extole.reporting.rest.fixup.filter.ProfileIdsFixupFilterEndpoints;
import com.extole.reporting.rest.fixup.filter.ProfileIdsFixupFilterRequest;
import com.extole.reporting.rest.fixup.filter.ProfileIdsFixupFilterResponse;
import com.extole.reporting.rest.fixup.filter.ProfileIdsFixupFilterValidationRestException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.filter.FixupFilterAlreadyExistsException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotEditableException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotFoundException;
import com.extole.reporting.service.fixup.filter.FixupFilterValidationException;
import com.extole.reporting.service.fixup.filter.ProfileIdsFixupFilterBuilder;
import com.extole.reporting.service.fixup.filter.ProfileIdsFixupFilterService;

@Provider
public class ProfileIdsFixupFilterEndpointsImpl implements ProfileIdsFixupFilterEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ProfileIdsFixupFilterService profileIdsFixupFilterService;
    private final ProfileIdsFixupFilterRestMapper restMapper;

    @Autowired
    public ProfileIdsFixupFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ProfileIdsFixupFilterService profileIdsFixupFilterService,
        ProfileIdsFixupFilterRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.profileIdsFixupFilterService = profileIdsFixupFilterService;
        this.restMapper = restMapper;
    }

    @Override
    public ProfileIdsFixupFilterResponse getFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper
                .toResponse(profileIdsFixupFilterService.get(authorization, Id.valueOf(fixupId), Id.valueOf(filterId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (FixupFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterRestException.class)
                .withErrorCode(FixupFilterRestException.FILTER_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId)
                .addParameter("fixup_filter_id", fixupId).build();
        }
    }

    @Override
    public ProfileIdsFixupFilterResponse createFilter(String accessToken, String fixupId,
        ProfileIdsFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        ProfileIdsFixupFilterValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ProfileIdsFixupFilterBuilder filterBuilder =
                profileIdsFixupFilterService.create(authorization, Id.valueOf(fixupId));
            if (request.getProfileIds() != null) {
                filterBuilder.withProfileIds(
                    request.getProfileIds().stream().map(Id::<ProfileHandle>valueOf)
                        .collect(Collectors.toSet()));
            }
            return restMapper.toResponse(filterBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId).build();
        } catch (FixupFilterAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterValidationRestException.class)
                .withErrorCode(FixupFilterValidationRestException.FILTER_ALREADY_EXISTS)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (FixupFilterValidationException e) {
            throw RestExceptionBuilder.newBuilder(ProfileIdsFixupFilterValidationRestException.class)
                .withErrorCode(ProfileIdsFixupFilterValidationRestException.FILTER_PROFILE_IDS_INVALID)
                .withCause(e).build();
        }
    }

    @Override
    public ProfileIdsFixupFilterResponse updateFilter(String accessToken, String fixupId, String filterId,
        ProfileIdsFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        ProfileIdsFixupFilterValidationRestException, FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ProfileIdsFixupFilterBuilder filterBuilder =
                profileIdsFixupFilterService.update(authorization, Id.valueOf(fixupId), Id.valueOf(filterId));
            if (request.getProfileIds() != null) {
                filterBuilder.withProfileIds(
                    request.getProfileIds().stream().map(Id::<ProfileHandle>valueOf)
                        .collect(Collectors.toSet()));
            }
            return restMapper.toResponse(filterBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterRestException.class)
                .withErrorCode(FixupFilterRestException.FILTER_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_filter_id", e.getFilterId()).build();
        } catch (FixupFilterValidationException e) {
            throw RestExceptionBuilder.newBuilder(ProfileIdsFixupFilterValidationRestException.class)
                .withErrorCode(ProfileIdsFixupFilterValidationRestException.FILTER_PROFILE_IDS_INVALID)
                .withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .build();
        } catch (FixupFilterNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterUpdateRestException.class)
                .withErrorCode(FixupFilterUpdateRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }

    @Override
    public ProfileIdsFixupFilterResponse deleteFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(
                profileIdsFixupFilterService.delete(authorization, Id.valueOf(fixupId), Id.valueOf(filterId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId).build();
        } catch (FixupFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterRestException.class)
                .withErrorCode(FixupFilterRestException.FILTER_NOT_FOUND)
                .addParameter("fixup_id", fixupId)
                .addParameter("fixup_filter_id", filterId)
                .withCause(e).build();
        } catch (FixupRuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (FixupFilterNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterUpdateRestException.class)
                .withErrorCode(FixupFilterUpdateRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }
}
