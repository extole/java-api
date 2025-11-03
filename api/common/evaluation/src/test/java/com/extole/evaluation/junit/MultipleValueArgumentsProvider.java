package com.extole.evaluation.junit;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.util.Preconditions;

class MultipleValueArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<MultipleValueSource> {

    private Object[] arguments;

    @Override
    public void accept(MultipleValueSource source) {
        List<Object> inputs =
            Stream.<Object>of(
                source.shorts(),
                source.bytes(),
                source.ints(),
                source.longs(),
                source.floats(),
                source.doubles(),
                source.chars(),
                source.booleans(),
                source.strings(),
                source.classes())
                .filter(array -> Array.getLength(array) > 0)
                .collect(toList());

        Preconditions.condition(
            inputs.stream().map(array -> Integer.valueOf(Array.getLength(array))).distinct().count() == 1,
            () -> "All type inputs must be provided in the @"
                + MultipleValueSource.class.getSimpleName() + " annotation, must have same number of values");

        List<Object> allInvocationArguments = Lists.newArrayList();

        for (int i = 0; i < Array.getLength(inputs.get(0)); i++) {
            Object[] oneInvocationArguments = new Object[inputs.size()];
            for (int j = 0; j < inputs.size(); j++) {
                oneInvocationArguments[j] = Array.get(inputs.get(j), i);
            }
            allInvocationArguments.add(oneInvocationArguments);
        }

        arguments = allInvocationArguments.toArray();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Arrays.stream(arguments).map(Arguments::of);
    }

}
