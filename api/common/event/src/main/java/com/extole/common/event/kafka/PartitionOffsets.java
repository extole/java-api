package com.extole.common.event.kafka;

import java.util.Optional;

import com.extole.common.lang.ToString;

public final class PartitionOffsets {

    private final Long endOffset;
    private final Long committedOffset;

    public PartitionOffsets(Long endOffset, Long committedOffset) {
        this.endOffset = endOffset;
        this.committedOffset = committedOffset;
    }

    public Optional<Long> getEndOffset() {
        return Optional.ofNullable(endOffset);
    }

    public Optional<Long> getCommittedOffset() {
        return Optional.ofNullable(committedOffset);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
