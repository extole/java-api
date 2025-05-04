package com.extole.api.impl.campaign;

import java.util.List;

import com.extole.common.lang.ToString;

public class ElementImpl implements com.extole.api.campaign.Element {
    private final String id;
    private final List<String> tags;
    private final ElementType elementType;

    public ElementImpl(String id,
        List<String> tags,
        ElementType elementType) {
        this.id = id;
        this.tags = tags;
        this.elementType = elementType;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public String getType() {
        return elementType.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
