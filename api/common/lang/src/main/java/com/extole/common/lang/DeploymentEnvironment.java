package com.extole.common.lang;

import java.util.Locale;

public enum DeploymentEnvironment {

    LO("lo"), PR("pr");

    private static final DeploymentEnvironment CURRENT_ENVIRONMENT = determineCurrentEnvironment();
    private final String shortName;

    DeploymentEnvironment(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public static DeploymentEnvironment getDeploymentEnvironment() {
        return CURRENT_ENVIRONMENT;
    }

    public static boolean isLo() {
        return CURRENT_ENVIRONMENT.equals(LO);
    }

    private static DeploymentEnvironment determineCurrentEnvironment() {
        String environment = System.getProperty("extole.environment", "lo");
        switch (environment.toLowerCase(Locale.ENGLISH)) {
            case "lo":
                return LO;
            case "pr":
                return PR;
            default:
                return LO;
        }
    }
}
