package com.extole.common.event.migration;

import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.Set;

@Deprecated // TODO replace with event migrators ENG-14892
public class UsPacificNewLegacyZoneRulesProvider extends ZoneRulesProvider {

    private static final String ZONE_ID_US_PACIFIC_NEW = "US/Pacific-New";
    private static final String ZONE_ID_US_PACIFIC = "US/Pacific";

    @Override
    protected Set<String> provideZoneIds() {
        return Collections.singleton(ZONE_ID_US_PACIFIC_NEW);
    }

    @Override
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        return ZoneRulesProvider.getRules(ZONE_ID_US_PACIFIC, forCaching);
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        return ZoneRulesProvider.getVersions(ZONE_ID_US_PACIFIC);
    }
}
