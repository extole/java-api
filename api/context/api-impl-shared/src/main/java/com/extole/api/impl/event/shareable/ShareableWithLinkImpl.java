package com.extole.api.impl.event.shareable;

import java.util.Map;

import com.extole.api.event.shareable.ShareableWithLink;
import com.extole.api.impl.person.ShareableImpl;
import com.extole.api.person.Shareable;
import com.extole.api.person.ShareableContent;
import com.extole.common.lang.ToString;
import com.extole.event.consumer.shareable.ShareableConsumerEventShareable;

public class ShareableWithLinkImpl implements ShareableWithLink {

    private final Shareable shareable;
    private final String link;

    public ShareableWithLinkImpl(ShareableConsumerEventShareable shareable) {
        this.shareable = new ShareableImpl(shareable);
        this.link = shareable.getLink().toString();
    }

    @Override
    public String getId() {
        return shareable.getId();
    }

    @Override
    public String getCode() {
        return shareable.getCode();
    }

    @Override
    public String getKey() {
        return shareable.getKey();
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public ShareableContent getContent() {
        return shareable.getContent();
    }

    @Override
    public Map<String, String> getData() {
        return shareable.getData();
    }

    @Override
    public String getLabel() {
        return shareable.getLabel();
    }

    @Override
    public String getClientDomainId() {
        return shareable.getClientDomainId();
    }

    @Override
    public String getPersonId() {
        return shareable.getPersonId();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
