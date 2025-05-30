package com.extole.api.event.client;

import org.apache.commons.lang3.StringUtils;

public enum EventPojoType {
    NONE(StringUtils.EMPTY),
    CLIENT("ClientChangeEvent"),
    AUDIENCE("Audience"),
    AUTH_PROVIDER_TYPE("AuthProviderTypeChangeEvent"),
    MANAGED_AUTHORIZATION("ManagedAuthorizationChangeEvent"),
    BLOCK("BlockChangePojo"),
    CLIENT_SETTINGS("ClientSettingsEvent"),
    DIMENSION_MAPPING("DimensionMappingChangeEvent"),
    INJECTOR_LABEL("InjectorLabelChangeEvent"),
    LEGACY_PREHANDLER("PrehandlerChangeEvent"),
    AUTH_PROVIDER("AuthProviderChangeEvent"),
    EMAIL_DOMAIN("EmailDomainPojo"),
    LOGO("LogoChangeEvent"),
    CLIENT_SALESFORCE_SETTINGS("ClientSalesforceSettingsPojo"),
    CLIENT_SECURITY_SETTINGS("ClientSecuritySettingsChangeEvent"),
    GENERIC_CLIENT_KEY("GenericClientKeyChangeEvent"),
    OAUTH_CLIENT_KEY("OAuthClientKeyChangeEvent"),
    OAUTH_GENERIC_CLIENT_KEY("OAuthGenericClientKeyChangeEvent"),
    OAUTH_LEAD_PERFECTION_CLIENT_KEY("OAuthLeadPerfectionClientKeyChangeEvent"),
    OAUTH_LISTRAK_CLIENT_KEY("OAuthListrakClientKeyChangeEvent"),
    OAUTH_OPTIMOVE_CLIENT_KEY("OAuthOptimoveClientKeyChangeEvent"),
    OAUTH_SALESFORCE_CLIENT_KEY("OAuthSalesforceClientKeyChangeEvent"),
    OAUTH_SFDC_CLIENT_KEY("OAuthSfdcClientKeyChangeEvent"),
    OAUTH_SFDC_PASSWORD_CLIENT_KEY("OAuthSfdcPasswordClientKeyChangeEvent"),
    SSL_PKS_CLIENT_KEY("SslPkcsClientKey"),
    BUILT_GENERIC_CLIENT_KEY("BuiltGenericClientKeyChangeEvent"),
    BUILT_OAUTH_CLIENT_KEY("BuiltOAuthClientKeyChangeEvent"),
    BUILT_OAUTH_GENERIC_CLIENT_KEY("BuiltOAuthGenericClientKeyChangeEvent"),
    BUILT_OAUTH_LISTRAK_CLIENT_KEY("BuiltOAuthListrakClientKeyChangeEvent"),
    BUILT_OAUTH_LEAD_PERFECTION_CLIENT_KEY("BuiltOAuthLeadPerfectionClientKeyChangeEvent"),
    BUILT_OAUTH_OPTIMOVE_CLIENT_KEY("BuiltOAuthOptimoveClientKeyChangeEvent"),
    BUILT_OAUTH_SALESFORCE_CLIENT_KEY("BuiltOAuthSalesforceClientKeyChangeEvent"),
    BUILT_OAUTH_SFDC_CLIENT_KEY("BuiltOAuthSfdcClientKeyChangeEvent"),
    BUILT_OAUTH_SFDC_PASSWORD_CLIENT_KEY("BuiltOAuthSfdcPasswordClientKeyChangeEvent"),
    BUILT_SSL_PKCS12_CLIENT_KEY("BuiltSslPkcsClientKey"),
    EXTERNAL_SFTP_DESTINATION("ExternalSftpDestination"),
    LOCAL_SFTP_DESTINATION("LocalSftpDestination"),
    SUPPORT("SupportPojo"),
    CLIENT_TANGO_SETTINGS("ClientTangoSettingsPojo"),
    PUBLIC_PROGRAM("ProgramChangeEvent"),
    PROMOTION_LINK("PromotionLinkPojo"),
    CLIENT_PROPERTY("ClientPropertyChangeEvent"),
    USER_PROPERTY("UserPropertyChangeEvent"),
    CUSTOM_REWARD_SUPPLIER("BaseCustomRewardSupplierChangeEvent"),
    MANUAL_COUPON_REWARD_SUPPLIER("BaseManualCouponRewardSupplierChangeEvent"),
    PAYPAL_PAYOUTS_REWARD_SUPPLIER("BasePayPalPayoutsRewardSupplierChangeEvent"),
    SALESFORCE_COUPON_REWARD_SUPPLIER("BaseSalesforceCouponRewardSupplierChangeEvent"),
    TANGO_REWARD_SUPPLIER("BaseTangoRewardSupplierChangeEvent"),
    BUILT_CUSTOM_REWARD_SUPPLIER("CustomRewardSupplierChangeEvent"),
    BUILT_MANUAL_COUPON_REWARD_SUPPLIER("ManualCouponRewardSupplierChangeEvent"),
    BUILT_PAYPAL_PAYOUTS_REWARD_SUPPLIER("PayPalPayoutsRewardSupplierChangeEvent"),
    BUILT_SALESFORCE_COUPON_REWARD_SUPPLIER("SalesforceCouponRewardSupplierChangeEvent"),
    BUILT_TANGO_REWARD_SUPPLIER("TangoRewardSupplierChangeEvent"),
    USER_SUBSCRIPTION("UserSubscription"),
    TANGO_ACCOUNT("TangoAccountPojo"),
    CLIENT_TIMELINE("ClientTimelineEventPojo"),
    USER_CHANGE("UserChangeEvent"),
    BUILT_CLIENT_WEBHOOK("BuiltClientWebhookChangeEvent"),
    BUILT_GENERIC_WEBHOOK("BuiltGenericWebhookChangeEvent"),
    BUILT_REWARD_WEBHOOK("BuiltRewardWebhookChangeEvent"),
    CLIENT_WEBHOOK("ClientWebhookChangeEvent"),
    GENERIC_WEBHOOK("GenericWebhookChangeEvent"),
    REWARD_WEBHOOK("RewardWebhookChangeEvent"),
    CAMPAIGN("CampaignEvent"),
    BUILT_CAMPAIGN("BuiltCampaignPojo"),
    CLIENT_VARIABLE("ClientVariable"),
    MEDIA_ASSET("MediaAsset"),
    PREHANDLER("Prehandler"),
    BUILT_PREHANDLER("BuiltPrehandler"),
    SNOOZE("Snooze"),
    DYNAMIC_AUDIENCE_LIST("DynamicAudienceList"),
    STATIC_AUDIENCE_LIST("StaticAudienceList"),
    UPLOADED_AUDIENCE_LIST("UploadedAudienceList"),
    BATCH_JOB("BatchJob"),
    FILE_ASSET("FileAsset"),
    REPORT_POST_HANDLER("ReportPostHandler"),
    REPORT_RUNNER("ReportRunnerChangeEvent"),
    REPORT_TYPE("ReportTypeChangeEvent");

    EventPojoType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private final String value;

}
