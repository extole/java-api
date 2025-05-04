package com.extole.reporting.rest.impl.fixup.transformation;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.transformation.ConditionalAliasChangeFixupTransformation;
import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.rest.fixup.transformation.ConditionalAliasChangeFixupTransformationResponse;

@Component
public class ConditionalAliasChangeFixupTransformationRestMapper
    implements FixupTransformationRestMapper<ConditionalAliasChangeFixupTransformation,
        ConditionalAliasChangeFixupTransformationResponse> {

    @Override
    public ConditionalAliasChangeFixupTransformationResponse
        toResponse(ConditionalAliasChangeFixupTransformation transformation) {
        return new ConditionalAliasChangeFixupTransformationResponse(
            transformation.getId().getValue(),
            com.extole.reporting.rest.fixup.transformation.FixupTransformationType
                .valueOf(transformation.getType().name()),
            transformation.getClientIdFilterColumn(),
            transformation.getProgramLabelFilterColumn(),
            transformation.getStepNameFilterColumn(),
            transformation.getAliasesToAddColumn(),
            transformation.getAliasesToRemoveColumn(),
            transformation.getFileAssetId().getValue());
    }

    @Override
    public FixupTransformationType getType() {
        return FixupTransformationType.CONDITIONAL_ALIAS_CHANGE;
    }
}
