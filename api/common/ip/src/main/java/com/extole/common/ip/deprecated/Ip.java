package com.extole.common.ip.deprecated;

import java.net.InetAddress;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@Deprecated // TODO to be removed in ENG-13511
public final class Ip {
    public static final Ip UNKNOWN_IP = new Ip("0.0.0.0");

    private static final String JSON_ADDRESS = "address";

    private final String address;

    @JsonCreator
    private Ip(@JsonProperty(JSON_ADDRESS) String address) {
        this.address = address;
    }

    @JsonProperty(JSON_ADDRESS)
    public String getAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || otherObject.getClass() != Ip.class) {
            return false;
        }

        Ip otherIp = (Ip) otherObject;
        return Objects.equals(address, otherIp.address);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Ip fromInetAddress(InetAddress inetAddress) {
        return new Ip(inetAddress.getHostAddress());
    }

}
