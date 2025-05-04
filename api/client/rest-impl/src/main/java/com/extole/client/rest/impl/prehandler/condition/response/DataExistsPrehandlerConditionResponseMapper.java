package com.extole.client.rest.impl.prehandler.condition.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.condition.response.DataExistsPrehandlerConditionResponse;
import com.extole.model.entity.prehandler.PrehandlerConditionType;
import com.extole.model.entity.prehandler.condition.DataExistsPrehandlerCondition;

@Component
public class DataExistsPrehandlerConditionResponseMapper implements
    PrehandlerConditionResponseMapper<DataExistsPrehandlerCondition, DataExistsPrehandlerConditionResponse> {

    @Override
    public DataExistsPrehandlerConditionResponse toResponse(DataExistsPrehandlerCondition condition) {
        return new DataExistsPrehandlerConditionResponse(condition.getId().getValue(), condition.getDataKeys());
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.DATA_EXISTS;
    }
}
