package com.extole.consumer.rest.common;

public interface PollingResponse {

    String POLLING_ID = "polling_id";
    String STATUS = "status";

    String getPollingId();

    PollingStatus getStatus();
}
