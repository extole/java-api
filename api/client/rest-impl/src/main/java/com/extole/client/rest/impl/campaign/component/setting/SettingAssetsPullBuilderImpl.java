package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.ComponentOwner;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;
import com.extole.model.entity.campaign.built.BuiltCampaignComponentAsset;
import com.extole.model.entity.campaign.built.BuiltVariable;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.BuiltCampaignService;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignVersionState;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.ComponentNotFoundException;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentSizeTooBigException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetDescriptionLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameLengthException;
import com.extole.model.service.campaign.component.asset.ComponentAsset;
import com.extole.model.service.campaign.component.asset.ComponentAssetNotFoundException;
import com.extole.model.service.campaign.component.asset.ComponentAssetService;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.spring.ServiceLocator;

@Component
@Scope(ServiceLocator.PROTOTYPE)
final class SettingAssetsPullBuilderImpl implements SettingAssetsPullBuilder {

    private final BuiltCampaignService builtCampaignService;
    private final ComponentService componentService;
    private final ComponentAssetService componentAssetService;

    private ClientAuthorization authorization;
    private Campaign campaign;
    private CampaignComponent campaignComponent;
    private CampaignComponentBuilder componentBuilder;

    private BuiltCampaign builtCampaign;
    private BuiltCampaignComponent builtComponent;

    private Variable variable;
    private VariableBuilder variableBuilder;
    private Map<String,
        BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values;

    @Autowired
    SettingAssetsPullBuilderImpl(
        BuiltCampaignService builtCampaignService,
        ComponentService componentService,
        ComponentAssetService componentAssetService) {
        this.builtCampaignService = builtCampaignService;
        this.componentService = componentService;
        this.componentAssetService = componentAssetService;
    }

    @Override
    public SettingAssetsPullBuilder initialize(
        ClientAuthorization authorization,
        Campaign campaign,
        CampaignComponent campaignComponent,
        CampaignComponentBuilder componentBuilder) {
        this.authorization = authorization;
        this.campaign = campaign;
        this.campaignComponent = campaignComponent;
        this.componentBuilder = componentBuilder;

        this.builtCampaign = getBuilt(campaign);
        this.builtComponent = lookupComponent(builtCampaign, campaignComponent.getId());
        return this;
    }

    @Override
    public PullOperation buildForVariable(
        Variable variable,
        VariableBuilder variableBuilder,
        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values) {
        this.variable = variable;
        this.variableBuilder = variableBuilder;
        this.values = values;
        return () -> {
            try {
                internalPull();
            } catch (ComponentNotFoundException | AuthorizationException | CampaignNotFoundException
                | CampaignComponentAssetContentMissingException | CampaignComponentAssetNameInvalidException
                | CampaignComponentAssetFilenameInvalidException | CampaignComponentAssetFilenameLengthException
                | CampaignComponentAssetDescriptionLengthException | CampaignComponentAssetNameLengthException
                | ComponentAssetNotFoundException | CampaignComponentAssetContentSizeTooBigException
                | VariableValueKeyLengthException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        };
    }

    private void internalPull() throws ComponentNotFoundException, AuthorizationException, CampaignNotFoundException,
        CampaignComponentAssetContentMissingException, CampaignComponentAssetNameInvalidException,
        CampaignComponentAssetFilenameInvalidException, CampaignComponentAssetFilenameLengthException,
        CampaignComponentAssetDescriptionLengthException, CampaignComponentAssetNameLengthException,
        ComponentAssetNotFoundException, CampaignComponentAssetContentSizeTooBigException,
        VariableValueKeyLengthException {

        BuiltVariable builtVariable = builtComponent.getSettings().stream()
            .filter(value -> value.getName().equalsIgnoreCase(variable.getName()))
            .map(candidate -> (BuiltVariable) candidate)
            .findFirst().orElseThrow();
        Id<CampaignComponent> sourceComponentId = builtVariable.getSourceComponentId();
        com.extole.model.entity.campaign.Component component =
            extractFromCampaignOrElseLookupForExternalComponent(authorization,
                campaign, sourceComponentId);
        boolean sourceRefersToOtherCampaign = !component.getCampaign().getId().equals(builtCampaign.getId());

        BuiltCampaign sourceBuiltCampaign = sourceRefersToOtherCampaign
            ? builtCampaignService.getBuiltCampaign(authorization.getClientId(), component.getCampaign().getId(),
                CampaignVersionState.PUBLISHED)
            : builtCampaign;

        List<PulledAssetFromSourceComponent> sourceAssets =
            pullAssetsFromSourceComponent(sourceBuiltCampaign, sourceComponentId, campaignComponent);

        values = modifyValuesConsideringSourceAssets(authorization, sourceAssets, builtVariable, values,
            () -> componentBuilder.addAsset());
        variableBuilder.withValues(values);
    }

    private Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        modifyValuesConsideringSourceAssets(
            Authorization authorization,
            List<PulledAssetFromSourceComponent> sourceAssets,
            BuiltVariable currentBuiltVariable,
            Map<String,
                BuildtimeEvaluatable<VariableBuildtimeContext,
                    RuntimeEvaluatable<Object, Optional<Object>>>> initialValues,
            Supplier<CampaignComponentAssetBuilder> createAssetSupplier)
            throws AuthorizationException, ComponentAssetNotFoundException, CampaignComponentAssetNameInvalidException,
            CampaignComponentAssetNameLengthException, CampaignComponentAssetContentMissingException,
            CampaignComponentAssetContentSizeTooBigException, CampaignComponentAssetFilenameInvalidException,
            CampaignComponentAssetFilenameLengthException, CampaignComponentAssetDescriptionLengthException {

        Map<String, String> filteredProvidedValues =
            currentBuiltVariable.getSourcedValues().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Provided)
                .map(entry -> Pair.of(entry.getKey(), (Provided<Object, Optional<Object>>) entry.getValue()))
                .filter(value -> value.getRight().getValue().isPresent()
                    && value.getRight().getValue().get() instanceof String)
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().getValue().get()))
                .collect(
                    Collectors.toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight().toString()));

        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> resultValues =
                Maps.newLinkedHashMap(initialValues);

        for (Map.Entry<String,
            BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>> entry : initialValues
                    .entrySet()) {

            boolean isProvidedAndNotOriginUrl = filteredProvidedValues.containsKey(entry.getKey()) &&
                !filteredProvidedValues.get(entry.getKey()).contains("origin");
            if (isProvidedAndNotOriginUrl) {
                resultValues.put(entry.getKey(),
                    Provided.nestedOptionalOf(filteredProvidedValues.get(entry.getKey())));
            }
        }

        for (PulledAssetFromSourceComponent pulledAsset : sourceAssets) {
            ComponentAsset assetWithContent =
                componentAssetService.get(authorization, pulledAsset.getBuiltAsset().getId(),
                    pulledAsset.getCampaignVersion());

            BuiltCampaignComponentAsset builtCampaignComponentAsset = pulledAsset.getBuiltAsset();
            CampaignComponentAssetBuilder assetBuilder = createAssetSupplier.get()
                .withName(builtCampaignComponentAsset.getName())
                .withContent(assetWithContent.getContent())
                .withFilename(builtCampaignComponentAsset.getFilename())
                .withTags(builtCampaignComponentAsset.getTags());

            if (!pulledAsset.getOldName().equals(pulledAsset.getNewName())) {
                assetBuilder.withName(pulledAsset.getNewName());

                initialValues.entrySet().stream()
                    .filter(entry -> !(entry.getValue() instanceof Provided))
                    .forEach(entry -> {
                        BuildtimeEvaluatable<VariableBuildtimeContext,
                            RuntimeEvaluatable<Object, Optional<Object>>> value =
                                entry.getValue();
                        String serialized = ObjectMapperProvider.getConfiguredInstance()
                            .convertValue(value, String.class);
                        if (serialized.contains(builtCampaignComponentAsset.getName())) {
                            value = deserialize(
                                serialized.replaceAll(builtCampaignComponentAsset.getName(), pulledAsset.getNewName()),
                                new TypeReference<>() {});
                            resultValues.put(entry.getKey(), value);
                        }
                    });
            }

            if (builtCampaignComponentAsset.getDescription().isPresent()) {
                assetBuilder.withDescription(builtCampaignComponentAsset.getDescription().get());
            }
        }

        return Collections.unmodifiableMap(resultValues);
    }

    public static <T> T deserialize(String serialized, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = ObjectMapperProvider.getConfiguredInstance();
            return objectMapper.readValue(objectMapper.writeValueAsString(serialized), typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PulledAssetFromSourceComponent> pullAssetsFromSourceComponent(
        BuiltCampaign sourceBuiltCampaign,
        Id<CampaignComponent> sourceComponentId,
        CampaignComponent currentCampaignComponent) {

        BuiltCampaignComponent sourceBuiltComponent = sourceBuiltCampaign.getComponents().stream()
            .filter(value -> value.getId().equals(sourceComponentId))
            .findFirst()
            .orElseThrow();
        List<BuiltCampaignComponentAsset> variableSourceAssets = sourceBuiltComponent.getAssets();

        Set<String> existingAssetsNames = currentCampaignComponent.getAssets().stream().map(asset -> asset.getName())
            .collect(Collectors.toUnmodifiableSet());

        return variableSourceAssets.stream().map(builtAsset -> {
            return new PulledAssetFromSourceComponent() {
                private final String newName = incrementNameIfNeeded(builtAsset.getName(),
                    existingAssetsNames::contains);

                @Override
                public String getOldName() {
                    return builtAsset.getName();
                }

                @Override
                public String getNewName() {
                    return newName;
                }

                @Override
                public BuiltCampaignComponentAsset getBuiltAsset() {
                    return builtAsset;
                }

                @Override
                public Integer getCampaignVersion() {
                    return sourceBuiltCampaign.getVersion();
                }
            };
        }).collect(Collectors.toUnmodifiableList());
    }

    private String incrementNameIfNeeded(String name, Predicate<String> hasCollisionPredicate) {
        if (!hasCollisionPredicate.test(name)) {
            return name;
        }

        String incrementedName = name + "_copy";

        if (hasCollisionPredicate.test(incrementedName)) {
            int i;
            for (i = 1; hasCollisionPredicate.test(incrementedName + "_" + i);) {
                i++;
            }

            return incrementedName + "_" + i;
        }

        return incrementedName;
    }

    private BuiltCampaignComponent lookupComponent(BuiltCampaign builtCampaign,
        Id<CampaignComponent> componentId) {
        return builtCampaign.getComponents().stream().filter(component -> component.getId().equals(componentId))
            .findFirst()
            .orElseThrow();
    }

    private com.extole.model.entity.campaign.Component extractFromCampaignOrElseLookupForExternalComponent(
        ClientAuthorization authorization,
        Campaign campaign,
        Id<CampaignComponent> sourceComponentId)
        throws ComponentNotFoundException, AuthorizationException {

        Optional<CampaignComponent> sourceComponentFromTheCurrentCampaign =
            campaign.getComponents().stream().filter(component -> component.getId().equals(sourceComponentId))
                .findFirst();

        if (sourceComponentFromTheCurrentCampaign.isPresent()) {
            return new ComponentImpl(campaign, sourceComponentFromTheCurrentCampaign.get());
        } else {
            return componentService.get(authorization, sourceComponentId);
        }
    }

    private BuiltCampaign getBuilt(Campaign campaign) {
        try {
            return builtCampaignService.buildCampaign(campaign);
        } catch (BuildCampaignException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private static final class ComponentImpl implements com.extole.model.entity.campaign.Component {
        private final Campaign campaign;
        private final CampaignComponent component;

        private ComponentImpl(Campaign campaign, CampaignComponent component) {
            this.campaign = campaign;
            this.component = component;
        }

        @Override
        public Campaign getCampaign() {
            return campaign;
        }

        @Override
        public CampaignComponent getCampaignComponent() {
            return component;
        }

        @Override
        public ComponentOwner getOwner() {
            return ComponentOwner.CLIENT;
        }
    }

    private interface PulledAssetFromSourceComponent {
        String getOldName();

        String getNewName();

        BuiltCampaignComponentAsset getBuiltAsset();

        Integer getCampaignVersion();
    }

}
