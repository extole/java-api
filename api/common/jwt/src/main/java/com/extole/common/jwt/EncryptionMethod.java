package com.extole.common.jwt;

import java.util.Set;

import com.extole.common.lang.ToString;

public final class EncryptionMethod {

    public static final EncryptionMethod A128CBC_HS256 = new EncryptionMethod("A128CBC_HS256", "A128CBC-HS256", 256);
    public static final EncryptionMethod A192CBC_HS384 = new EncryptionMethod("A192CBC_HS384", "A192CBC-HS384", 384);
    public static final EncryptionMethod A256CBC_HS512 = new EncryptionMethod("A256CBC_HS512", "A256CBC-HS512", 512);
    public static final EncryptionMethod A128GCM = new EncryptionMethod("A128GCM", 128);
    public static final EncryptionMethod A192GCM = new EncryptionMethod("A192GCM", 192);
    public static final EncryptionMethod A256GCM = new EncryptionMethod("A256GCM", 256);
    public static final EncryptionMethod XC20P = new EncryptionMethod("XC20P", 256);

    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS =
        Set.of(A128CBC_HS256, A192CBC_HS384, A256CBC_HS512, A128GCM, A192GCM, A256GCM, XC20P);

    private final String name;
    private final String specName;
    private final int cekBitLength;

    private EncryptionMethod(String specName, int cekBitLength) {
        this.name = specName;
        this.specName = specName;
        this.cekBitLength = cekBitLength;
    }

    private EncryptionMethod(String name, String specName, int cekBitLength) {
        this.name = name;
        this.specName = specName;
        this.cekBitLength = cekBitLength;
    }

    public String getName() {
        return name;
    }

    public String getSpecName() {
        return specName;
    }

    public int getCekBitLength() {
        return cekBitLength;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
