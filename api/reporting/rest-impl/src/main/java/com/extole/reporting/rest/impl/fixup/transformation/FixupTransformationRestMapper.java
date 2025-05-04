package com.extole.reporting.rest.impl.fixup.transformation;

import com.extole.reporting.entity.fixup.transformation.FixupTransformation;
import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationResponse;

public interface FixupTransformationRestMapper<T extends FixupTransformation, R extends FixupTransformationResponse> {

    R toResponse(T transformation);

    FixupTransformationType getType();
}
