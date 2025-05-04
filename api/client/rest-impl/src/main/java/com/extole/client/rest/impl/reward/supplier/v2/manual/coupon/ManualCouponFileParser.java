package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;

import com.extole.client.rest.reward.supplier.v2.ManualCouponRequest;

public interface ManualCouponFileParser {
    List<ManualCouponRequest> parse(InputStream inputStream, ZoneId timeZone) throws ManualCouponFileParsingException;

    ManualCouponFormat getManualCouponFormat();
}
