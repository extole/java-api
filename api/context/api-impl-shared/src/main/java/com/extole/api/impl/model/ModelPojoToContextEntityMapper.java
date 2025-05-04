package com.extole.api.impl.model;

import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.event.client.EventPojoType;
import com.extole.api.impl.model.campaign.CampaignImpl;
import com.extole.api.impl.model.campaign.built.BuiltCampaignImpl;
import com.extole.api.impl.model.reward.supplier.CustomRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.ManualCouponRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.PayPalPayoutsRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.SalesforceCouponRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.TangoRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.built.BuiltCustomRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.built.BuiltManualCouponRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.built.BuiltPayPalPayoutsRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.built.BuiltSalesforceCouponRewardSupplierImpl;
import com.extole.api.impl.model.reward.supplier.built.BuiltTangoRewardSupplierImpl;
import com.extole.api.model.Audience;
import com.extole.api.model.Client;
import com.extole.api.model.ClientProperty;
import com.extole.api.model.ClientSettings;
import com.extole.api.model.DynamicAudienceList;
import com.extole.api.model.EventEntity;
import com.extole.api.model.ExternalSftpDestination;
import com.extole.api.model.LocalSftpDestination;
import com.extole.api.model.ReportRunner;
import com.extole.api.model.ReportType;
import com.extole.api.model.StaticAudienceList;
import com.extole.api.model.UploadedAudienceList;
import com.extole.api.model.UserProperty;
import com.extole.api.model.UserSubscription;
import com.extole.api.model.campaign.Campaign;
import com.extole.api.model.campaign.built.BuiltCampaign;
import com.extole.api.model.reward.supplier.CustomRewardSupplier;
import com.extole.api.model.reward.supplier.ManualCouponRewardSupplier;
import com.extole.api.model.reward.supplier.PayPalPayoutsRewardSupplier;
import com.extole.api.model.reward.supplier.SalesforceCouponRewardSupplier;
import com.extole.api.model.reward.supplier.TangoRewardSupplier;
import com.extole.api.model.reward.supplier.built.BuiltCustomRewardSupplier;
import com.extole.api.model.reward.supplier.built.BuiltManualCouponRewardSupplier;
import com.extole.api.model.reward.supplier.built.BuiltPayPalPayoutsRewardSupplier;
import com.extole.api.model.reward.supplier.built.BuiltSalesforceCouponRewardSupplier;
import com.extole.api.model.reward.supplier.built.BuiltTangoRewardSupplier;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.event.model.change.audience.AudiencePojo;
import com.extole.event.model.change.auth.provider.type.AuthProviderTypePojo;
import com.extole.event.model.change.blocks.BlockChangePojo;
import com.extole.event.model.change.client.ClientPojo;
import com.extole.event.model.change.client.ClientSettingsPojo;
import com.extole.event.model.change.client.DimensionMappingPojo;
import com.extole.event.model.change.client.InjectorLabelPojo;
import com.extole.event.model.change.client.LegacyPrehandlerPojo;
import com.extole.event.model.change.client.auth.provider.AuthProviderPojo;
import com.extole.event.model.change.client.email.EmailDomainPojo;
import com.extole.event.model.change.client.logo.LogoPojo;
import com.extole.event.model.change.client.salesforce.ClientSalesforceSettingsPojo;
import com.extole.event.model.change.client.security.ClientSecuritySettingsPojo;
import com.extole.event.model.change.client.security.key.GenericClientKeyPojo;
import com.extole.event.model.change.client.security.key.OAuthClientKeyPojo;
import com.extole.event.model.change.client.security.key.OAuthLeadPerfectionClientKeyPojo;
import com.extole.event.model.change.client.security.key.OAuthListrakClientKeyPojo;
import com.extole.event.model.change.client.security.key.OAuthOptimoveClientKeyPojo;
import com.extole.event.model.change.client.security.key.OAuthSalesforceClientKeyPojo;
import com.extole.event.model.change.client.security.key.OAuthSfdcClientKeyPojo;
import com.extole.event.model.change.client.security.key.SslPkcs12ClientKeyPojo;
import com.extole.event.model.change.client.sftp.ExternalSftpDestinationPojo;
import com.extole.event.model.change.client.sftp.LocalSftpDestinationPojo;
import com.extole.event.model.change.client.support.SupportPojo;
import com.extole.event.model.change.client.tango.ClientTangoSettingsPojo;
import com.extole.event.model.change.program.PublicProgramPojo;
import com.extole.event.model.change.promotion.PromotionLinkPojo;
import com.extole.event.model.change.property.ClientPropertyPojo;
import com.extole.event.model.change.property.UserPropertyPojo;
import com.extole.event.model.change.reward.supplier.CustomRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.ManualCouponRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.PayPalPayoutsRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.SalesforceCouponRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.TangoRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.built.BuiltCustomRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.built.BuiltManualCouponRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.built.BuiltPayPalPayoutsRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.built.BuiltSalesforceCouponRewardSupplierPojo;
import com.extole.event.model.change.reward.supplier.built.BuiltTangoRewardSupplierPojo;
import com.extole.event.model.change.snooze.SnoozePojo;
import com.extole.event.model.change.subscription.UserSubscriptionPojo;
import com.extole.event.model.change.tango.TangoAccountPojo;
import com.extole.event.model.change.timeline.ClientTimelineEntryPojo;
import com.extole.event.model.change.user.UserPojo;
import com.extole.event.model.change.webhook.built.client.BuiltClientWebhookPojo;
import com.extole.event.model.change.webhook.built.generic.BuiltGenericWebhookPojo;
import com.extole.event.model.change.webhook.built.reward.BuiltRewardWebhookPojo;
import com.extole.event.model.change.webhook.client.ClientWebhookPojo;
import com.extole.event.model.change.webhook.generic.GenericWebhookPojo;
import com.extole.event.model.change.webhook.reward.RewardWebhookPojo;
import com.extole.model.pojo.campaign.CampaignPojo;
import com.extole.model.pojo.campaign.built.BuiltCampaignPojo;
import com.extole.model.pojo.client.variable.ClientVariablePojo;
import com.extole.model.pojo.media.MediaAssetPojo;
import com.extole.model.pojo.prehandler.PrehandlerPojo;
import com.extole.reporting.pojo.audience.list.DynamicAudienceListPojo;
import com.extole.reporting.pojo.audience.list.StaticAudienceListPojo;
import com.extole.reporting.pojo.audience.list.UploadedAudienceListPojo;
import com.extole.reporting.pojo.batch.BatchJobPojo;
import com.extole.reporting.pojo.file.assets.FileAssetPojo;
import com.extole.reporting.pojo.posthandler.ReportPostHandlerPojo;
import com.extole.reporting.pojo.report.runner.RefreshingReportRunnerPojo;
import com.extole.reporting.pojo.report.runner.ReportRunnerPojo;
import com.extole.reporting.pojo.report.runner.ScheduledReportRunnerPojo;
import com.extole.reporting.pojo.report.type.BaseReportTypePojo;
import com.extole.reporting.pojo.report.type.ConfiguredReportTypePojo;
import com.extole.reporting.pojo.report.type.DashboardReportTypePojo;
import com.extole.reporting.pojo.report.type.SparkReportTypePojo;
import com.extole.reporting.pojo.report.type.SqlReportTypePojo;

public final class ModelPojoToContextEntityMapper {
    private static final Logger LOG = LoggerFactory.getLogger(ModelPojoToContextEntityMapper.class);
    private static final EmptyEventEntity DEFAULT_EMPTY_EVENT_ENTITY = new EmptyEventEntity();
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();

    // TODO define an entities and mappers for the remain pojos with this annotation @ClientPojoType ENG-20247
    private static final Map<EventPojoType, Class<?>> MODEL_POJO_TYPES_BY_CONTEXT_POJO_TYPE =
        ImmutableMap
            .<EventPojoType, Class<?>>builder()
            .put(EventPojoType.AUDIENCE, AudiencePojo.class)
            .put(EventPojoType.AUTH_PROVIDER_TYPE, AuthProviderTypePojo.class)
            .put(EventPojoType.BLOCK, BlockChangePojo.class)
            .put(EventPojoType.AUTH_PROVIDER, AuthProviderPojo.class)
            .put(EventPojoType.EMAIL_DOMAIN, EmailDomainPojo.class)
            .put(EventPojoType.LOGO, LogoPojo.class)
            .put(EventPojoType.CLIENT_SALESFORCE_SETTINGS, ClientSalesforceSettingsPojo.class)
            .put(EventPojoType.GENERIC_CLIENT_KEY, GenericClientKeyPojo.class)
            .put(EventPojoType.OAUTH_CLIENT_KEY, OAuthClientKeyPojo.class)
            .put(EventPojoType.OAUTH_LEAD_PERFECTION_CLIENT_KEY, OAuthLeadPerfectionClientKeyPojo.class)
            .put(EventPojoType.OAUTH_LISTRAK_CLIENT_KEY, OAuthListrakClientKeyPojo.class)
            .put(EventPojoType.OAUTH_OPTIMOVE_CLIENT_KEY, OAuthOptimoveClientKeyPojo.class)
            .put(EventPojoType.OAUTH_SALESFORCE_CLIENT_KEY, OAuthSalesforceClientKeyPojo.class)
            .put(EventPojoType.OAUTH_SFDC_CLIENT_KEY, OAuthSfdcClientKeyPojo.class)
            .put(EventPojoType.SSL_PKS_CLIENT_KEY, SslPkcs12ClientKeyPojo.class)
            .put(EventPojoType.CLIENT_SECURITY_SETTINGS, ClientSecuritySettingsPojo.class)
            .put(EventPojoType.EXTERNAL_SFTP_DESTINATION, ExternalSftpDestinationPojo.class)
            .put(EventPojoType.LOCAL_SFTP_DESTINATION, LocalSftpDestinationPojo.class)
            .put(EventPojoType.SUPPORT, SupportPojo.class)
            .put(EventPojoType.CLIENT_TANGO_SETTINGS, ClientTangoSettingsPojo.class)
            .put(EventPojoType.CLIENT, ClientPojo.class)
            .put(EventPojoType.CLIENT_SETTINGS, ClientSettingsPojo.class)
            .put(EventPojoType.DIMENSION_MAPPING, DimensionMappingPojo.class)
            .put(EventPojoType.INJECTOR_LABEL, InjectorLabelPojo.class)
            .put(EventPojoType.LEGACY_PREHANDLER, LegacyPrehandlerPojo.class)
            .put(EventPojoType.PUBLIC_PROGRAM, PublicProgramPojo.class)
            .put(EventPojoType.PROMOTION_LINK, PromotionLinkPojo.class)
            .put(EventPojoType.CLIENT_PROPERTY, ClientPropertyPojo.class)
            .put(EventPojoType.USER_PROPERTY, UserPropertyPojo.class)
            .put(EventPojoType.CUSTOM_REWARD_SUPPLIER, CustomRewardSupplierPojo.class)
            .put(EventPojoType.MANUAL_COUPON_REWARD_SUPPLIER, ManualCouponRewardSupplierPojo.class)
            .put(EventPojoType.PAYPAL_PAYOUTS_REWARD_SUPPLIER, PayPalPayoutsRewardSupplierPojo.class)
            .put(EventPojoType.SALESFORCE_COUPON_REWARD_SUPPLIER, SalesforceCouponRewardSupplierPojo.class)
            .put(EventPojoType.TANGO_REWARD_SUPPLIER, TangoRewardSupplierPojo.class)
            .put(EventPojoType.BUILT_CUSTOM_REWARD_SUPPLIER, BuiltCustomRewardSupplierPojo.class)
            .put(EventPojoType.BUILT_MANUAL_COUPON_REWARD_SUPPLIER, BuiltManualCouponRewardSupplierPojo.class)
            .put(EventPojoType.BUILT_PAYPAL_PAYOUTS_REWARD_SUPPLIER, BuiltPayPalPayoutsRewardSupplierPojo.class)
            .put(EventPojoType.BUILT_SALESFORCE_COUPON_REWARD_SUPPLIER, BuiltSalesforceCouponRewardSupplierPojo.class)
            .put(EventPojoType.BUILT_TANGO_REWARD_SUPPLIER, BuiltTangoRewardSupplierPojo.class)
            .put(EventPojoType.USER_SUBSCRIPTION, UserSubscriptionPojo.class)
            .put(EventPojoType.TANGO_ACCOUNT, TangoAccountPojo.class)
            .put(EventPojoType.CLIENT_TIMELINE, ClientTimelineEntryPojo.class)
            .put(EventPojoType.USER_CHANGE, UserPojo.class)
            .put(EventPojoType.BUILT_CLIENT_WEBHOOK, BuiltClientWebhookPojo.class)
            .put(EventPojoType.BUILT_GENERIC_WEBHOOK, BuiltGenericWebhookPojo.class)
            .put(EventPojoType.BUILT_REWARD_WEBHOOK, BuiltRewardWebhookPojo.class)
            .put(EventPojoType.CLIENT_WEBHOOK, ClientWebhookPojo.class)
            .put(EventPojoType.GENERIC_WEBHOOK, GenericWebhookPojo.class)
            .put(EventPojoType.REWARD_WEBHOOK, RewardWebhookPojo.class)
            .put(EventPojoType.BUILT_CAMPAIGN, BuiltCampaignPojo.class)
            .put(EventPojoType.CAMPAIGN, CampaignPojo.class)
            .put(EventPojoType.CLIENT_VARIABLE, ClientVariablePojo.class)
            .put(EventPojoType.MEDIA_ASSET, MediaAssetPojo.class)
            .put(EventPojoType.PREHANDLER, PrehandlerPojo.class)
            .put(EventPojoType.SNOOZE, SnoozePojo.class)
            .put(EventPojoType.DYNAMIC_AUDIENCE_LIST, DynamicAudienceListPojo.class)
            .put(EventPojoType.STATIC_AUDIENCE_LIST, StaticAudienceListPojo.class)
            .put(EventPojoType.UPLOADED_AUDIENCE_LIST, UploadedAudienceListPojo.class)
            .put(EventPojoType.BATCH_JOB, BatchJobPojo.class)
            .put(EventPojoType.FILE_ASSET, FileAssetPojo.class)
            .put(EventPojoType.REPORT_POST_HANDLER, ReportPostHandlerPojo.class)
            .put(EventPojoType.REPORT_RUNNER, ReportRunnerPojo.class)
            .put(EventPojoType.REPORT_TYPE, BaseReportTypePojo.class)
            .build();
    private static final Map<EventPojoType, Function<Object, EventEntity>> POJO_MAPPERS_BY_TYPE =
        ImmutableMap
            .<EventPojoType, Function<Object, EventEntity>>builder()
            .put(EventPojoType.EXTERNAL_SFTP_DESTINATION,
                value -> mapExternalSftpDestinationPojo((ExternalSftpDestinationPojo) value))
            .put(EventPojoType.LOCAL_SFTP_DESTINATION,
                value -> mapLocalSftpDestinationPojo((LocalSftpDestinationPojo) value))
            .put(EventPojoType.AUDIENCE, value -> mapAudiencePojo((AudiencePojo) value))
            .put(EventPojoType.AUTH_PROVIDER_TYPE,
                value -> mapAuthProviderTypePojo((AuthProviderTypePojo) value))
            .put(EventPojoType.BLOCK, value -> mapBlockChangePojo((BlockChangePojo) value))
            .put(EventPojoType.CLIENT, value -> mapClientPojo((ClientPojo) value))
            .put(EventPojoType.CLIENT_SETTINGS, value -> mapClientSettingsPojo((ClientSettingsPojo) value))
            .put(EventPojoType.DIMENSION_MAPPING,
                value -> mapDimensionMappingPojo((DimensionMappingPojo) value))
            .put(EventPojoType.INJECTOR_LABEL, value -> mapInjectorLabelPojo((InjectorLabelPojo) value))
            .put(EventPojoType.LEGACY_PREHANDLER,
                value -> mapLegacyPrehandlerPojo((LegacyPrehandlerPojo) value))
            .put(EventPojoType.AUTH_PROVIDER, value -> mapAuthProviderPojo((AuthProviderPojo) value))
            .put(EventPojoType.EMAIL_DOMAIN, value -> mapEmailDomainPojo((EmailDomainPojo) value))
            .put(EventPojoType.LOGO, value -> mapLogoPojo((LogoPojo) value))
            .put(EventPojoType.CLIENT_SALESFORCE_SETTINGS,
                value -> mapClientSalesforceSettingsPojo((ClientSalesforceSettingsPojo) value))
            .put(EventPojoType.CLIENT_SECURITY_SETTINGS,
                value -> mapClientSecuritySettingsPojo((ClientSecuritySettingsPojo) value))
            .put(EventPojoType.GENERIC_CLIENT_KEY,
                value -> mapGenericClientKeyPojo((GenericClientKeyPojo) value))
            .put(EventPojoType.OAUTH_CLIENT_KEY, value -> mapOAuthClientKeyPojo((OAuthClientKeyPojo) value))
            .put(EventPojoType.OAUTH_LEAD_PERFECTION_CLIENT_KEY,
                value -> mapOAuthLeadPerfectionClientKeyPojo((OAuthLeadPerfectionClientKeyPojo) value))
            .put(EventPojoType.OAUTH_LISTRAK_CLIENT_KEY,
                value -> mapOAuthListrakClientKeyPojo((OAuthListrakClientKeyPojo) value))
            .put(EventPojoType.OAUTH_OPTIMOVE_CLIENT_KEY,
                value -> mapOAuthOptimoveClientKeyPojo((OAuthOptimoveClientKeyPojo) value))
            .put(EventPojoType.OAUTH_SALESFORCE_CLIENT_KEY,
                value -> mapOAuthSalesforceClientKeyPojo((OAuthSalesforceClientKeyPojo) value))
            .put(EventPojoType.OAUTH_SFDC_CLIENT_KEY,
                value -> mapOAuthSfdcClientKeyPojo((OAuthSfdcClientKeyPojo) value))
            .put(EventPojoType.SSL_PKS_CLIENT_KEY,
                value -> mapSslPkcs12ClientKeyPojo((SslPkcs12ClientKeyPojo) value))
            .put(EventPojoType.SUPPORT, value -> mapSupportPojo((SupportPojo) value))
            .put(EventPojoType.CLIENT_TANGO_SETTINGS,
                value -> mapClientTangoSettingsPojo((ClientTangoSettingsPojo) value))
            .put(EventPojoType.PUBLIC_PROGRAM, value -> mapPublicProgramPojo((PublicProgramPojo) value))
            .put(EventPojoType.PROMOTION_LINK, value -> mapPromotionLinkPojo((PromotionLinkPojo) value))
            .put(EventPojoType.CLIENT_PROPERTY, value -> mapClientPropertyPojo((ClientPropertyPojo) value))
            .put(EventPojoType.USER_PROPERTY, value -> mapUserPropertyPojo((UserPropertyPojo) value))
            .put(EventPojoType.CUSTOM_REWARD_SUPPLIER,
                value -> mapCustomRewardSupplierPojo((CustomRewardSupplierPojo) value))
            .put(EventPojoType.MANUAL_COUPON_REWARD_SUPPLIER,
                value -> mapManualCouponRewardSupplierPojo((ManualCouponRewardSupplierPojo) value))
            .put(EventPojoType.PAYPAL_PAYOUTS_REWARD_SUPPLIER,
                value -> mapPayPalPayoutsRewardSupplierPojo((PayPalPayoutsRewardSupplierPojo) value))
            .put(EventPojoType.SALESFORCE_COUPON_REWARD_SUPPLIER,
                value -> mapSalesforceCouponRewardSupplierPojo((SalesforceCouponRewardSupplierPojo) value))
            .put(EventPojoType.TANGO_REWARD_SUPPLIER,
                value -> mapTangoRewardSupplierPojo((TangoRewardSupplierPojo) value))
            .put(EventPojoType.BUILT_CUSTOM_REWARD_SUPPLIER,
                (value) -> mapBuiltCustomRewardSupplierPojo((BuiltCustomRewardSupplierPojo) value))
            .put(EventPojoType.BUILT_MANUAL_COUPON_REWARD_SUPPLIER,
                (value) -> mapBuiltManualCouponRewardSupplierPojo((BuiltManualCouponRewardSupplierPojo) value))
            .put(EventPojoType.BUILT_PAYPAL_PAYOUTS_REWARD_SUPPLIER,
                (value) -> mapBuiltPayPalPayoutsRewardSupplierPojo((BuiltPayPalPayoutsRewardSupplierPojo) value))
            .put(EventPojoType.BUILT_SALESFORCE_COUPON_REWARD_SUPPLIER,
                (value) -> mapBuiltSalesforceCouponRewardSupplierPojo((BuiltSalesforceCouponRewardSupplierPojo) value))
            .put(EventPojoType.BUILT_TANGO_REWARD_SUPPLIER,
                (value) -> mapBuiltTangoRewardSupplierPojo((BuiltTangoRewardSupplierPojo) value))
            .put(EventPojoType.USER_SUBSCRIPTION,
                value -> mapUserSubscriptionPojo((UserSubscriptionPojo) value))
            .put(EventPojoType.TANGO_ACCOUNT, value -> mapTangoAccountPojo((TangoAccountPojo) value))
            .put(EventPojoType.CLIENT_TIMELINE,
                value -> mapClientTimelineEntryPojo((ClientTimelineEntryPojo) value))
            .put(EventPojoType.USER_CHANGE, value -> mapUserPojo((UserPojo) value))
            .put(EventPojoType.BUILT_CLIENT_WEBHOOK,
                value -> mapBuiltClientWebhookPojo((BuiltClientWebhookPojo) value))
            .put(EventPojoType.BUILT_GENERIC_WEBHOOK,
                value -> mapBuiltGenericWebhookPojo((BuiltGenericWebhookPojo) value))
            .put(EventPojoType.BUILT_REWARD_WEBHOOK,
                value -> mapBuiltRewardWebhookPojo((BuiltRewardWebhookPojo) value))
            .put(EventPojoType.CLIENT_WEBHOOK, value -> mapClientWebhookPojo((ClientWebhookPojo) value))
            .put(EventPojoType.GENERIC_WEBHOOK, value -> mapGenericWebhookPojo((GenericWebhookPojo) value))
            .put(EventPojoType.REWARD_WEBHOOK, value -> mapRewardWebhookPojo((RewardWebhookPojo) value))
            .put(EventPojoType.CAMPAIGN, value -> mapCampaignPojo((CampaignPojo) value))
            .put(EventPojoType.BUILT_CAMPAIGN, value -> mapBuiltCampaignPojo((BuiltCampaignPojo) value))
            .put(EventPojoType.CLIENT_VARIABLE, value -> mapClientVariablePojo((ClientVariablePojo) value))
            .put(EventPojoType.MEDIA_ASSET, value -> mapMediaAssetPojo((MediaAssetPojo) value))
            .put(EventPojoType.PREHANDLER, value -> mapPrehandlerPojo((PrehandlerPojo) value))
            .put(EventPojoType.SNOOZE, value -> mapSnoozePojo((SnoozePojo) value))
            .put(EventPojoType.DYNAMIC_AUDIENCE_LIST,
                value -> mapDynamicAudienceListPojo((DynamicAudienceListPojo) value))
            .put(EventPojoType.STATIC_AUDIENCE_LIST,
                value -> mapStaticAudienceListPojo((StaticAudienceListPojo) value))
            .put(EventPojoType.UPLOADED_AUDIENCE_LIST,
                value -> mapUploadedAudienceListPojo((UploadedAudienceListPojo) value))
            .put(EventPojoType.BATCH_JOB, value -> mapBatchJobPojo((BatchJobPojo) value))
            .put(EventPojoType.FILE_ASSET, value -> mapFileAssetPojo((FileAssetPojo) value))
            .put(EventPojoType.REPORT_POST_HANDLER,
                value -> mapReportPostHandlerPojo((ReportPostHandlerPojo) value))
            .put(EventPojoType.REPORT_RUNNER,
                value -> mapReportRunnerPojo((ReportRunnerPojo) value))
            .put(EventPojoType.REPORT_TYPE,
                value -> mapBaseReportTypePojo((BaseReportTypePojo) value))
            .build();

    public EventEntity map(EventPojoType pojoType, Map<String, Object> pojo) {
        Class<?> modelPojoClass = MODEL_POJO_TYPES_BY_CONTEXT_POJO_TYPE.get(pojoType);
        if (modelPojoClass == null) {
            LOG.warn("Was not possible to construct event entity for the eventPojoType:" + pojoType.getValue()
                + " due to unhandled pojo type");
            return DEFAULT_EMPTY_EVENT_ENTITY;
        }

        Function<Object, EventEntity> mapper = POJO_MAPPERS_BY_TYPE.get(pojoType);
        if (mapper == null) {
            LOG.warn("Was not possible to construct event entity for the eventPojoType:" + pojoType.getValue()
                + " due to missed mapper");
            return DEFAULT_EMPTY_EVENT_ENTITY;
        }

        try {
            return mapper.apply(OBJECT_MAPPER.convertValue(pojo, modelPojoClass));
        } catch (IllegalArgumentException e) {
            LOG.warn("Was not possible to construct event entity for the eventPojoType:" + pojoType.getValue()
                + " due to deserialize error", e);
            return DEFAULT_EMPTY_EVENT_ENTITY;
        }
    }

    private static ReportRunner mapReportRunnerPojo(ReportRunnerPojo value) {
        if (value instanceof ScheduledReportRunnerPojo) {
            return new ScheduleReportRunnerImpl((ScheduledReportRunnerPojo) value);
        }
        if (value instanceof RefreshingReportRunnerPojo) {
            return new RefreshingReportRunnerImpl((RefreshingReportRunnerPojo) value);
        }
        throw new IllegalStateException("Unknown report runner: " + value);
    }

    private static EventEntity mapReportPostHandlerPojo(ReportPostHandlerPojo value) {
        return new ReportPostHandlerImpl(value);
    }

    private static UploadedAudienceList mapUploadedAudienceListPojo(UploadedAudienceListPojo value) {
        return new UploadedAudienceListImpl(value);
    }

    private static StaticAudienceList mapStaticAudienceListPojo(StaticAudienceListPojo value) {
        return new StaticAudienceListImpl(value);
    }

    private static DynamicAudienceList mapDynamicAudienceListPojo(DynamicAudienceListPojo value) {
        return new DynamicAudienceListImpl(value);
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapSnoozePojo(SnoozePojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapPrehandlerPojo(PrehandlerPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapMediaAssetPojo(MediaAssetPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapClientVariablePojo(ClientVariablePojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    private static BuiltCampaign mapBuiltCampaignPojo(BuiltCampaignPojo value) {
        return new BuiltCampaignImpl(value);
    }

    private static Campaign mapCampaignPojo(CampaignPojo value) {
        return new CampaignImpl(value);
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapRewardWebhookPojo(RewardWebhookPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapGenericWebhookPojo(GenericWebhookPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapClientWebhookPojo(ClientWebhookPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapBuiltRewardWebhookPojo(BuiltRewardWebhookPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapBuiltGenericWebhookPojo(BuiltGenericWebhookPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapBuiltClientWebhookPojo(BuiltClientWebhookPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapUserPojo(UserPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapClientTimelineEntryPojo(ClientTimelineEntryPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapTangoAccountPojo(TangoAccountPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    private static TangoRewardSupplier mapTangoRewardSupplierPojo(TangoRewardSupplierPojo value) {
        return new TangoRewardSupplierImpl(value);
    }

    private static SalesforceCouponRewardSupplier
        mapSalesforceCouponRewardSupplierPojo(SalesforceCouponRewardSupplierPojo value) {
        return new SalesforceCouponRewardSupplierImpl(value);
    }

    private static PayPalPayoutsRewardSupplier
        mapPayPalPayoutsRewardSupplierPojo(PayPalPayoutsRewardSupplierPojo value) {
        return new PayPalPayoutsRewardSupplierImpl(value);
    }

    private static ManualCouponRewardSupplier
        mapManualCouponRewardSupplierPojo(ManualCouponRewardSupplierPojo value) {
        return new ManualCouponRewardSupplierImpl(value);
    }

    private static CustomRewardSupplier
        mapCustomRewardSupplierPojo(CustomRewardSupplierPojo value) {
        return new CustomRewardSupplierImpl(value);
    }

    private static BuiltTangoRewardSupplier mapBuiltTangoRewardSupplierPojo(BuiltTangoRewardSupplierPojo value) {
        return new BuiltTangoRewardSupplierImpl(value);
    }

    private static BuiltSalesforceCouponRewardSupplier
        mapBuiltSalesforceCouponRewardSupplierPojo(BuiltSalesforceCouponRewardSupplierPojo value) {
        return new BuiltSalesforceCouponRewardSupplierImpl(value);
    }

    private static BuiltPayPalPayoutsRewardSupplier
        mapBuiltPayPalPayoutsRewardSupplierPojo(BuiltPayPalPayoutsRewardSupplierPojo value) {
        return new BuiltPayPalPayoutsRewardSupplierImpl(value);
    }

    private static BuiltManualCouponRewardSupplier
        mapBuiltManualCouponRewardSupplierPojo(BuiltManualCouponRewardSupplierPojo value) {
        return new BuiltManualCouponRewardSupplierImpl(value);
    }

    private static BuiltCustomRewardSupplier mapBuiltCustomRewardSupplierPojo(BuiltCustomRewardSupplierPojo value) {
        return new BuiltCustomRewardSupplierImpl(value);
    }

    private static UserProperty mapUserPropertyPojo(UserPropertyPojo value) {
        return new UserPropertyImpl(value);
    }

    private static ClientProperty mapClientPropertyPojo(ClientPropertyPojo value) {
        return new ClientPropertyImpl(value);
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapPromotionLinkPojo(PromotionLinkPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapPublicProgramPojo(PublicProgramPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapClientTangoSettingsPojo(ClientTangoSettingsPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapSupportPojo(SupportPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapSslPkcs12ClientKeyPojo(SslPkcs12ClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapOAuthSfdcClientKeyPojo(OAuthSfdcClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapOAuthSalesforceClientKeyPojo(OAuthSalesforceClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapOAuthOptimoveClientKeyPojo(OAuthOptimoveClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapOAuthListrakClientKeyPojo(OAuthListrakClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapOAuthLeadPerfectionClientKeyPojo(OAuthLeadPerfectionClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapOAuthClientKeyPojo(OAuthClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapGenericClientKeyPojo(GenericClientKeyPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapClientSecuritySettingsPojo(ClientSecuritySettingsPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapClientSalesforceSettingsPojo(ClientSalesforceSettingsPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapLogoPojo(LogoPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapEmailDomainPojo(EmailDomainPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapAuthProviderPojo(AuthProviderPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapLegacyPrehandlerPojo(LegacyPrehandlerPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapDimensionMappingPojo(DimensionMappingPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    private static ReportType mapBaseReportTypePojo(BaseReportTypePojo value) {
        if (value instanceof SqlReportTypePojo) {
            return new SqlReportTypeImpl((SqlReportTypePojo) value);
        }
        if (value instanceof ConfiguredReportTypePojo) {
            return new ConfiguredReportTypeImpl((ConfiguredReportTypePojo) value);
        }
        if (value instanceof SparkReportTypePojo) {
            return new SparkReportTypeImpl((SparkReportTypePojo) value);
        }
        if (value instanceof DashboardReportTypePojo) {
            return new DashboardReportTypeImpl((DashboardReportTypePojo) value);
        }
        throw new IllegalStateException("Unknown report type: " + value);
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapInjectorLabelPojo(InjectorLabelPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    private static ClientSettings mapClientSettingsPojo(ClientSettingsPojo value) {
        return new ClientSettingsImpl(value);
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapBatchJobPojo(BatchJobPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapFileAssetPojo(FileAssetPojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapBlockChangePojo(BlockChangePojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    // TODO define an interface and impl for this event entity ENG-20247
    private static EventEntity mapAuthProviderTypePojo(AuthProviderTypePojo value) {
        return DEFAULT_EMPTY_EVENT_ENTITY;
    }

    private static Audience mapAudiencePojo(AudiencePojo value) {
        return new AudienceImpl(value);
    }

    private static LocalSftpDestination mapLocalSftpDestinationPojo(LocalSftpDestinationPojo value) {
        return new LocalSftpDestinationImpl(value);
    }

    private static ExternalSftpDestination mapExternalSftpDestinationPojo(ExternalSftpDestinationPojo value) {
        return new ExternalSftpDestinationImpl(value);
    }

    private static Client mapClientPojo(ClientPojo clientPojo) {
        return new ClientImpl(clientPojo);
    }

    private static UserSubscription mapUserSubscriptionPojo(UserSubscriptionPojo userSubscriptionPojo) {
        return new UserSubscriptionImpl(userSubscriptionPojo);
    }

    public static final class EmptyEventEntity implements EventEntity {
        @Override
        public String getId() {
            return StringUtils.EMPTY;
        }
    }

}
