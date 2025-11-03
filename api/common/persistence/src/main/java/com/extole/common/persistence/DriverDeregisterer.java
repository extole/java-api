package com.extole.common.persistence;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.extole.spring.StartFirstStopLast;

@Component
public class DriverDeregisterer implements StartFirstStopLast {

    private static final Logger LOG = LoggerFactory.getLogger(DriverDeregisterer.class);

    @Override
    public void stop(Runnable callback) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    LOG.error("Unable to deregister driver", e);
                }
            }
        }

        AbandonedConnectionCleanupThread.checkedShutdown();

        callback.run();
    }
}
