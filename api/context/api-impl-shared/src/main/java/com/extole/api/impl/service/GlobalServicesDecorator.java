package com.extole.api.impl.service;

import com.extole.api.Language;
import com.extole.api.service.BigDecimalService;
import com.extole.api.service.BlockService;
import com.extole.api.service.CouponService;
import com.extole.api.service.DateService;
import com.extole.api.service.DoubleService;
import com.extole.api.service.EmailVerificationService;
import com.extole.api.service.EncoderService;
import com.extole.api.service.GlobalServices;
import com.extole.api.service.IntegerService;
import com.extole.api.service.JsonService;
import com.extole.api.service.JwtService;
import com.extole.api.service.MonthDayService;
import com.extole.api.service.NotificationService;
import com.extole.api.service.PersonService;
import com.extole.api.service.PublicClientDomainService;
import com.extole.api.service.RandomService;
import com.extole.api.service.RewardSupplierService;
import com.extole.api.service.ShareService;
import com.extole.api.service.ShareableService;
import com.extole.api.service.StringService;
import com.extole.api.service.UnicodeService;
import com.extole.api.service.UrlService;
import com.extole.api.service.UserService;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.event.client.ClientEventService;
import com.extole.id.Id;

public class GlobalServicesDecorator implements GlobalServices {

    private final LazyLoadingSupplier<GlobalServices> globalServicesSupplier;
    private final LazyLoadingSupplier<NotificationService> notificationServiceSupplier;

    public GlobalServicesDecorator(LazyLoadingSupplier<GlobalServices> globalServicesSupplier,
        Id<ClientHandle> clientId, ClientEventService clientEventService, String contextObjectName,
        Id<?> contextObjectId) {
        this.globalServicesSupplier = globalServicesSupplier;
        this.notificationServiceSupplier = new LazyLoadingSupplier<>(
            () -> new NotificationServiceImpl(clientId, clientEventService, contextObjectName, contextObjectId));
    }

    @Override
    public RandomService getRandomService() {
        return globalServicesSupplier.get().getRandomService();
    }

    @Override
    public UnicodeService getUnicodeService() {
        return globalServicesSupplier.get().getUnicodeService();
    }

    @Override
    public IntegerService getIntegerService() {
        return globalServicesSupplier.get().getIntegerService();
    }

    @Override
    public DoubleService getDoubleService() {
        return globalServicesSupplier.get().getDoubleService();
    }

    @Override
    public BigDecimalService getBigDecimalService() {
        return globalServicesSupplier.get().getBigDecimalService();
    }

    @Override
    public MonthDayService getMonthDayService() {
        return globalServicesSupplier.get().getMonthDayService();
    }

    @Override
    public PublicClientDomainService getPublicClientDomainService() {
        return globalServicesSupplier.get().getPublicClientDomainService();
    }

    @Override
    public EncoderService getEncoderService() {
        return globalServicesSupplier.get().getEncoderService();
    }

    @Override
    public EmailVerificationService getEmailVerificationService() {
        return globalServicesSupplier.get().getEmailVerificationService();
    }

    @Override
    public PersonService getPersonService() {
        return globalServicesSupplier.get().getPersonService();
    }

    @Override
    public JsonService getJsonService() {
        return globalServicesSupplier.get().getJsonService();
    }

    @Override
    public NotificationService getNotificationService() {
        return notificationServiceSupplier.get();
    }

    @Override
    public StringService getStringService() {
        return globalServicesSupplier.get().getStringService();
    }

    @Override
    public CouponService getCouponService() {
        return globalServicesSupplier.get().getCouponService();
    }

    @Override
    public JwtService getJwtService() {
        return globalServicesSupplier.get().getJwtService();
    }

    @Override
    public DateService getDateService() {
        return globalServicesSupplier.get().getDateService();
    }

    @Override
    public ShareableService getShareableService() {
        return globalServicesSupplier.get().getShareableService();
    }

    @Override
    public RewardSupplierService getRewardSupplierService() {
        return globalServicesSupplier.get().getRewardSupplierService();
    }

    @Override
    public Language getLanguage() {
        return globalServicesSupplier.get().getLanguage();
    }

    @Override
    public ShareService getShareService() {
        return globalServicesSupplier.get().getShareService();
    }

    @Override
    public UserService getUserService() {
        return globalServicesSupplier.get().getUserService();
    }

    @Override
    public BlockService getBlockService() {
        return globalServicesSupplier.get().getBlockService();
    }

    @Override
    public UrlService getUrlService() {
        return globalServicesSupplier.get().getUrlService();
    }

}
