package com.extole.api.impl.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.extole.api.service.PersonBuilder;
import com.extole.api.service.ShareableContentBuilder;
import com.extole.api.service.ShareableUpdateBuilder;
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
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableNotFoundException;

public class ShareableUpdateBuilderImpl implements ShareableUpdateBuilder {

    private final PersonBuilder personBuilder;
    private final String code;

    private Optional<ShareableContentBuilderImpl> shareableContentBuilder = Optional.empty();
    private final Map<String, String> dataToAdd = new HashMap<>();
    private final Set<String> dataToRemove = new HashSet<>();
    private Optional<String> label = Optional.empty();
    private boolean clearLabel = false;
    private Optional<String> key = Optional.empty();

    public ShareableUpdateBuilderImpl(
        PersonBuilder personBuilder,
        String code) {
        this.personBuilder = personBuilder;
        this.code = code;
    }

    @Override
    public ShareableContentBuilder getContentBuilder() {
        if (shareableContentBuilder.isEmpty()) {
            shareableContentBuilder = Optional.of(new ShareableContentBuilderImpl());
        }

        return shareableContentBuilder.get();
    }

    @Override
    public ShareableUpdateBuilder withKey(String key) {
        this.key = Optional.ofNullable(key);
        return this;
    }

    @Override
    public ShareableUpdateBuilder withLabel(String label) {
        this.label = Optional.ofNullable(label);
        return this;
    }

    @Override
    public ShareableUpdateBuilder withNoLabel() {
        this.label = Optional.empty();
        this.clearLabel = true;
        return this;
    }

    @Override
    public ShareableUpdateBuilder addData(String name, String value) {
        dataToAdd.put(name, value);
        return this;
    }

    @Override
    public ShareableUpdateBuilder removeData(String name) {
        dataToRemove.add(name);
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
        ShareableCodeReservedException, ShareableNotFoundException {
        com.extole.person.service.shareable.ShareableBuilder shareableBuilder =
            personBuilder.editShareable(code);

        if (shareableContentBuilder.isPresent()) {
            shareableContentBuilder.get().build(shareableBuilder.getContentBuilder());
        }

        for (String dataKey : dataToRemove) {
            shareableBuilder.removeData(dataKey);
        }

        for (String dataKey : dataToAdd.keySet()) {
            shareableBuilder.addData(dataKey, dataToAdd.get(dataKey));
        }

        if (clearLabel) {
            shareableBuilder.clearLabel();
        }

        if (label.isPresent()) {
            shareableBuilder.withLabel(label.get());
        }

        if (key.isPresent()) {
            shareableBuilder.withKey(key.get());
        }
    }

}
