package com.extole.consumer.rest.web.zone;

// TODO should be linked in ZoneEndpoints ENG-11932
public final class ZoneRenderResponse {

    public static final int HTTP_OK = 200;
    public static final int HTTP_FOUND = 302;

    // TODO endpoint classes/response objects should be able to define response headers ENG-11933
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_EXTOLE_LOG = "X-Extole-Log";
    public static final String HEADER_EXTOLE_TOKEN = "X-Extole-Token";
    public static final String HEADER_EXTOLE_CAMPAIGN = "X-Extole-Campaign";
    public static final String HEADER_EXTOLE_FRONTEND_CONTROLLER_ID = "X-Extole-Frontend-Controller-Id";
    public static final String HEADER_EXTOLE_CREATIVE_ACTION_ID = "X-Extole-Creative-Action-Id";
    public static final String HEADER_EXTOLE_CREATIVE = "X-Extole-Creative";
    public static final String HEADER_EXTOLE_CREATIVE_VERSION = "X-Extole-Creative-Version";
    public static final String HEADER_EXTOLE_INPUT_EVENT_ID = "X-Extole-Input-Event-Id";

    private ZoneRenderResponse() {

    }

}
