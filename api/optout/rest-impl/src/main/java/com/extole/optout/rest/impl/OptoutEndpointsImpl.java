package com.extole.optout.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.id.Id;
import com.extole.optout.rest.OptoutEndpoints;
import com.extole.optout.rest.OptoutResponse;
import com.extole.optout.service.ClientInfoNotFoundException;
import com.extole.optout.service.EmailListType;
import com.extole.optout.service.OptoutService;
import com.extole.optout.service.OptoutType;

@Provider
public class OptoutEndpointsImpl implements OptoutEndpoints {
    public static final EmailListType TYPE = EmailListType.OPTOUT;

    private static final Logger LOG = LoggerFactory.getLogger(OptoutEndpointsImpl.class);

    private final OptoutService optoutService;

    @Inject
    public OptoutEndpointsImpl(OptoutService optoutService) {
        this.optoutService = optoutService;
    }

    @Override
    public Boolean isOptout(long clientId, String email) {
        try {
            return optoutService.isOptout(Id.valueOf(String.valueOf(clientId)), email, TYPE);
        } catch (ClientInfoNotFoundException e) {
            LOG.error("Error checking optout:{} with type:{} and clientId: {}", email, TYPE, clientId, e);
            return Boolean.FALSE;
        }
    }

    @Override
    public OptoutResponse addOptout(long clientId, String email, String type, String fallbackEmail, String fallbackType,
        Boolean online) {
        String optoutType = Strings.isNullOrEmpty(type) ? fallbackType : type;
        String optoutEmail = Strings.isNullOrEmpty(email) ? fallbackEmail : email;
        Boolean isOnline = online == null || online;

        boolean result = false;
        try {
            optoutService.addOptout(Id.valueOf(String.valueOf(clientId)), optoutEmail,
                optoutType == null ? null : OptoutType.valueOf(optoutType), TYPE, isOnline);
            result = true;
        } catch (Exception e) {
            LOG.error("Error adding optout:" + email + " for clientId:" + clientId, e);
        }
        return new OptoutResponse(result);
    }

    @Override
    public OptoutResponse deleteOptout(long clientId, String email, String value) {
        String emailToUse = email == null ? value : email;
        boolean result = false;
        try {
            optoutService.removeOptout(Id.valueOf(String.valueOf(clientId)), emailToUse, TYPE);
            result = true;
        } catch (Exception e) {
            LOG.error("Error deleting optout:" + email + " for clientId:" + clientId, e);
        }
        return new OptoutResponse(result);
    }
}
