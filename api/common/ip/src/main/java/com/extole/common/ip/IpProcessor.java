package com.extole.common.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IpProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(IpProcessor.class);

    private IpProcessor() {
    }

    public static List<Ip> readSourceIps(List<String> sourceIpStrings) {
        if (sourceIpStrings == null || sourceIpStrings.isEmpty()) {
            return List.of();
        }
        return sourceIpStrings.stream()
            .map(host -> readSourceIp(host))
            .filter(inetAddress -> inetAddress.isPresent() && !isLocalOrInternalAddress(inetAddress.get()))
            .map(inetAddress -> Ip.valueOf(inetAddress.get().getHostAddress()))
            .collect(Collectors.toUnmodifiableList());
    }

    public static boolean isLocalOrInternal(String host) {
        Optional<InetAddress> inetAddress = readSourceIp(host);
        return inetAddress.filter(IpProcessor::isLocalOrInternalAddress).isPresent();
    }

    private static Optional<InetAddress> readSourceIp(String host) {
        try {
            return Optional.of(InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            LOG.warn("Invalid host={} due to: {}", host, e.toString());
            return Optional.empty();
        }
    }

    private static boolean isLocalOrInternalAddress(InetAddress inetAddress) {
        return inetAddress.getHostAddress().equals(Ip.UNKNOWN_IP.getValue())
            || inetAddress.isSiteLocalAddress()
            || inetAddress.isLoopbackAddress()
            || inetAddress.isLinkLocalAddress();
    }
}
