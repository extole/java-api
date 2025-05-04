package com.extole.consumer.rest.barcode;

import java.util.Arrays;

public final class BarcodeResponse {

    private final byte[] bytes;

    public BarcodeResponse(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
