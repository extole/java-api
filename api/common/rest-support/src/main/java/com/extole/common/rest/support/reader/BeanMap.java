package com.extole.common.rest.support.reader;

import java.util.HashMap;
import java.util.Map;

public class BeanMap {

    static class ParsePropertyError extends RuntimeException {
        ParsePropertyError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final Map<String, String> properties = new HashMap<>();
    private final Map<String, BeanMap> subBeans = new HashMap<>();

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> result = new HashMap<>();
        result.putAll(properties);
        for (String beanName : subBeans.keySet()) {
            result.put(beanName, subBeans.get(beanName).asMap());
        }
        return result;
    }

    private int findClosingQuote(String name, int startIndex) {
        int closeBeanNameIndex = name.indexOf("\".", startIndex);
        if (closeBeanNameIndex > -1) {
            return closeBeanNameIndex;
        }
        if (name.endsWith("\"") && !name.equals("\"")) {
            return name.length() - 1;
        }
        return -1;
    }

    public void setProperty(String name, String value) {
        try {
            if (name.startsWith("\"")) {
                int closeQuoteIndex = findClosingQuote(name, 1);
                if (closeQuoteIndex > -1) {
                    String unquotedProperty = name.substring(1, closeQuoteIndex);
                    if (closeQuoteIndex == (name.length() - 1)) {
                        properties.put(unquotedProperty, value);
                        return;
                    } else {
                        String nextProperty = name.substring(closeQuoteIndex + 2);
                        getSubBean(unquotedProperty).setProperty(nextProperty, value);
                        return;
                    }
                }
            }
            int dotIndex = name.indexOf(".");
            if (dotIndex > -1) {
                String firstProperty = name.substring(0, dotIndex);
                getSubBean(firstProperty).setProperty(name.substring(dotIndex + 1), value);
            } else {
                properties.put(name, value);
            }
        } catch (Throwable e) {
            // Catching all exceptions here as we don't want to propagate failures in our response processing.
            throw new ParsePropertyError("Error setting property:" + name + " with value: " + value, e);
        }
    }

    public BeanMap getSubBean(String name) {
        BeanMap subBean = subBeans.get(name);
        if (subBean == null) {
            subBean = new BeanMap();
            subBeans.put(name, subBean);
        }
        return subBean;
    }
}
