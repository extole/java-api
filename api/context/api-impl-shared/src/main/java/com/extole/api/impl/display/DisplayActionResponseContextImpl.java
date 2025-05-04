package com.extole.api.impl.display;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.extole.api.ClientContext;
import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.Sandbox;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.event.step.StepConsumerEvent;
import com.extole.api.person.JourneyKey;
import com.extole.api.person.Person;
import com.extole.api.person.PersonJourney;
import com.extole.api.service.GlobalServices;
import com.extole.api.service.PersonBuilder;
import com.extole.api.step.Campaign;
import com.extole.api.step.action.display.ApiResponse;
import com.extole.api.step.action.display.ApiResponseBuilder;
import com.extole.api.step.action.display.ApiResponseImpl;
import com.extole.api.step.action.display.DisplayActionContext;
import com.extole.api.step.action.display.DisplayActionResponseContext;

public class DisplayActionResponseContextImpl implements DisplayActionResponseContext {

    private static final int HTTP_OK = 200;
    private final DisplayActionContext delegate;
    private final String body;
    private final Map<String, String> headers;

    public DisplayActionResponseContextImpl(DisplayActionContext delegate, String body, Map<String, String> headers) {
        this.delegate = delegate;
        this.body = body;
        this.headers = headers;
    }

    @Override
    public Campaign getCampaign() {
        return delegate.getCampaign();
    }

    @Override
    public boolean isMobile() {
        return delegate.isMobile();
    }

    @Override
    public boolean isScraper() {
        return delegate.isScraper();
    }

    @Override
    public ClientContext getClientContext() {
        return delegate.getClientContext();
    }

    @Override
    public GlobalServices getGlobalServices() {
        return delegate.getGlobalServices();
    }

    @Override
    public void log(String message) {
        delegate.log(message);
    }

    @Override
    public Person getPerson() {
        return delegate.getPerson();
    }

    @Override
    public PersonBuilder updatePerson() {
        return delegate.updatePerson();
    }

    @Override
    public ConsumerEvent getCauseEvent() {
        return delegate.getCauseEvent();
    }

    @Override
    public StepConsumerEvent getStepEvent() {
        return delegate.getStepEvent();
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public ApiResponseBuilder getResponseBuilder() {
        return new ApiResponseBuilder() {

            private int statusCode = HTTP_OK;
            private String body = StringUtils.EMPTY;
            private final Map<String, String> headers = new HashMap<>();

            @Override
            public ApiResponseBuilder withBody(String body) {
                this.body = body;
                return this;
            }

            @Override
            public ApiResponseBuilder withHeader(String name, String value) {
                headers.put(name, value);
                return this;
            }

            @Override
            public ApiResponseBuilder withHeaders(Map<String, String> headers) {
                this.headers.putAll(headers);
                return this;
            }

            @Override
            public ApiResponseBuilder withStatusCode(int statusCode) {
                this.statusCode = statusCode;
                return this;
            }

            @Override
            public ApiResponse build() {
                return new ApiResponseImpl(body, headers, statusCode);
            }
        };
    }

    @Override
    public InternalConsumerEventBuilder internalConsumerEventBuilder() {
        return delegate.internalConsumerEventBuilder();
    }

    @Nullable
    @Override
    public Object getVariable(String name) {
        return delegate.getVariable(name);
    }

    @Nullable
    @Override
    public Object getVariable(String name, String key) {
        return delegate.getVariable(name, key);
    }

    @Nullable
    @Override
    public Object getVariable(String name, String... keys) {
        return delegate.getVariable(name, keys);
    }

    @Override
    public Object get(String name) {
        return delegate.get(name);
    }

    @Override
    public Object get(String name, String key) {
        return delegate.get(name, key);
    }

    @Override
    public Object get(String name, String... keys) {
        return delegate.get(name, keys);
    }

    @Override
    public String getCampaignId() {
        return delegate.getCampaignId();
    }

    @Override
    public String getProgramLabel() {
        return delegate.getProgramLabel();
    }

    @Override
    public String getStepName() {
        return delegate.getStepName();
    }

    @Override
    public String getJourneyName() {
        return delegate.getJourneyName();
    }

    @Override
    public Sandbox getSandbox() {
        return delegate.getSandbox();
    }

    @Nullable
    @Override
    public PersonJourney getJourney() {
        return delegate.getJourney();
    }

    @Nullable
    @Override
    public Person getOtherPerson() {
        return delegate.getOtherPerson();
    }

    @Nullable
    @Override
    public PersonJourney getCandidateJourney() {
        return delegate.getCandidateJourney();
    }

    @Nullable
    @Override
    public JourneyKey getJourneyKey() {
        return delegate.getJourneyKey();
    }

}
