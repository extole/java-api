package com.extole.api.prehandler;

import com.extole.api.person.Person;

public interface PrehandlerActionContext extends PrehandlerContext {

    void replaceCandidatePerson(Person person);

    @Deprecated // TODO Use void log(String message) instead ENG-16894
    void addLogMessage(String logMessage);

    ProcessedRawEventBuilder getEventBuilder();
}
