package com.extole.api.campaign;

public interface ElementsQueryBuilder {

    ElementsQueryBuilder withType(String elementType);

    ElementsQueryBuilder withTag(String tag);

    Element[] list();
}
