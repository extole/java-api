package com.extole.client.rest.impl.campaign.migration.global;

import static com.extole.model.entity.campaign.CampaignComponent.ROOT;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.client.rest.campaign.migration.GlobalCampaignMigrationRestException;
import com.extole.client.rest.campaign.migration.global.GlobalCampaignMigrationEndpoints;
import com.extole.client.rest.campaign.migration.global.GlobalCampaignMigrationResponse;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lang.ToString;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.lock.LockKey;
import com.extole.common.lock.LockService;
import com.extole.common.lock.LockService.LockClosure;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentAsset;
import com.extole.model.entity.campaign.ComponentElementPart;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionState;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.asset.ComponentAssetNotFoundException;
import com.extole.model.service.campaign.component.asset.ComponentAssetService;
import com.extole.model.service.campaign.setting.VariableBuilder;

@Provider
public class GlobalCampaignMigrationEndpointsImpl implements GlobalCampaignMigrationEndpoints {
    private static final Predicate<CampaignComponent> IS_ROOT_COMPONENT =
        component -> component.getName().equalsIgnoreCase("root");
    private static final String TRANSLATABLE_TAG = "translatable";
    private static final Predicate<Campaign> IS_GLOBAL = campaign -> campaign.getName().equalsIgnoreCase("global");
    private final CampaignService campaignService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final LockService lockService;
    private final ComponentAssetService componentAssetService;
    private final CampaignComponentSettingRestMapper mapper;
    private static final LockDescription LOCK_DESCRIPTION = new LockDescription("pull-client-level-variables");
    private static final Duration MAX_LOCK_CLOSURE_DURATION = Duration.ofMinutes(10);
    private static final Duration MAX_LOCK_ACQUIRE_DURATION = Duration.ZERO;

    private ClientAuthorization authorization;
    private List<Campaign> allLatestCampaigns;
    private List<Campaign> allPublishedCampaigns;
    private Campaign global;
    private CampaignComponent globalRoot;
    private Set<String> clientLevelVariables;

    @Inject
    public GlobalCampaignMigrationEndpointsImpl(CampaignService campaignService,
        ClientAuthorizationProvider authorizationProvider,
        LockService lockService,
        ComponentAssetService componentAssetService,
        CampaignComponentSettingRestMapper mapper) {
        this.campaignService = campaignService;
        this.authorizationProvider = authorizationProvider;
        this.lockService = lockService;
        this.componentAssetService = componentAssetService;
        this.mapper = mapper;
    }

    @Override
    public GlobalCampaignMigrationResponse pullClientVariables(String accessToken,
        Optional<Id<com.extole.api.campaign.BuiltCampaign>> sourceCampaignId,
        ZoneId timeZone) throws UserAuthorizationRestException, GlobalCampaignMigrationRestException {
        authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.isClientAuthorized(authorization.getClientId(), Authorization.Scope.CLIENT_SUPERUSER)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
        allLatestCampaigns = campaignService.createCampaignQueryBuilder(authorization)
            .withCampaignVersionState(CampaignVersionState.LATEST)
            .list().stream().filter(campaign -> campaign.getLocks().isEmpty()).collect(Collectors.toList());
        global = allLatestCampaigns.stream()
            .filter(IS_GLOBAL)
            .findFirst()
            .get();
        allPublishedCampaigns = campaignService.createCampaignQueryBuilder(authorization)
            .withCampaignVersionState(CampaignVersionState.PUBLISHED)
            .list().stream().filter(campaign -> campaign.getLocks().isEmpty()).collect(Collectors.toList());

        Campaign publishedGlobal = allPublishedCampaigns.stream()
            .filter(IS_GLOBAL)
            .findFirst()
            .get();

        if (!global.getVersion().equals(publishedGlobal.getVersion())) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignMigrationRestException.class)
                .withErrorCode(GlobalCampaignMigrationRestException.GLOBAL_NOT_PUBLISHED)
                .build();
        }

        if (sourceCampaignId.isPresent()) {
            validateCampaignExists(authorization, sourceCampaignId.get());
            allLatestCampaigns = filterBySourceCampaignIdIfNecessary(allLatestCampaigns, sourceCampaignId.get());
            allPublishedCampaigns = filterBySourceCampaignIdIfNecessary(allPublishedCampaigns, sourceCampaignId.get());
        }

        globalRoot = lookupRoot(global.getComponents());
        clientLevelVariables = globalRoot.getSettings()
            .stream()
            .map(variable -> variable.getName())
            .collect(Collectors.toSet());

        LockClosure<GlobalCampaignMigrationResponse> migrationClosure = () -> {
            List<VariableAndAsset> latestVariableAndAssets = extractAllRootVariables(allLatestCampaigns,
                CampaignVersionState.LATEST);
            List<VariableAndAsset> publishedVariableAndAssets = extractAllRootVariables(allPublishedCampaigns,
                CampaignVersionState.PUBLISHED);
            List<VariableAndAsset> allVariables = Stream
                .concat(latestVariableAndAssets.stream(), publishedVariableAndAssets.stream())
                .collect(Collectors.toUnmodifiableList());

            KeyCaseInsensitiveMap<List<VariableAndAsset>> variablesToReuse = KeyCaseInsensitiveMap.create();
            for (VariableAndAsset variableAndAsset : allVariables) {
                variablesToReuse.compute(variableAndAsset.getVariable().getName(), (name, list) -> {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }
                    if (!isVariableFromRootComponentAndGlobalCampaign(variableAndAsset)) {
                        list.add(variableAndAsset);
                    }
                    return list;
                });
            }

            try {
                Map<String, Map<String, CampaignComponentVariableResponse>> skippedVariables = Maps.newHashMap();

                List<String> migratedAssets = migrateAssets(variablesToReuse);
                List<String> migratedVariables = migrateVariables(variablesToReuse, skippedVariables);

                Set<String> migratedVariablesSet = Sets.newHashSet(migratedVariables);
                changeCampaignsRootVariables(authorization, allVariables, migratedVariablesSet::contains);
                return new GlobalCampaignMigrationResponse(migratedVariables, migratedAssets, skippedVariables);
            } catch (Exception e) {
                throw new LockClosureException(e);
            }
        };

        try {
            return lockService.executeWithinLock(new LockKey("update-campaign", global.getId()),
                LOCK_DESCRIPTION, migrationClosure, MAX_LOCK_CLOSURE_DURATION, MAX_LOCK_ACQUIRE_DURATION);
        } catch (Exception e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private List<String> migrateVariables(KeyCaseInsensitiveMap<List<VariableAndAsset>> variablesToReuse,
        Map<String, Map<String, CampaignComponentVariableResponse>> skippedVariables)
        throws Exception {
        CampaignBuilder globalBuilder = campaignService.editCampaign(authorization, global.getId());
        CampaignComponentBuilder globalRootBuilder = globalBuilder
            .updateComponent(globalRoot);
        List<String> migratedVariables = Lists.newArrayList();
        for (Variable variable : getVariables(globalRoot.getSettings())) {
            List<VariableAndAsset> variables =
                Optional.ofNullable(variablesToReuse.get(variable.getName())).orElse(Lists.newArrayList());
            VariableBuilder updateVariableBuilder = globalRootBuilder.updateSetting(variable);
            updateVariableBuilder.withSource(VariableSource.LOCAL)
                .withTags(variable.getTags());

            Optional<VariableAndAsset> merged = mergeIfNeeded(variables);
            if (merged.isPresent()) {
                VariableAndAsset clientVariableAndAsset = merged.get();
                Map<String,
                    BuildtimeEvaluatable<VariableBuildtimeContext,
                        RuntimeEvaluatable<Object, Optional<Object>>>> values =
                            clientVariableAndAsset.getVariable().getValues();
                if (variable.getTags().contains(TRANSLATABLE_TAG)) {
                    values = moveDefaultValueToEnIfNeeded(values);
                }
                updateVariableBuilder.withValues(values);

                Optional<String> displayName = clientVariableAndAsset.getVariable().getDisplayName();
                if (displayName.isPresent()) {
                    globalRootBuilder.updateSetting(variable).withDisplayName(displayName.get());
                }
                migratedVariables.add(variable.getName());
            } else {
                skippedVariables.put(variable.getName(), variables.stream()
                    .collect(Collectors.toMap(variableAndAsset -> variableAndAsset.getCampaign().getId() + "-" +
                        variableAndAsset.getCampaign().getVersion() + "-" +
                        variableAndAsset.getComponent().getId() + "-" +
                        variable.getName(),
                        variableAndAsset -> (CampaignComponentVariableResponse) mapper
                            .toSettingResponse(variableAndAsset.getVariable()),
                        (first, last) -> last)));
            }
        }
        globalBuilder.withPublished().save();
        return migratedVariables;
    }

    private Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        moveDefaultValueToEnIfNeeded(
            Map<String,
                BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values) {

        if (!values.containsKey("default")) {
            return values;
        }

        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> copiedMap =
                new LinkedHashMap<>(values);
        BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>> defaultValue =
            copiedMap.remove("default");

        if (!copiedMap.containsKey("en")) {
            copiedMap.put("en", defaultValue);
        }
        return Collections.unmodifiableMap(copiedMap);
    }

    private List<String> migrateAssets(KeyCaseInsensitiveMap<List<VariableAndAsset>> variablesToReuse)
        throws Exception {
        CampaignBuilder globalBuilder = campaignService.editCampaign(authorization, global.getId());
        CampaignComponentBuilder globalRootBuilder = globalBuilder.updateComponent(globalRoot);
        Map<String, CampaignComponentAsset> clientLevelAssetsByName = globalRoot.getAssets().stream()
            .collect(Collectors.toUnmodifiableMap(asset -> asset.getName(), Function.identity()));
        List<String> migratedAssets = Lists.newArrayList();

        for (Variable variable : getVariables(globalRoot.getSettings())) {
            List<VariableAndAsset> variables =
                Optional.ofNullable(variablesToReuse.get(variable.getName())).orElse(Lists.newArrayList());
            Optional<VariableAndAsset> merged = mergeIfNeeded(variables);
            if (merged.isPresent()) {
                VariableAndAsset clientVariableAndAsset = merged.get();

                for (Map.Entry<String, CampaignComponentAsset> entry : clientVariableAndAsset.getAssets()
                    .entrySet()) {
                    CampaignComponentAsset asset = entry.getValue();
                    boolean isNew = !clientLevelAssetsByName.containsKey(asset.getName());
                    CampaignComponentAssetBuilder assetBuilder = isNew
                        ? globalRootBuilder.addAsset()
                        : globalRootBuilder.updateAsset(clientLevelAssetsByName.get(asset.getName()));
                    assetBuilder.withName(asset.getName())
                        .withFilename(asset.getFilename());

                    if (asset.getDescription().isPresent()) {
                        assetBuilder.withDescription(asset.getDescription().get());
                    }

                    assetBuilder.withContent(clientVariableAndAsset.getAssetBinaries().get(entry.getKey()));
                    assetBuilder.withTags(asset.getTags());
                    migratedAssets.add(asset.getName());
                }
            }
        }

        globalBuilder.save();
        return migratedAssets;
    }

    private void validateCampaignExists(Authorization authorization, Id<?> campaignId)
        throws GlobalCampaignMigrationRestException {
        try {
            campaignService.getCampaignByIdAndVersionState(authorization, Id.valueOf(campaignId.getValue()),
                CampaignVersionState.LATEST);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignMigrationRestException.class)
                .withCause(e)
                .withErrorCode(GlobalCampaignMigrationRestException.SOURCE_CAMPAIGN_NOT_FOUND)
                .build();
        }
    }

    private List<Campaign> filterBySourceCampaignIdIfNecessary(List<Campaign> campaigns, Id<?> sourceCampaignId) {
        return campaigns.stream().filter(campaign -> campaign.getId().getValue().equals(sourceCampaignId.getValue()))
            .collect(Collectors.toUnmodifiableList());
    }

    private void changeCampaignsRootVariables(ClientAuthorization authorization,
        List<VariableAndAsset> variables, Predicate<String> filterByVariableName) {

        Map<Id<Campaign>, Map<Id<CampaignComponent>, Multimap<String, VariableAndAsset>>> groupedAllVariables =
            Maps.newHashMap();

        List<VariableAndAsset> filteredVariables = variables
            .stream()
            .filter(variableAndAsset -> !isVariableFromRootComponentAndGlobalCampaign(variableAndAsset))
            .filter(value -> !value.getVariable().getSource().equals(VariableSource.INHERITED))
            .filter(value -> filterByVariableName.test(value.getVariable().getName()))
            .collect(Collectors.toUnmodifiableList());

        for (VariableAndAsset variable : filteredVariables) {
            Id<Campaign> campaignId = variable.getCampaign().getId();
            Id<CampaignComponent> componentId = variable.getComponent().getId();
            String variableName = variable.getVariable().getName();

            Multimap<String, VariableAndAsset> allGroupedByVariableName =
                groupedAllVariables
                    .computeIfAbsent(campaignId, key -> Maps.newHashMap())
                    .computeIfAbsent(componentId, key -> LinkedHashMultimap.create());

            allGroupedByVariableName.put(variableName, variable);
        }

        Id<Campaign> campaignId = null;
        try {
            for (Iterator<Id<Campaign>> campaignIterator = groupedAllVariables.keySet().iterator(); campaignIterator
                .hasNext();) {
                campaignId = campaignIterator.next();
                Map<Id<CampaignComponent>, Multimap<String, VariableAndAsset>> variablesGroupedByComponents =
                    groupedAllVariables.get(campaignId);
                List<VariableAndAsset> campaignVariables = variablesGroupedByComponents.values().stream()
                    .flatMap(value -> value.asMap().values().stream().flatMap(values -> values.stream()))
                    .collect(Collectors.toUnmodifiableList());

                if (isLatestAndPublishedInTheSameTime(variablesGroupedByComponents)) {
                    CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, campaignId);
                    makeVariablesInherited(campaignBuilder, campaignVariables);
                    campaignBuilder.withPublished().save();
                } else if (hasOnlySpecificCampaignVersionState(campaignVariables, CampaignVersionState.PUBLISHED)) {
                    Campaign originalLatest = campaignService.getCampaignByIdAndVersionState(authorization,
                        campaignId, CampaignVersionState.LATEST);
                    Campaign currentPublishedCampaign = campaignVariables.get(0).getCampaign();
                    campaignService.makeLatestByIdAndVersion(authorization, campaignId,
                        new CampaignVersion(currentPublishedCampaign.getVersion())).save();
                    CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, campaignId);
                    makeVariablesInherited(campaignBuilder, campaignVariables);
                    campaignBuilder.withPublished().save();
                    campaignService.makeLatestByIdAndVersion(authorization, campaignId,
                        new CampaignVersion(originalLatest.getVersion())).save();
                } else if (hasOnlySpecificCampaignVersionState(campaignVariables, CampaignVersionState.LATEST)) {
                    CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, campaignId);
                    makeVariablesInherited(campaignBuilder, campaignVariables);
                    campaignBuilder.save();
                } else {
                    List<VariableAndAsset> latestCampaignVariables =
                        campaignVariables.stream()
                            .filter(value -> value.getCampaignState().equals(CampaignVersionState.LATEST))
                            .collect(Collectors.toUnmodifiableList());
                    List<VariableAndAsset> publishedCampaignVariables =
                        campaignVariables.stream()
                            .filter(value -> value.getCampaignState().equals(CampaignVersionState.PUBLISHED))
                            .collect(Collectors.toUnmodifiableList());
                    Campaign latestCampaign = latestCampaignVariables.get(0).getCampaign();
                    Campaign publishedCampaign = publishedCampaignVariables.get(0).getCampaign();

                    campaignService.makeLatestByIdAndVersion(authorization, campaignId,
                        new CampaignVersion(publishedCampaign.getVersion())).save();
                    CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, campaignId);
                    makeVariablesInherited(campaignBuilder, publishedCampaignVariables);
                    campaignBuilder.withPublished().save();

                    campaignService.makeLatestByIdAndVersion(authorization, campaignId,
                        new CampaignVersion(latestCampaign.getVersion())).save();
                    campaignBuilder = campaignService.editCampaign(authorization, campaignId);
                    makeVariablesInherited(campaignBuilder, latestCampaignVariables);
                    campaignBuilder.save();
                }

            }

        } catch (Exception e) {
            throw new IllegalStateException(
                "Was not possible to perform editCampaign for campaign " + campaignId, e);
        }
    }

    private void makeVariablesInherited(CampaignBuilder campaignBuilder, List<VariableAndAsset> campaignVariables) {
        for (VariableAndAsset currentVariable : campaignVariables) {
            VariableBuilder variableBuilder = campaignBuilder.updateComponent(currentVariable.getComponent())
                .updateSetting(currentVariable.getVariable());
            variableBuilder.withSource(VariableSource.INHERITED);
        }
    }

    private boolean isLatestAndPublishedInTheSameTime(
        Map<Id<CampaignComponent>, Multimap<String, VariableAndAsset>> variablesByComponent) {

        return variablesByComponent.values().stream().flatMap(values -> values.asMap().values().stream())
            .anyMatch(variables -> {
                if (variables.size() == 2) {
                    VariableAndAsset[] array = variables.toArray(VariableAndAsset[]::new);
                    return array[0].getCampaign().getVersion().equals(array[1].getCampaign().getVersion());
                }
                return false;
            });
    }

    private boolean hasOnlySpecificCampaignVersionState(List<VariableAndAsset> variables,
        CampaignVersionState campaignVersionState) {

        return variables.stream().allMatch(variable -> variable.getCampaignState().equals(campaignVersionState));
    }

    private Optional<VariableAndAsset> mergeIfNeeded(List<VariableAndAsset> variables) {
        if (variables.isEmpty()) {
            return Optional.empty();
        }
        if (variables.size() == 1) {
            return Optional.of(variables.get(0));
        }

        Set<String> allLocales = variables
            .stream()
            .flatMap(variable -> variable.getVariable().getValues().keySet().stream())
            .collect(Collectors.toSet());
        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> mergedValues =
                Maps.newHashMap();
        Map<String, ByteSource> mergedAssetBinaries = Maps.newHashMap();
        for (String locale : allLocales) {
            mergedValues.put(locale, mostCommon(variables
                .stream()
                .map(variable -> variable.getVariable().getValues().get(locale))
                .collect(Collectors.toList())));

            List<ByteSource> binaries = variables
                .stream()
                .map(variable -> variable.getAssetBinaries().get(locale))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (!binaries.isEmpty()) {
                mergedAssetBinaries.put(locale, mostCommon(binaries));
            }
        }
        return Optional.of(new VariableAndAsset() {
            @Override
            public Campaign getCampaign() {
                throw new UnsupportedOperationException(
                    "This is a merged variable, it does not belong to any campaign.");
            }

            @Override
            public CampaignComponent getComponent() {
                throw new UnsupportedOperationException(
                    "This is a merged variable, it does not belong to any component.");
            }

            @Override
            public Variable getVariable() {
                return new Variable() {
                    @Override
                    public boolean coreEquals(ComponentElementPart other) {
                        return false;
                    }

                    private final Variable variable = variables.get(0).getVariable();

                    @Override
                    public String getName() {
                        return variable.getName();
                    }

                    @Override
                    public Optional<String> getDisplayName() {
                        return variable.getDisplayName();
                    }

                    @Override
                    public SettingType getType() {
                        return variable.getType();
                    }

                    @Override
                    public
                        Map<String,
                            BuildtimeEvaluatable<VariableBuildtimeContext,
                                RuntimeEvaluatable<Object, Optional<Object>>>>
                        getValues() {
                        return mergedValues;
                    }

                    @Override
                    public VariableSource getSource() {
                        return VariableSource.LOCAL;
                    }

                    @Override
                    public BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>
                        getDescription() {
                        return variable.getDescription();
                    }

                    @Override
                    public Set<String> getTags() {
                        return variables.stream()
                            .flatMap(variableAndAsset -> variableAndAsset.getVariable().getTags().stream())
                            .collect(Collectors.toSet());
                    }

                    @Override
                    public DeweyDecimal getPriority() {
                        return variable.getPriority();
                    }
                };
            }

            @Override
            public Map<String, CampaignComponentAsset> getAssets() {
                return variables.stream()
                    .map(VariableAndAsset::getAssets)
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
            }

            @Override
            public Map<String, ByteSource> getAssetBinaries() {
                return mergedAssetBinaries;
            }

            @Override
            public CampaignVersionState getCampaignState() {
                throw new UnsupportedOperationException(
                    "This is a merged variable, it does not belong to any campaign.");
            }
        });
    }

    private <T> T mostCommon(Collection<T> values) {
        return values.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .get()
            .getKey();
    }

    private List<VariableAndAsset> extractAllRootVariables(List<Campaign> campaigns,
        CampaignVersionState campaignVersionState) {
        Stream<VariableAndAsset> allVariables = campaigns.stream()
            .flatMap(campaign -> campaign.getComponents().stream().map(component -> Pair.of(campaign, component)))
            .filter(pair -> IS_ROOT_COMPONENT.test(pair.getRight()))
            .flatMap(pair -> getVariables(pair.getRight().getSettings()).stream()
                .map(variable -> Triple.of(pair.getLeft(), pair.getRight(), variable)))
            .filter(triple -> clientLevelVariables.contains(triple.getRight().getName()))
            .map(triple -> new VariableAndAsset() {
                private final Campaign campaign = triple.getLeft();
                private final CampaignComponent component = triple.getMiddle();
                private final Variable variable = triple.getRight();

                @Override
                public Campaign getCampaign() {
                    return campaign;
                }

                @Override
                public CampaignComponent getComponent() {
                    return component;
                }

                @Override
                public Variable getVariable() {
                    return variable;
                }

                @Override
                public Map<String, CampaignComponentAsset> getAssets() {
                    if (variable.getType() != SettingType.IMAGE
                        && variable.getType() != SettingType.FONT
                        && variable.getType() != SettingType.STRING) {
                        return Map.of();
                    }

                    Map<String, CampaignComponentAsset> variableAssets = Maps.newHashMap();
                    Map<String, CampaignComponentAsset> allAssetsByName = component.getAssets()
                        .stream()
                        .collect(Collectors.toMap(asset -> asset.getName(), Function.identity()));
                    variable.getValues().forEach((locale, value) -> {
                        String assetName = StringUtils.substringBetween(value.toString(),
                            "context.getAsset('", "').getUrl()");
                        if (StringUtils.isNotBlank(assetName)) {
                            variableAssets.put(locale, allAssetsByName.get(assetName));
                        } else {
                            assetName = StringUtils.substringBetween(value.toString(),
                                "context.getAsset(\"", "\").getUrl()");
                            if (StringUtils.isNotBlank(assetName)) {
                                variableAssets.put(locale, allAssetsByName.get(assetName));
                            }
                        }
                    });

                    return variableAssets;
                }

                @Override
                public Map<String, ByteSource> getAssetBinaries() {
                    Set<Map.Entry<String, CampaignComponentAsset>> entries = getAssets().entrySet();
                    Map<String, ByteSource> binaries = Maps.newHashMap();
                    for (Map.Entry<String, CampaignComponentAsset> entry : entries) {
                        try {
                            binaries.put(entry.getKey(), componentAssetService
                                .get(authorization, entry.getValue().getId(), campaign.getVersion())
                                .getContent());
                        } catch (AuthorizationException | ComponentAssetNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return binaries;
                }

                @Override
                public CampaignVersionState getCampaignState() {
                    return campaignVersionState;
                }

                @Override
                public String toString() {
                    return ToString.create(this);
                }
            });

        return allVariables.collect(Collectors.toUnmodifiableList());
    }

    private CampaignComponent lookupRoot(List<CampaignComponent> components) {
        return components.stream()
            .filter(component -> ROOT.equalsIgnoreCase(component.getName()))
            .findFirst()
            .get();
    }

    private boolean isVariableFromRootComponentAndGlobalCampaign(VariableAndAsset variableAndAsset) {
        return IS_GLOBAL.test(variableAndAsset.getCampaign())
            && IS_ROOT_COMPONENT.test(variableAndAsset.getComponent());
    }

    private List<Variable> getVariables(List<Setting> settings) {
        return settings.stream()
            .filter(setting -> setting instanceof Variable)
            .map(setting -> (Variable) setting)
            .collect(Collectors.toList());
    }

    interface VariableAndAsset {

        Campaign getCampaign();

        CampaignComponent getComponent();

        Variable getVariable();

        Map<String, CampaignComponentAsset> getAssets();

        Map<String, ByteSource> getAssetBinaries();

        CampaignVersionState getCampaignState();

    }
}
