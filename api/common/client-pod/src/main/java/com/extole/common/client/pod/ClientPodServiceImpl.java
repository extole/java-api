package com.extole.common.client.pod;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class ClientPodServiceImpl implements ClientPodService {
    private final ClientPod currentPod;

    @Autowired
    ClientPodServiceImpl(@Value("${extole.pod:}") String currentPod) {
        this.currentPod = Optional.ofNullable(StringUtils.trimToNull(currentPod)).map(ClientPod::new)
            .orElse(ClientPod.UNDEFINED);
    }

    @Override
    public ClientPod getCurrentPod() {
        return currentPod;
    }
}
