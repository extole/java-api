package com.extole.reporting.rest.impl.fixup.transformation;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.transformation.ContainerFixupTransformation;
import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.rest.fixup.transformation.ContainerFixupTransformationResponse;

@Component
public class ContainerFixupTransformationRestMapper
    implements FixupTransformationRestMapper<ContainerFixupTransformation, ContainerFixupTransformationResponse> {

    @Override
    public ContainerFixupTransformationResponse toResponse(ContainerFixupTransformation transformation) {
        return new ContainerFixupTransformationResponse(transformation.getId().getValue(),
            com.extole.reporting.rest.fixup.transformation.FixupTransformationType
                .valueOf(transformation.getType().name()),
            transformation.getContainer().getName());
    }

    @Override
    public FixupTransformationType getType() {
        return FixupTransformationType.CONTAINER;
    }
}
