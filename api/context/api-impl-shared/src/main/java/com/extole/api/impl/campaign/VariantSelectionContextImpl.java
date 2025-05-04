package com.extole.api.impl.campaign;

import java.util.List;

import javax.annotation.Nullable;

import com.extole.api.campaign.VariantSelectionContext;
import com.extole.api.impl.person.PersonImpl;
import com.extole.api.impl.service.LocaleParseServiceImpl;
import com.extole.api.impl.service.VariantServiceImpl;
import com.extole.api.person.Person;
import com.extole.api.service.LocaleParseService;
import com.extole.api.service.VariantService;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.sandbox.SandboxService;

public class VariantSelectionContextImpl implements VariantSelectionContext {

    private final LazyLoadingSupplier<Person> personSupplier;
    private final LazyLoadingSupplier<VariantService> variantServiceSupplier;
    private final LazyLoadingSupplier<LocaleParseServiceImpl> localeParseServiceSupplier;
    private final String[] campaignVariants;

    public VariantSelectionContextImpl(
        SandboxService sandboxService,
        com.extole.common.variant.VariantService variantService,
        com.extole.common.variant.LocaleParseService localeParseService,
        ExecutionLogger executionLogger,
        com.extole.person.service.profile.Person person,
        List<String> campaignVariants) {
        this.variantServiceSupplier = new LazyLoadingSupplier<>(() -> new VariantServiceImpl(variantService,
            executionLogger));
        this.localeParseServiceSupplier =
            new LazyLoadingSupplier<>(() -> new LocaleParseServiceImpl(localeParseService, executionLogger));
        this.personSupplier = new LazyLoadingSupplier<>(() -> new PersonImpl(person, sandboxService));
        this.campaignVariants = campaignVariants.toArray(new String[] {});
    }

    @Override
    public String selectVariant(@Nullable String[] preferredVariants) {
        return variantServiceSupplier.get().selectVariant(campaignVariants, preferredVariants);
    }

    @Override
    public LocaleParseService getLocaleParseService() {
        return localeParseServiceSupplier.get();
    }

    @Override
    public Person getPerson() {
        return personSupplier.get();
    }

}
