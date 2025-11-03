package com.extole.common.variant.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.extole.common.variant.VariantService;

@Component
class VariantServiceImpl implements VariantService {
    private static final Logger LOG = LoggerFactory.getLogger(VariantServiceImpl.class);

    @Override
    public String selectVariant(String defaultVariant, List<String> enabledVariants) {
        return selectVariant(defaultVariant, enabledVariants, new ArrayList<>());
    }

    @Override
    public String selectVariant(String defaultVariant,
        List<String> enabledVariants,
        List<String> preferredVariants) {
        List<String> matchingVariants = Lists.newArrayList(preferredVariants);
        LOG.trace("Matching variants: {}", matchingVariants);
        matchingVariants.retainAll(enabledVariants);
        LOG.trace("Matching variants after retain: {}", matchingVariants);

        String selectedVariant = null;
        if (!matchingVariants.isEmpty()) {
            selectedVariant = Strings.emptyToNull(matchingVariants.get(0));
        }

        if (selectedVariant == null) {
            selectedVariant = defaultVariant;
            LOG.trace("No matching variants, using default variant: " + selectedVariant);
        }

        LOG.debug("Selected variant: " + selectedVariant + " based on preferred variants: " + preferredVariants +
            ", default variant: " + defaultVariant + " and enabled variants (first is default): " + enabledVariants);

        return selectedVariant;
    }
}
