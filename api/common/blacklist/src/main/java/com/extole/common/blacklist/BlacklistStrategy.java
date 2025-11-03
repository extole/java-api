package com.extole.common.blacklist;

public interface BlacklistStrategy {

    BlacklistedResult verify(String value);

}
