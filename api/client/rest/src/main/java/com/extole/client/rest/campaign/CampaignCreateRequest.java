package com.extole.client.rest.campaign;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariantSelectionContext;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class CampaignCreateRequest {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PROGRAM_TYPE = "program_type";
    private static final String TAGS = "tags";
    private static final String VARIANT_SELECTOR = "variant_selector";
    private static final String VARIANTS = "variants";
    private static final String CAMPAIGN_TYPE = "campaign_type";

    private final String name;
    private final String description;
    private final String programType;
    private final Set<String> tags;

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<VariantSelectionContext, String>>> variantSelector;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>>> variants;
    private final Omissible<CampaignType> campaignType;

    public CampaignCreateRequest(
        @JsonProperty(NAME) String name,
        @Nullable @JsonProperty(DESCRIPTION) String description,
        @Nullable @JsonProperty(PROGRAM_TYPE) String programType,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(VARIANT_SELECTOR) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<VariantSelectionContext, String>>> variantSelector,
        @JsonProperty(VARIANTS) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>>> variants,
        @JsonProperty(CAMPAIGN_TYPE) Omissible<CampaignType> campaignType) {
        this.name = name;
        this.description = description;
        this.programType = programType;
        this.tags = tags == null ? Collections.emptySet() : Collections.unmodifiableSet(tags);
        this.variantSelector = variantSelector;
        this.variants = variants;
        this.campaignType = campaignType;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @JsonProperty(PROGRAM_TYPE)
    public Optional<String> getProgramType() {
        return Optional.ofNullable(programType);
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(VARIANT_SELECTOR)
    public
        Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<VariantSelectionContext, String>>>
        getVariantSelector() {
        return variantSelector;
    }

    @JsonProperty(VARIANTS)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>>> getVariants() {
        return variants;
    }

    @JsonProperty(CAMPAIGN_TYPE)
    public Omissible<CampaignType> getCampaignType() {
        return campaignType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {

        private final String name;
        private String description;
        private String programType;
        private Set<String> tags = Collections.emptySet();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<VariantSelectionContext, String>>> variantSelector =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>>> variants = Omissible.omitted();
        private Omissible<CampaignType> campaignType = Omissible.omitted();

        private Builder(String name) {
            this.name = name;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withProgramType(String programType) {
            this.programType = programType;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Collections.unmodifiableSet(tags);
            return this;
        }

        public Builder withVariantSelector(
            BuildtimeEvaluatable<CampaignBuildtimeContext,
                RuntimeEvaluatable<VariantSelectionContext, String>> variantSelector) {
            this.variantSelector = Omissible.of(variantSelector);
            return this;
        }

        public Builder withVariants(
            BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> variants) {
            this.variants = Omissible.of(variants);
            return this;
        }

        public Builder withCampaignType(CampaignType campaignType) {
            this.campaignType = Omissible.of(campaignType);
            return this;
        }

        public CampaignCreateRequest build() {
            return new CampaignCreateRequest(name, description, programType, tags, variantSelector,
                variants, campaignType);
        }

    }

}
