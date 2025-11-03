package com.extole.client.rest.impl.program;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternType;
import com.extole.client.rest.email.DomainValidationStatus;
import com.extole.client.rest.program.GlobPatternResponse;
import com.extole.client.rest.program.ProgramArchiveRestException;
import com.extole.client.rest.program.ProgramCreateRequest;
import com.extole.client.rest.program.ProgramCreateRestException;
import com.extole.client.rest.program.ProgramDomainValidationResponse;
import com.extole.client.rest.program.ProgramEndpoints;
import com.extole.client.rest.program.ProgramResponse;
import com.extole.client.rest.program.ProgramRestException;
import com.extole.client.rest.program.ProgramUpdateRequest;
import com.extole.client.rest.program.ProgramValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.program.GlobPattern;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.entity.program.SslGenerationPolicy;
import com.extole.model.service.domain.DomainValidationService;
import com.extole.model.service.domain.ProgramDomainValidationResult;
import com.extole.model.service.program.ExpiredChainSslCertificateException;
import com.extole.model.service.program.ExpiredPublicSslCertificateException;
import com.extole.model.service.program.InvalidFormatPrivateSslCertificateException;
import com.extole.model.service.program.InvalidFormatSslCertificateException;
import com.extole.model.service.program.InvalidPrivateSslCertificateException;
import com.extole.model.service.program.InvalidSslCertificateException;
import com.extole.model.service.program.InvalidSslCertificateSubjectException;
import com.extole.model.service.program.NotSignedByChainCertificateException;
import com.extole.model.service.program.NotYetValidChainSslCertificateException;
import com.extole.model.service.program.NotYetValidPublicSslCertificateException;
import com.extole.model.service.program.Program;
import com.extole.model.service.program.ProgramArchiveLastExtoleProgramException;
import com.extole.model.service.program.ProgramArchiveLastProgramException;
import com.extole.model.service.program.ProgramBuilder;
import com.extole.model.service.program.ProgramCnameTargetDuplicateException;
import com.extole.model.service.program.ProgramCnameTargetInvalidException;
import com.extole.model.service.program.ProgramCnameTargetInvalidFormatException;
import com.extole.model.service.program.ProgramCnameTargetNotApplicableForExtoleDomainsException;
import com.extole.model.service.program.ProgramDomainBuildException;
import com.extole.model.service.program.ProgramDuplicateProgramDomainException;
import com.extole.model.service.program.ProgramException;
import com.extole.model.service.program.ProgramInvalidNameException;
import com.extole.model.service.program.ProgramInvalidProgramDomainException;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.program.ProgramRedirectGeneratesLoopException;
import com.extole.model.service.program.ProgramService;
import com.extole.model.service.program.ProgramServiceInvalidException;
import com.extole.model.service.program.RedirectProgramNotFoundException;
import com.extole.model.service.program.SelfSignedCertificateException;
import com.extole.model.service.program.SslCertificatePrivateKeyMissingException;
import com.extole.model.service.program.SslCertificatePublicPrivateKeyMismatchException;
import com.extole.model.service.program.SslKeysNotAllowedForExtoleDomainException;
import com.extole.model.service.program.SslPrivateKeyDecryptException;
import com.extole.model.service.program.SslPrivateKeyEncryptionException;

@Provider
public class ProgramEndpointsImpl implements ProgramEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ProgramEndpointsImpl.class);

    private static final String SHARE_URL_PROTOCOL_PREFIX = "https://";

    private final ClientAuthorizationProvider authorizationProvider;
    private final ProgramService programService;
    private final DomainValidationService domainValidationService;

    @Autowired
    public ProgramEndpointsImpl(ProgramService programService, ClientAuthorizationProvider authorizationProvider,
        DomainValidationService domainValidationService) {
        this.programService = programService;
        this.authorizationProvider = authorizationProvider;
        this.domainValidationService = domainValidationService;
    }

    @Override
    public List<ProgramResponse> getPrograms(String accessToken, Optional<DomainValidationStatus> status)
        throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<PublicProgram> programs = programService.getByClientId(userAuthorization.getClientId());

            if (status.isPresent()) {
                com.extole.model.service.domain.DomainValidationStatus domainValidationStatus =
                    com.extole.model.service.domain.DomainValidationStatus.valueOf(status.get().name());
                List<PublicProgram> validatedPrograms = new ArrayList<>();
                for (PublicProgram program : programs) {
                    ProgramDomainValidationResult validationResult =
                        domainValidationService.validate(userAuthorization, program);
                    if (domainValidationStatus == validationResult.getDomainValidationStatus() ||
                        program.getSslGenerationPolicy() == SslGenerationPolicy.GENERATE_FORCE) {
                        validatedPrograms.add(program);
                    }
                }
                programs = validatedPrograms;
            }

            return programs.stream()
                .map(this::buildPublicProgramResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ProgramResponse getProgram(String accessToken, String programId)
        throws UserAuthorizationRestException, ProgramRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return buildProgramResponse(programService.getById(userAuthorization, Id.valueOf(programId)));
        } catch (AuthorizationException | ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        }
    }

    @Override
    public ProgramResponse create(String accessToken, ProgramCreateRequest request)
        throws UserAuthorizationRestException, ProgramValidationRestException, ProgramCreateRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ProgramBuilder programBuilder = programService.create(userAuthorization);

            programBuilder.withName(request.getName())
                .withProgramDomain(request.getDomain());

            URI shareUri = URI.create(SHARE_URL_PROTOCOL_PREFIX + request.getDomain());
            if (request.getShareUri().isPresent() && !request.getShareUri().get().isEmpty()) {
                shareUri = new URI(request.getShareUri().get());
            }
            programBuilder.withShareUri(shareUri);

            if (request.getRedirectProgramId().isPresent()) {
                programBuilder.withRedirectProgramId(Id.valueOf(request.getRedirectProgramId().get()));
            }

            if (request.getSslCertificate().isPresent()) {
                programBuilder.withSslCertificate(request.getSslCertificate().get());
            }
            if (request.getSslCertificateChain().isPresent()) {
                programBuilder.withSslCertificateChain(request.getSslCertificateChain().get());
            }
            if (request.getSslPrivateKey().isPresent()
                && !request.getSslPrivateKey().get().equals(Program.PRIVATE_KEY_OBFUSCATED_VALUE)) {
                programBuilder.withSslPrivateKey(request.getSslPrivateKey().get());
            }
            if (request.getSslGenerationPolicy().isPresent()) {
                SslGenerationPolicy sslGenerationPolicy =
                    SslGenerationPolicy.valueOf(request.getSslGenerationPolicy().get().name());
                programBuilder.withSslGenerationPolicy(sslGenerationPolicy);
            }
            if (request.getCnameTarget().isPresent()) {
                programBuilder =
                    programBuilder.withCnameTarget(InternetDomainName.from(request.getCnameTarget().get()));
            }

            return buildProgramResponse(programBuilder.save());

        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ProgramDuplicateProgramDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramCreateRestException.class)
                .withErrorCode(ProgramCreateRestException.DUPLICATE_PROGRAM_DOMAIN)
                .addParameter("program_domain", request.getDomain()).withCause(e).build();
        } catch (ProgramInvalidProgramDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.INVALID_PROGRAM_DOMAIN)
                .addParameter("program_domain", request.getDomain()).withCause(e).build();
        } catch (ProgramInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.INVALID_NAME)
                .addParameter("program_name", request.getName())
                .withCause(e).build();
        } catch (URISyntaxException e) {
            throw RestExceptionBuilder.newBuilder(ProgramCreateRestException.class)
                .withErrorCode(ProgramCreateRestException.INVALID_URI_SYNTAX).withCause(e).build();
        } catch (RedirectProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.NON_EXISTENT_REDIRECT_PROGRAM).withCause(e).build();
        } catch (ProgramRedirectGeneratesLoopException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.PROGRAM_REDIRECT_GENERATES_LOOP)
                .addParameter("redirect_program", e.getRedirectProgramId())
                .withCause(e)
                .build();
        } catch (NotYetValidPublicSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PUBLIC_KEY_NOT_YET_VALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (NotYetValidChainSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CHAIN_KEY_NOT_YET_VALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidFormatSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_KEY_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_KEY_CHAIN_OR_PRIVATE_KEY_MISSING_OR_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslCertificatePrivateKeyMissingException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PRIVATE_KEY_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidFormatPrivateSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PRIVATE_KEY_INVALID_FORMAT)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidPrivateSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PRIVATE_KEY_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslCertificatePublicPrivateKeyMismatchException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PUBLIC_PRIVATE_KEYS_NOT_MATCHING)
                .withCause(e)
                .build();
        } catch (ExpiredPublicSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PUBLIC_KEY_EXPIRED)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ExpiredChainSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CHAIN_KEY_EXPIRED)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SelfSignedCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_SELF_SIGNED_CERTIFICATE_NOT_ACCEPTED)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (NotSignedByChainCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CERTIFICATE_NOT_SIGNED_BY_CHAIN)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslKeysNotAllowedForExtoleDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_KEYS_NOT_ALLOWED_FOR_EXTOLE_DOMAIN)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetNotApplicableForExtoleDomainsException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_NOT_APPLICABLE_FOR_EXTOLE_DOMAINS)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetInvalidFormatException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_INVALID_FORMAT)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_DUPLICATE)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidSslCertificateSubjectException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CERTIFICATE_INVALID_DOMAIN)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslPrivateKeyEncryptionException | SslPrivateKeyDecryptException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ProgramResponse edit(String accessToken, String programId, ProgramUpdateRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramValidationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Program program = programService.getById(userAuthorization, Id.valueOf(programId));
            ProgramBuilder programBuilder = programService.edit(userAuthorization, program);

            request.getName().ifPresent(name -> programBuilder.withName(name));
            request.getCnameTarget().ifPresent(cname -> programBuilder.withCnameTarget(InternetDomainName.from(cname)));
            request.getSslCertificate().ifPresent(sslCertificate -> programBuilder.withSslCertificate(sslCertificate));
            request.getSslPrivateKey().ifPresent(sslPrivateKey -> programBuilder.withSslPrivateKey(sslPrivateKey));
            request.getSslCertificateChain()
                .ifPresent(sslCertificateChain -> programBuilder.withSslCertificateChain(sslCertificateChain));
            request.getSslGenerationPolicy().ifPresent(sslGenerationPolicy -> programBuilder
                .withSslGenerationPolicy(SslGenerationPolicy.valueOf(sslGenerationPolicy.name())));

            request.getRedirectProgramId().ifPresent(redirectProgramId -> {
                if (!redirectProgramId.isPresent()) {
                    programBuilder.withRemovedRedirectProgramId();
                } else {
                    programBuilder.withRedirectProgramId(Id.valueOf(redirectProgramId.get()));
                }
            });

            return buildProgramResponse(programBuilder.save());
        } catch (ProgramInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.INVALID_NAME)
                .addParameter("program_name", request.getName()).withCause(e).build();
        } catch (ProgramRedirectGeneratesLoopException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.PROGRAM_REDIRECT_GENERATES_LOOP)
                .addParameter("redirect_program", e.getRedirectProgramId())
                .withCause(e)
                .build();
        } catch (ProgramInvalidProgramDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.INVALID_PROGRAM_DOMAIN).withCause(e).build();
        } catch (ProgramNotFoundException | ProgramServiceInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (NotYetValidPublicSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PUBLIC_KEY_NOT_YET_VALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (NotYetValidChainSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CHAIN_KEY_NOT_YET_VALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidFormatSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_KEY_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_KEY_CHAIN_OR_PRIVATE_KEY_MISSING_OR_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslCertificatePrivateKeyMissingException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PRIVATE_KEY_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidFormatPrivateSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PRIVATE_KEY_INVALID_FORMAT)
                .withCause(e)
                .build();
        } catch (InvalidPrivateSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PRIVATE_KEY_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslCertificatePublicPrivateKeyMismatchException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PUBLIC_PRIVATE_KEYS_NOT_MATCHING)
                .withCause(e)
                .build();
        } catch (ExpiredPublicSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_PUBLIC_KEY_EXPIRED)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ExpiredChainSslCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CHAIN_KEY_EXPIRED)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SelfSignedCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_SELF_SIGNED_CERTIFICATE_NOT_ACCEPTED)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (NotSignedByChainCertificateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CERTIFICATE_NOT_SIGNED_BY_CHAIN)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SslKeysNotAllowedForExtoleDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_KEYS_NOT_ALLOWED_FOR_EXTOLE_DOMAIN)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetNotApplicableForExtoleDomainsException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_NOT_APPLICABLE_FOR_EXTOLE_DOMAINS)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetInvalidFormatException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_INVALID_FORMAT)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_INVALID)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ProgramCnameTargetDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.CNAME_TARGET_DUPLICATE)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (InvalidSslCertificateSubjectException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.SSL_CERTIFICATE_INVALID_DOMAIN)
                .addParameter("client_id", userAuthorization.getClientId().getValue())
                .withCause(e).build();
        } catch (RedirectProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramValidationRestException.class)
                .withErrorCode(ProgramValidationRestException.NON_EXISTENT_REDIRECT_PROGRAM)
                .withCause(e)
                .build();
        } catch (ProgramDomainBuildException | SslPrivateKeyDecryptException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ProgramResponse getDecrypt(String accessToken, String programId)
        throws UserAuthorizationRestException, ProgramRestException {

        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        LOG.debug("Program decrypt for program: " + programId);

        try {
            Program program = programService.getProgramDecrypt(userAuthorization,
                Id.valueOf(programId));
            return buildDecryptProgramResponse(program);
        } catch (ProgramException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        }
    }

    @Override
    public ProgramResponse archive(String accessToken, String programId, boolean force)
        throws UserAuthorizationRestException, ProgramRestException, ProgramArchiveRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Program program = programService.getById(userAuthorization, Id.valueOf(programId));
            ProgramDomainValidationResult validationResult =
                domainValidationService.validate(userAuthorization, program);

            if (validationResult
                .getDomainValidationStatus() == com.extole.model.service.domain.DomainValidationStatus.PASS && !force) {
                throw RestExceptionBuilder.newBuilder(ProgramArchiveRestException.class)
                    .withErrorCode(ProgramArchiveRestException.CANNOT_ARCHIVE_VALID_PROGRAM)
                    .build();
            }

            Program deletedProgram = programService.deleteProgram(userAuthorization, Id.valueOf(programId));
            return buildProgramResponse(deletedProgram);
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ProgramArchiveLastProgramException e) {
            throw RestExceptionBuilder.newBuilder(ProgramArchiveRestException.class)
                .withErrorCode(ProgramArchiveRestException.LAST_PROGRAM)
                .withCause(e)
                .build();
        } catch (ProgramArchiveLastExtoleProgramException e) {
            throw RestExceptionBuilder.newBuilder(ProgramArchiveRestException.class)
                .withErrorCode(ProgramArchiveRestException.LAST_EXTOLE_PROGRAM)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ProgramDomainValidationResponse validate(String accessToken, String programId)
        throws UserAuthorizationRestException, ProgramRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Program program = programService.getById(authorization, Id.valueOf(programId));
            ProgramDomainValidationResult validationResult = domainValidationService.validate(authorization, program);

            DomainValidationStatus validationStatus =
                DomainValidationStatus.valueOf(validationResult.getDomainValidationStatus().name());
            if (validationStatus == DomainValidationStatus.FAIL
                && program.getSslGenerationPolicy() == SslGenerationPolicy.GENERATE_FORCE) {
                validationStatus = DomainValidationStatus.PASS;
            }

            String canonicalName = validationResult.getCanonicalName().orElse(null);
            return new ProgramDomainValidationResponse(validationStatus, validationResult.getProgramDomain().toString(),
                canonicalName);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.PROGRAM_DOMAIN_NOT_FOUND)
                .addParameter("program_id", programId)
                .withCause(e)
                .build();
        }
    }

    private ProgramResponse buildPublicProgramResponse(PublicProgram program) {
        List<GlobPatternResponse> sitePatterns =
            program.getSitePatterns().stream().map(this::buildSitePatternResponse)
                .collect(Collectors.toList());

        return new ProgramResponse(program.getId().getValue(), program.getName(), program.getProgramDomain().toString(),
            program.isExtoleDomain(), program.getShareUri().toString(),
            program.getRedirectProgramId().map(Id::getValue).orElse(null),
            sitePatterns, null, null, null,
            com.extole.client.rest.program.SslGenerationPolicy.valueOf(program.getSslGenerationPolicy().name()),
            program.getCnameTarget().toString());
    }

    private ProgramResponse buildProgramResponse(Program program) {
        List<GlobPatternResponse> sitePatterns =
            program.getSitePatterns().stream().map(this::buildSitePatternResponse)
                .collect(Collectors.toList());

        return new ProgramResponse(program.getId().getValue(), program.getName(), program.getProgramDomain().toString(),
            program.isExtoleDomain(), program.getShareUri().toString(),
            program.getRedirectProgramId().map(Id::getValue).orElse(null),
            sitePatterns, program.getSslCertificate().orElse(null), program.getSslCertificateChain().orElse(null),
            program.getObfuscatedSslPrivateKey().orElse(null),
            com.extole.client.rest.program.SslGenerationPolicy.valueOf(program.getSslGenerationPolicy().name()),
            program.getCnameTarget().toString());
    }

    private ProgramResponse buildDecryptProgramResponse(Program program) {
        List<GlobPatternResponse> sitePatterns =
            program.getSitePatterns().stream().map(this::buildSitePatternResponse)
                .collect(Collectors.toList());

        return new ProgramResponse(program.getId().getValue(), program.getName(), program.getProgramDomain().toString(),
            program.isExtoleDomain(), program.getShareUri().toString(),
            program.getRedirectProgramId().map(Id::getValue).orElse(null),
            sitePatterns, program.getSslCertificate().orElse(null), program.getSslCertificateChain().orElse(null),
            program.getSslPrivateKey().orElse(null),
            com.extole.client.rest.program.SslGenerationPolicy.valueOf(program.getSslGenerationPolicy().name()),
            program.getCnameTarget().toString());
    }

    private GlobPatternResponse buildSitePatternResponse(GlobPattern programSitePattern) {
        return new GlobPatternResponse(programSitePattern.getPattern(), programSitePattern.getRegex().pattern(),
            ClientDomainPatternType.valueOf(programSitePattern.getType().name()));
    }
}
