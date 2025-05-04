package com.extole.client.rest.campaign.component.setting;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

public final class VariableTagsFilter {
    private VariableTagsFilter() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<Set<String>> havingAnyTags = Optional.empty();
        private Optional<Set<String>> havingAllTags = Optional.empty();
        private Optional<Set<String>> excludeHavingAnyTags = Optional.empty();
        private Optional<Set<String>> excludeHavingAllTags = Optional.empty();

        private Builder() {
        }

        public Builder withHavingAnyTags(Set<String> havingAnyTags) {
            this.havingAnyTags = Optional.of(havingAnyTags);
            return this;
        }

        public Builder withHavingAllTags(Set<String> havingAllTags) {
            this.havingAllTags = Optional.of(havingAllTags);
            return this;
        }

        public Builder withExcludeHavingAnyTags(Set<String> excludeHavingAnyTags) {
            this.excludeHavingAnyTags = Optional.of(excludeHavingAnyTags);
            return this;
        }

        public Builder withExcludeHavingAllTags(Set<String> excludeHavingAllTags) {
            this.excludeHavingAllTags = Optional.of(excludeHavingAllTags);
            return this;
        }

        public Predicate<Set<String>> buildFilter() {
            Predicate<Set<String>> result = tags -> true;

            if (havingAnyTags.isPresent()) {
                Predicate<Set<String>> havingAny = tags -> !Sets.intersection(havingAnyTags.get(), tags).isEmpty();
                result = result.and(havingAny);
            }
            if (havingAllTags.isPresent()) {
                Predicate<Set<String>> havingAll =
                    tags -> Sets.intersection(havingAllTags.get(), tags).size() == havingAllTags.get().size();
                result = result.and(havingAll);
            }
            if (excludeHavingAnyTags.isPresent()) {
                Predicate<Set<String>> excludeHavingAny =
                    tags -> Sets.intersection(excludeHavingAnyTags.get(), tags).isEmpty();
                result = result.and(excludeHavingAny);
            }
            if (excludeHavingAllTags.isPresent()) {
                Predicate<Set<String>> excludeHavingAny =
                    tags -> Sets.intersection(excludeHavingAllTags.get(), tags).size() != excludeHavingAllTags.get()
                        .size();
                result = result.and(excludeHavingAny);
            }

            return result;
        }
    }
}
