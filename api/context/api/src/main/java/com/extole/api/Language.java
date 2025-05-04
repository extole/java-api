package com.extole.api;

import java.util.List;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Language {

    @Nullable
    Object firstNotNull(Object... objects);

    /**
     * Sorts the specified array
     *
     * @param objects the array to be sorted
     * @return a copy of the original array, sorted in ascending order.
     */
    @Nullable
    Object[] sort(Object... objects);

    /**
     * Reverses the specified array
     *
     * @param objects the array to be reversed
     * @return a copy of the original array, reversed
     */
    @Nullable
    Object[] reverse(Object... objects);

    /**
     * Convert specified array to list
     *
     * @param objects the array to be converted
     * @return a list that contains original arrays elements
     */
    @Nullable
    List<?> toList(Object... objects);

}
