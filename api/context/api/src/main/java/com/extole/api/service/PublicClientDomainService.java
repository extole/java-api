package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.PublicClientDomain;

@Schema
public interface PublicClientDomainService {

    PublicClientDomain[] getPublicClientDomains();
}
