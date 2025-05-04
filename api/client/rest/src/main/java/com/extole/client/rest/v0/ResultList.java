package com.extole.client.rest.v0;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.extole.common.lang.ToString;

@JsonPropertyOrder({"result_count", "more_results", "results"})
public final class ResultList<T> {

    private int resultCount;
    private boolean moreResults;
    private List<T> results;

    public ResultList() {
    }

    public ResultList(boolean moreResults, List<T> results) {
        this.resultCount = results.size();
        this.moreResults = moreResults;
        this.results = results;
    }

    @JsonProperty("result_count")
    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    @JsonProperty("more_results")
    public boolean isMoreResults() {
        return moreResults;
    }

    public void setMoreResults(boolean moreResults) {
        this.moreResults = moreResults;
    }

    @JsonProperty("results")
    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
