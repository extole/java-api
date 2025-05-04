package com.extole.reporting.rest.impl.fixup.transformation;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.entity.fixup.transformation.ScriptFixupTransformation;
import com.extole.reporting.rest.fixup.transformation.ScriptFixupTransformationResponse;

@Component
public class ScriptFixupTransformationRestMapper
    implements FixupTransformationRestMapper<ScriptFixupTransformation, ScriptFixupTransformationResponse> {

    @Override
    public ScriptFixupTransformationResponse toResponse(ScriptFixupTransformation transformation) {
        return new ScriptFixupTransformationResponse(transformation.getId().getValue(),
            com.extole.reporting.rest.fixup.transformation.FixupTransformationType
                .valueOf(transformation.getType().name()),
            transformation.getScript());
    }

    @Override
    public FixupTransformationType getType() {
        return FixupTransformationType.SCRIPT;
    }
}
