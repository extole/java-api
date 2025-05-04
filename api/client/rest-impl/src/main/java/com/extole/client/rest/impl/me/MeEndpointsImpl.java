package com.extole.client.rest.impl.me;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.client.user.UserAuthorization;
import com.extole.client.rest.me.MeClientProgramResponse;
import com.extole.client.rest.me.MeClientResponse;
import com.extole.client.rest.me.MeEndpoints;
import com.extole.client.rest.me.MeResponse;
import com.extole.client.rest.me.UserZendeskSingleSignOnResponse;
import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.JwtException;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.encode.JwtBuilder;
import com.extole.common.jwt.encode.SecuredJwtEncoderProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.entity.property.Property;
import com.extole.model.entity.user.User;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.program.ProgramService;
import com.extole.model.service.property.ClientPropertyService;
import com.extole.model.service.property.UserPropertyService;
import com.extole.model.service.user.UserIntercomService;
import com.extole.model.service.user.UserNotFoundException;
import com.extole.model.service.user.UserService;

@Provider
public class MeEndpointsImpl implements MeEndpoints {

    private final ClientService clientService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final UserService userService;
    private final ClientPropertyService clientPropertyService;
    private final UserPropertyService userPropertyService;
    private final ProgramService programService;
    private final String zendeskSharedKey;
    private final String zendeskHost;
    private final UserIntercomService userIntercomService;

    @Autowired
    public MeEndpointsImpl(ClientService clientService, ClientAuthorizationProvider authorizationProvider,
        UserService userService, ClientPropertyService clientPropertyService, UserPropertyService userPropertyService,
        @Value("${zendesk.sharedKey:kEMcnX8R8X6gyVzao7qEzHjZbMFAe3b8Jgk0OQ8zzs9TiIPN}") String zendeskSharedKey,
        @Value("${zendesk.host:extole1400706620.zendesk.com}") String zendeskHost,
        ProgramService programService,
        UserIntercomService userIntercomService) {
        this.clientService = clientService;
        this.authorizationProvider = authorizationProvider;
        this.userService = userService;
        this.clientPropertyService = clientPropertyService;
        this.userPropertyService = userPropertyService;
        this.zendeskHost = zendeskHost;
        this.zendeskSharedKey = zendeskSharedKey;
        this.programService = programService;
        this.userIntercomService = userIntercomService;
    }

    @Override
    public MeResponse get(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getUserAuthorization(accessToken);
        try {
            List<Property> allProperties = new ArrayList<>();
            allProperties.addAll(clientPropertyService.listDefault(authorization));
            allProperties.addAll(clientPropertyService.list(authorization));

            User user = userService.get(authorization);
            allProperties.addAll(userPropertyService.list(user));

            Map<String, String> mergedProperties = new HashMap<>();
            for (Property property : allProperties) {
                mergedProperties.put(property.getName(), property.getValue());
            }
            String intercomUserHash = userIntercomService.getHmacValue(user);
            return new MeResponse(user.getId().getValue(), user.getNormalizedEmail(),
                authorization.getClientId().getValue(), mergedProperties, intercomUserHash);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<MeClientResponse> listClients(String accessToken) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return clientService.getAll(authorization).stream()
                .map(client -> new MeClientResponse(client.getId().getValue(), client.getName()))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public UserZendeskSingleSignOnResponse createZendeskSingleSignOnUrl(String accessToken)
        throws UserAuthorizationRestException {
        UserAuthorization authorization = authorizationProvider.getUserAuthorization(accessToken);

        if (!authorization.isClientAuthorized(authorization.getClientId(), Authorization.Scope.USER_SUPPORT)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }

        String email = authorization.getIdentity().getNormalizedEmail();
        try {
            String nameFromEmail = email.split("@")[0];
            String jwt = createZendeskJwt(nameFromEmail, email);
            URI url = UriBuilder.fromUri("https://" + zendeskHost).path("/access/jwt").queryParam("jwt", jwt).build();
            return new UserZendeskSingleSignOnResponse(url.toString());
        } catch (ZendeskSingleSignOnException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public List<MeClientProgramResponse> listPrograms(String accessToken) throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        return programService.getAll(userAuthorization).stream()
            .map(this::buildMeClientProgramResponse)
            .collect(Collectors.toList());
    }

    private MeClientProgramResponse buildMeClientProgramResponse(PublicProgram program) {
        return new MeClientProgramResponse(program.getClientId().getValue(), program.getProgramDomain().toString());
    }

    private String createZendeskJwt(String name, String email) throws ZendeskSingleSignOnException {
        Algorithm algorithm = SignedJwtAlgorithm.HS256;
        try {
            return JwtBuilder.newEncoder()
                .withJwtId(UUID.randomUUID().toString())
                .withClaim("name", name)
                .withClaim("email", email)
                .withIssuedAt(Instant.now())
                .withSecuredEncoder(SecuredJwtEncoderProvider.createSigningEncoder(algorithm)
                    .withKey(new SecretKeySpec(zendeskSharedKey.getBytes(), algorithm.getJcaName())).build())
                .encode();
        } catch (JwtException e) {
            throw new ZendeskSingleSignOnException("Failed to create jwt for name " + name + " and email " + email, e);
        }
    }
}
