package com.extole.id;

import com.google.common.primitives.Longs;
import org.apache.commons.codec.binary.Base64;

/**
 * Encodes/decodes 64-bit long values in a Base64 format modified to be safe for
 * inclusion in URLs. (See http://www.ietf.org/rfc/rfc4648.txt) The long's bytes
 * are in big-endian (network) order.
 *
 * @author atalaat
 */
public final class Base64Url implements IdCoder<Long> {

    @Override
    public String encodeAsString(Long id) {
        return Base64.encodeBase64URLSafeString(Longs.toByteArray(id.longValue()));
    }

    @Override
    public Long decodeFromString(String s) {
        return Long.valueOf(Longs.fromByteArray(Base64.decodeBase64(s)));
    }

    @Override
    public byte[] encodeAsByteArray(Long id) {
        return Base64.encodeBase64URLSafe(Longs.toByteArray(id.longValue()));
    }

    @Override
    public Long decodeFromByteArray(byte[] b) {
        return Long.valueOf(Longs.fromByteArray(Base64.decodeBase64(b)));
    }
}
