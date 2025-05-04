package com.extole.api.impl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.extole.api.impl.ContextApiRuntimeException;
import com.extole.api.service.PersonBuilder;
import com.extole.api.service.ShareableContentBuilder;
import com.extole.api.service.ShareableCreateBuilder;
import com.extole.id.Id;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
import com.extole.person.service.shareable.ShareableCodeReservedException;
import com.extole.person.service.shareable.ShareableCodeTakenByPromotionException;
import com.extole.person.service.shareable.ShareableCodeTakenException;
import com.extole.person.service.shareable.ShareableContentDescriptionTooLongException;
import com.extole.person.service.shareable.ShareableDataAttributeNameInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeNameLengthException;
import com.extole.person.service.shareable.ShareableDataAttributeValueInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeValueLengthException;
import com.extole.person.service.shareable.ShareableFieldLengthException;
import com.extole.person.service.shareable.ShareableFieldValueException;
import com.extole.person.service.shareable.ShareableKeyTakenException;
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableMissingProgramIdException;
import com.extole.person.service.shareable.ShareableProgramNotFoundException;

public class ShareableCreateBuilderImpl implements ShareableCreateBuilder {

    private final PersonBuilder personBuilder;
    private final Id<ProgramHandle> clientDomainId;

    private Optional<ShareableContentBuilderImpl> shareableContentBuilder = Optional.empty();
    private final Map<String, String> data = new HashMap<>();
    private Optional<String> label = Optional.empty();
    private Optional<String> key = Optional.empty();
    private Optional<String> code = Optional.empty();
    private Optional<String> programId = Optional.empty();
    private List<String> preferredCodePrefixes = ImmutableList.of();
    private boolean ignoringNaughtyWords = false;

    public ShareableCreateBuilderImpl(
        PersonBuilder personBuilder,
        Id<ProgramHandle> clientDomainId) {
        this.personBuilder = personBuilder;
        this.clientDomainId = clientDomainId;
    }

    @Override
    public ShareableContentBuilder getContentBuilder() {
        if (!shareableContentBuilder.isPresent()) {
            shareableContentBuilder = Optional.of(new ShareableContentBuilderImpl());
        }

        return shareableContentBuilder.get();
    }

    @Override
    public ShareableCreateBuilder withKey(String key) {
        this.key = Optional.ofNullable(key);
        return this;
    }

    @Override
    public ShareableCreateBuilder withCode(String code) {
        this.code = Optional.ofNullable(code);
        return this;
    }

    @Override
    public ShareableCreateBuilder withPreferredCodePrefixes(List<String> preferredCodePrefixes) {
        this.preferredCodePrefixes = CollectionUtils.isEmpty(preferredCodePrefixes) ? List.of()
            : preferredCodePrefixes.stream()
                .filter(codePrefix -> StringUtils.isNotBlank(codePrefix))
                .collect(Collectors.toUnmodifiableList());
        return this;
    }

    @Override
    public ShareableCreateBuilder withLabel(String label) {
        this.label = Optional.ofNullable(label);
        return this;
    }

    @Override
    public ShareableCreateBuilder withNoLabel() {
        this.label = Optional.empty();
        return this;
    }

    @Override
    public ShareableCreateBuilder addData(String name, String value) {
        data.put(name, value);
        return this;
    }

    @Override
    public ShareableCreateBuilder withData(Map<String, String> data) {
        this.data.clear();
        this.data.putAll(data);
        return this;
    }

    @Override
    public ShareableCreateBuilder withClientDomainId(String clientDomainId) {
        this.programId = Optional.ofNullable(clientDomainId);
        return this;
    }

    @Override
    public ShareableCreateBuilder withIgnoringNaughtyWords() {
        this.ignoringNaughtyWords = true;
        return this;
    }

    @Override
    public PersonBuilder done() {
        return personBuilder;
    }

    public void build(com.extole.person.service.profile.PersonBuilder personBuilder)
        throws ShareableDataAttributeNameInvalidException, ShareableDataAttributeValueInvalidException,
        ShareableDataAttributeNameLengthException, ShareableDataAttributeValueLengthException,
        ShareableLabelIllegalCharacterInNameException, ShareableLabelNameLengthException,
        ShareableContentDescriptionTooLongException, ShareableBlockedUrlException, ShareableFieldLengthException,
        ShareableFieldValueException, ShareableCodeTakenException, ShareableCodeTakenByPromotionException,
        ShareableCodeReservedException, ShareableProgramNotFoundException {
        com.extole.person.service.shareable.ShareableBuilder shareableBuilder =
            personBuilder.createShareable().withProgramId(clientDomainId);

        if (ignoringNaughtyWords) {
            shareableBuilder.withIgnoringNaughtyWords();
        }

        if (shareableContentBuilder.isPresent()) {
            shareableContentBuilder.get().build(shareableBuilder.getContentBuilder());
        }

        for (String dataKey : data.keySet()) {
            shareableBuilder.addData(dataKey, data.get(dataKey));
        }

        if (label.isPresent()) {
            shareableBuilder.withLabel(label.get());
        }

        if (key.isPresent()) {
            shareableBuilder.withKey(key.get());
        }

        if (code.isPresent()) {
            shareableBuilder.withCode(code.get());
        }

        if (programId.isPresent()) {
            shareableBuilder.withProgramId(Id.valueOf(programId.get()));
        }

        if (!preferredCodePrefixes.isEmpty()) {
            shareableBuilder.withPreferredCodePrefixes(preferredCodePrefixes);
        }

        try {
            shareableBuilder.validate();
        } catch (ShareableMissingProgramIdException | ShareableKeyTakenException e) {
            throw new ContextApiRuntimeException("Could not validate new shareable", e);
        }
    }

}
