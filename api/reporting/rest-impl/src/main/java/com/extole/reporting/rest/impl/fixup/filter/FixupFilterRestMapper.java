package com.extole.reporting.rest.impl.fixup.filter;

import com.extole.reporting.entity.fixup.filter.FixupFilter;
import com.extole.reporting.entity.fixup.filter.FixupFilterType;
import com.extole.reporting.rest.fixup.filter.FixupFilterResponse;

public interface FixupFilterRestMapper<T extends FixupFilter, R extends FixupFilterResponse> {

    R toResponse(T filter);

    FixupFilterType getType();
}
