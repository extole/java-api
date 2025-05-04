package com.extole.client.rest.impl.program;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.program.ProgramRestException;
import com.extole.client.rest.program.ProgramSitePatternEndpoints;
import com.extole.client.rest.program.ProgramSitePatternRequest;
import com.extole.client.rest.program.ProgramSitePatternResponse;
import com.extole.client.rest.program.ProgramSitePatternRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.program.ProgramSitePattern;
import com.extole.model.service.program.ExpiredChainSslCertificateException;
import com.extole.model.service.program.ExpiredPublicSslCertificateException;
import com.extole.model.service.program.InvalidFormatPrivateSslCertificateException;
import com.extole.model.service.program.InvalidFormatSslCertificateException;
import com.extole.model.service.program.InvalidPrivateSslCertificateException;
import com.extole.model.service.program.InvalidSitePatternDomainException;
import com.extole.model.service.program.InvalidSitePatternSyntaxException;
import com.extole.model.service.program.InvalidSslCertificateException;
import com.extole.model.service.program.InvalidSslCertificateSubjectException;
import com.extole.model.service.program.NotSignedByChainCertificateException;
import com.extole.model.service.program.NotYetValidChainSslCertificateException;
import com.extole.model.service.program.NotYetValidPublicSslCertificateException;
import com.extole.model.service.program.Program;
import com.extole.model.service.program.ProgramBuilder;
import com.extole.model.service.program.ProgramCnameTargetInvalidFormatException;
import com.extole.model.service.program.ProgramCnameTargetNotApplicableForExtoleDomainsException;
import com.extole.model.service.program.ProgramInvalidNameException;
import com.extole.model.service.program.ProgramInvalidProgramDomainException;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.program.ProgramService;
import com.extole.model.service.program.ProgramServiceInvalidException;
import com.extole.model.service.program.ProgramSitePatternBuilder;
import com.extole.model.service.program.ProgramSitePatternMissingException;
import com.extole.model.service.program.SelfSignedCertificateException;
import com.extole.model.service.program.SslCertificatePrivateKeyMissingException;
import com.extole.model.service.program.SslCertificatePublicPrivateKeyMismatchException;
import com.extole.model.service.program.SslKeysNotAllowedForExtoleDomainException;
import com.extole.model.service.program.SslPrivateKeyDecryptException;
import com.extole.person.service.ProgramHandle;

@Provider
public class ProgramSitePatternEndpointsImpl implements ProgramSitePatternEndpoints {

    private final ProgramService programService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public ProgramSitePatternEndpointsImpl(ProgramService programService,
        ClientAuthorizationProvider authorizationProvider) {
        this.programService = programService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<ProgramSitePatternResponse> getProgramSitePatterns(String accessToken, String programId)
        throws UserAuthorizationRestException, ProgramRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Program program = programService.getById(userAuthorization, Id.valueOf(programId));

            return program.getConfiguredSitePatterns().stream().map(this::buildSitePatternResponse)
                .collect(Collectors.toList());

        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ProgramSitePatternResponse getProgramSitePattern(String accessToken, String programId,
        String programSitePatternId)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Program program = programService.getById(userAuthorization, Id.valueOf(programId));
            Id<ProgramSitePattern> patternId = Id.valueOf(programSitePatternId);
            ProgramSitePattern sitePattern = program.getConfiguredSitePatterns().stream()
                .filter(sitePatterns -> sitePatterns.getId().equals(patternId)).findFirst()
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(ProgramSitePatternRestException.class)
                    .withErrorCode(ProgramSitePatternRestException.INVALID_SITE_PATTERN).build());
            return buildSitePatternResponse(sitePattern);
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ProgramSitePatternResponse create(String accessToken, String programId, ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Program program = programService.getById(userAuthorization, Id.valueOf(programId));
            ProgramBuilder programBuilder = programService.edit(userAuthorization, program);
            ProgramSitePatternBuilder programSitePatternBuilder = programBuilder
                .addSitePattern()
                .withPattern(request.getSitePattern());
            request.getType().ifPresent(type -> programSitePatternBuilder
                .withType(com.extole.model.entity.program.ProgramSitePatternType.valueOf(type.name())));
            return buildSitePatternResponse(programSitePatternBuilder.save());
        } catch (ProgramNotFoundException | ProgramServiceInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ProgramInvalidProgramDomainException | ProgramInvalidNameException
            | InvalidFormatSslCertificateException | InvalidSslCertificateException
            | InvalidFormatPrivateSslCertificateException | InvalidPrivateSslCertificateException
            | ExpiredPublicSslCertificateException | ExpiredChainSslCertificateException
            | NotYetValidPublicSslCertificateException | NotYetValidChainSslCertificateException
            | SelfSignedCertificateException | NotSignedByChainCertificateException | SslPrivateKeyDecryptException
            | SslKeysNotAllowedForExtoleDomainException | InvalidSslCertificateSubjectException
            | ProgramCnameTargetNotApplicableForExtoleDomainsException | ProgramCnameTargetInvalidFormatException
            | SslCertificatePublicPrivateKeyMismatchException | SslCertificatePrivateKeyMissingException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (InvalidSitePatternSyntaxException e) {
            throw RestExceptionBuilder.newBuilder(ProgramSitePatternRestException.class)
                .withErrorCode(ProgramSitePatternRestException.INVALID_SITE_PATTERN_SYNTAX)
                .addParameter("type", e.getType())
                .addParameter("site_pattern", e.getSitePattern())
                .withCause(e)
                .build();
        } catch (InvalidSitePatternDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramSitePatternRestException.class)
                .withErrorCode(ProgramSitePatternRestException.INVALID_SITE_PATTERN_DOMAIN)
                .addParameter("type", e.getType())
                .addParameter("site_pattern", e.getSitePattern())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ProgramSitePatternResponse editWithPost(String accessToken, String programId, String programSitePatternId,
        ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException {
        return editSitePattern(accessToken, programId, programSitePatternId, request);
    }

    @Override
    public ProgramSitePatternResponse edit(String accessToken, String programId, String programSitePatternId,
        ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException {
        return editSitePattern(accessToken, programId, programSitePatternId, request);
    }

    @Override
    public ProgramSitePatternResponse discard(String accessToken, String programId, String programSitePatternId)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        ProgramSitePatternBuilder programSitePatternBuilder =
            editSitePattern(userAuthorization, Id.valueOf(programId), Id.valueOf(programSitePatternId));
        try {
            return buildSitePatternResponse(programSitePatternBuilder.withArchived(Boolean.TRUE).save());
        } catch (ProgramInvalidProgramDomainException | ProgramInvalidNameException
            | InvalidFormatSslCertificateException | InvalidSslCertificateException
            | InvalidFormatPrivateSslCertificateException | InvalidPrivateSslCertificateException
            | ExpiredPublicSslCertificateException | ExpiredChainSslCertificateException
            | NotYetValidPublicSslCertificateException | NotYetValidChainSslCertificateException
            | SelfSignedCertificateException | NotSignedByChainCertificateException | SslPrivateKeyDecryptException
            | SslKeysNotAllowedForExtoleDomainException | InvalidSslCertificateSubjectException
            | ProgramCnameTargetNotApplicableForExtoleDomainsException | ProgramCnameTargetInvalidFormatException
            | InvalidSitePatternSyntaxException | InvalidSitePatternDomainException
            | SslCertificatePublicPrivateKeyMismatchException | SslCertificatePrivateKeyMissingException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private ProgramSitePatternResponse editSitePattern(String accessToken, String programId,
        String programSitePatternId, ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ProgramSitePatternBuilder programSitePatternBuilder =
                editSitePattern(userAuthorization, Id.valueOf(programId), Id.valueOf(programSitePatternId))
                    .withPattern(request.getSitePattern());
            request.getType().ifPresent(type -> programSitePatternBuilder
                .withType(com.extole.model.entity.program.ProgramSitePatternType.valueOf(type.name())));
            return buildSitePatternResponse(programSitePatternBuilder.save());
        } catch (InvalidSitePatternSyntaxException e) {
            throw RestExceptionBuilder.newBuilder(ProgramSitePatternRestException.class)
                .withErrorCode(ProgramSitePatternRestException.INVALID_SITE_PATTERN_SYNTAX)
                .addParameter("type", e.getType())
                .addParameter("site_pattern", e.getSitePattern())
                .withCause(e)
                .build();
        } catch (InvalidSitePatternDomainException e) {
            throw RestExceptionBuilder.newBuilder(ProgramSitePatternRestException.class)
                .withErrorCode(ProgramSitePatternRestException.INVALID_SITE_PATTERN_DOMAIN)
                .addParameter("type", e.getType())
                .addParameter("site_pattern", e.getSitePattern())
                .withCause(e)
                .build();
        } catch (ProgramInvalidProgramDomainException | ProgramInvalidNameException
            | InvalidFormatSslCertificateException | InvalidSslCertificateException
            | InvalidFormatPrivateSslCertificateException | InvalidPrivateSslCertificateException
            | ExpiredPublicSslCertificateException | ExpiredChainSslCertificateException
            | NotYetValidPublicSslCertificateException | NotYetValidChainSslCertificateException
            | SelfSignedCertificateException | NotSignedByChainCertificateException | SslPrivateKeyDecryptException
            | SslKeysNotAllowedForExtoleDomainException | InvalidSslCertificateSubjectException
            | ProgramCnameTargetNotApplicableForExtoleDomainsException | ProgramCnameTargetInvalidFormatException
            | SslCertificatePublicPrivateKeyMismatchException | SslCertificatePrivateKeyMissingException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private ProgramSitePatternResponse buildSitePatternResponse(ProgramSitePattern programSitePattern) {
        return new ProgramSitePatternResponse(programSitePattern.getId().getValue(),
            programSitePattern.getSitePattern(),
            com.extole.client.rest.program.ProgramSitePatternType.valueOf(programSitePattern.getType().name()));
    }

    private ProgramSitePatternBuilder editSitePattern(Authorization userAuthorization, Id<ProgramHandle> programId,
        Id<ProgramSitePattern> programSitePatternId)
        throws ProgramSitePatternRestException, ProgramRestException, UserAuthorizationRestException {

        try {
            Program program = programService.getById(userAuthorization, programId);
            ProgramBuilder programBuilder = programService.edit(userAuthorization, program);
            return programBuilder.getProgramSitePatternBuilder(programSitePatternId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ProgramNotFoundException | ProgramServiceInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ProgramRestException.class)
                .withErrorCode(ProgramRestException.INVALID_PROGRAM).withCause(e).build();
        } catch (ProgramSitePatternMissingException e) {
            throw RestExceptionBuilder.newBuilder(ProgramSitePatternRestException.class)
                .withErrorCode(ProgramSitePatternRestException.INVALID_SITE_PATTERN).withCause(e).build();
        }

    }

}
