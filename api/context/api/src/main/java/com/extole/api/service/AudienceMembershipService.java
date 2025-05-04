package com.extole.api.service;

public interface AudienceMembershipService {
    void create(String audienceId, String personId);

    void remove(String audienceId, String personId);
}
