package com.extole.api.prehandler;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.RuntimeVariableContext;
import com.extole.api.event.ProcessedRawEvent;
import com.extole.api.event.RawEvent;
import com.extole.api.person.Person;

public interface PrehandlerContext extends GlobalContext, LoggerContext,
    RuntimeVariableContext {

    RawEvent getRawEvent();

    ProcessedRawEvent getProcessedRawEvent();

    @Nullable
    Person getCandidatePerson();

}
