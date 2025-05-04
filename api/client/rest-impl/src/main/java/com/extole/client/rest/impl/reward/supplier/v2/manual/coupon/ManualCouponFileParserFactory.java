package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ManualCouponFileParserFactory {

    private final Map<ManualCouponFormat, ManualCouponFileParser> manualCouponFileParsersMap;

    @Autowired
    ManualCouponFileParserFactory(List<ManualCouponFileParser> manualCouponFileParsers) {
        manualCouponFileParsersMap = ImmutableMap.<ManualCouponFormat, ManualCouponFileParser>builder()
            .putAll(manualCouponFileParsers.stream()
                .collect(Collectors.toMap(ManualCouponFileParser::getManualCouponFormat,
                    manualCouponFileParser -> manualCouponFileParser)))
            .build();
    }

    public ManualCouponFileParser getParser(ManualCouponFormat fileFormat)
        throws ManualCouponInvalidFileExtensionException {
        try {
            return manualCouponFileParsersMap.get(fileFormat);
        } catch (IllegalArgumentException e) {
            throw new ManualCouponInvalidFileExtensionException(
                "Could not find a parser for the extension: " + fileFormat.getExtension());
        }
    }
}
