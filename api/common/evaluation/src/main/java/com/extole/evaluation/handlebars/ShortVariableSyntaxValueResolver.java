
package com.extole.evaluation.handlebars;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

import com.github.jknack.handlebars.ValueResolver;

import com.extole.evaluateable.handlebars.ShortVariableSyntaxContext;

public enum ShortVariableSyntaxValueResolver implements ValueResolver {

    INSTANCE;

    @Override
    public Object resolve(final Object context, final String name) {
        Object value = null;
        if (context instanceof ShortVariableSyntaxContext) {
            value = ((ShortVariableSyntaxContext) context).getVariable(name);
        }
        return value == null ? UNRESOLVED : value;
    }

    @Override
    public Object resolve(final Object context) {
        if (context instanceof ShortVariableSyntaxContext) {
            return context;
        }
        return UNRESOLVED;
    }

    @Override
    public Set<Entry<String, Object>> propertySet(final Object context) {
        return Collections.emptySet();
    }
}
