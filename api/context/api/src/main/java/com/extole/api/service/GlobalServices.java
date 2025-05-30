package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.Language;

@Schema
public interface GlobalServices {

    RandomService getRandomService();

    UnicodeService getUnicodeService();

    IntegerService getIntegerService();

    DoubleService getDoubleService();

    BigDecimalService getBigDecimalService();

    MonthDayService getMonthDayService();

    PublicClientDomainService getPublicClientDomainService();

    EncoderService getEncoderService();

    EmailVerificationService getEmailVerificationService();

    PersonService getPersonService();

    JsonService getJsonService();

    NotificationService getNotificationService();

    StringService getStringService();

    CouponService getCouponService();

    JwtService getJwtService();

    DateService getDateService();

    ShareableService getShareableService();

    ShareService getShareService();

    RewardSupplierService getRewardSupplierService();

    Language getLanguage();

    UserService getUserService();

    BlockService getBlockService();

    UrlService getUrlService();

}
