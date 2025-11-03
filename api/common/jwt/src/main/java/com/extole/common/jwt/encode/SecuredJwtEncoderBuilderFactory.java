package com.extole.common.jwt.encode;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;

public interface SecuredJwtEncoderBuilderFactory {

    SecuredJwtEncoder.SecuredJwtEncoderBuilder createBuilder(Algorithm algorithm) throws UnsupportedAlgorithmException;

}
