package com.extole.client.rest.impl.blocks;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.blocks.BlockCheckRequest;
import com.extole.client.rest.blocks.BlockCheckResponse;
import com.extole.client.rest.blocks.BlockCreateRequest;
import com.extole.client.rest.blocks.BlockEndpoints;
import com.extole.client.rest.blocks.BlockListRequest;
import com.extole.client.rest.blocks.BlockResponse;
import com.extole.client.rest.blocks.BlockRestException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmail;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.model.entity.blocks.Block;
import com.extole.model.entity.blocks.FilterType;
import com.extole.model.entity.blocks.ListType;
import com.extole.model.service.blocks.BlockDuplicateException;
import com.extole.model.service.blocks.BlockFilterTypeMissingException;
import com.extole.model.service.blocks.BlockListTypeMissingException;
import com.extole.model.service.blocks.BlockNotFoundException;
import com.extole.model.service.blocks.BlockService;
import com.extole.model.service.blocks.BlockValueMissingException;
import com.extole.model.service.blocks.BlockValueValidationException;
import com.extole.model.shared.blocklist.BlockEvaluationCache;

@Provider
public class BlockEndpointsImpl implements BlockEndpoints {
    private static final int MAX_FETCH_SIZE = 1000;
    private static final BlockListRequest DEFAULT_LIST_REQUEST = BlockListRequest.newblockListRequest()
        .withLimit(Integer.parseInt(BlockListRequest.DEFAULT_LIMIT))
        .withOffset(Integer.parseInt(BlockListRequest.DEFAULT_LIMIT))
        .withTimeZone(ZoneOffset.UTC)
        .build();
    private final ClientAuthorizationProvider authorizationProvider;

    private final BlockService blockService;
    private final BlockEvaluationCache blockEvaluationCache;
    private final VerifiedEmailService verifiedEmailService;

    @Inject
    public BlockEndpointsImpl(ClientAuthorizationProvider authorizationProvider, BlockService blockService,
        BlockEvaluationCache blockEvaluationCache, VerifiedEmailService verifiedEmailService) {
        this.authorizationProvider = authorizationProvider;
        this.blockService = blockService;
        this.blockEvaluationCache = blockEvaluationCache;
        this.verifiedEmailService = verifiedEmailService;
    }

    @Override
    public BlockResponse create(String accessToken, BlockCreateRequest blockCreateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BlockRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        validateCreateRequest(blockCreateRequest);

        FilterType filterType = FilterType.valueOf(blockCreateRequest.getFilterType().name());
        ListType listType = ListType.valueOf(blockCreateRequest.getListType().name());
        String value = blockCreateRequest.getValue().toLowerCase();

        try {

            if (listType.equals(ListType.NORMALIZED_EMAIL)) {
                VerifiedEmail verifiedEmail = verifiedEmailService.verifyEmail(value);
                value = verifiedEmail.getEmail().getNormalizedAddress();
            }

            Block block = blockService.create(authorization, filterType, listType, value);
            return BlocksRestMapper.toResponse(block, timeZone);
        } catch (BlockDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.DUPLICATE_BLOCK)
                .addParameter("filterType", blockCreateRequest.getFilterType())
                .addParameter("listType", blockCreateRequest.getListType())
                .addParameter("value", blockCreateRequest.getValue())
                .withCause(e).build();
        } catch (BlockValueValidationException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.INVALID_VALUE)
                .addParameter("value", blockCreateRequest.getValue())
                .withCause(e)
                .build();
        } catch (InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.INVALID_EMAIL_DOMAIN_VALUE)
                .addParameter("value", e.getMessage())
                .withCause(e)
                .build();
        } catch (InvalidEmailAddress e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.INVALID_EMAIL_ADDRESS)
                .addParameter("email", e.getMessage())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private void validateCreateRequest(BlockCreateRequest blockCreateRequest) throws BlockRestException {
        try {
            validateValue(blockCreateRequest.getValue());
            validateFilterType(blockCreateRequest.getFilterType());
            validateListType(blockCreateRequest.getListType());
        } catch (BlockFilterTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.MISSING_FILTER_TYPE)
                .withCause(e)
                .build();
        } catch (BlockListTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.MISSING_LIST_TYPE)
                .withCause(e)
                .build();
        } catch (BlockValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.MISSING_VALUE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BlockResponse getBlockById(String accessToken, String blockId, ZoneId timeZone)
        throws UserAuthorizationRestException, BlockRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Block block = blockService.getBlockById(authorization, blockId);
            return BlocksRestMapper.toResponse(block, timeZone);
        } catch (BlockNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.BLOCK_NOT_FOUND)
                .addParameter("blockId", blockId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BlockResponse> getBlocks(String accessToken, BlockListRequest blockListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        if (blockListRequest == null) {
            blockListRequest = DEFAULT_LIST_REQUEST;
        }

        validateLimits(blockListRequest.getLimit(), blockListRequest.getOffset());

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<Block> blocks = blockService.createBlockQueryBuilder(authorization)
                .withLimit(blockListRequest.getLimit())
                .withOffset(blockListRequest.getOffset())
                .list();
            ZoneId timeZone = blockListRequest.getTimeZone();
            return blocks.stream().map(block -> BlocksRestMapper.toResponse(block, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BlockResponse delete(String accessToken, String blockId, ZoneId timeZone)
        throws UserAuthorizationRestException, BlockRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Block block = blockService.deleteBlock(authorization, blockId);
            return BlocksRestMapper.toResponse(block, timeZone);
        } catch (BlockNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.BLOCK_NOT_FOUND)
                .addParameter("blockId", blockId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BlockCheckResponse test(String accessToken, BlockCheckRequest blockCheckRequest)
        throws UserAuthorizationRestException, BlockRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            validateListType(blockCheckRequest.getListType());
            validateValue(blockCheckRequest.getValue());
        } catch (BlockListTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.MISSING_LIST_TYPE)
                .withCause(e)
                .build();
        } catch (BlockValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(BlockRestException.class)
                .withErrorCode(BlockRestException.MISSING_VALUE)
                .withCause(e)
                .build();
        }

        ListType listType = ListType.valueOf(blockCheckRequest.getListType().name());
        String value = blockCheckRequest.getValue();
        return BlocksRestMapper.toCheckResponse(
            blockEvaluationCache.evaluateValueAndGetBlockId(authorization.getClientId(), value, listType));
    }

    private void validateFilterType(com.extole.client.rest.blocks.FilterType filterType)
        throws BlockFilterTypeMissingException {
        if (filterType == null) {
            throw new BlockFilterTypeMissingException("Block filter type may not be missing");
        }
    }

    private void validateListType(com.extole.client.rest.blocks.ListType listType)
        throws BlockListTypeMissingException {
        if (listType == null) {
            throw new BlockListTypeMissingException("Block list type may not be missing");
        }
    }

    private void validateValue(String value) throws BlockValueMissingException {
        if (value == null) {
            throw new BlockValueMissingException("Block value may not be missing");
        }
    }

    private void validateLimits(int limit, int offset) throws QueryLimitsRestException {
        if (limit < 0) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_LIMIT)
                .addParameter("limit", limit)
                .build();
        }

        if (offset < 0) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_OFFSET)
                .addParameter("offset", offset)
                .build();
        }

        if (limit - offset > MAX_FETCH_SIZE) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.MAX_FETCH_SIZE_1000)
                .addParameter("limit", limit)
                .addParameter("offset", offset)
                .build();
        }
    }
}
