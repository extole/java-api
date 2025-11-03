package com.extole.client.rest.impl.campaign.component.asset;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentAssetResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetCreateRequest;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetEndpoints;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetResponse;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetRestException;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetUpdateRequest;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentAsset;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;
import com.extole.model.entity.campaign.built.BuiltCampaignComponentAsset;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentAssetNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentSizeTooBigException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetDescriptionLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameMissingException;
import com.extole.model.service.campaign.component.asset.ComponentAssetNotFoundException;
import com.extole.model.service.campaign.component.asset.ComponentAssetService;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveIncompatibleApiVersionException;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class CampaignComponentAssetEndpointsImpl implements CampaignComponentAssetEndpoints {

    private static final int CONTENT_FILE_THRESHOLD = 256 * 1024;

    private final CampaignService campaignService;
    private final ComponentAssetService componentAssetService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignComponentRestMapper campaignComponentRestMapper;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Inject
    public CampaignComponentAssetEndpointsImpl(CampaignService campaignService,
        ComponentAssetService componentAssetService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignComponentRestMapper campaignComponentRestMapper,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.campaignService = campaignService;
        this.componentAssetService = componentAssetService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.campaignComponentRestMapper = campaignComponentRestMapper;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Override
    public List<CampaignComponentAssetResponse> list(String accessToken, String campaignId, String version,
        String componentId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        CampaignComponent campaignComponent = getCampaignComponent(componentId, campaign);
        return campaignComponent.getAssets()
            .stream()
            .map(asset -> campaignComponentRestMapper.toAssetResponse(asset))
            .collect(Collectors.toList());
    }

    @Override
    public CampaignComponentAssetResponse get(String accessToken, String campaignId, String version, String componentId,
        String assetId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        return campaignComponentRestMapper.toAssetResponse(getComponentAsset(assetId, componentId, campaign));
    }

    @Override
    public Response getContent(String accessToken, String campaignId, String version, String componentId,
        String assetId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        if (!authorization.getClientId().equals(campaign.getClientId())) {
            authorization = getClientBackendAuthorization(campaign.getClientId());
        }

        CampaignComponentAsset componentAsset = getComponentAsset(assetId, componentId, campaign);

        try {
            ByteSource content = componentAssetService.get(authorization, componentAsset.getId(), campaign.getVersion())
                .getContent();
            return Response.ok((StreamingOutput) outputStream -> content.copyTo(outputStream))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + componentAsset.getFilename())
                .build();
        } catch (AuthorizationException | ComponentAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentAssetResponse create(String accessToken, String campaignId, String expectedCurrentVersion,
        String componentId, CampaignComponentAssetCreateRequest request, InputStream inputStream,
        FormDataContentDisposition fileMetadata, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetValidationRestException, BuildCampaignRestException, CampaignUpdateRestException {
        request = request == null ? CampaignComponentAssetCreateRequest.builder().build() : request;
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
        CampaignComponent campaignComponent = getCampaignComponent(componentId, campaign);
        CampaignComponentBuilder campaignComponentBuilder = campaignBuilder.updateComponent(campaignComponent);
        try {
            CampaignComponentAssetBuilder assetBuilder = campaignComponentBuilder.addAsset();

            request.getName().ifPresent(name -> assetBuilder.withName(name));
            request.getTags().ifPresent(tags -> assetBuilder.withTags(tags));
            request.getDescription().ifPresent(description -> {
                if (description.isPresent()) {
                    assetBuilder.withDescription(description.get());
                }
            });
            if (fileMetadata != null && fileMetadata.getFileName() != null) {
                assetBuilder.withFilename(fileMetadata.getFileName());
            }
            if (inputStream != null) {
                assetBuilder.withContent(copyToByteSource(inputStream));
            }
            return campaignComponentRestMapper.toAssetResponse(assetBuilder.save());
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (CampaignComponentAssetNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_LENGTH_OUT_OF_RANGE)
                .addParameter("filename", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("filename", e.getFilename())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_SIZE_OUT_OF_RANGE)
                .addParameter("size", e.getContentSize())
                .addParameter("max_size", e.getMaxContentSize())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", e.getDescriptionMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_UPLOAD_ERROR)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetContentMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_MISSING)
                .withCause(e)
                .addParameter("asset_name", e.getAssetName())
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentAssetResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        String assetId,
        CampaignComponentAssetUpdateRequest request,
        InputStream inputStream,
        FormDataContentDisposition fileMetadata,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException, CampaignComponentAssetValidationRestException, BuildCampaignRestException,
        CampaignUpdateRestException {
        request = request == null ? CampaignComponentAssetUpdateRequest.builder().build() : request;
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign =
            campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
        CampaignComponent campaignComponent = getCampaignComponent(componentId, campaign);
        CampaignComponentAsset componentAsset = getComponentAsset(assetId, componentId, campaign);
        CampaignComponentAssetBuilder assetBuilder = campaignBuilder.updateComponent(campaignComponent)
            .updateAsset(componentAsset);
        try {
            request.getName().ifPresent(name -> assetBuilder.withName(name));
            request.getTags().ifPresent(tags -> assetBuilder.withTags(tags));
            request.getDescription().ifPresent(description -> {
                if (description.isPresent()) {
                    assetBuilder.withDescription(description.get());
                }
            });
            if (fileMetadata != null && fileMetadata.getFileName() != null) {
                assetBuilder.withFilename(fileMetadata.getFileName());
            }
            if (inputStream != null) {
                assetBuilder.withContent(copyToByteSource(inputStream));
            }
            return campaignComponentRestMapper.toAssetResponse(assetBuilder.save());
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (CampaignComponentAssetNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("filename", e.getFilename())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_LENGTH_OUT_OF_RANGE)
                .addParameter("filename", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_SIZE_OUT_OF_RANGE)
                .addParameter("size", e.getContentSize())
                .addParameter("max_size", e.getMaxContentSize())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", e.getDescriptionMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_UPLOAD_ERROR)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetContentMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_MISSING)
                .withCause(e)
                .addParameter("asset_name", e.getAssetName())
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentAssetResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        String assetId,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign =
            campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
        CampaignComponent campaignComponent = getCampaignComponent(componentId, campaign);
        CampaignComponentAsset componentAsset = getComponentAsset(assetId, componentId, campaign);
        try {
            campaignBuilder.updateComponent(campaignComponent)
                .removeAsset(componentAsset)
                .save();
            return campaignComponentRestMapper.toAssetResponse(componentAsset);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | CampaignComponentException | CreativeArchiveIncompatibleApiVersionException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BuiltCampaignComponentAssetResponse> listBuilt(String accessToken, String campaignId, String version,
        String componentId, @Nullable ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException,
        BuildCampaignRestException, CampaignComponentRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignComponent campaignComponent = getCampaignComponent(componentId, campaign);
        return campaignComponent.getAssets()
            .stream()
            .map(asset -> campaignComponentRestMapper.toBuiltAssetResponse(asset))
            .collect(Collectors.toList());
    }

    @Override
    public BuiltCampaignComponentAssetResponse getBuilt(String accessToken, String campaignId, String version,
        String componentId, String assetId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignComponentAsset componentAsset = getComponentAsset(assetId, componentId, campaign);
        return campaignComponentRestMapper.toBuiltAssetResponse(componentAsset);
    }

    private Authorization getClientBackendAuthorization(Id<ClientHandle> clientId)
        throws UserAuthorizationRestException {
        try {
            return backendAuthorizationProvider.getAuthorizationForBackend(clientId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    private CampaignBuilder getCampaignBuilder(String campaignId, Authorization authorization,
        String expectedCurrentVersion)
        throws CampaignRestException {
        try {
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            return campaignBuilder;
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }
    }

    private CampaignComponent getCampaignComponent(String componentId, Campaign campaign)
        throws CampaignComponentRestException {
        return campaign.getComponents()
            .stream()
            .filter(campaignComponentCandidate -> campaignComponentCandidate.getId().getValue().equals(componentId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .build());
    }

    private CampaignComponentAsset getComponentAsset(String assetId, String componentId, Campaign campaign)
        throws CampaignComponentRestException, CampaignComponentAssetRestException {
        return getCampaignComponent(componentId, campaign)
            .getAssets()
            .stream()
            .filter(asset -> asset.getId().getValue().equals(assetId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignComponentAssetRestException.class)
                .withErrorCode(CampaignComponentAssetRestException.CAMPAIGN_COMPONENT_ASSET_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .addParameter("asset_id", assetId)
                .build());
    }

    private BuiltCampaignComponent getCampaignComponent(String componentId, BuiltCampaign campaign)
        throws CampaignComponentRestException {
        return campaign.getComponents()
            .stream()
            .filter(campaignComponentCandidate -> campaignComponentCandidate.getId().getValue().equals(componentId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .build());

    }

    private BuiltCampaignComponentAsset getComponentAsset(String assetId, String componentId, BuiltCampaign campaign)
        throws CampaignComponentRestException, CampaignComponentAssetRestException {
        return getCampaignComponent(componentId, campaign)
            .getAssets()
            .stream()
            .filter(asset -> asset.getId().getValue().equals(assetId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignComponentAssetRestException.class)
                .withErrorCode(CampaignComponentAssetRestException.CAMPAIGN_COMPONENT_ASSET_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .addParameter("asset_id", assetId)
                .build());
    }

    private ByteSource copyToByteSource(InputStream inputStream) throws IOException {
        try (FileBackedOutputStream outputStream = new FileBackedOutputStream(CONTENT_FILE_THRESHOLD, true)) {
            ByteStreams.copy(inputStream, outputStream);
            return outputStream.asByteSource();
        } finally {
            inputStream.close();
        }
    }

}
