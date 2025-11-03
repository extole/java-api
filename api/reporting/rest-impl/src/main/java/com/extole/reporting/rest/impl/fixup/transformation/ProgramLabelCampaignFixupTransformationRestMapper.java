package com.extole.reporting.rest.impl.fixup.transformation;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.entity.fixup.transformation.ProgramLabelCampaignFixupTransformation;
import com.extole.reporting.rest.fixup.transformation.ProgramLabelCampaignFixupTransformationResponse;

@Component
public class ProgramLabelCampaignFixupTransformationRestMapper
    implements
    FixupTransformationRestMapper<ProgramLabelCampaignFixupTransformation,
        ProgramLabelCampaignFixupTransformationResponse> {

    @Override
    public ProgramLabelCampaignFixupTransformationResponse
        toResponse(ProgramLabelCampaignFixupTransformation transformation) {
        return new ProgramLabelCampaignFixupTransformationResponse(transformation.getId().getValue(),
            com.extole.reporting.rest.fixup.transformation.FixupTransformationType
                .valueOf(transformation.getType().name()),
            transformation.getProgramLabel(), transformation.getCampaignId());
    }

    @Override
    public FixupTransformationType getType() {
        return FixupTransformationType.PROGRAM_LABEL_CAMPAIGN;
    }
}
