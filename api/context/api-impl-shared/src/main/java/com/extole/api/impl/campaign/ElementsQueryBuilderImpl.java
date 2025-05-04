package com.extole.api.impl.campaign;

import java.util.List;
import java.util.Optional;

import com.extole.api.campaign.Element;
import com.extole.api.campaign.ElementsQueryBuilder;

public class ElementsQueryBuilderImpl implements ElementsQueryBuilder {

    private final List<Element> elementsList;

    private Optional<String> elementType = Optional.empty();
    private Optional<String> filterTag = Optional.empty();

    public ElementsQueryBuilderImpl(List<Element> elementsList) {
        this.elementsList = elementsList;
    }

    @Override
    public ElementsQueryBuilder withType(String elementType) {
        this.elementType = Optional.ofNullable(elementType);
        return this;
    }

    @Override
    public ElementsQueryBuilder withTag(String tag) {
        this.filterTag = Optional.ofNullable(tag);
        return this;
    }

    @Override
    public Element[] list() {
        return elementsList.stream()
            .filter(
                element -> elementType.map(elementType -> element.getType().equalsIgnoreCase(elementType)).orElse(true))
            .filter(element -> filterTag.map(tag -> element.getTags().contains(tag)).orElse(true))
            .toArray(Element[]::new);
    }
}
