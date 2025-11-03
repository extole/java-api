package com.extole.security.backend;

import com.extole.authorization.service.Identity;
import com.extole.authorization.service.client.ClientAuthorization;

public interface BackendAuthorization extends ClientAuthorization, Identity {
}
