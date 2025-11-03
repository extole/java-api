package com.extole.id;

/**
 * Implementations which do not support encoding/decoding for an input/output
 * type (e.g. String, byte[]) should throw an UnsupportedOperationException for
 * the corresponding methods.
 *
 * @author atalaat
 * @param <T> the ID type handled by this IdCoder instance
 */
public interface IdCoder<T> {

    String encodeAsString(T id);

    T decodeFromString(String s);

    byte[] encodeAsByteArray(T id);

    T decodeFromByteArray(byte[] b);
}
