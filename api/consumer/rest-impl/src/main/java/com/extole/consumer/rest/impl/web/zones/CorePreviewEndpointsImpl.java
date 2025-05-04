package com.extole.consumer.rest.impl.web.zones;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.web.zones.CorePreviewEndpoints;
import com.extole.consumer.service.zone.ZoneRenderRequest;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CreativeArchive;
import com.extole.model.entity.campaign.CreativeArchiveApiVersion;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.creative.CreativeArchiveService;
import com.extole.model.service.creative.exception.CreativeArchiveNotFoundException;
import com.extole.running.service.campaign.provider.RunningCampaignService;
import com.extole.running.service.campaign.step.RunningStepType;
import com.extole.running.service.campaign.step.controller.RunningController;
import com.extole.running.service.campaign.step.controller.RunningFrontendController;
import com.extole.running.service.campaign.step.controller.action.RunningControllerActionType;
import com.extole.running.service.campaign.step.controller.action.creative.RunningControllerActionCreative;
import com.extole.running.service.campaign.step.trigger.RunningStepTriggerEvent;
import com.extole.running.service.campaign.step.trigger.RunningStepTriggerType;
import com.extole.sandbox.Container;
import com.extole.sandbox.SandboxModel;
import com.extole.sandbox.SandboxNotFoundException;
import com.extole.sandbox.SandboxService;
import com.extole.sandbox.SandboxType;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class CorePreviewEndpointsImpl implements CorePreviewEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(CorePreviewEndpointsImpl.class);

    private static final Pattern PROGRAM_DOMAIN_PATTERN = Pattern.compile("__PROGRAM_DOMAIN__");
    private static final Pattern ZONE_NAME_PATTERN = Pattern.compile("__ZONE_NAME__");
    private static final String CORE_PREVIEW_RESPONSE_TEMPLATE_RESOURCE = "core-preview-response-template.html";
    private static final String CORE_PREVIEW_RESPONSE_TEMPLATE;
    private static final String CORE_PREVIEW_NESTED_CREATIVE_TEMPLATE_RESOURCE = "core-preview-nested-creative.js";
    private static final String CORE_PREVIEW_NESTED_CREATIVE_TEMPLATE;
    static {
        try {
            CORE_PREVIEW_RESPONSE_TEMPLATE = Resources.toString(
                Resources.getResource(CORE_PREVIEW_RESPONSE_TEMPLATE_RESOURCE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to find resource: " + CORE_PREVIEW_RESPONSE_TEMPLATE_RESOURCE + " in classpath", e);
        }
    }
    static {
        try {
            CORE_PREVIEW_NESTED_CREATIVE_TEMPLATE = Resources.toString(
                Resources.getResource(CORE_PREVIEW_NESTED_CREATIVE_TEMPLATE_RESOURCE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to find resource: " + CORE_PREVIEW_NESTED_CREATIVE_TEMPLATE_RESOURCE + " in classpath", e);
        }
    }

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final RunningCampaignService runningCampaignService;
    private final SandboxService sandboxService;
    private final CreativeArchiveService creativeArchiveService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Autowired
    public CorePreviewEndpointsImpl(
        @Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        RunningCampaignService runningCampaignService,
        SandboxService sandboxService,
        CreativeArchiveService creativeArchiveService,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.runningCampaignService = runningCampaignService;
        this.sandboxService = sandboxService;
        this.creativeArchiveService = creativeArchiveService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Override
    public Response preview() {
        PublicProgram programDomain = consumerRequestContextService.extractProgramDomain(servletRequest);
        String responseHtml = PROGRAM_DOMAIN_PATTERN.matcher(CORE_PREVIEW_RESPONSE_TEMPLATE)
            .replaceAll(programDomain.getProgramDomain().toString());
        return Response.ok(responseHtml).build();
    }

    @Override
    public Response previewNestedCreativeJs(String campaignId, String zoneName, String journeyName) {
        PublicProgram programDomain = consumerRequestContextService.extractProgramDomain(servletRequest);
        Authorization authorization = getAuthorizationByClientId(programDomain.getClientId());
        boolean isV8Creative = checkIsV8Creative(campaignId, zoneName, journeyName, authorization);
        String responseJs = "";
        if (isV8Creative) {
            responseJs = ZONE_NAME_PATTERN.matcher(CORE_PREVIEW_NESTED_CREATIVE_TEMPLATE)
                .replaceAll(zoneName);
        }
        return Response.ok(responseJs).build();
    }

    private boolean checkIsV8Creative(String campaignId, String zoneName, String journeyName,
        Authorization authorization) {
        Id<SandboxModel> sandboxId;
        String requestSandboxId = servletRequest.getParameter(ZoneRenderRequest.ZONE_PARAMETER_SANDBOX);
        if (StringUtils.isNotBlank(requestSandboxId)) {
            sandboxId = Id.valueOf(requestSandboxId);
        } else {
            sandboxId = Id.valueOf(SandboxType.CAMPAIGN.name() + "-" + campaignId + "-" + Container.DEFAULT.getName());
        }

        SandboxModel sandbox;
        try {
            sandbox = sandboxService.getById(authorization.getClientId(), sandboxId);
        } catch (SandboxNotFoundException e) {
            LOG.error("Was unable to retrieve sandbox for sandbox ID={}", sandboxId, e);
            return false;
        }

        return runningCampaignService.getCampaign(authorization.getClientId(), Id.valueOf(campaignId), sandbox)
            .filter(campaign -> {
                for (RunningController controller : campaign.getControllers()) {
                    if (!(controller.getType() == RunningStepType.FRONTEND_CONTROLLER)) {
                        continue;
                    }

                    RunningFrontendController frontendController = (RunningFrontendController) controller;
                    if (frontendControllerHasZone(frontendController, zoneName)
                        && frontendControllerHasJourneyName(frontendController, journeyName)
                        && frontendControllerHasAnyV8Creative(frontendController, authorization)) {
                        return true;
                    }
                }
                return false;
            })
            .isPresent();
    }

    private Authorization getAuthorizationByClientId(Id<ClientHandle> clientId) {
        try {
            return backendAuthorizationProvider.getAuthorizationForBackend(clientId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder
                .newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private boolean frontendControllerHasAnyV8Creative(RunningFrontendController controller,
        Authorization authorization) {
        return controller.getActions().stream()
            .filter(action -> action.getType() == RunningControllerActionType.CREATIVE)
            .map(RunningControllerActionCreative.class::cast)
            .map(action -> action.getCreativeArchiveId())
            .map(archiveId -> getCreativeArchive(authorization,
                new CreativeArchiveId(archiveId.getId(), archiveId.getVersion())))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(creativeArchive -> creativeArchive.getApiVersion() == CreativeArchiveApiVersion.EIGHT);

    }

    private boolean frontendControllerHasJourneyName(RunningFrontendController controller, String journeyName) {
        return controller.getJourneyName().getValue().equalsIgnoreCase(journeyName);
    }

    private boolean frontendControllerHasZone(RunningFrontendController controller, String zoneName) {
        return controller.getTriggers().stream()
            .filter(trigger -> trigger.getType() == RunningStepTriggerType.EVENT)
            .map(RunningStepTriggerEvent.class::cast)
            .anyMatch(trigger -> trigger.getEventNames().stream()
                .anyMatch(eventName -> eventName.equalsIgnoreCase(zoneName)));
    }

    private Optional<CreativeArchive> getCreativeArchive(Authorization authorization, CreativeArchiveId creativeId) {
        try {
            return Optional.of(creativeArchiveService.getCreativeArchive(authorization, creativeId));
        } catch (CreativeArchiveNotFoundException e) {
            return Optional.empty();
        }
    }

}
