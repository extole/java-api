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
import com.extole.common.lang.LazyLoadingSupplier;

public class GlobalServicesImpl implements GlobalServices {

    private static final LazyLoadingSupplier<EncoderService> ENCODER_SERVICE_SUPPLIER =
        new LazyLoadingSupplier<>(() -> new EncoderServiceImpl());
    private static final LazyLoadingSupplier<JsonService> JSON_SERVICE_SUPPLIER =
        new LazyLoadingSupplier<>(() -> new JsonServiceImpl());
    private static final LazyLoadingSupplier<StringService> STRING_SERVICE_SUPPLIER =
        new LazyLoadingSupplier<>(() -> new StringServiceImpl());

    private final LazyLoadingSupplier<RandomService> randomServiceSupplier;
    private final LazyLoadingSupplier<UnicodeService> unicodeServiceSupplier;
    private final LazyLoadingSupplier<IntegerService> integerServiceSupplier;
    private final LazyLoadingSupplier<DoubleService> doubleServiceSupplier;
    private final LazyLoadingSupplier<BigDecimalService> bigDecimalServiceSupplier;
    private final LazyLoadingSupplier<MonthDayService> monthDayServiceSupplier;
    private final LazyLoadingSupplier<PublicClientDomainService> publicClientDomainServiceSupplier;
    private final LazyLoadingSupplier<EmailVerificationService> emailVerificationServiceSupplier;
    private final LazyLoadingSupplier<PersonService> personServiceSupplier;
    private final LazyLoadingSupplier<NotificationService> notificationServiceSupplier;
    private final LazyLoadingSupplier<CouponService> couponServiceSupplier;
    private final LazyLoadingSupplier<JwtService> jwtServiceSupplier;
    private final LazyLoadingSupplier<DateService> dateServiceSupplier;
    private final LazyLoadingSupplier<ShareableService> shareableServiceSupplier;
    private final LazyLoadingSupplier<RewardSupplierService> rewardSupplierServiceSupplier;
    private final LazyLoadingSupplier<Language> languageSupplier;
    private final LazyLoadingSupplier<ShareService> shareSupplier;
    private final LazyLoadingSupplier<UserService> userServiceSupplier;
    private final LazyLoadingSupplier<BlockService> blockServiceSupplier;
    private final LazyLoadingSupplier<UrlService> urlServiceSupplier;

    public GlobalServicesImpl(LazyLoadingSupplier<RandomService> randomServiceSupplier,
        LazyLoadingSupplier<UnicodeService> unicodeServiceSupplier,
        LazyLoadingSupplier<IntegerService> integerServiceSupplier,
        LazyLoadingSupplier<DoubleService> doubleServiceSupplier,
        LazyLoadingSupplier<BigDecimalService> bigDecimalServiceSupplier,
        LazyLoadingSupplier<MonthDayService> monthDayServiceSupplier,
        LazyLoadingSupplier<PublicClientDomainService> publicClientDomainServiceSupplier,
        LazyLoadingSupplier<EmailVerificationService> emailVerificationServiceSupplier,
        LazyLoadingSupplier<PersonService> personServiceSupplier,
        LazyLoadingSupplier<NotificationService> notificationServiceSupplier,
        LazyLoadingSupplier<CouponService> couponServiceSupplier,
        LazyLoadingSupplier<JwtService> jwtServiceSupplier,
        LazyLoadingSupplier<DateService> dateServiceSupplier,
        LazyLoadingSupplier<ShareableService> shareableServiceSupplier,
        LazyLoadingSupplier<RewardSupplierService> rewardSupplierServiceSupplier,
        LazyLoadingSupplier<Language> languageSupplier,
        LazyLoadingSupplier<ShareService> shareSupplier,
        LazyLoadingSupplier<UserService> userServiceSupplier,
        LazyLoadingSupplier<BlockService> blockServiceSupplier,
        LazyLoadingSupplier<UrlService> urlServiceSupplier) {
        this.randomServiceSupplier = randomServiceSupplier;
        this.unicodeServiceSupplier = unicodeServiceSupplier;
        this.integerServiceSupplier = integerServiceSupplier;
        this.doubleServiceSupplier = doubleServiceSupplier;
        this.bigDecimalServiceSupplier = bigDecimalServiceSupplier;
        this.monthDayServiceSupplier = monthDayServiceSupplier;
        this.publicClientDomainServiceSupplier = publicClientDomainServiceSupplier;
        this.emailVerificationServiceSupplier = emailVerificationServiceSupplier;
        this.personServiceSupplier = personServiceSupplier;
        this.notificationServiceSupplier = notificationServiceSupplier;
        this.couponServiceSupplier = couponServiceSupplier;
        this.jwtServiceSupplier = jwtServiceSupplier;
        this.dateServiceSupplier = dateServiceSupplier;
        this.shareableServiceSupplier = shareableServiceSupplier;
        this.rewardSupplierServiceSupplier = rewardSupplierServiceSupplier;
        this.languageSupplier = languageSupplier;
        this.shareSupplier = shareSupplier;
        this.userServiceSupplier = userServiceSupplier;
        this.blockServiceSupplier = blockServiceSupplier;
        this.urlServiceSupplier = urlServiceSupplier;
    }

    @Override
    public RandomService getRandomService() {
        return randomServiceSupplier.get();
    }

    @Override
    public UnicodeService getUnicodeService() {
        return unicodeServiceSupplier.get();
    }

    @Override
    public IntegerService getIntegerService() {
        return integerServiceSupplier.get();
    }

    @Override
    public DoubleService getDoubleService() {
        return doubleServiceSupplier.get();
    }

    @Override
    public BigDecimalService getBigDecimalService() {
        return bigDecimalServiceSupplier.get();
    }

    @Override
    public MonthDayService getMonthDayService() {
        return monthDayServiceSupplier.get();
    }

    @Override
    public PublicClientDomainService getPublicClientDomainService() {
        return publicClientDomainServiceSupplier.get();
    }

    @Override
    public EncoderService getEncoderService() {
        return ENCODER_SERVICE_SUPPLIER.get();
    }

    @Override
    public EmailVerificationService getEmailVerificationService() {
        return emailVerificationServiceSupplier.get();
    }

    @Override
    public PersonService getPersonService() {
        return personServiceSupplier.get();
    }

    @Override
    public JsonService getJsonService() {
        return JSON_SERVICE_SUPPLIER.get();
    }

    @Override
    public NotificationService getNotificationService() {
        return notificationServiceSupplier.get();
    }

    @Override
    public StringService getStringService() {
        return STRING_SERVICE_SUPPLIER.get();
    }

    @Override
    public CouponService getCouponService() {
        return couponServiceSupplier.get();
    }

    @Override
    public JwtService getJwtService() {
        return jwtServiceSupplier.get();
    }

    @Override
    public DateService getDateService() {
        return dateServiceSupplier.get();
    }

    @Override
    public ShareableService getShareableService() {
        return shareableServiceSupplier.get();
    }

    @Override
    public RewardSupplierService getRewardSupplierService() {
        return rewardSupplierServiceSupplier.get();
    }

    @Override
    public Language getLanguage() {
        return languageSupplier.get();
    }

    @Override
    public ShareService getShareService() {
        return shareSupplier.get();
    }

    @Override
    public UserService getUserService() {
        return userServiceSupplier.get();
    }

    @Override
    public BlockService getBlockService() {
        return blockServiceSupplier.get();
    }

    @Override
    public UrlService getUrlService() {
        return urlServiceSupplier.get();
    }

}
