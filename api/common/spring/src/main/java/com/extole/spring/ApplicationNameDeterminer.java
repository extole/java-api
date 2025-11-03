package com.extole.spring;

import com.google.common.base.Strings;
import org.springframework.context.ApplicationContext;

public class ApplicationNameDeterminer {

    public String fromApplicationContext(ApplicationContext applicationContext) {
        String appName = applicationContext.getApplicationName();
        String trimmedAppName;
        if (!Strings.isNullOrEmpty(appName)) {
            trimmedAppName = appName.substring(1);
        } else {
            trimmedAppName = "unspecified";
        }
        return trimmedAppName;
    }
}
