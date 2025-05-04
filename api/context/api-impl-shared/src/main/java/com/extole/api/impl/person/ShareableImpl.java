package com.extole.api.impl.person;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import com.extole.api.person.Shareable;
import com.extole.api.person.ShareableContent;
import com.extole.common.lang.ToString;
import com.extole.event.consumer.shareable.ShareableConsumerEventShareable;
import com.extole.id.Id;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.PersonHandle;

public class ShareableImpl implements Shareable {
    private final String id;
    private final String key;
    private final String code;
    private final ShareableContent content;
    private final Map<String, String> data;
    private final String label;
    private final String clientDomainId;
    private final String personId;

    public ShareableImpl(com.extole.person.service.shareable.Shareable shareable) {
        this(shareable.getShareableId(), shareable.getKey(), shareable.getCode(),
            shareable.getContent(), shareable.getData(), shareable.getLabel(),
            shareable.getProgramId(), shareable.getPersonId());
    }

    public ShareableImpl(ShareableConsumerEventShareable shareable) {
        this(shareable.getShareableId(), shareable.getKey(), shareable.getCode(), shareable.getContent(),
            shareable.getData(), shareable.getLabel(), shareable.getProgramId(), shareable.getPersonId());
    }

    private ShareableImpl(Id<com.extole.person.service.shareable.Shareable> id, String key, String code,
        Optional<? extends com.extole.person.service.shareable.ShareableContent> content, Map<String, String> data,
        Optional<String> label, Id<ProgramHandle> clientDomainId, Id<PersonHandle> personId) {
        this.id = id.getValue();
        this.key = key;
        this.code = code;
        this.content = new ShareableContentImpl(content.orElse(null));
        this.data = ImmutableMap.copyOf(data);
        this.label = label.orElse(null);
        this.clientDomainId = clientDomainId.getValue();
        this.personId = personId.getValue();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ShareableContent getContent() {
        return content;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getClientDomainId() {
        return clientDomainId;
    }

    @Override
    public String getPersonId() {
        return personId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
