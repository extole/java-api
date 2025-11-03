package com.extole.common.event.migration;

import java.util.List;

public interface EventMigrationService {
    List<String> migrate(String event) throws EventMigrationServiceException;
}
