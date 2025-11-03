package com.extole.block;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class BlockProviderTest {

    @Test
    public void testProvideAll() {
        assertThat(BlockProvider.provideAll().collect(Collectors.toUnmodifiableList()))
            .hasSizeGreaterThan(100);
    }

}
