package com.extole.client.rest.impl.label;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.label.LabelInjectorCreateRequest;
import com.extole.client.rest.label.LabelInjectorEndpoints;
import com.extole.client.rest.label.LabelInjectorResponse;
import com.extole.client.rest.label.LabelInjectorRestException;
import com.extole.client.rest.label.LabelInjectorUpdateRequest;
import com.extole.client.rest.label.LabelInjectorValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.label.LabelInjector;
import com.extole.model.service.label.LabelChildHasChildrenException;
import com.extole.model.service.label.LabelChildItselfException;
import com.extole.model.service.label.LabelForwardDepthException;
import com.extole.model.service.label.LabelHasChildrenThatAreBothNormalAndOptionalException;
import com.extole.model.service.label.LabelIllegalCharacterInNameException;
import com.extole.model.service.label.LabelInjectorBuilder;
import com.extole.model.service.label.LabelInjectorService;
import com.extole.model.service.label.LabelMissingNameException;
import com.extole.model.service.label.LabelNameAlreadyDefinedException;
import com.extole.model.service.label.LabelNameLengthException;
import com.extole.model.service.label.LabelNotFoundException;
import com.extole.model.service.label.LabelSelfForwardException;
import com.extole.model.service.program.ProgramNotFoundException;

@Provider
public class LabelInjectorEndpointsImpl implements LabelInjectorEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final LabelInjectorService labelInjectorService;

    @Autowired
    public LabelInjectorEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        LabelInjectorService labelInjectorService) {
        this.authorizationProvider = authorizationProvider;
        this.labelInjectorService = labelInjectorService;
    }

    @Override
    public LabelInjectorResponse create(String accessToken, LabelInjectorCreateRequest request, ZoneId timeZone)
        throws LabelInjectorValidationRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            LabelInjectorBuilder labelInjectorBuilder = labelInjectorService.create(authorization);
            if (request.getName() != null) {
                labelInjectorBuilder.withName(request.getName());
            }
            if (request.getProgramDomainId() != null) {
                labelInjectorBuilder.withProgramDomainId(Id.valueOf(request.getProgramDomainId()));
            }
            if (request.isRequired().booleanValue()) {
                labelInjectorBuilder.withRequired();
            } else {
                labelInjectorBuilder.withOptional();
            }

            withForwardFromLabel(labelInjectorBuilder, request.getForwardFromLabel());
            withChildren(labelInjectorBuilder, request.getChildLabels(), request.getOptionalChildLabels());

            LabelInjector labelInjector = labelInjectorBuilder.save();

            return toLabelResponse(labelInjector, timeZone);
        } catch (AuthorizationException e) {
            // create can be performed only by CLIENT_ADMIN
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (LabelMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.NAME_MISSING)
                .withCause(e).build();
        } catch (LabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (LabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        } catch (LabelForwardDepthException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.FORWARD_DEPTH_EXCEEDED)
                .addParameter("forward_from_label", request.getForwardFromLabel())
                .withCause(e).build();
        } catch (LabelHasChildrenThatAreBothNormalAndOptionalException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILDREN_ARE_BOTH_NORMAL_AND_OPTIONAL)
                .withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.PROGRAM_DOMAIN_NOT_FOUND)
                .addParameter("program_domain_id", request.getProgramDomainId())
                .withCause(e).build();
        } catch (LabelNameAlreadyDefinedException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (LabelChildHasChildrenException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILD_CANNOT_HAVE_CHILDREN)
                .addParameter("child_label", e.getChildLabel()).withCause(e).build();
        }
    }

    @Override
    public LabelInjectorResponse update(String accessToken, String labelInjectorId, LabelInjectorUpdateRequest request,
        ZoneId timeZone)
        throws LabelInjectorRestException, LabelInjectorValidationRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            LabelInjector labelInjector = labelInjectorService.get(authorization, Id.valueOf(labelInjectorId));
            LabelInjectorBuilder labelInjectorBuilder = labelInjectorService.update(authorization, labelInjector);
            if (request.getProgramDomainId() != null) {
                labelInjectorBuilder.withProgramDomainId(Id.valueOf(request.getProgramDomainId()));
            }
            if (request.isRequired() != null) {
                if (request.isRequired().booleanValue()) {
                    labelInjectorBuilder.withRequired();
                } else {
                    labelInjectorBuilder.withOptional();
                }
            }

            withForwardFromLabel(labelInjectorBuilder, request.getForwardFromLabel());
            withChildren(labelInjectorBuilder, request.getChildLabels(), request.getOptionalChildLabels());

            labelInjector = labelInjectorBuilder.save();

            return toLabelResponse(labelInjector, timeZone);
        } catch (AuthorizationException e) {
            // create can be performed only by CLIENT_ADMIN
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (LabelNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorRestException.class)
                .withErrorCode(LabelInjectorRestException.LABEL_INJECTOR_NOT_FOUND)
                .addParameter("label_injector_id", labelInjectorId)
                .withCause(e).build();
        } catch (LabelForwardDepthException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.FORWARD_DEPTH_EXCEEDED)
                .addParameter("forward_from_label", request.getForwardFromLabel())
                .withCause(e).build();
        } catch (LabelHasChildrenThatAreBothNormalAndOptionalException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILDREN_ARE_BOTH_NORMAL_AND_OPTIONAL)
                .withCause(e).build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.PROGRAM_DOMAIN_NOT_FOUND)
                .addParameter("program_domain_id", request.getProgramDomainId())
                .withCause(e).build();
        } catch (LabelMissingNameException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (LabelChildHasChildrenException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILD_CANNOT_HAVE_CHILDREN)
                .addParameter("child_label", e.getChildLabel()).withCause(e).build();
        }
    }

    @Override
    public LabelInjectorResponse delete(String accessToken, String labelInjectorId, ZoneId timeZone)
        throws LabelInjectorRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            LabelInjector labelInjector = labelInjectorService.get(authorization, Id.valueOf(labelInjectorId));
            labelInjectorService.delete(authorization, labelInjector);
            return toLabelResponse(labelInjector, timeZone);
        } catch (LabelNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorRestException.class)
                .withErrorCode(LabelInjectorRestException.LABEL_INJECTOR_NOT_FOUND)
                .addParameter("label_injector_id", labelInjectorId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            // DELETE can be performed only by CLIENT_SUPERUSER
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public LabelInjectorResponse read(String accessToken, String labelInjectorId, ZoneId timeZone)
        throws LabelInjectorRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            LabelInjector labelInjector = labelInjectorService.get(authorization, Id.valueOf(labelInjectorId));
            return toLabelResponse(labelInjector, timeZone);
        } catch (LabelNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorRestException.class)
                .withErrorCode(LabelInjectorRestException.LABEL_INJECTOR_NOT_FOUND)
                .addParameter("label_injector_id", labelInjectorId)
                .withCause(e).build();
        }
    }

    @Override
    public List<LabelInjectorResponse> list(String accessToken, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        return labelInjectorService.getLabelInjectors(authorization).stream()
            .map(label -> toLabelResponse(label, timeZone))
            .collect(Collectors.toList());
    }

    private LabelInjectorResponse toLabelResponse(LabelInjector labelInjector, ZoneId timeZone) {
        String forwardFromLabel = labelInjector.getForwardFromLabel().orElse(null);
        String programDomainId = labelInjector.getProgramDomainId()
            .map(programDomain -> programDomain.getValue())
            .orElse(null);

        return new LabelInjectorResponse(labelInjector.getId().getValue(),
            labelInjector.getName(),
            labelInjector.isRequired(),
            forwardFromLabel,
            labelInjector.getChildLabels(),
            labelInjector.getOptionalChildLabels(),
            programDomainId,
            labelInjector.getCreatedDate().atZone(timeZone));
    }

    private void withForwardFromLabel(LabelInjectorBuilder labelInjectorBuilder, String forwardFromLabel)
        throws LabelInjectorValidationRestException, LabelForwardDepthException, LabelMissingNameException {
        if (forwardFromLabel == null) {
            return;
        }
        try {
            if (forwardFromLabel.isEmpty()) {
                labelInjectorBuilder.deleteForwardFromLabel();
            } else {
                labelInjectorBuilder.withForwardFromLabel(forwardFromLabel);
            }
        } catch (LabelSelfForwardException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CANNOT_FORWARD_ITSELF)
                .withCause(e).build();
        } catch (LabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.FORWARD_FROM_LABEL_NAME_ILLEGAL_CHARACTER)
                .addParameter("forward_from_label", forwardFromLabel)
                .withCause(e).build();
        } catch (LabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.FORWARD_FROM_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        }
    }

    private void withChildren(LabelInjectorBuilder labelInjectorBuilder, Set<String> childLabels,
        Set<String> optionalChildLabels)
        throws LabelInjectorValidationRestException, LabelChildHasChildrenException, LabelMissingNameException {
        try {
            if (childLabels != null) {
                labelInjectorBuilder.withChildLabels(childLabels);
            }
            if (optionalChildLabels != null) {
                labelInjectorBuilder.withOptionalChildLabels(optionalChildLabels);
            }
        } catch (LabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILD_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("child_label", e.getLabelName()).withCause(e).build();
        } catch (LabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILD_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        } catch (LabelChildItselfException e) {
            throw RestExceptionBuilder.newBuilder(LabelInjectorValidationRestException.class)
                .withErrorCode(LabelInjectorValidationRestException.CHILD_CANNOT_BE_ITSELF).withCause(e).build();
        }
    }
}
