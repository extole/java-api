package com.extole.client.rest.impl.webhook;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildWebhookExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.webhook.built.BuiltWebhookRestMapper;
import com.extole.client.rest.webhook.AssociatedWebhookControllerActionResponse;
import com.extole.client.rest.webhook.AssociatedWebhookUserSubscriptionChannelResponse;
import com.extole.client.rest.webhook.WebhookArchiveRestException;
import com.extole.client.rest.webhook.WebhookCreateRequest;
import com.extole.client.rest.webhook.WebhookEndpoints;
import com.extole.client.rest.webhook.WebhookResponse;
import com.extole.client.rest.webhook.WebhookRestException;
import com.extole.client.rest.webhook.WebhookType;
import com.extole.client.rest.webhook.WebhookUpdateRequest;
import com.extole.client.rest.webhook.built.BuiltWebhookResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.webhook.Webhook;
import com.extole.model.entity.webhook.built.BuiltWebhook;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.webhook.AssociatedWebhookControllerAction;
import com.extole.model.service.webhook.AssociatedWebhookUserSubscriptionChannel;
import com.extole.model.service.webhook.BuiltWebhookQueryBuilder;
import com.extole.model.service.webhook.InvalidWebhookTagException;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookControllerActionException;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookUserSubscriptionChannelException;
import com.extole.model.service.webhook.WebhookBuilder;
import com.extole.model.service.webhook.WebhookNotFoundException;
import com.extole.model.service.webhook.WebhookQueryBuilder;
import com.extole.model.service.webhook.WebhookService;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.service.webhook.built.BuiltWebhookQueryService;
import com.extole.model.service.webhook.built.BuiltWebhookService;
import com.extole.model.service.webhook.client.ClientWebhookBuilder;
import com.extole.model.service.webhook.client.ClientWebhookNotFoundException;
import com.extole.model.service.webhook.client.ClientWebhookService;
import com.extole.model.service.webhook.partner.PartnerWebhookBuilder;
import com.extole.model.service.webhook.partner.PartnerWebhookService;
import com.extole.model.service.webhook.reward.RewardWebhookBuilder;
import com.extole.model.service.webhook.reward.RewardWebhookNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookService;
import com.extole.model.service.webhook.reward.RewardWebhookUpdateBuilder;

@Provider
public class WebhookEndpointsImpl implements WebhookEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardWebhookService rewardWebhookService;
    private final WebhookService webhookService;
    private final BuiltWebhookService builtWebhookService;
    private final BuiltWebhookQueryService builtWebhookQueryService;
    private final ClientWebhookService clientWebhookService;
    private final PartnerWebhookService partnerWebhookService;
    private final ComponentService componentService;
    private final WebhookRestMapper webhookRestMapper;
    private final BuiltWebhookRestMapper builtWebhookRestMapper;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public WebhookEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardWebhookService rewardWebhookService,
        WebhookService webhookService,
        BuiltWebhookService builtWebhookService,
        BuiltWebhookQueryService builtWebhookQueryService,
        WebhookRestMapper webhookRestMapper,
        BuiltWebhookRestMapper builtWebhookRestMapper,
        ClientWebhookService clientWebhookService,
        PartnerWebhookService partnerWebhookService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.rewardWebhookService = rewardWebhookService;
        this.webhookService = webhookService;
        this.builtWebhookService = builtWebhookService;
        this.builtWebhookQueryService = builtWebhookQueryService;
        this.clientWebhookService = clientWebhookService;
        this.partnerWebhookService = partnerWebhookService;
        this.componentService = componentService;
        this.webhookRestMapper = webhookRestMapper;
        this.builtWebhookRestMapper = builtWebhookRestMapper;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<? extends WebhookResponse> listWebhooks(String accessToken, Boolean enabled, WebhookType type,
        ZoneId timeZone) throws UserAuthorizationRestException, BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            WebhookQueryBuilder webhookQueryBuilder = webhookService.createWebhookQueryBuilder(authorization);
            if (type != null) {
                webhookQueryBuilder.withType(com.extole.model.entity.webhook.WebhookType.valueOf(type.name()));
            }

            List<Webhook> webhooks = new ArrayList<>();
            if (enabled == null) {
                webhooks = webhookQueryBuilder.list();
            } else {
                for (Webhook webhook : webhookQueryBuilder.list()) {
                    BuiltWebhook builtWebhook = builtWebhookService.buildWebhook(authorization, webhook);
                    if (enabled.equals(builtWebhook.isEnabled())) {
                        webhooks.add(webhook);
                    }
                }
            }
            return webhooks.stream().map(webhook -> webhookRestMapper.toWebhookResponse(webhook, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw BuildWebhookExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public WebhookResponse getWebhook(String accessToken, String webhookId, ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Webhook webhook = webhookService.getWebhook(authorization, Id.valueOf(webhookId));
            return webhookRestMapper.toWebhookResponse(webhook, timeZone);
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BuiltWebhookResponse> listBuiltWebhooks(String accessToken, Boolean enabled, WebhookType type,
        String name, Boolean includeArchived, Integer limit, Integer offset,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltWebhookQueryBuilder webhookQueryBuilder =
                builtWebhookQueryService.createBuiltWebhookQueryBuilder(authorization);
            if (type != null) {
                webhookQueryBuilder.withType(com.extole.model.entity.webhook.WebhookType.valueOf(type.name()));
            }
            if ((Boolean.TRUE.equals(includeArchived))) {
                webhookQueryBuilder.includeArchived();
            }
            if (limit != null) {
                webhookQueryBuilder.withLimit(limit);
            }
            if (offset != null) {
                webhookQueryBuilder.withOffset(offset);
            }
            if (name != null) {
                webhookQueryBuilder.withName(name);
            }

            List<BuiltWebhook> webhooks = new ArrayList<>();
            for (BuiltWebhook builtWebhook : webhookQueryBuilder.list()) {
                if (enabled == null || enabled.equals(builtWebhook.isEnabled())) {
                    webhooks.add(builtWebhook);
                }
            }
            return webhooks.stream().map(webhook -> builtWebhookRestMapper.toBuiltWebhookResponse(webhook, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltWebhookResponse getBuiltWebhook(String accessToken, String webhookId, ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltWebhook builtWebhook = builtWebhookService.getWebhook(authorization, Id.valueOf(webhookId));
            return builtWebhookRestMapper.toBuiltWebhookResponse(builtWebhook, timeZone);
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw BuildWebhookExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public WebhookResponse createWebhook(String accessToken, WebhookCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException, BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Webhook webhook = doCreateWebhook(authorization, createRequest);
            return webhookRestMapper.toWebhookResponse(webhook, timeZone);
        } catch (BuildWebhookException e) {
            throw BuildWebhookExceptionMapper.getInstance().map(e);
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public WebhookResponse updateWebhook(String accessToken, String webhookId, WebhookUpdateRequest updateRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, CampaignComponentValidationRestException,
        BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Webhook webhook = doUpdateWebhook(authorization, webhookId, updateRequest);

            return webhookRestMapper.toWebhookResponse(webhook, timeZone);
        } catch (WebhookNotFoundException | RewardWebhookNotFoundException | ClientWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidWebhookTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw BuildWebhookExceptionMapper.getInstance().map(e);
        } catch (WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public WebhookResponse archiveWebhook(String accessToken, String webhookId, ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, WebhookArchiveRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Webhook archivedWebhook = doArchiveWebhook(authorization, webhookId);
            return webhookRestMapper.toWebhookResponse(archivedWebhook, timeZone);
        } catch (RewardWebhookNotFoundException | WebhookNotFoundException | ClientWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (WebhookAssociatedWithWebhookControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(WebhookArchiveRestException.class)
                .withErrorCode(WebhookArchiveRestException.WEBHOOK_ASSOCIATED_WITH_WEBHOOK_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("webhook_controller_actions",
                    toAssociatedWebhookControllerActionResponses(e.getAssociatedWebhookControllerActions()))
                .build();
        } catch (WebhookAssociatedWithWebhookUserSubscriptionChannelException e) {
            throw RestExceptionBuilder.newBuilder(WebhookArchiveRestException.class)
                .withErrorCode(WebhookArchiveRestException.WEBHOOK_ASSOCIATED_WITH_WEBHOOK_USER_SUBSCRIPTION_CHANNEL)
                .withCause(e)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("webhook_user_subscription_channels",
                    toAssociatedWebhookUserSubscriptionChannelResponses(e.getAssociatedWebhookChannels()))
                .build();
        } catch (InvalidComponentReferenceException | BuildWebhookException
            | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Webhook doCreateWebhook(Authorization authorization, WebhookCreateRequest createRequest)
        throws AuthorizationException, InvalidComponentReferenceException, CampaignComponentValidationRestException,
        BuildWebhookException, WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException,
        MoreThanOneComponentReferenceException, BuildWebhookRestException {
        WebhookType webhookType = createRequest.getType().orElse(WebhookType.GENERIC);

        if (webhookType == WebhookType.GENERIC) {
            return createGenericWebhook(authorization, createRequest);
        }

        if (webhookType == WebhookType.REWARD) {
            return createRewardWebhook(authorization, createRequest);
        }

        if (webhookType == WebhookType.CLIENT) {
            return createClientWebhook(authorization, createRequest);
        }

        if (webhookType == WebhookType.PARTNER) {
            return createPartnerWebhook(authorization, createRequest);
        }

        throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
            .withErrorCode(BuildWebhookRestException.WEBHOOK_UNKNOWN_TYPE)
            .addParameter("type", createRequest.getType())
            .build();
    }

    private Webhook createClientWebhook(Authorization authorization, WebhookCreateRequest createRequest)
        throws AuthorizationException, WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, InvalidComponentReferenceException,
        BuildWebhookException, MoreThanOneComponentReferenceException {
        ClientWebhookBuilder webhookBuilder = clientWebhookService.createClientWebhook(authorization)
            .withName(createRequest.getName())
            .withUrl(createRequest.getUrl());

        createRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        createRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        createRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        createRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        createRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        createRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        createRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        createRequest.getRetryIntervals()
            .ifPresent(retryIntervals -> webhookBuilder.withRetryIntervals(retryIntervals));

        return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
    }

    private Webhook createPartnerWebhook(Authorization authorization, WebhookCreateRequest createRequest)
        throws AuthorizationException, WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, InvalidComponentReferenceException,
        BuildWebhookException, InvalidWebhookTagException, MoreThanOneComponentReferenceException,
        CampaignComponentValidationRestException {
        PartnerWebhookBuilder webhookBuilder = partnerWebhookService.createWebhook(authorization)
            .withName(createRequest.getName())
            .withUrl(createRequest.getUrl());

        createRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        createRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        createRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        createRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        createRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        createRequest.getResponseBodyHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseBodyHandler(responseHandler));
        createRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        createRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        createRequest.getRetryIntervals()
            .ifPresent(retryIntervals -> webhookBuilder.withRetryIntervals(retryIntervals));
        createRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(webhookBuilder, componentIds);
        });
        createRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(webhookBuilder, componentReferences);
        });
        return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
    }

    private Webhook createGenericWebhook(Authorization authorization, WebhookCreateRequest createRequest)
        throws AuthorizationException, CampaignComponentValidationRestException, InvalidComponentReferenceException,
        BuildWebhookException, WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, MoreThanOneComponentReferenceException {
        WebhookBuilder webhookBuilder = webhookService.createGenericWebhook(authorization)
            .withName(createRequest.getName())
            .withUrl(createRequest.getUrl());

        createRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        createRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        createRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        createRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        createRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        createRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        createRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        createRequest.getRetryIntervals()
            .ifPresent(retryIntervals -> webhookBuilder.withRetryIntervals(retryIntervals));
        createRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(webhookBuilder, componentIds);
        });
        createRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(webhookBuilder, componentReferences);
        });
        return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
    }

    private Webhook createRewardWebhook(Authorization authorization, WebhookCreateRequest createRequest)
        throws AuthorizationException, CampaignComponentValidationRestException, InvalidComponentReferenceException,
        BuildWebhookException, InvalidWebhookTagException, MoreThanOneComponentReferenceException {
        RewardWebhookBuilder webhookBuilder = rewardWebhookService.createWebhook(authorization, createRequest.getUrl())
            .withName(createRequest.getName());

        createRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        createRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        createRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        createRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        createRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        createRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        createRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        createRequest.getRetryIntervals()
            .ifPresent(retryIntervals -> webhookBuilder.withRetryIntervals(retryIntervals));
        createRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(webhookBuilder, componentIds);
        });
        createRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(webhookBuilder, componentReferences);
        });

        try {
            return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        } catch (WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Webhook doUpdateWebhook(Authorization authorization, String webhookId, WebhookUpdateRequest updateRequest)
        throws WebhookNotFoundException, AuthorizationException, InvalidComponentReferenceException,
        CampaignComponentValidationRestException, BuildWebhookException, RewardWebhookNotFoundException,
        WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, ClientWebhookNotFoundException,
        MoreThanOneComponentReferenceException {
        Webhook webhook = webhookService.getWebhook(authorization, Id.valueOf(webhookId));
        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.GENERIC) {
            return updateGenericWebhook(authorization, webhookId, updateRequest);
        }

        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.REWARD) {
            return updateRewardWebhook(authorization, webhookId, updateRequest);
        }

        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.CLIENT) {
            return updateClientWebhook(authorization, webhookId, updateRequest);
        }

        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.PARTNER) {
            return updatePartnerWebhook(authorization, webhookId, updateRequest);
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(new RuntimeException("Unknown webhook type: " + webhook.getType()))
            .build();
    }

    private Webhook updateClientWebhook(Authorization authorization, String webhookId,
        WebhookUpdateRequest updateRequest) throws AuthorizationException, ClientWebhookNotFoundException,
        WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, InvalidComponentReferenceException,
        BuildWebhookException, MoreThanOneComponentReferenceException {
        ClientWebhookBuilder webhookBuilder =
            clientWebhookService.updateClientWebhook(authorization, Id.valueOf(webhookId));

        updateRequest.getName()
            .ifPresent(name -> webhookBuilder.withName(name));
        updateRequest.getUrl()
            .ifPresent(url -> webhookBuilder.withUrl(url));
        updateRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        updateRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        updateRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        updateRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        updateRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        updateRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        updateRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));

        return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
    }

    private Webhook updateGenericWebhook(Authorization authorization, String webhookId,
        WebhookUpdateRequest updateRequest) throws WebhookNotFoundException, AuthorizationException,
        WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, InvalidComponentReferenceException,
        BuildWebhookException, CampaignComponentValidationRestException,
        MoreThanOneComponentReferenceException {
        WebhookBuilder webhookBuilder = webhookService.updateGenericWebhook(authorization, Id.valueOf(webhookId));

        updateRequest.getName()
            .ifPresent(name -> webhookBuilder.withName(name));
        updateRequest.getUrl()
            .ifPresent(url -> webhookBuilder.withUrl(url));
        updateRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        updateRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        updateRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        updateRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        updateRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        updateRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        updateRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(webhookBuilder, componentIds);
        });
        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(webhookBuilder, componentReferences);
        });
        return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
    }

    private Webhook updateRewardWebhook(Authorization authorization, String webhookId,
        WebhookUpdateRequest updateRequest)
        throws RewardWebhookNotFoundException, AuthorizationException, CampaignComponentValidationRestException,
        InvalidComponentReferenceException, BuildWebhookException, MoreThanOneComponentReferenceException {
        RewardWebhookUpdateBuilder webhookBuilder =
            rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId));

        updateRequest.getName()
            .ifPresent(name -> webhookBuilder.withName(name));
        updateRequest.getUrl()
            .ifPresent(url -> webhookBuilder.withUrl(url));
        updateRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        updateRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        updateRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        updateRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        updateRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        updateRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        updateRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(webhookBuilder, componentIds);
        });
        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(webhookBuilder, componentReferences);
        });

        try {
            return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        } catch (WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Webhook updatePartnerWebhook(Authorization authorization, String webhookId,
        WebhookUpdateRequest updateRequest) throws AuthorizationException, WebhookNotFoundException,
        WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, InvalidComponentReferenceException,
        BuildWebhookException, InvalidWebhookTagException, MoreThanOneComponentReferenceException,
        CampaignComponentValidationRestException {
        PartnerWebhookBuilder webhookBuilder =
            partnerWebhookService.updateWebhook(authorization, Id.valueOf(webhookId));

        updateRequest.getName()
            .ifPresent(name -> webhookBuilder.withName(name));
        updateRequest.getUrl()
            .ifPresent(url -> webhookBuilder.withUrl(url));
        updateRequest.isEnabled()
            .ifPresent(enabled -> webhookBuilder.withEnabled(enabled));
        updateRequest.getDefaultMethod()
            .ifPresent(defaultMethod -> webhookBuilder.withDefaultMethod(defaultMethod));
        updateRequest.getClientKeyId()
            .ifPresent(clientKeyId -> webhookBuilder.withClientKeyId(clientKeyId));
        updateRequest.getDescription()
            .ifPresent(description -> webhookBuilder.withDescription(description));
        updateRequest.getRequest()
            .ifPresent(request -> webhookBuilder.withRequest(request));
        updateRequest.getResponseHandler()
            .ifPresent(responseHandler -> webhookBuilder.withResponseHandler(responseHandler));
        updateRequest.getTags()
            .ifPresent(tags -> webhookBuilder.withTags(tags));
        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(webhookBuilder, componentIds);
        });
        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(webhookBuilder, componentReferences);
        });
        return webhookBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private Webhook doArchiveWebhook(Authorization authorization, String webhookId)
        throws WebhookNotFoundException, AuthorizationException, InvalidComponentReferenceException,
        BuildWebhookException, RewardWebhookNotFoundException,
        WebhookAssociatedWithWebhookUserSubscriptionChannelException,
        WebhookAssociatedWithWebhookControllerActionException, ClientWebhookNotFoundException,
        MoreThanOneComponentReferenceException {
        Webhook webhook = webhookService.getWebhook(authorization, Id.valueOf(webhookId));

        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.GENERIC) {
            return webhookService.updateGenericWebhook(authorization, Id.valueOf(webhookId))
                .withArchived()
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        }
        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.REWARD) {
            return rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                .withArchived()
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        }
        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.CLIENT) {
            return clientWebhookService.updateClientWebhook(authorization, Id.valueOf(webhookId))
                .withArchived()
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        }
        if (webhook.getType() == com.extole.model.entity.webhook.WebhookType.PARTNER) {
            return partnerWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                .withArchived()
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(new RuntimeException("Unknown webhook type: " + webhook.getType()))
            .build();
    }

    private List<AssociatedWebhookControllerActionResponse> toAssociatedWebhookControllerActionResponses(
        List<AssociatedWebhookControllerAction> associatedWebhookActions) {
        return associatedWebhookActions.stream()
            .map(value -> new AssociatedWebhookControllerActionResponse(
                value.getCampaignId().getValue(),
                value.getControllerId().getValue(),
                value.getActionId().getValue()))
            .collect(Collectors.toList());
    }

    private List<AssociatedWebhookUserSubscriptionChannelResponse> toAssociatedWebhookUserSubscriptionChannelResponses(
        List<AssociatedWebhookUserSubscriptionChannel> associatedWebhookChannels) {
        return associatedWebhookChannels.stream()
            .map(value -> new AssociatedWebhookUserSubscriptionChannelResponse(
                value.getSubscriptionId().getValue(),
                value.getChannelId().getValue()))
            .collect(Collectors.toUnmodifiableList());
    }

}
