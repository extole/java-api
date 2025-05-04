package com.extole.api.service;

public interface BlockService {

    boolean isEmailBlocked(String email);

    boolean isEmailDomainBlocked(String email);

    boolean isIpGloballyBlocked(String ip);

    boolean isIpBlocked(String ip, String[] subnets);
}
