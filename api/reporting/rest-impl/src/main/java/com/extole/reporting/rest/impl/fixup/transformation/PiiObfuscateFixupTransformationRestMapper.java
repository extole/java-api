package com.extole.reporting.rest.impl.fixup.transformation;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.entity.fixup.transformation.PiiObfuscateFixupTransformation;
import com.extole.reporting.rest.fixup.transformation.PiiObfuscateFixupTransformationResponse;

@Component
public class PiiObfuscateFixupTransformationRestMapper
    implements FixupTransformationRestMapper<PiiObfuscateFixupTransformation, PiiObfuscateFixupTransformationResponse> {

    @Override
    public PiiObfuscateFixupTransformationResponse toResponse(PiiObfuscateFixupTransformation transformation) {
        return new PiiObfuscateFixupTransformationResponse(transformation.getId().getValue(),
            com.extole.reporting.rest.fixup.transformation.FixupTransformationType
                .valueOf(transformation.getType().name()),
            transformation.getRequestId());
    }

    @Override
    public FixupTransformationType getType() {
        return FixupTransformationType.PII_OBFUSCATE;
    }
}
