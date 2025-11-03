package com.extole.common.jwt.decode;

import java.util.Map;
import java.util.Set;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;

public class PlainJwtHeader extends AbstractHeader {

    PlainJwtHeader(Map<String, Object> headers) throws UnsupportedAlgorithmException {
        super(headers, Set.of(Algorithm.NONE));
    }
}
