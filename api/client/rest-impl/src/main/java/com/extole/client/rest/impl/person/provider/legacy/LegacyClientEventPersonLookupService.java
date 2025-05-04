package com.extole.client.rest.impl.person.provider.legacy;

import java.util.Set;

import javax.annotation.Nullable;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.key.PersonKey;

public interface LegacyClientEventPersonLookupService {

    LegacyClientEventPersonLookupBuilder newLookup(ClientAuthorization authorization);

    interface LegacyClientEventPersonLookupBuilder {

        LegacyClientEventPersonLookupBuilder withPersonId(@Nullable String personId);

        LegacyClientEventPersonLookupBuilder withEmail(@Nullable String email);

        LegacyClientEventPersonLookupBuilder withPartnerUserId(@Nullable String partnerUserId);

        LegacyClientEventPersonLookupBuilder withPartnerConversionId(@Nullable String partnerConversionId);

        LegacyClientEventPersonLookupBuilder withPersonKeys(Set<PersonKey> personKeys);

        LegacyClientEventPersonLookupBuilder withClientDomain(@Nullable PublicProgram clientDomain);

        LegacyClientEventPersonLookupResult lookup() throws AuthorizationException, PersonNotFoundException;

        LegacyClientEventPersonLookupOrCreateResult lookupOrCreate() throws AuthorizationException,
            PersonNotFoundException;

    }

}
