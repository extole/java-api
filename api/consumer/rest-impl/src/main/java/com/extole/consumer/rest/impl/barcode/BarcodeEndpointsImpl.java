package com.extole.consumer.rest.impl.barcode;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.barcode.Barcode;
import com.extole.barcode.BarcodeBuilder;
import com.extole.barcode.BarcodeInvalidHeightException;
import com.extole.barcode.BarcodeInvalidMarginWidthException;
import com.extole.barcode.BarcodeInvalidWidthException;
import com.extole.barcode.BarcodeMalformedContentException;
import com.extole.common.metrics.ExtoleCounter;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.barcode.BarcodeEndpoints;
import com.extole.consumer.rest.barcode.BarcodeResponse;
import com.extole.consumer.rest.barcode.BarcodeRestException;
import com.extole.consumer.rest.barcode.BarcodeType;
import com.extole.consumer.service.barcode.BarcodeService;

@Provider
public class BarcodeEndpointsImpl implements BarcodeEndpoints {

    private final BarcodeService barcodeService;
    private final ExtoleCounter counter;

    @Autowired
    public BarcodeEndpointsImpl(BarcodeService barcodeService, ExtoleMetricRegistry counter) {
        this.barcodeService = barcodeService;
        this.counter = counter.counter(BarcodeEndpoints.class + ".count.barcode");
    }

    @Override
    public BarcodeResponse generate(BarcodeType type, String content, Integer width, Integer height,
        Integer marginWidth) throws BarcodeRestException {
        if (type == null) {
            throw RestExceptionBuilder.newBuilder(BarcodeRestException.class)
                .withErrorCode(BarcodeRestException.MISSING_TYPE)
                .build();
        }

        try {
            counter.increment();
            BarcodeBuilder barcodeBuilder =
                barcodeService.newBarcode(com.extole.barcode.BarcodeType.valueOf(type.name()));
            if (width != null) {
                barcodeBuilder.withWidth(width);
            }

            if (height != null) {
                barcodeBuilder.withHeight(height);
            }

            if (marginWidth != null) {
                barcodeBuilder.withMarginWidth(marginWidth);
            }
            Barcode barcode = barcodeBuilder.generate(content);
            return new BarcodeResponse(barcode.getBytes());
        } catch (BarcodeInvalidWidthException e) {
            throw RestExceptionBuilder.newBuilder(BarcodeRestException.class)
                .withErrorCode(BarcodeRestException.INVALID_WIDTH)
                .addParameter("width", e.getWidth())
                .withCause(e)
                .build();
        } catch (BarcodeInvalidHeightException e) {
            throw RestExceptionBuilder.newBuilder(BarcodeRestException.class)
                .withErrorCode(BarcodeRestException.INVALID_HEIGHT)
                .addParameter("height", e.getHeight())
                .withCause(e)
                .build();
        } catch (BarcodeInvalidMarginWidthException e) {
            throw RestExceptionBuilder.newBuilder(BarcodeRestException.class)
                .withErrorCode(BarcodeRestException.INVALID_MARGIN_WIDTH)
                .addParameter("margin_width", e.getMarginWidth())
                .withCause(e)
                .build();
        } catch (BarcodeMalformedContentException e) {
            throw RestExceptionBuilder.newBuilder(BarcodeRestException.class)
                .withErrorCode(BarcodeRestException.INVALID_CONTENT)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BarcodeResponse generate(BarcodeType type, String content, Integer size) throws BarcodeRestException {
        return generate(type, content, null, size, null);
    }
}
