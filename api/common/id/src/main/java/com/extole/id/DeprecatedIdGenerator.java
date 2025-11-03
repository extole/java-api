package com.extole.id;

// Use IdGenerator (ensure varchar(24) - string, not long)
@Deprecated // TBD - OPEN TICKET
public interface DeprecatedIdGenerator<T> {

    T generateId();
}
