package com.extole.common.rest.timezone;

import java.util.Map;
import java.util.WeakHashMap;

import org.glassfish.jersey.model.internal.spi.ParameterServiceProvider;
import org.glassfish.jersey.server.model.Parameter;

import com.extole.common.rest.time.TimeZoneParam;

public final class TimeZoneParameterService implements ParameterServiceProvider {

    static final String PARAMETER_NAME = "time_zone";

    private final Parameter.ServerParameterService serverParameterService = new Parameter.ServerParameterService();

    @Override
    public Map<Class, Parameter.ParamAnnotationHelper> getParameterAnnotationHelperMap() {
        Map<Class, Parameter.ParamAnnotationHelper> m = new WeakHashMap<>();
        m.put(TimeZoneParam.class, new Parameter.ParamAnnotationHelper<TimeZoneParam>() {

            @Override
            public String getValueOf(TimeZoneParam a) {
                return PARAMETER_NAME;
            }

            @Override
            public org.glassfish.jersey.server.model.Parameter.Source getSource() {
                return Parameter.Source.UNKNOWN;
            }
        });
        return m;
    }

    @Override
    public Parameter.ParamCreationFactory<? extends Parameter> getParameterCreationFactory() {
        return serverParameterService.getParameterCreationFactory();
    }
}
