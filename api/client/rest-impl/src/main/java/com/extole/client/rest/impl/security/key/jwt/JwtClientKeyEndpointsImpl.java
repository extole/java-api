package com.extole.client.rest.impl.security.key.jwt;

import static com.extole.model.service.jwt.JwtKeySupplyFailureReason.ALGORITHM_MISSING;
import static com.extole.model.service.jwt.JwtKeySupplyFailureReason.KEY_ID_MISMATCH;
import static com.extole.model.service.jwt.JwtKeySupplyFailureReason.KEY_ID_MISSING;

import java.security.InvalidKeyException;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.jwt.JwtClientKeyEndpoints;
import com.extole.client.rest.security.key.jwt.JwtClientKeyJwtVerifyRestException;
import com.extole.client.rest.security.key.jwt.JwtClientKeyVerifyJwtRequest;
import com.extole.client.rest.security.key.jwt.JwtClientKeyVerifyJwtResponse;
import com.extole.client.rest.security.key.jwt.JwtClientKeyVerifyJwtResponse.Result;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.client.security.key.ClientKeyNotFoundException;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.encryption.EncryptionService;
import com.extole.model.service.jwt.JwtAlgorithm;
import com.extole.model.service.jwt.JwtClientKeyConversionRuntimeException;
import com.extole.model.service.jwt.JwtExpiredException;
import com.extole.model.service.jwt.JwtKeySupplyException;
import com.extole.model.service.jwt.JwtService;
import com.extole.model.service.jwt.JwtVerificationException;

@Provider
public class JwtClientKeyEndpointsImpl implements JwtClientKeyEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientKeyService clientKeyService;
    private final EncryptionService<byte[]> encryptionService;
    private final JwtService jwtService;

    @Autowired
    public JwtClientKeyEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientKeyService clientKeyService,
        EncryptionService<byte[]> encryptionService,
        JwtService jwtService) {
        this.authorizationProvider = authorizationProvider;
        this.clientKeyService = clientKeyService;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    @Override
    public JwtClientKeyVerifyJwtResponse verifyJwt(String accessToken, String keyId,
        JwtClientKeyVerifyJwtRequest request)
        throws UserAuthorizationRestException, ClientKeyRestException, JwtClientKeyJwtVerifyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request.getJwt() == null) {
                throw RestExceptionBuilder.newBuilder(JwtClientKeyJwtVerifyRestException.class)
                    .withErrorCode(JwtClientKeyJwtVerifyRestException.CLIENT_KEY_VERIFY_MISSING_JWT)
                    .build();
            }

            ClientKey jwtClientKey = clientKeyService.getClientKey(authorization, Id.valueOf(keyId));
            return new JwtClientKeyVerifyJwtResponse(verifyJwtUsingClientKey(request.getJwt(), jwtClientKey));
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("key_id", keyId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private Result verifyJwtUsingClientKey(String jwt, ClientKey jwtClientKey) {
        try {
            jwtService.verifyJwt(jwt, (keyId, algorithm) -> {
                if (keyId == null) {
                    throw new JwtKeySupplyException(KEY_ID_MISSING);
                }

                if (!jwtClientKey.getPartnerKeyId().equals(keyId)) {
                    throw new JwtKeySupplyException(KEY_ID_MISMATCH);
                }

                if (algorithm == null) {
                    throw new JwtKeySupplyException(ALGORITHM_MISSING);
                }
                JwtService.KeySupplier.validateKeyAlgorithm(jwtClientKey.getAlgorithm().name(), algorithm);

                try {
                    return JwtAlgorithm.valueOf(jwtClientKey.getAlgorithm().name())
                        .convert(encryptionService.decrypt(jwtClientKey.getEncryptedKey()));
                } catch (InvalidKeyException e) {
                    throw new JwtClientKeyConversionRuntimeException(e);
                }
            });
            return Result.MATCHED;
        } catch (JwtExpiredException e) {
            return Result.EXPIRED;
        } catch (JwtVerificationException e) {
            return Result.valueOf(e.getReason().name());
        } catch (JwtKeySupplyException e) {
            return Result.valueOf(e.getReason().name());
        }
    }
}
