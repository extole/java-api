package com.extole.common.rest.support.header;

import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;
import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Component;

import com.extole.common.ip.IpProcessor;
import com.extole.common.ip.deprecated.Ip;
import com.extole.common.rest.ExtoleHeaderType;

// TODO Exclude proxy, VPN IPs - ENG-22431
@Component
public class SourceIpProcessor {

    private static final String CLOUDFARE_HEADER_NAME_CONNECTING_IP = "cf-connecting-ip";

    public List<Ip> readSourceIps(HttpServletRequest request) {
        String sourceIpHeaderValue = getSourceIpHeaderValue(request);
        return readSourceIps(sourceIpHeaderValue);
    }

    public List<Ip> readSourceIps(String sourceIpHeaderValue) {
        if (StringUtils.isBlank(sourceIpHeaderValue)) {
            return List.of();
        }

        Spliterator<String> headerIpsSpliterator =
            Splitter.on(",").trimResults().split(sourceIpHeaderValue).spliterator();
        List<String> sourceIpStrings = StreamSupport.stream(headerIpsSpliterator, false)
            .filter(sourceIpString -> InetAddressValidator.getInstance().isValid(sourceIpString))
            .collect(Collectors.toList());
        Collections.reverse(sourceIpStrings);

        return IpProcessor.readSourceIps(sourceIpStrings).stream()
            .map(ip -> Ip.fromInetAddress(InetAddresses.forString(ip.getValue())))
            .collect(Collectors.toUnmodifiableList());
    }

    private String getSourceIpHeaderValue(HttpServletRequest request) {
        String cloudfareConnectingIp = request.getHeader(CLOUDFARE_HEADER_NAME_CONNECTING_IP);
        if (StringUtils.isNotBlank(cloudfareConnectingIp)) {
            return cloudfareConnectingIp;
        }
        return request.getHeader(ExtoleHeaderType.SOURCE_IP.getHeaderName());
    }
}
