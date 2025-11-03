package com.extole.client.rest.event.stream;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

public class EventStreamEventFilterQueryParams {

    private static final String FILTERS = "json_path_filters";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";

    private final Integer limit;
    private final Integer offset;
    private final Optional<ZonedDateTime> startDate;
    private final Optional<ZonedDateTime> endDate;
    private final String jsonPathFilters;

    public EventStreamEventFilterQueryParams(
        @Parameter(
            description = "Optional limit filter") @Nullable @QueryParam(LIMIT) @DefaultValue("50") Integer limit,
        @Parameter(
            description = "Optional offset filter") @Nullable @QueryParam(OFFSET) @DefaultValue("0") Integer offset,
        @Nullable @QueryParam(START_DATE) ZonedDateTime startDate,
        @Nullable @QueryParam(END_DATE) ZonedDateTime endDate,
        @Parameter(
            description = "Optional json path filters, example: type = INPUT | data.key = value") @Nullable @QueryParam(FILTERS) @DefaultValue("") String jsonPathFilters) {
        this.limit = limit;
        this.offset = offset;
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.jsonPathFilters = jsonPathFilters;
    }

    @QueryParam(FILTERS)
    public String getJsonPathFilters() {
        return jsonPathFilters;
    }

    @QueryParam(LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @QueryParam(START_DATE)
    public Optional<ZonedDateTime> getStartDate() {
        return startDate;
    }

    @QueryParam(END_DATE)
    public Optional<ZonedDateTime> getEndDate() {
        return endDate;
    }
}
