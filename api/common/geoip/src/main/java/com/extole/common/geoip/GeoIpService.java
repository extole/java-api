package com.extole.common.geoip;

import java.net.InetAddress;

import com.extole.common.ip.GeoIp;

public interface GeoIpService {

    GeoIp fromInetAddress(InetAddress inetAddress);
}
