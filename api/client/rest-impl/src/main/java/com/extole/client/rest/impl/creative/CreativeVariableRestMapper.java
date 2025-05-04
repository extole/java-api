package com.extole.client.rest.impl.creative;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.net.HostAndPort;

import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.creative.CreativeVariableResponse;
import com.extole.client.rest.creative.CreativeVariableScope;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.campaign.CreativeVariable;
import com.extole.model.service.creative.CreativeVariables;
import com.extole.origin.client.OriginUriException;
import com.extole.origin.client.creative.AssembledCreativeArchiveOriginUriBuilder;

public final class CreativeVariableRestMapper {

    private static final String CREATIVE_ROOT = "creative-root://";

    private CreativeVariableRestMapper() {
    }

    public static CreativeVariableResponse toCreativeVariableResponse(HostAndPort originHostAndPort,
        Id<ClientHandle> clientId, Long coreAssetsVersion, CreativeVariable creativeVariable,
        Function<CreativeArchiveId, Integer> buildVersionProvider,
        String creativeActionId) {

        Map<String, String> replacedLocaleValues = new HashMap<>(creativeVariable.getValues());
        Map<String, String> replacedValues = new HashMap<>(creativeVariable.getValues());
        if (CreativeVariable.Type.IMAGE.equals(creativeVariable.getType())) {
            replaceCreativeRootInValues(originHostAndPort, clientId, coreAssetsVersion, creativeVariable,
                replacedLocaleValues, buildVersionProvider);
            replaceCreativeRootInValues(originHostAndPort, clientId, coreAssetsVersion, creativeVariable,
                replacedValues, buildVersionProvider);
        }
        return new CreativeVariableResponse(creativeVariable.getName(), creativeVariable.getLabel(),
            CreativeVariableScope.valueOf(creativeVariable.getScope().toString()),
            CreativeVariableScope.valueOf(creativeVariable.getDefaultScope().toString()),
            CreativeVariableResponse.Type.valueOf(creativeVariable.getType().toString()),
            creativeVariable.getTags(), replacedValues,
            creativeVariable.isVisible(),
            creativeActionId);
    }

    private static void replaceCreativeRootInValues(HostAndPort originHostAndPort, Id<ClientHandle> clientId,
        Long coreAssetsVersion, CreativeVariable creativeVariable, Map<String, String> values,
        Function<CreativeArchiveId, Integer> buildVersionProvider) {
        for (Map.Entry<String, String> localeValue : values.entrySet()) {
            String localeValueReplaced = localeValue.getValue();
            if (localeValue.getValue().startsWith(CREATIVE_ROOT)) {
                localeValueReplaced = getUri(originHostAndPort, clientId, coreAssetsVersion,
                    creativeVariable.getCreativeArchiveId(),
                    localeValue.getValue().replace(CREATIVE_ROOT, ""), buildVersionProvider).toString();
            }
            values.put(localeValue.getKey(), localeValueReplaced);
        }
    }

    public static List<CreativeVariableResponse> toCreativeVariablesResponse(HostAndPort originHostAndPort,
        Id<ClientHandle> clientId, Long coreAssetsVersion, CreativeVariables creativeVariables,
        Function<CreativeArchiveId, Integer> buildVersionProvider) {
        List<CreativeVariableResponse> response = new ArrayList<>();
        for (CreativeVariable creativeVariable : creativeVariables.getVariables()) {
            response.add(toCreativeVariableResponse(originHostAndPort, clientId, coreAssetsVersion, creativeVariable,
                buildVersionProvider, creativeVariables.getActionCreativeId().getValue()));
        }
        return response;
    }

    private static URI getUri(HostAndPort originHostAndPort, Id<ClientHandle> clientId, Long coreAssetsVersion,
        CreativeArchiveId creativeArchiveId, String path, Function<CreativeArchiveId, Integer> buildVersionProvider) {
        AssembledCreativeArchiveOriginUriBuilder builder =
            new AssembledCreativeArchiveOriginUriBuilder(originHostAndPort);
        builder.withClientId(clientId.getValue());
        builder.withCreativeArchiveId(creativeArchiveId.getId().getValue());
        builder.withCreativeArchiveVersion(creativeArchiveId.getVersion());
        builder.withCoreAssetsVersion(coreAssetsVersion);
        builder.withBuildVersion(buildVersionProvider.apply(creativeArchiveId));
        builder.withPath(Paths.get(path));
        try {
            return builder.build().toUri();
        } catch (OriginUriException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }
}
