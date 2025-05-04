package com.extole.api.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import com.extole.api.Language;

public class LanguageImpl implements Language {

    @Nullable
    @Override
    public Object firstNotNull(Object... objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }

        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                return objects[i];
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object[] sort(Object... objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }

        Object[] newArray = Arrays.copyOf(objects, objects.length);
        Arrays.sort(newArray);
        return newArray;
    }

    @Nullable
    @Override
    public Object[] reverse(Object... objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }

        Object[] newArray = Arrays.copyOf(objects, objects.length);
        ArrayUtils.reverse(newArray);
        return newArray;
    }

    @Nullable
    @Override
    public List<?> toList(Object... objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }

        return List.of(objects);
    }

}
