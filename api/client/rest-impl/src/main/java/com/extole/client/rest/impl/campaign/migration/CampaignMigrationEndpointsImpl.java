package com.extole.client.rest.impl.campaign.migration;

import static com.extole.model.entity.campaign.CampaignComponent.PATH_DELIMITER;
import static com.extole.model.entity.campaign.CampaignComponent.ROOT;
import static com.extole.model.entity.campaign.CampaignComponent.ROOT_REFERENCE;
import static com.extole.model.entity.campaign.CreativeVariable.Type.COLOR;
import static com.extole.model.entity.campaign.CreativeVariable.Type.IMAGE;
import static com.extole.model.entity.campaign.CreativeVariable.Type.TEXT;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.replaceEachRepeatedly;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.client.rest.campaign.migration.CampaignMigrationEndpoints;
import com.extole.client.rest.campaign.migration.CampaignMigrationRestException;
import com.extole.client.rest.campaign.migration.MigratedCreativeActionCreativeResponse;
import com.extole.client.rest.campaign.migration.MigratedCreativeResponse;
import com.extole.client.rest.campaign.migration.MigrationRequest;
import com.extole.client.rest.campaign.migration.MigrationResponse;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.common.lang.CaseInsensitiveSet;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.lock.LockAcquireException;
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
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluation.EvaluationException;
import com.extole.evaluation.EvaluationService;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentAsset;
import com.extole.model.entity.campaign.CampaignComponentReference;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionSchedule;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignControllerTriggerEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerEventType;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.ClientKeyFlowVariable;
import com.extole.model.entity.campaign.CreativeArchive;
import com.extole.model.entity.campaign.CreativeArchiveApiVersion;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.campaign.CreativeVariable;
import com.extole.model.entity.campaign.EnumListVariable;
import com.extole.model.entity.campaign.EnumVariable;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignController;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionSchedule;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerEvent;
import com.extole.model.entity.campaign.built.BuiltFrontendController;
import com.extole.model.service.campaign.BuiltCampaignService;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignVersionState;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.ComponentAbsoluteNameFinder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentSizeTooBigException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetDescriptionLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameLengthException;
import com.extole.model.service.campaign.component.asset.ComponentAssetService;
import com.extole.model.service.campaign.controller.CampaignControllerBuilder;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBuilder;
import com.extole.model.service.campaign.controller.trigger.event.CampaignControllerTriggerEventBuilder;
import com.extole.model.service.campaign.setting.ClientKeyFlowVariableBuilder;
import com.extole.model.service.campaign.setting.EnumVariableBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.creative.CreativeArchiveBuilder;
import com.extole.model.service.creative.CreativeArchiveService;
import com.extole.model.service.creative.CreativeVariableService;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.security.backend.BackendAuthorizationProvider;
import com.extole.util.file.FileTypeDetector;
import com.extole.util.file.MimeType;
import com.extole.util.file.MimeTypeException;

@Provider
public class CampaignMigrationEndpointsImpl implements CampaignMigrationEndpoints {

    private static final String EXPERT_TAG = "importance:expert";
    private static final Set<String> VARIABLE_NAMES_TO_BE_MIGRATED = CaseInsensitiveSet
        .create(ImmutableList.<String>builder()
            .add("triggerEvent.name")
            .add("triggerEvent.names")
            .add("triggerEventName")
            .add("triggerEventNames")
            .add("triggerEvent.quality")
            .add("triggerEventQuality")
            .add("triggerEvent.type")
            .add("triggerEventType")
            .add("journeyNames")
            .add("otherPersonJourneyNames")
            .add("recipientJourneyNames")
            .add("rewardName")
            .add("experienceTypes")
            .build());
    public static final String[] DEFAULT_SEARCH_LIST = {
        "{{$1000}}",
        "{{friend.name}}",
        "{{friend.firstName}}",
        "{{ friend.firstName }}",
        "{{FriendFirstName}}",
        "{{friend.reward}}",
        "{{advocateFirstName}}",
        "{{advocate_firstName}}",
        "{{AdvocateName}}",
        "{{Advocate.firstName}}",
        "{{Advocate.Name}}",
        "{{AdvocateName}}",
        "{{Advocate.Name}}",
        "{{advocateadvocate}}",
        "{{advcateReward}}",
        "{{share.data.source}}",
        "{{share.link}}",
        "{{ share.link }}",
        "{{ safeHtmlAttribute(share.link) }}",
        "{{# share.channel }}{{share.channel }}{{/ share.channel }}",
        "{{^ share.channel }}",
        "{{/ share.channel }}",
        "{{# share.data.source }}{{share.data.source }}{{/ share.data.source }}",
        "{{^ share.data.source }}",
        "{{/ share.data.source }}",
        "{{# share.data.friend_first_name}}{{share.data.friend_first_name}}{{/ share.data.friend_first_name}}" +
            "{{^ share.data.friend_first_name}}{{fallbackFriendFirstName}}{{/ share.data.friend_first_name}}",
        "{{shareable.code}}",
        "{{recipient.parameters.company_name}}",
        "{{recipient.parameters.advocate_company_name}}",
        "{{recipient.firstName}}",
        "handlebars@buildtime:handlebars@runtime:{[defaultPromoteUrl]}",
        "{{currentYear}}",
        "{{ currentYear }}",

        "{{friendRewardAmount}}",
        "{{friendAmount}}",

        "{{advocateName}}",
        "{{ advocateName }}",

        "{{defaultPromoteUrl}}",
        "{{ defaultPromoteUrl }}",
        "{{safeHtmlAttribute defaultPromoteUrl}}",
        "{{# advocate.firstName}}, {{advocate.firstName}} {{/ advocate.firstName}}",
        "{{# advocate.firstName}}, {{advocate.firstName}}{{/ advocate.firstName}}",
        "{{# advocate.firstName}}{{advocate.firstName}}{{/ advocate.firstName}}",
        "{{# advocate.firstName }}{{ advocate.firstName }}{{/ advocate.firstName }}",
        "{{# advocate.firstName }}, {{ advocate.firstName }} {{/ advocate.firstName }}",
        "{{# advocate.firstName }}, {{ advocate.firstName }}{{/ advocate.firstName }}",
        "{{# advocate.firstName }},  {{/ advocate.firstName}}",
        "{{# advocate.parameters.officerLink}}{{safeHtmlContent advocate.parameters.officerLink}}" +
            "{{/ advocate.parameters.officerLink}}",
        "{{advocate.firstName}}",
        "{{ advocate.firstName }}",
        "{{advocate.email}}",
        "{{^ advocate.firstName }}Your friend{{/ advocate.firstName }}",
        "{{advocate.data.description}}",
        "{{advocate.reward}}",
        "{{^ advocate.firstName}}Your Friend{{/ advocate.firstName}}",
        "{{^ advocate.firstName}}your friend{{/ advocate.firstName}}",
        "{{advocate.share.link}}",
        "{{advocate.name}}",
        "{{advocate.parameters.partner_user_id}}",
        "{{^ advocate.parameters.officerLink}}{[defaultPromoteUrl]}{{/ advocate.parameters.officerLink}}",
        "{{# isShowCouponCode }} also{{/ isShowCouponCode }}",
        "{{2022}}"
    };
    public static final String[] DEFAULT_REPLACEMENT_LIST = {
        "$1000",
        "{[friend.firstName]}",
        "{[friend.firstName]}",
        "{[friend.firstName]}",
        "{[friend.firstName]}",
        "{[friend.reward]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{{advocateReward}}",
        "{{advocateReward}}",
        "{[share.data.source]}",
        "{[share.link]}",
        "{[share.link]}",
        "{[safeHtmlAttribute share.link]}",
        "{[#share.channel]}{[share.channel]}{[/share.channel]}",
        "{[^share.channel]}",
        "{[/share.channel]}",
        "{[#share.data.source]}{[share.data.source]}{[/share.data.source]}",
        "{[^share.data.source]}",
        "{[/share.data.source]}",
        "{[#share.data.friend_first_name]}{[share.data.friend_first_name]}{[/share.data.friend_first_name]}" +
            "{[^share.data.friend_first_name]}{[fallbackFriendFirstName]}{[/share.data.friend_first_name]}",
        "{[shareable.code]}",
        "{[recipient.parameters.company_name]}",
        "{[recipient.parameters.advocate_company_name]}",
        "{[recipient.firstName]}",
        "handlebars@runtime:{[defaultPromoteUrl]}",
        "{[currentYear]}",
        "{[currentYear]}",

        "{{friendReward}}",
        "{{friendReward}}",

        "{[advocate.firstName]}",
        "{[advocate.firstName]}",

        "{[defaultPromoteUrl]}",
        "{[defaultPromoteUrl]}",
        "{[safeHtmlAttribute defaultPromoteUrl]}",
        "{[#advocate.firstName]}, {[advocate.firstName]} {[/advocate.firstName]}",
        "{[#advocate.firstName]}, {[advocate.firstName]}{[/advocate.firstName]}",
        "{[#advocate.firstName]}{[advocate.firstName]}{[/advocate.firstName]}",
        "{[#advocate.firstName]}{[advocate.firstName]}{[/advocate.firstName]}",
        "{[#advocate.firstName]}, {[advocate.firstName]} {[/advocate.firstName]}",
        "{[#advocate.firstName]}, {[advocate.firstName]}{[/advocate.firstName]}",
        "{[#advocate.firstName]},  {[/advocate.firstName]}",
        "{[#advocate.parameters.officerLink]}{[safeHtmlContent advocate.parameters.officerLink]}" +
            "{[/advocate.parameters.officerLink]}",
        "{[advocate.firstName]}",
        "{[advocate.firstName]}",
        "{[advocate.email]}",
        "{[^advocate.firstName]}Your friend{[/advocate.firstName]}",
        "{[advocate.data.description]}",
        "{[advocate.reward]}",
        "{[^advocate.firstName]}Your Friend{[/advocate.firstName]}",
        "{[^advocate.firstName]}your friend{[/advocate.firstName]}",
        "{[advocate.share.link]}",
        "{[advocate.name]}",
        "{[advocate.parameters.partner_user_id]}",
        "{[^advocate.parameters.officerLink]}{[defaultPromoteUrl]}{[/advocate.parameters.officerLink]}",
        "{{#if isShowCouponCode }} also{{/if}}",
        "2022"
    };
    public static final String CSV_FILE_LOCATION = "eng21748/ENG-21748.csv";

    private static final Logger LOG = LoggerFactory.getLogger(CampaignMigrationEndpointsImpl.class);

    private static final BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> EVENT_NAMES_AS_COMPONENT_NAME;
    private static final BuildtimeEvaluatable<ControllerBuildtimeContext,
        List<String>> EVENT_NAMES_AS_REWARD_ZONE_NAME_VARIABLE;
    private static final BuildtimeEvaluatable<ControllerBuildtimeContext,
        List<String>> EVENT_NAMES_AS_ZONE_NAME_VARIABLE;
    private static final BuildtimeEvaluatable<ControllerBuildtimeContext,
        List<String>> EVENT_NAMES_AS_JAVASCRIPT_COMPONENT_NAME;
    private static final BuildtimeEvaluatable<ControllerBuildtimeContext,
        List<String>> EVENT_NAMES_WITH_GIFT_CARD_CONFIGURATION_SUFFIX;

    private static final BuildtimeEvaluatable<ControllerBuildtimeContext, String> TRIGGER_NAME_AS_COMPONENT_NAME;
    private static final BuildtimeEvaluatable<CampaignBuildtimeContext,
        String> FRONTEND_CONTROLLER_NAME_AS_COMPONENT_NAME;

    private static final String TRANSLATABLE_TAG = "translatable";
    private static final String TRANSPARENT_RGBA = "RGBA(0,0,0,0.0)";
    private static final String DEFAULT_COLOR = "inherit";
    private static final FileTypeDetector FILE_TYPE_DETECTOR = new FileTypeDetector();
    private static final Pattern INVALID_HANDLEBARS_TEMPLATE_REGEX_PATTERN = Pattern
        .compile("\\{\\{([a-zA-Z0-9 ]*?)\\}\\}");
    private static final String[] HANDLEBARS_HELPERS = new String[] {
        "safeUriComponent", "safeJs", "safeJsAttribute", "safeJsBlock", "safeCssUrl", "safeCssString",
        "safeHtmlUnquotedAttribute", "safeHtmlAttribute", "safeHtmlContent", "safeHtml"};
    private static final String IMAGE_NAME_CHARACTERS_TO_BE_REPLACED = "[^^[0-9a-zA-Z_\\\\-]+$]";
    private static final String IMAGE_NAME_REPLACEMENT = "_";
    private static final String HIDDEN_TAG = "internal:hidden";
    private static final int DEFAULT_STARTING_PRIORITY = 100;
    private static final Set<String> V8_REMOVED_FILES =
        Set.of("/manifest.js", "/read-manifest.js", "/read-variables.js", "/write-variables.js");

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = ImmutableSet.<String>builder()
        .add("apng")
        .add("avif")
        .add("gif")
        .add("ico")
        .add("jpeg")
        .add("jpg")
        .add("jfif")
        .add("pjpeg")
        .add("pjp")
        .add("png")
        .add("svg")
        .add("webp")
        .build();
    private static final Map<String, String> ALLOWED_IMAGE_EXTENSIONS_BY_MIME_TYPES = ImmutableMap
        .<String, String>builder()
        .put("image/apng", "apng")
        .put("image/avif", "avif")
        .put("image/gif", "gif")
        .put("image/vnd.microsoft.icon", "ico")
        .put("image/jpeg", "jpeg")
        .put("image/jpg", "jpg")
        .put("image/jfif", "jfif")
        .put("image/pjpeg", "pjpeg")
        .put("image/pjp", "pjp")
        .put("image/png", "png")
        .put("image/svg+xml", "svg")
        .put("image/webp", "webp")
        .build();
    private static final LockDescription LOCK_DESCRIPTION = new LockDescription("migrating-archives-from-v7-to-v8");
    private static final Duration MAX_LOCK_CLOSURE_DURATION = Duration.ofMinutes(10);
    private static final Duration MAX_LOCK_ACQUIRE_DURATION = Duration.ZERO;
    private static final int THRESHOLD = 256 * 1024;
    private static final String TRANSPARENT_PIXEL_BASE64 =
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

    static {
        try {
            EVENT_NAMES_AS_COMPONENT_NAME =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue("\"spel@buildtime:{context.getVariableContext().get('component.name')}\"",
                        new TypeReference<>() {});
            EVENT_NAMES_AS_REWARD_ZONE_NAME_VARIABLE =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue("\"spel@buildtime:{context.getVariableContext().get('reward_zone_name')}\"",
                        new TypeReference<>() {});
            EVENT_NAMES_AS_ZONE_NAME_VARIABLE =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue("\"javascript@buildtime:[context.getVariableContext().get(\\\"zoneName\\\")]\"",
                        new TypeReference<>() {});
            EVENT_NAMES_AS_JAVASCRIPT_COMPONENT_NAME =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue("\"javascript@buildtime:[context.getComponent().getName()]\"",
                        new TypeReference<>() {});
            EVENT_NAMES_WITH_GIFT_CARD_CONFIGURATION_SUFFIX =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue(
                        "\"javascript@buildtime:"
                            + "[context.getComponent().getName() + \\\"_gift_card_configuration\\\"]\"",
                        new TypeReference<>() {});

            TRIGGER_NAME_AS_COMPONENT_NAME =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue("\"spel@buildtime:context.getVariableContext().get('component.name')\"",
                        new TypeReference<>() {});
            FRONTEND_CONTROLLER_NAME_AS_COMPONENT_NAME =
                ObjectMapperProvider.getConfiguredInstance()
                    .readValue("\"spel@buildtime:context.getVariableContext().get('component.name') + '_rendered'\"",
                        new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private final KeyCaseInsensitiveMap<NewVariableDefinition> campaignVariables = KeyCaseInsensitiveMap.create();
    private static final Predicate<String> COLOR_HEX_WITHOUT_HASHTAG = Pattern.compile("^(([A-Fa-f0-9]{2}){3,4})$")
        .asMatchPredicate();

    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final CampaignService campaignService;
    private final BuiltCampaignService builtCampaignService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CreativeArchiveService creativeArchiveService;
    private final CreativeVariableService creativeVariableService;
    private final LockService lockService;
    private final ComponentAssetService componentAssetService;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final ComponentAbsoluteNameFinder componentAbsoluteNameFinder;
    private final EvaluationService evaluationService;

    private final List<MigratedCreativeResponse> creativeResponses = Lists.newArrayList();
    private final Map<String, Variable> rootVariablesBeforeMigration = KeyCaseInsensitiveMap.create();
    private final Map<String, Map<String, Variable>> rootVariables = KeyCaseInsensitiveMap.create();
    private final Set<String> conflictingRootVariables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, String> fixedBrokenColors = KeyCaseInsensitiveMap.create();
    private final Map<String, Map<String, CampaignComponentAsset>> rootAssets = KeyCaseInsensitiveMap.create();
    private final Map<String, CampaignComponentAsset> existingRootAssetsByName = Maps.newHashMap();
    private final Map<String, Map<String, ByteSource>> rootAssetsContent = KeyCaseInsensitiveMap.create();
    private final Map<Id<CampaignComponent>, Set<String>> campaignAbsoluteNames = new HashMap<>();
    private Campaign campaign;
    private BuiltCampaign builtCampaign;
    private CampaignComponent root;
    private CampaignBuilder campaignBuilder;
    private CampaignComponentBuilder rootBuilder;
    private Authorization authorization;
    private boolean dryRun;

    @Inject
    public CampaignMigrationEndpointsImpl(BackendAuthorizationProvider backendAuthorizationProvider,
        CampaignService campaignService,
        BuiltCampaignService builtCampaignService,
        ClientAuthorizationProvider authorizationProvider,
        CreativeArchiveService creativeArchiveService,
        CreativeVariableService creativeVariableService, LockService lockService,
        ComponentAssetService componentAssetService,
        CampaignComponentSettingRestMapper settingRestMapper,
        ComponentAbsoluteNameFinder componentAbsoluteNameFinder,
        EvaluationService evaluationService) throws IOException {
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.campaignService = campaignService;
        this.builtCampaignService = builtCampaignService;
        this.authorizationProvider = authorizationProvider;
        this.creativeArchiveService = creativeArchiveService;
        this.creativeVariableService = creativeVariableService;
        this.lockService = lockService;
        this.componentAssetService = componentAssetService;
        this.settingRestMapper = settingRestMapper;
        this.componentAbsoluteNameFinder = componentAbsoluteNameFinder;
        this.evaluationService = evaluationService;
        try (InputStream csv = new ClassPathResource(CSV_FILE_LOCATION).getInputStream()) {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema columns = csvMapper.schemaFor(NewVariableDefinition.class).withHeader();
            ObjectReader reader = csvMapper.reader(columns).forType(NewVariableDefinition.class);
            List<NewVariableDefinition> newVariableDefinitions =
                reader.<NewVariableDefinition>readValues(csv).readAll();
            newVariableDefinitions
                .forEach(definition -> campaignVariables.put(definition.getCurrentVariableName(),
                    definition));
        }
    }

    @Override
    public MigrationResponse migrateCreativesV7ToV8(String accessToken, String campaignId,
        Optional<com.extole.client.rest.campaign.component.setting.VariableSource> rootComponentSource,
        Optional<MigrationRequest> request, boolean dryRun, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignMigrationRestException, CampaignRestException {
        this.dryRun = dryRun;

        authorization = validateAndGetAuthorization(accessToken);

        LockClosure<MigrationResponse> migrationClosure = initializeMigrationClosure(campaignId,
            rootComponentSource.map(source -> VariableSource.valueOf(source.name())), request);

        try {
            campaign = campaignService.getCampaignByIdAndVersionState(authorization, Id.valueOf(campaignId),
                CampaignVersionState.LATEST);
            componentAbsoluteNameFinder.findAllAbsoluteNamesById(campaign.getComponents())
                .forEach((key, value) -> campaignAbsoluteNames.put(key, new LinkedHashSet<>(value)));
            return lockService.executeWithinLock(new LockKey("update-campaign", Id.valueOf(campaignId)),
                LOCK_DESCRIPTION, migrationClosure, MAX_LOCK_CLOSURE_DURATION, MAX_LOCK_ACQUIRE_DURATION);
        } catch (LockAcquireException e) {
            throw RestExceptionBuilder.newBuilder(CampaignMigrationRestException.class)
                .withErrorCode(CampaignMigrationRestException.MIGRATION_WAS_ALREADY_STARTED)
                .addParameter("campaign_id", campaignId)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (LockClosureException e) {
            remapAndThrowExceptionIfNecessary(e);
            throw RestExceptionBuilder.newBuilder(CampaignMigrationRestException.class)
                .withErrorCode(CampaignMigrationRestException.MIGRATION_FAILED)
                .addParameter("client_id", campaign.getClientId())
                .addParameter("campaign_id", campaign.getId())
                .addParameter("stacktrace", ExceptionUtils.getStackTrace(e))
                .withCause(e)
                .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            remapAndThrowExceptionIfNecessary(e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void remapAndThrowExceptionIfNecessary(Exception e) throws CampaignMigrationRestException {
        if (e.getCause() instanceof CampaignMigrationRestException) {
            throw (CampaignMigrationRestException) e.getCause();
        }
    }

    private Authorization validateAndGetAuthorization(String accessToken) throws UserAuthorizationRestException {
        Authorization superuserAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!superuserAuthorization.isAuthorized(superuserAuthorization.getClientId(),
            Authorization.Scope.CLIENT_ADMIN)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }

        return superuserAuthorization;
    }

    private LockClosure<MigrationResponse> initializeMigrationClosure(String campaignId,
        Optional<VariableSource> rootSourceOverride,
        Optional<MigrationRequest> request) {
        return () -> {
            try {
                if (!campaign.getClientId().equals(authorization.getClientId())) {
                    authorization = backendAuthorizationProvider.getAuthorizationForBackend(campaign.getClientId());
                }

                builtCampaign = builtCampaignService.buildCampaign(campaign);
                root = lookupRoot(campaign.getComponents());

                campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId))
                    .withExpectedVersion(campaign.getVersion());
                rootBuilder = campaignBuilder.updateComponent(root);

                getVariables(root.getSettings()).forEach(variable -> {
                    rootVariables.put(variable.getName(), Maps.newHashMap(Map.of("root", variable)));
                    rootVariablesBeforeMigration.put(variable.getName(), variable);
                });
                root.getAssets().forEach(asset -> {
                    KeyCaseInsensitiveMap<CampaignComponentAsset> existing = KeyCaseInsensitiveMap.create();
                    existing.put("asset-" + asset.getId(), asset);
                    rootAssets.put(asset.getName(), existing);
                    existingRootAssetsByName.put(asset.getName(), asset);
                });

                for (CampaignComponentAsset asset : root.getAssets()) {
                    ByteSource content = componentAssetService
                        .get(authorization, asset.getId(), campaign.getVersion()).getContent();
                    KeyCaseInsensitiveMap<ByteSource> existing = KeyCaseInsensitiveMap.create();
                    existing.put("asset-" + asset.getId(), content);
                    rootAssetsContent.put(asset.getName(), existing);
                }

                Set<JourneyName> journeyNamesBasedOnFrontendControllers =
                    builtCampaign.getFrontendControllers().stream()
                        .map(BuiltFrontendController.class::cast)
                        .flatMap(value -> value.getJourneyNames().stream())
                        .collect(Collectors.toUnmodifiableSet());
                Set<JourneyName> journeyNames = ImmutableSet.<JourneyName>builder()
                    .addAll(journeyNamesBasedOnFrontendControllers)
                    .build();

                for (JourneyName journeyName : journeyNames) {
                    String name = journeyName.getValue().toLowerCase();
                    if (campaignAbsoluteNames.values()
                        .stream()
                        .noneMatch(absoluteNames -> absoluteNames.stream().findFirst().get().toLowerCase()
                            .equals(ROOT_REFERENCE + name))) {
                        campaignBuilder.addComponent().withName(name)
                            .withTags(Set.of("internal:journey"));
                    }
                }
                Map<String, String> valueOverrides = request
                    .map(migrationRequest -> migrationRequest.getValueOverrides().orElse(Map.of()))
                    .orElse(Map.of());

                FrontendController[] frontendControllers = campaign.getFrontendControllers().stream()
                    .toArray(FrontendController[]::new);
                Map<Id<com.extole.model.entity.campaign.CampaignStep>,
                    BuiltFrontendController> builtFrontendControllersById =
                        builtCampaign.getFrontendControllers().stream()
                            .map(BuiltFrontendController.class::cast)
                            .collect(Collectors.toUnmodifiableMap(value -> value.getId(), Function.identity()));
                for (FrontendController frontendController : frontendControllers) {
                    migrateArchiveAndVariablesIfNecessary(frontendController,
                        builtFrontendControllersById.get(frontendController.getId()),
                        valueOverrides);
                }

                saveRootVariablesWithMostCommonValues(rootSourceOverride);
                saveRootAssetsWithMostCommonAttributes();

                campaignBuilder.withMessage("Migrated creatives from V7 to V8");
                if (request.isPresent()) {
                    request.get().getPublish().ifPresent(shouldPublish -> {
                        if (shouldPublish.booleanValue()) {
                            campaignBuilder.withPublished();
                        }
                    });
                    request.get().getMessage().ifPresent(historyMessage -> {
                        historyMessage.ifPresent(value -> campaignBuilder.withMessage(value));
                    });
                }
                if (dryRun) {
                    campaignBuilder.dryRun();
                } else {
                    campaignBuilder.save();
                }
            } catch (Exception e) {
                throw new LockClosureException(e);
            }

            Map<String, Map<String, CampaignComponentVariableResponse>> forcefulMergedRootVariables = Maps.newHashMap();

            for (String conflictingRootVariable : conflictingRootVariables) {
                Map<String, CampaignComponentVariableResponse> mapperVariables = Maps.newHashMap();
                for (Map.Entry<String, Variable> entry : rootVariables.get(conflictingRootVariable).entrySet()) {
                    mapperVariables.put(entry.getKey(),
                        (CampaignComponentVariableResponse) settingRestMapper.toSettingResponse(entry.getValue()));
                }
                forcefulMergedRootVariables.put(conflictingRootVariable, mapperVariables);
            }
            return new MigrationResponse(forcefulMergedRootVariables, fixedBrokenColors, creativeResponses);
        };
    }

    private void saveRootVariablesWithMostCommonValues(Optional<VariableSource> rootSourceOverride)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, SettingTagLengthException, VariableValueKeyLengthException {
        KeyCaseInsensitiveMap<Variable> globalRootVariables = getGlobalRootVariables();
        for (Map.Entry<String, Map<String, Variable>> rootVariableEntry : rootVariables.entrySet()) {
            String variableName = rootVariableEntry.getKey();
            Optional<Variable> existingRootVariable =
                Optional.ofNullable(rootVariablesBeforeMigration.get(rootVariableEntry.getKey()));
            VariableBuilder rootVariableBuilder = existingRootVariable
                .map(value -> rootBuilder.<VariableBuilder>updateSetting(value))
                .orElseGet(() -> (VariableBuilder) rootBuilder.addSetting());
            rootVariableBuilder.withName(variableName);

            Variable rootVariable = rootVariableEntry.getValue().values().iterator().next();
            Optional<String> displayName = rootVariable.getDisplayName();
            SettingType settingType = rootVariable.getType();
            if (displayName.isPresent()) {
                rootVariableBuilder.withDisplayName(displayName.get());
            }
            rootVariableBuilder.clearDescription();
            rootVariableBuilder.withType(settingType);
            Set<String> mergedTags = rootVariableEntry.getValue().values().stream()
                .flatMap(variable -> variable.getTags().stream().map(tag -> tag.toLowerCase()))
                .collect(Collectors.toSet());
            if (VARIABLE_NAMES_TO_BE_MIGRATED.contains(variableName)) {
                mergedTags.add(EXPERT_TAG);
            }
            rootVariableBuilder.withTags(mergedTags);

            Set<String> allLocales = rootVariableEntry.getValue().values()
                .stream()
                .flatMap(variable -> variable.getValues().keySet().stream())
                .map(locale -> {
                    if (mergedTags.contains(TRANSLATABLE_TAG) && locale.equalsIgnoreCase("default")) {
                        return "en";
                    }
                    return locale;
                })
                .collect(Collectors.toUnmodifiableSet());

            Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>> mergedValues = Maps.newHashMap();
            for (String locale : allLocales) {
                BuildtimeEvaluatable<VariableBuildtimeContext,
                    RuntimeEvaluatable<Object, Optional<Object>>> mostCommon =
                        mostCommon(rootVariableEntry.getValue()
                            .values()
                            .stream()
                            .map(variable -> variable.getValues().get(locale))
                            .collect(Collectors.toList()));
                mergedValues.put(locale, mostCommon);
            }
            rootVariableBuilder.withValues(mergedValues);
            VariableSource rootVariableSource = computeSource(rootSourceOverride, globalRootVariables, variableName,
                existingRootVariable, mergedValues);
            rootVariableBuilder.withSource(rootVariableSource);

            if (campaignVariables.containsKey(variableName)) {
                applyEng21748NewDefinition(campaignVariables.get(variableName),
                    rootVariableBuilder,
                    mergedTags,
                    mergedValues.containsKey("default"));
            }
            if (rootVariableEntry.getValue().values().stream()
                .anyMatch(variable -> !variable.getTags().equals(mergedTags))) {
                conflictingRootVariables.add(rootVariableEntry.getKey());
            }

            if (rootVariableEntry.getValue().values().stream()
                .anyMatch(variable -> !variable.getValues().equals(mergedValues))) {
                conflictingRootVariables.add(rootVariableEntry.getKey());
            }
        }
    }

    private static VariableSource computeSource(Optional<VariableSource> rootSourceOverride,
        KeyCaseInsensitiveMap<Variable> globalRootVariables, String variableName,
        Optional<Variable> existingRootVariable,
        Map<String, ?> mergedValues) {

        if (!globalRootVariables.containsKey(variableName)) {
            return VariableSource.LOCAL;
        }

        if (rootSourceOverride.isPresent()) {
            return rootSourceOverride.get();
        }

        if (existingRootVariable.isPresent() && existingRootVariable.get().getSource() == VariableSource.INHERITED) {
            return VariableSource.INHERITED;
        }

        Map<String, ?> inheritableValues = globalRootVariables.get(variableName).getValues();
        for (Map.Entry<String, ?> entry : mergedValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!inheritableValues.containsKey(key) || !value.equals(inheritableValues.get(key))) {
                return VariableSource.LOCAL;
            }
        }

        return VariableSource.INHERITED;
    }

    private KeyCaseInsensitiveMap<Variable> getGlobalRootVariables() {
        KeyCaseInsensitiveMap<Variable> globalRootVariables = KeyCaseInsensitiveMap.create();
        CampaignComponent rootComponent = lookupRoot(campaignService.getGlobal(authorization).getComponents());
        getVariables(rootComponent.getSettings())
            .forEach(variable -> globalRootVariables.put(variable.getName(), variable));
        return globalRootVariables;
    }

    private void saveRootAssetsWithMostCommonAttributes()
        throws CampaignComponentAssetNameLengthException, CampaignComponentAssetNameInvalidException,
        CampaignComponentAssetFilenameLengthException, CampaignComponentAssetFilenameInvalidException,
        CampaignComponentAssetContentMissingException, CampaignComponentAssetContentSizeTooBigException {
        for (Map.Entry<String, Map<String, CampaignComponentAsset>> entry : rootAssets.entrySet()) {
            Collection<CampaignComponentAsset> values = entry.getValue().values();
            String mostCommonFilename = mostCommon(values.stream().map(asset -> asset.getFilename())
                .collect(Collectors.toList()));
            Optional<String> mostCommonDescription = mostCommon(values.stream().map(asset -> asset.getDescription())
                .collect(Collectors.toList()));
            Collection<ByteSource> byteSources = rootAssetsContent.get(entry.getKey()).values();
            ByteSource mostCommonContent;
            if (byteSources.isEmpty() || byteSources.stream().allMatch(byteSource -> byteSource == null)) {
                mostCommonContent = ByteSource.wrap(Base64.decodeBase64(TRANSPARENT_PIXEL_BASE64));
            } else {
                mostCommonContent = mostCommon(byteSources);
            }

            CampaignComponentAssetBuilder rootAssetBuilder;
            CampaignComponentAsset campaignComponentAsset = existingRootAssetsByName.get(entry.getKey());

            if (campaignComponentAsset == null) {
                rootAssetBuilder = rootBuilder.addAsset();
            } else {
                rootAssetBuilder = rootBuilder.updateAsset(campaignComponentAsset);
            }

            rootAssetBuilder.withName(entry.getKey());
            rootAssetBuilder.withFilename(mostCommonFilename);
            mostCommonDescription.ifPresent(description -> {
                try {
                    rootAssetBuilder.withDescription(description);
                } catch (CampaignComponentAssetDescriptionLengthException e) {
                    throw new RuntimeException(e);
                }
            });
            rootAssetBuilder.withContent(mostCommonContent);
        }
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

    @SuppressWarnings("checkstyle:methodLength")
    private void migrateArchiveAndVariablesIfNecessary(FrontendController frontendController,
        BuiltFrontendController builtFrontendController, Map<String, String> valueOverrides) throws Exception {

        BuiltCampaignControllerTriggerEvent builtEventTrigger =
            lookupForInputEventTriggerWithNonEmptyEventNames(builtFrontendController)
                .orElse(null);
        if (builtEventTrigger == null) {
            return;
        }
        String eventName = builtEventTrigger.getEventNames().get(0);
        CampaignControllerTriggerEvent eventTrigger = frontendController.getTriggers()
            .stream().filter(value -> value.getId().equals(builtEventTrigger.getId()))
            .map(CampaignControllerTriggerEvent.class::cast)
            .findFirst()
            .orElseThrow();

        CampaignControllerActionCreative[] creativeActions = frontendController.getActions().stream()
            .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
            .map(value -> (CampaignControllerActionCreative) value)
            .toArray(CampaignControllerActionCreative[]::new);

        for (int i = 0; i < creativeActions.length; i++) {
            CampaignControllerActionCreative actionCreative = creativeActions[i];

            if (actionCreative.getCreativeArchiveId().isEmpty()) {
                continue;
            }

            CreativeArchiveId creativeArchiveId = actionCreative.getCreativeArchiveId().get();
            CreativeArchive oldCreativeArchive =
                creativeArchiveService.getCreativeArchive(authorization, creativeArchiveId);

            if (oldCreativeArchive.getApiVersion() != CreativeArchiveApiVersion.SEVEN) {
                continue;
            }
            Set<String> uniqueVariableNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            JourneyName journeyName = builtFrontendController.getJourneyNames()
                .stream()
                .sorted(Comparator.comparing(JourneyName::getValue))
                .findFirst()
                .get();
            String journeyParentAbsoluteName = ROOT_REFERENCE + journeyName.getValue() + PATH_DELIMITER;

            Id<CampaignComponent> reusedComponentId = null;
            String componentAbsoluteName;
            String componentName;
            CampaignComponentBuilder componentBuilder;
            List<CampaignComponentReference> actionReferences = actionCreative.getCampaignComponentReferences();

            FrontendControllerBuilder frontendControllerBuilder = campaignBuilder
                .updateFrontendController(frontendController);
            CampaignControllerActionCreativeBuilder actionCreativeBuilder =
                frontendControllerBuilder.updateAction(actionCreative);
            CampaignControllerTriggerEventBuilder triggerBuilder =
                frontendControllerBuilder.updateTrigger(eventTrigger);
            if (actionReferences.size() == 1) {
                CampaignComponent existingComponent = campaign.getComponents()
                    .stream()
                    .filter(candidate -> candidate.getId().equals(actionReferences.get(0).getComponentId()))
                    .findFirst()
                    .get();

                if (eventTrigger.getEventNames().equals(EVENT_NAMES_AS_COMPONENT_NAME)
                    || eventTrigger.getEventNames().equals(EVENT_NAMES_AS_REWARD_ZONE_NAME_VARIABLE)
                    || eventTrigger.getEventNames().equals(EVENT_NAMES_AS_ZONE_NAME_VARIABLE)
                    || eventTrigger.getEventNames().equals(EVENT_NAMES_AS_JAVASCRIPT_COMPONENT_NAME)
                    || eventTrigger.getEventNames().equals(EVENT_NAMES_WITH_GIFT_CARD_CONFIGURATION_SUFFIX)) {
                    componentName = convertZoneNameToComponentName(existingComponent.getName());
                    reusedComponentId = existingComponent.getId();
                    componentAbsoluteName = journeyParentAbsoluteName + componentName;
                    componentBuilder = campaignBuilder.updateComponent(existingComponent);
                } else if (eventName.equals(existingComponent.getName())) {
                    componentName = convertZoneNameToComponentName(existingComponent.getName());
                    reusedComponentId = existingComponent.getId();
                    componentAbsoluteName = journeyParentAbsoluteName + componentName;
                    componentBuilder = campaignBuilder.updateComponent(existingComponent);
                    triggerBuilder.withEventNames(EVENT_NAMES_AS_COMPONENT_NAME);
                    triggerBuilder.withName(TRIGGER_NAME_AS_COMPONENT_NAME);
                    frontendControllerBuilder.withName(FRONTEND_CONTROLLER_NAME_AS_COMPONENT_NAME);
                } else {
                    componentName = convertZoneNameToComponentName(eventName);
                    componentAbsoluteName = journeyParentAbsoluteName + componentName;
                    componentBuilder = campaignBuilder.addComponent().withName(componentName);
                    triggerBuilder.withName(TRIGGER_NAME_AS_COMPONENT_NAME);
                    frontendControllerBuilder.withName(FRONTEND_CONTROLLER_NAME_AS_COMPONENT_NAME);
                    triggerBuilder.withEventNames(EVENT_NAMES_AS_COMPONENT_NAME);
                    triggerBuilder.clearComponentReferences();
                    triggerBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                    actionCreativeBuilder.clearComponentReferences();
                    actionCreativeBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                    frontendControllerBuilder.clearComponentReferences();
                    frontendControllerBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                    if (existingComponent != root) {
                        componentBuilder.addComponentReference(existingComponent.getId());
                        for (Variable existingVariable : getVariables(existingComponent.getSettings())) {
                            if (!uniqueVariableNames.add(existingVariable.getName())) {
                                continue;
                            }
                            addExistingComponentVariableToNewComponent(componentBuilder, existingVariable,
                                componentName);
                        }
                    }
                }

                Set<String> absoluteNames =
                    Objects.requireNonNullElseGet(campaignAbsoluteNames.get(existingComponent.getId()),
                        () -> {
                            Set<String> list = new LinkedHashSet<>();
                            campaignAbsoluteNames.put(existingComponent.getId(), list);
                            return list;
                        });
                String journeyBasedAbsoluteName =
                    ROOT_REFERENCE + journeyName.getValue() + PATH_DELIMITER + componentName;
                if (absoluteNames.stream()
                    .noneMatch(absoluteName -> absoluteName.equalsIgnoreCase(journeyBasedAbsoluteName))) {

                    componentBuilder.addComponentReferenceByAbsoluteName(ROOT_REFERENCE + journeyName.getValue());
                    absoluteNames.add(ROOT_REFERENCE + journeyName.getValue());
                }
            } else {
                componentName = convertZoneNameToComponentName(eventName);
                componentAbsoluteName = journeyParentAbsoluteName + componentName;
                componentBuilder = campaignBuilder.addComponent().withName(componentName);
                for (CampaignComponentReference reference : actionReferences) {
                    componentBuilder.addComponentReference(reference.getComponentId());

                    CampaignComponent existingComponent = campaign.getComponents()
                        .stream()
                        .filter(candidate -> candidate.getId().equals(reference.getComponentId()))
                        .findFirst()
                        .get();
                    for (Variable existingVariable : getVariables(existingComponent.getSettings())) {
                        if (!uniqueVariableNames.add(existingVariable.getName())) {
                            continue;
                        }
                        addExistingComponentVariableToNewComponent(componentBuilder, existingVariable, componentName);
                    }
                }

                triggerBuilder.withName(TRIGGER_NAME_AS_COMPONENT_NAME);
                frontendControllerBuilder.withName(FRONTEND_CONTROLLER_NAME_AS_COMPONENT_NAME);
                triggerBuilder.withEventNames(EVENT_NAMES_AS_COMPONENT_NAME);
                triggerBuilder.clearComponentReferences();
                triggerBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                actionCreativeBuilder.clearComponentReferences();
                actionCreativeBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                frontendControllerBuilder.clearComponentReferences();
                frontendControllerBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                componentBuilder.addComponentReferenceByAbsoluteName(ROOT_REFERENCE + journeyName.getValue());
            }

            ProcessedZip processedZip = writeNewArchiveZipAndExtractImages(oldCreativeArchive,
                () -> actionCreativeBuilder.getCreativeArchive().get(),
                authorization);
            actionCreativeBuilder.withCreativeArchiveApiVersion(CreativeArchiveApiVersion.EIGHT);
            Map<String, ByteSource> images = processedZip.getImages();
            Set<String> localMigratedAssets = new HashSet<>();
            List<String> dedupedLegacyVariables = Lists.newArrayList();

            List<CreativeVariable> orderedCreativeVariables =
                creativeVariableService.getCreativeVariables(campaign, oldCreativeArchive.getCreativeArchiveId());

            Map<String, CreativeVariable> creativeVariables = orderedCreativeVariables.stream()
                .collect(Collectors.toMap(creativeVariable -> creativeVariable.getName(), Function.identity(),
                    (first, last) -> {
                        dedupedLegacyVariables.add(first.getName());
                        return last;
                    }));
            creativeVariables = fixMissReferencedVariables(creativeVariables);
            creativeVariables = fixCorruptedImageVariables(creativeVariables);
            creativeVariables = migrateBrokenHandlebarVariableReferences(creativeVariables);
            creativeVariables = fixHandlebarsSafeHtmlMethodsToClearHtmlHelpers(creativeVariables);
            Set<String> rendertimeVariableNames = processedZip.getRendertimeVariableNames();
            creativeVariables = changeToRendertimeBracketsIfNeeded(creativeVariables, rendertimeVariableNames);
            creativeVariables =
                fixIndividualVariableCasesIfNeeded(creativeVariables, "facebookImageUrl", "brandImageUrl",
                    Map.of("en", "https://my.extole.com/default.png"), IMAGE);
            creativeVariables = fixIndividualVariableCasesIfNeeded(creativeVariables, "facebookTitle", "brandTitle",
                Map.of("en", "https://my.extole.com/default.png"), IMAGE);
            creativeVariables =
                fixIndividualVariableCasesIfNeeded(creativeVariables, "successHeadline", "shareLinkColor",
                    Map.of("en", "#E3721F"), COLOR);

            int priority = DEFAULT_STARTING_PRIORITY;
            for (CreativeVariable unfixedVariable : ListUtils.union(orderedCreativeVariables,
                Lists.newArrayList(creativeVariables.values()))) {
                CreativeVariable oldVariable = creativeVariables.get(unfixedVariable.getName());
                String variableName = oldVariable.getName();
                SettingType settingType = mapType(oldVariable.getType());
                if (!uniqueVariableNames.add(variableName)) {
                    continue;
                }
                String variablePath = createVariablePath(frontendController.getId().getValue(),
                    actionCreative.getId().getValue(), creativeArchiveId, variableName);

                VariableBuilder localVariableBuilder = getVariableBuilder(componentBuilder, variableName, settingType);
                localVariableBuilder.withPriority(DeweyDecimal.valueOf(String.valueOf(priority)));
                priority += 1;

                Optional<String> scheduleName = processedZip.getScheduleName(variableName);
                Optional<String> variableDisplayName;
                String label = oldVariable.getLabel();
                if (StringUtils.isNoneEmpty(label) && !StringUtils
                    .equalsIgnoreCase(addSpacesAndCapitalize(oldVariable.getName()), label)) {
                    if (scheduleName.isPresent()) {
                        label = label.replaceAll("Day", "Delay");
                        label = label.replaceAll("day", "delay");
                    }
                    variableDisplayName = Optional.of(label);
                } else {
                    variableDisplayName = Optional.empty();
                }
                VariableSource variableSource = mapSource(oldVariable);
                Set<String> variableTags = mapTags(oldVariable);
                Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                    RuntimeEvaluatable<Object, Optional<Object>>>> values =
                        mapValues(variablePath, oldVariable, creativeVariables.values(), valueOverrides);

                localVariableBuilder.withName(variableName);
                if (variableDisplayName.isPresent()) {
                    localVariableBuilder.withDisplayName(variableDisplayName.get());
                }
                localVariableBuilder.clearDescription();
                localVariableBuilder.withType(settingType);
                localVariableBuilder.withTags(variableTags);
                localVariableBuilder.withSource(variableSource);
                localVariableBuilder.withValues(values);
                if (campaignVariables.containsKey(variableName)) {
                    applyEng21748NewDefinition(campaignVariables.get(variableName),
                        localVariableBuilder,
                        variableTags,
                        values.containsKey("default"));
                }
                if (scheduleName.isPresent()) {
                    for (CampaignController controller : campaign.getControllers()) {
                        for (CampaignControllerAction action : controller.getActions()) {
                            if (action.getType() == CampaignControllerActionType.SCHEDULE) {
                                CampaignControllerActionSchedule scheduleAction =
                                    (CampaignControllerActionSchedule) action;
                                if (scheduleAction.getScheduleName() instanceof Provided
                                    && ((Provided<?, String>) scheduleAction.getScheduleName()).getValue()
                                        .equals(scheduleName.get())) {

                                    CampaignControllerBuilder controllerBuilder =
                                        campaignBuilder.updateController(controller);
                                    CampaignControllerActionScheduleBuilder actionBuilder =
                                        controllerBuilder.updateAction(scheduleAction);
                                    if (controller.getCampaignComponentReferences().size() == 1 && controller
                                        .getCampaignComponentReferences().get(0).getComponentId()
                                        .equals(root.getId())) {
                                        controllerBuilder.clearComponentReferences();
                                    }
                                    if (action.getCampaignComponentReferences().size() == 1 && action
                                        .getCampaignComponentReferences().get(0).getComponentId()
                                        .equals(root.getId())) {
                                        actionBuilder.clearComponentReferences();
                                    }

                                    Id<CampaignComponent> finalComponentId = reusedComponentId;
                                    if (action.getCampaignComponentReferences()
                                        .stream()
                                        .noneMatch(reference -> reference.getComponentId().equals(finalComponentId))) {
                                        actionBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                                    }

                                    if (controller.getCampaignComponentReferences()
                                        .stream()
                                        .noneMatch(reference -> reference.getComponentId().equals(finalComponentId))) {
                                        controllerBuilder.addComponentReferenceByAbsoluteName(componentAbsoluteName);
                                    }

                                    BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled =
                                        ObjectMapperProvider.getConfiguredInstance()
                                            .readValue("\"javascript@buildtime:context.getVariableContext().get('"
                                                + variableName + "').size() != 0\"",
                                                new TypeReference<>() {
                                                    @Override
                                                    public Type getType() {
                                                        return super.getType();
                                                    }
                                                });
                                    BuiltCampaignController builtController = builtCampaign.getControllers()
                                        .stream()
                                        .filter(builtCampaignController -> builtCampaignController.getId()
                                            .equals(controller.getId()))
                                        .findFirst().get();
                                    BuiltCampaignControllerActionSchedule builtAction =
                                        builtController.getActions().stream()
                                            .filter(candidate -> candidate.getId().equals(action.getId()))
                                            .findFirst()
                                            .map(candidate -> ((BuiltCampaignControllerActionSchedule) candidate))
                                            .get();

                                    List<String> scheduleDelays = builtAction.getScheduleDelays()
                                        .stream()
                                        .map(Duration::toString)
                                        .collect(Collectors.toUnmodifiableList());
                                    try {
                                        values.clear();
                                        values.put("default", Provided.nestedOptionalOf(scheduleDelays));
                                        settingType = SettingType.DELAY_LIST;
                                        localVariableBuilder.withValues(values);
                                        localVariableBuilder.withType(settingType);
                                        if (scheduleDelays.isEmpty() && !builtAction.getEnabled().booleanValue()) {
                                            actionBuilder.withEnabled(enabled);
                                        }
                                    } catch (VariableValueKeyLengthException e) {
                                        throw new RuntimeException(e);
                                    }
                                    actionBuilder
                                        .withScheduleDelays(ObjectMapperProvider.getConfiguredInstance()
                                            .readValue("\"javascript@buildtime:context.getVariableContext().get('"
                                                + variableName + "')\"",
                                                new TypeReference<>() {
                                                    @Override
                                                    public Type getType() {
                                                        return super.getType();
                                                    }
                                                }));
                                }
                            }
                        }
                    }
                }

                if (oldVariable.getType() == IMAGE && variableSource == VariableSource.LOCAL) {
                    Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                        RuntimeEvaluatable<Object, Optional<Object>>>> filteredValues =
                            new HashMap<>(values);
                    for (Map.Entry<String, String> entry : oldVariable.getValues().entrySet()) {
                        String value = entry.getValue();
                        String valueLocale = entry.getKey();
                        if (StringUtils.isNotBlank(value) && value.contains("/")) {
                            String originalFilename = StringUtils.substringAfterLast(value, "/");
                            if (!value.startsWith("https://") && localMigratedAssets.add(value)) {
                                ByteSource content = images.get(originalFilename);
                                addImageAsset(content, originalFilename, eventName, componentBuilder, valueLocale,
                                    filteredValues);
                            } else if (value.startsWith("https://")
                                && ALLOWED_IMAGE_EXTENSIONS.stream().noneMatch(value::endsWith)
                                && localMigratedAssets.add(value)) {

                                ByteSource content = fetchImage(value).map(bytes -> ByteSource.wrap(bytes))
                                    .orElseThrow();
                                addImageAsset(content, originalFilename, eventName, componentBuilder, valueLocale,
                                    filteredValues);
                            }
                        }
                    }
                    localVariableBuilder.withValues(unmodifiableMap(filteredValues));
                }
                if (variableSource == VariableSource.INHERITED) {
                    SettingType finalSettingType = settingType;
                    rootVariables.compute(variableName, (key, variables) -> {
                        if (variables == null) {
                            variables = KeyCaseInsensitiveMap.create();
                        }
                        variables.put(variablePath, createDummyVariable(variableName, variableDisplayName,
                            variableSource, variableTags, finalSettingType, values));
                        return variables;
                    });
                    if (oldVariable.getType() == IMAGE) {
                        localVariableBuilder.withValues(values.entrySet().stream()
                            .collect(
                                Collectors.toMap(entry -> entry.getKey(), entry -> Provided.nestedOptionalOf(null))));
                        for (String value : getInternalArchivePaths(oldVariable.getValues())) {
                            String originalFilename = StringUtils.substringAfterLast(value, "/");
                            ByteSource content = images.get(originalFilename);
                            addRootAsset(originalFilename, content, eventName, variablePath);
                        }
                        for (String value : getUrlLinks(oldVariable.getValues())
                            .stream()
                            .filter(
                                value -> !ALLOWED_IMAGE_EXTENSIONS.contains(StringUtils.substringAfterLast(value, ".")))
                            .collect(Collectors.toUnmodifiableList())) {

                            String originalFilename = StringUtils.substringAfterLast(value, "/");
                            ByteSource content = fetchImage(value)
                                .map(bytes -> ByteSource.wrap(bytes))
                                .orElseThrow();
                            addRootAsset(originalFilename, content, eventName, variablePath);
                        }
                    }
                }
            }
            creativeResponses.add(
                new MigratedCreativeActionCreativeResponse(
                    frontendController.getId().getValue(),
                    actionCreative.getId().getValue(),
                    eventTrigger.getId().getValue(),
                    creativeArchiveId.getId().getValue(),
                    componentName,
                    dedupedLegacyVariables));
        }
    }

    private String convertZoneNameToComponentName(String zoneName) {
        return zoneName.trim().replace(' ', '_');
    }

    private void verifyRecursivelyIfVariablePointsToRendertime(
        CreativeVariable currentVariable,
        Set<String> traversedVariableNames,
        Set<String> runtimeVariables,
        Map<String, CreativeVariable> variables) {

        String mergedValues = String.join("", currentVariable.getValues().values());

        List<String> buildtimeVariables = extractBuildtimeVariables(mergedValues);
        for (String buildtimeVariableName : buildtimeVariables) {
            if (runtimeVariables.contains(buildtimeVariableName)) {
                runtimeVariables.add(currentVariable.getName());
                return;
            }
            if (!traversedVariableNames.add(buildtimeVariableName)) {
                continue;
            }
            CreativeVariable nextVariable = variables.get(buildtimeVariableName);
            if (nextVariable != null) {
                verifyRecursivelyIfVariablePointsToRendertime(nextVariable, traversedVariableNames,
                    runtimeVariables, variables);
            }
        }

    }

    private static List<String> extractBuildtimeVariables(String input) {
        List<String> substrings = new LinkedList<>();

        Pattern pattern = Pattern.compile("\\{\\{\\s*(.*?)\\s*}}");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            substrings.add(matcher.group(1).trim());
        }

        return Collections.unmodifiableList(substrings);
    }

    private void addExistingComponentVariableToNewComponent(CampaignComponentBuilder componentBuilder,
        Variable existingVariable, String componentName)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {

        VariableBuilder variableBuilder =
            getVariableBuilder(componentBuilder, existingVariable.getName(), existingVariable.getType());
        variableBuilder.withName(existingVariable.getName());
        if (existingVariable.getDisplayName().isPresent()) {
            variableBuilder.withDisplayName(existingVariable.getDisplayName().get());
        }
        variableBuilder.clearDescription();
        variableBuilder.withType(existingVariable.getType());
        Set<String> tags = existingVariable.getTags().stream().map(String::toLowerCase).collect(Collectors.toSet());
        tags.add(HIDDEN_TAG);
        if (VARIABLE_NAMES_TO_BE_MIGRATED.contains(existingVariable.getName())) {
            tags.add(EXPERT_TAG);
        }
        variableBuilder.withTags(tags);
        variableBuilder.withValues(existingVariable.getValues());
        variableBuilder.withSource(VariableSource.INHERITED);

        handleCustomVariableAttributes(existingVariable, variableBuilder);

        if (campaignVariables.containsKey(existingVariable.getName())) {
            applyEng21748NewDefinition(campaignVariables.get(existingVariable.getName()),
                variableBuilder,
                existingVariable.getTags(),
                existingVariable.getValues().containsKey("default"));
        }
    }

    private void handleCustomVariableAttributes(Variable existingVariable, VariableBuilder variableBuilder) {
        switch (existingVariable.getType()) {
            case ENUM:
                EnumVariable enumVariable = (EnumVariable) existingVariable;
                ((EnumVariableBuilder) variableBuilder).withAllowedValues(enumVariable.getAllowedValues());
                break;
            case ENUM_LIST:
                EnumListVariable enumListVariable = (EnumListVariable) existingVariable;
                ((EnumVariableBuilder) variableBuilder).withAllowedValues(enumListVariable.getAllowedValues());
                break;
            case CLIENT_KEY_FLOW:
                ClientKeyFlowVariable clientKeyFlowVariable = (ClientKeyFlowVariable) existingVariable;
                ((ClientKeyFlowVariableBuilder) variableBuilder)
                    .withRedirectUri(clientKeyFlowVariable.getRedirectUri());
                ((ClientKeyFlowVariableBuilder) variableBuilder)
                    .withClientKeyUrl(clientKeyFlowVariable.getClientKeyUrl());
                ((ClientKeyFlowVariableBuilder) variableBuilder)
                    .withClientKeyOAuthFlow(clientKeyFlowVariable.getClientKeyOauthFlow());
                break;
            default:
                break;
        }
    }

    private void addImageAsset(ByteSource content, String originalFilename, String zoneName,
        CampaignComponentBuilder componentBuilder, String valueLocale, Map<String, ?> filteredValues)
        throws Exception {
        if (content != null) {
            FilenameAndExtensionData filenameAndExtensionData =
                processFilename(originalFilename, content, zoneName);
            String name =
                cleanFromUnacceptableImageNameCharacters(
                    filenameAndExtensionData.getFilenameWithoutExtension());
            String newFileName = name + "." + filenameAndExtensionData.getFileExtension();
            String description = prettify(name);
            CampaignComponentAssetBuilder assetBuilder = componentBuilder.addAsset();
            assetBuilder.withName(name);
            assetBuilder.withFilename(newFileName);
            assetBuilder.withDescription(description);
            assetBuilder.withContent(content);
        } else {
            String key = "en".equals(valueLocale) ? "default" : valueLocale;
            filteredValues.remove(key);
        }
    }

    private void addRootAsset(String originalFilename, ByteSource content, String zoneName,
        String variablePath)
        throws Exception {
        FilenameAndExtensionData filenameAndExtensionData =
            processFilename(originalFilename, content, zoneName);
        String name =
            cleanFromUnacceptableImageNameCharacters(
                filenameAndExtensionData.getFilenameWithoutExtension());
        String newFilename = name + "." + filenameAndExtensionData.getFileExtension();
        String description = prettify(name);

        rootAssets.compute(name, (key, variables) -> {
            if (variables == null) {
                variables = KeyCaseInsensitiveMap.create();
            }
            variables.put(variablePath, createDummyAsset(newFilename, name, description));
            return variables;
        });
        rootAssetsContent.compute(name, (key, variables) -> {
            if (variables == null) {
                variables = KeyCaseInsensitiveMap.create();
            }
            variables.put(variablePath, content);
            return variables;
        });
    }

    private List<String> getInternalArchivePaths(Map<String, String> pathsGroupedByLocales) {
        return pathsGroupedByLocales
            .values()
            .stream()
            .filter(path -> path.contains("/"))
            .filter(path -> !path.startsWith("https://"))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getUrlLinks(Map<String, String> pathsGroupedByLocales) {
        return pathsGroupedByLocales
            .values()
            .stream()
            .filter(path -> path.contains("/"))
            .filter(path -> path.startsWith("https://"))
            .collect(Collectors.toUnmodifiableList());
    }

    private String createVariablePath(String parentId, String childId, CreativeArchiveId creativeArchiveId,
        String variableName) {
        return parentId + "-" + childId + "-" + creativeArchiveId.getId().getValue() + "-" + variableName;
    }

    private Map<String, CreativeVariable> fixCorruptedImageVariables(Map<String, CreativeVariable> creativeVariables) {
        return creativeVariables.entrySet().stream()
            .map(entry -> {
                CreativeVariable variable = entry.getValue();
                CreativeVariable.Type newType = variable.getType();

                if (variable.getType().equals(IMAGE)) {
                    for (String value : variable.getValues().values()) {
                        try {
                            Optional<BuildtimeEvaluatable<VariableBuildtimeContext,
                                RuntimeEvaluatable<Object, Optional<Object>>>> mappedValue =
                                    mapImageVariable(value);
                            if (mappedValue.isPresent()) {
                                BuildtimeEvaluatable<VariableBuildtimeContext,
                                    RuntimeEvaluatable<Object, Optional<Object>>> evaluatable =
                                        mappedValue.get();
                                if (evaluatable instanceof Provided) {
                                    String extractedValue =
                                        extractValueFromProvidedNestedOptional(evaluatable, String.class);
                                    if (StringUtils.equals(extractedValue, value) && !isValidURL(extractedValue)) {
                                        newType = CreativeVariable.Type.TEXT;
                                        break;
                                    }
                                }
                            }
                        } catch (JsonProcessingException e) {
                            newType = CreativeVariable.Type.TEXT;
                            break;
                        }
                    }

                }

                return Pair.of(entry.getKey(), new MigrationCreativeVariable(variable.getName(), variable.getLabel(),
                    variable.getScope(), variable.getDefaultScope(), newType, variable.getTags(),
                    variable.getValues(), variable.isVisible().booleanValue(), variable.getCreativeArchiveId(),
                    variable.getOutput()));
            })
            .collect(Collectors.toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
    }

    private Map<String, CreativeVariable> fixMissReferencedVariables(Map<String, CreativeVariable> creativeVariables)
        throws JsonProcessingException {
        Map<String, CreativeVariable> variables = creativeVariables.entrySet().stream()
            .map(entry -> {
                CreativeVariable variable = entry.getValue();

                KeyCaseInsensitiveMap<String> newValues = KeyCaseInsensitiveMap.create();

                variable.getValues().forEach((variant, value) -> {
                    newValues.put(variant,
                        replaceEachRepeatedly(value, DEFAULT_SEARCH_LIST, DEFAULT_REPLACEMENT_LIST));
                });

                return Pair.of(entry.getKey(), new MigrationCreativeVariable(variable.getName(), variable.getLabel(),
                    variable.getScope(), variable.getDefaultScope(), variable.getType(), variable.getTags(),
                    newValues, variable.isVisible().booleanValue(), variable.getCreativeArchiveId(),
                    variable.getOutput()));
            })
            .collect(Collectors.toMap(pair -> pair.getLeft(), pair -> pair.getRight()));

        List<Map<String, String>> valuesOnly = variables.values()
            .stream()
            .map(creativeVariable -> creativeVariable.getValues())
            .collect(Collectors.toList());

        String serializedVariables = ObjectMapperProvider.getConfiguredInstance().writeValueAsString(valuesOnly);

        boolean copyright = variables.containsKey("copyright");
        boolean footerTextColor = variables.containsKey("footerTextColor");

        if (copyright && !footerTextColor) {
            variables.put("footerTextColor",
                new MigrationCreativeVariable("footerTextColor", "Footer Text Color",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, COLOR,
                    new String[] {"category:styling"},
                    Map.of("en", "#4D4F53"),
                    true, null,
                    null));
        }
        if (!variables.containsKey("secondaryAccentColor") &&
            StringUtils.contains(serializedVariables, "{{secondaryAccentColor}}")) {
            variables.put("secondaryAccentColor",
                new MigrationCreativeVariable("secondaryAccentColor", "Secondary Accent Color",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, COLOR,
                    new String[] {"category:styling"},
                    Map.of("en", "#E3721F"),
                    true, null,
                    null));
        }
        if (!variables.containsKey("brandDescription") &&
            StringUtils.contains(serializedVariables, "{{brandDescription}}")) {
            variables.put("brandDescription",
                new MigrationCreativeVariable("brandDescription", "Brand description",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {},
                    Map.of("en", ""),
                    true, null,
                    null));
        }
        if (!variables.containsKey("advocateReward") &&
            StringUtils.containsAny(serializedVariables, "{{ advocateReward }}", "{{advocateReward}}")) {
            variables.put("advocateReward",
                new MigrationCreativeVariable("advocateReward", "Advocate Reward Amount",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {"translatable",
                        "keep",
                        "category:content:rewarding"},
                    Map.of("en", ""),
                    true, null,
                    null));
        }
        if (!variables.containsKey("friendReward") &&
            StringUtils.containsAny(serializedVariables, "{{ friendReward }}", "{{friendReward}}")) {
            variables.put("friendReward",
                new MigrationCreativeVariable("friendReward", "Friend Reward Amount",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {"translatable",
                        "keep",
                        "category:content:rewarding"},
                    Map.of("en", ""),
                    true, null,
                    null));
        }
        if (!variables.containsKey("companyName") &&
            StringUtils.containsAny(serializedVariables, "{{ companyName }}", "{{companyName}}")) {
            variables.put("companyName",
                new MigrationCreativeVariable("companyName", "Company Name",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {"translatable",
                        "category:content:copy"},
                    Map.of("en", ""),
                    true, null,
                    null));
        }
        if (!variables.containsKey("heading") &&
            StringUtils.contains(serializedVariables, "{{heading}}")) {
            variables.put("heading",
                new MigrationCreativeVariable("heading", "Heading Text (leave blank to suppress)",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {"translatable",
                        "primary",
                        "category:content:copy"},
                    Map.of("en", ""),
                    true, null,
                    null));
        }
        if (!variables.containsKey("privacyUrl") &&
            StringUtils.containsAny(serializedVariables, "{{privacyUrl}}", "{{ privacyUrl }}")) {
            variables.put("privacyUrl",
                new MigrationCreativeVariable("privacyUrl", "Privacy Policy URL",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {"translatable",
                        "primary",
                        "category:content:copy"},
                    Map.of("en", ""),
                    true, null,
                    null));
        }
        if (!variables.containsKey("defaultVerificationUrl") &&
            StringUtils.containsAny(serializedVariables, "{{defaultVerificationUrl}}",
                "{{ defaultVerificationUrl }}")) {
            variables.put("defaultVerificationUrl",
                new MigrationCreativeVariable("defaultVerificationUrl", "Privacy Policy URL",
                    CreativeVariable.Scope.CAMPAIGN, CreativeVariable.Scope.CAMPAIGN, TEXT,
                    new String[] {},
                    Map.of("en", ""),
                    false, null,
                    null));
        }
        return variables;
    }

    private Map<String, CreativeVariable> migrateBrokenHandlebarVariableReferences(
        Map<String, CreativeVariable> creativeVariables) {
        return creativeVariables.entrySet().stream()
            .map(entry -> {
                CreativeVariable variable = entry.getValue();
                Map<String, String> newValues = Maps.newHashMap(variable.getValues());

                if (variable.getType().equals(TEXT)) {
                    newValues.replaceAll((k, v) -> fixHandlebarsTemplateIfNeeded(v));
                }

                return Pair.of(entry.getKey(), new MigrationCreativeVariable(variable.getName(), variable.getLabel(),
                    variable.getScope(), variable.getDefaultScope(), variable.getType(), variable.getTags(),
                    unmodifiableMap(newValues),
                    variable.isVisible().booleanValue(), variable.getCreativeArchiveId(), variable.getOutput()));
            })
            .collect(Collectors.toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
    }

    private Map<String, CreativeVariable> fixIndividualVariableCasesIfNeeded(
        Map<String, CreativeVariable> creativeVariables,
        String variableName,
        String referencedVariableName,
        Map<String, String> defaultReferencedValues,
        CreativeVariable.Type type) {

        Map<String, CreativeVariable> mutableCreativeVariables = Maps.newHashMap(creativeVariables);

        boolean campaignVariableReferencesToComponentVariable = creativeVariables.containsKey(variableName)
            && creativeVariables.get(variableName).getScope() == CreativeVariable.Scope.CAMPAIGN
            && StringUtils.deleteWhitespace(String.join("", creativeVariables.get(variableName).getValues().values()))
                .contains("{{" + referencedVariableName + "}}")
            && creativeVariables.containsKey(referencedVariableName)
            && creativeVariables.get(referencedVariableName).getScope() != CreativeVariable.Scope.CAMPAIGN;
        if (campaignVariableReferencesToComponentVariable) {
            CreativeVariable referencedVariable = creativeVariables.get(referencedVariableName);
            mutableCreativeVariables.put(referencedVariableName,
                new MigrationCreativeVariable(referencedVariable.getName(),
                    referencedVariable.getLabel(),
                    CreativeVariable.Scope.CAMPAIGN,
                    CreativeVariable.Scope.CAMPAIGN,
                    referencedVariable.getType(),
                    referencedVariable.getTags(),
                    unmodifiableMap(referencedVariable.getValues()),
                    referencedVariable.isVisible().booleanValue(),
                    referencedVariable.getCreativeArchiveId(),
                    referencedVariable.getOutput()));
        }

        boolean variableReferencesToMissedVariable = creativeVariables.containsKey(variableName)
            && StringUtils.deleteWhitespace(String.join("", creativeVariables.get(variableName).getValues().values()))
                .contains("{{" + referencedVariableName + "}}")
            && !creativeVariables.containsKey(referencedVariableName);
        if (variableReferencesToMissedVariable) {
            mutableCreativeVariables.put(referencedVariableName,
                new MigrationCreativeVariable(referencedVariableName,
                    StringUtils.EMPTY,
                    creativeVariables.get(variableName).getScope(),
                    creativeVariables.get(variableName).getDefaultScope(),
                    type,
                    new String[] {},
                    defaultReferencedValues,
                    true,
                    creativeVariables.get(variableName).getCreativeArchiveId(),
                    List.of()));
        }

        return Collections.unmodifiableMap(mutableCreativeVariables);
    }

    private Map<String, CreativeVariable> changeToRendertimeBracketsIfNeeded(
        Map<String, CreativeVariable> creativeVariables, Set<String> rendertimeVariableNames) {

        Set<String> variablesThatPointToRendertime = new HashSet<>(rendertimeVariableNames);
        for (Map.Entry<String, CreativeVariable> entry : creativeVariables.entrySet()) {
            Set<String> traversedVariableNames = new HashSet<>();
            traversedVariableNames.add(entry.getKey());

            verifyRecursivelyIfVariablePointsToRendertime(entry.getValue(), traversedVariableNames,
                variablesThatPointToRendertime, creativeVariables);
        }

        String regexPattern = variablesThatPointToRendertime.stream()
            .collect(Collectors.joining("|",
                "\\{\\{(\\W*(",
                ")([. ]+\\w+)?(\\s)*)}}"));

        Pattern pattern = Pattern.compile(regexPattern);
        return creativeVariables.entrySet().stream()
            .map(entry -> {
                CreativeVariable variable = entry.getValue();
                Map<String, String> newValues = Maps.newHashMap(variable.getValues());

                if (variable.getType().equals(TEXT)) {
                    newValues.replaceAll((key, value) -> replaceBracesWithRendertimeBraces(value, pattern));
                }

                return Pair.of(entry.getKey(), new MigrationCreativeVariable(variable.getName(), variable.getLabel(),
                    variable.getScope(), variable.getDefaultScope(), variable.getType(), variable.getTags(),
                    unmodifiableMap(newValues),
                    variable.isVisible().booleanValue(), variable.getCreativeArchiveId(), variable.getOutput()));
            })
            .collect(Collectors.toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
    }

    private String fixHandlebarsTemplateIfNeeded(String input) {
        if (!containsHandlebars(input)) {
            return input;
        }

        String normalizedInput = normalizeHandlebars(input);
        Matcher matcher = INVALID_HANDLEBARS_TEMPLATE_REGEX_PATTERN.matcher(normalizedInput);
        StringBuilder output = new StringBuilder();
        while (matcher.find()) {
            String value = matcher.group();
            String replacement = shouldBeReplaced(value) ? value.replaceAll("\\s+", ".") : value;
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);

        String expression =
            output.toString().replaceAll("generalTerms&ConditionsUrl", " [generalTerms&ConditionsUrl] ");
        String[] coalesces = StringUtils.substringsBetween(expression, "{{", "}}");
        if (coalesces != null) {
            for (String stringLiteral : coalesces) {
                if (stringLiteral.contains("||")) {
                    String substringBefore = StringUtils.substringBefore(stringLiteral, "||");
                    String substringAfter = StringUtils.substringAfter(stringLiteral, "||");
                    expression = StringUtils.replaceOnce(expression, "{{" + stringLiteral + "}}",
                        "{{# " + substringBefore + "}}{{" + substringBefore + "}}{{/ " + substringBefore + "}}{{^ "
                            + substringBefore + "}}" + substringAfter.trim() + "{{/ " + substringBefore + "}}");
                }
                if (stringLiteral.contains(",")) {
                    expression = StringUtils.replaceOnce(expression, "{{" + stringLiteral + "}}",
                        "{{" + stringLiteral.replaceAll("\\s*,\\s*", StringUtils.EMPTY) + "}}");
                }
            }
        }

        return expression;
    }

    private static String replaceBracesWithRendertimeBraces(String targetString, Pattern rendertimeVariablePattern) {
        Matcher matcher = rendertimeVariablePattern.matcher(targetString);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String matchedName = matcher.group(1);
            matcher.appendReplacement(result, "{[" + matchedName + "]}");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private boolean shouldBeReplaced(String value) {
        for (String helper : HANDLEBARS_HELPERS) {
            if (value.contains(helper)) {
                return false;
            }
        }
        return true;
    }

    private String normalizeHandlebars(String input) {
        String normalizeDoubleCurlyBraces = "\\{\\{\\s*(.*?)\\s*}}";
        String normalizeTripleCurlyBraces = "\\{\\{\\{\\s*(.*?)\\s*}}}";
        return input.replaceAll(normalizeTripleCurlyBraces, "{{{$1}}}")
            .replaceAll(normalizeDoubleCurlyBraces, "{{$1}}");
    }

    private FilenameAndExtensionData processFilename(String filename, ByteSource content, String zoneName)
        throws CampaignMigrationRestException {

        filename = filename.replaceAll("\\.+$", "");
        Optional<String> fileExtensionFromFilename = tryToExtractFileExtensionByFilename(filename);

        String fileExtension = fileExtensionFromFilename.isPresent()
            ? fileExtensionFromFilename.get()
            : determineFileExtension(filename, content, zoneName);
        String filenameWithoutExtension = fileExtensionFromFilename.isPresent()
            ? StringUtils.removeEnd(filename, "." + fileExtensionFromFilename.get())
            : filename;

        return new FilenameAndExtensionData() {
            @Override
            public String getFilenameWithoutExtension() {
                return filenameWithoutExtension;
            }

            @Override
            public String getFileExtension() {
                return fileExtension;
            }
        };
    }

    private Optional<String> tryToExtractFileExtensionByFilename(String filename) {
        String extensionFromName = FilenameUtils.getExtension(filename);
        if (StringUtils.isNotEmpty(extensionFromName)) {
            return Optional.of(extensionFromName);
        }
        return Optional.empty();
    }

    private String determineFileExtension(String filename, ByteSource content, String zoneName)
        throws CampaignMigrationRestException {

        try (InputStream inputStream = content.openBufferedStream()) {
            MimeType mimeType = FILE_TYPE_DETECTOR.detect(inputStream);
            String extension = ALLOWED_IMAGE_EXTENSIONS_BY_MIME_TYPES.get(mimeType.getMimeType());

            if (StringUtils.isEmpty(extension) || !ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
                throw RestExceptionBuilder.newBuilder(CampaignMigrationRestException.class)
                    .withErrorCode(CampaignMigrationRestException.UNIDENTIFIED_ASSET)
                    .addParameter("filename", filename)
                    .addParameter("step_mapping_name", zoneName)
                    .build();
            }

            return extension;
        } catch (IOException | MimeTypeException e) {
            throw RestExceptionBuilder.newBuilder(CampaignMigrationRestException.class)
                .withCause(e)
                .withErrorCode(CampaignMigrationRestException.UNIDENTIFIED_ASSET)
                .addParameter("filename", filename)
                .addParameter("step_mapping_name", zoneName)
                .build();
        }
    }

    private String addSpacesAndCapitalize(String value) {
        String[] words = value.split("(?=\\p{Upper})");

        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(word.substring(0, 1).toUpperCase())
                .append(word.substring(1))
                .append(" ");
        }

        return StringUtils.trim(stringBuilder.toString());
    }

    private CampaignComponentAsset createDummyAsset(String filename, String name, String description) {
        return new CampaignComponentAsset() {
            @Override
            public Id<CampaignComponentAsset> getId() {
                return null;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getFilename() {
                return filename;
            }

            @Override
            public Set<String> getTags() {
                return Set.of();
            }

            @Override
            public Optional<String> getDescription() {
                return Optional.ofNullable(description);
            }
        };
    }

    private Variable createDummyVariable(String variableName, Optional<String> variableDisplayName,
        VariableSource variableSource, Set<String> variableTags, SettingType settingType,
        Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values) {
        return new Variable() {
            @Override
            public String getName() {
                return variableName;
            }

            @Override
            public Optional<String> getDisplayName() {
                return variableDisplayName;
            }

            @Override
            public SettingType getType() {
                return settingType;
            }

            @Override
            public Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>> getValues() {
                return values;
            }

            @Override
            public VariableSource getSource() {
                return variableSource;
            }

            @Override
            public BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> getDescription() {
                return Provided.optionalEmpty();
            }

            @Override
            public Set<String> getTags() {
                return variableTags;
            }

            @Override
            public DeweyDecimal getPriority() {
                return DEFAULT_SETTING_PRIORITY;
            }
        };
    }

    private Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        mapValues(String variablePath, CreativeVariable oldVariable, Collection<CreativeVariable> allOldVariables,
            Map<String, String> valueOverrides)
            throws JsonProcessingException, EvaluationException {
        Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values = Maps.newHashMap();
        boolean isTranslateable = mapTags(oldVariable).contains(TRANSLATABLE_TAG);
        for (Map.Entry<String, String> entry : oldVariable.getValues().entrySet()) {
            if (StringUtils.isBlank(entry.getKey())) {
                continue;
            }
            String key = !isTranslateable && entry.getKey().equals("en") ? "default" : entry.getKey();
            String value = valueOverrides.getOrDefault(entry.getValue(), entry.getValue());
            if (oldVariable.getType() == CreativeVariable.Type.COLOR) {
                if (StringUtils.isEmpty(value)) {
                    values.put(key, Provided.nestedOptionalOf(DEFAULT_COLOR));
                    fixedBrokenColors.put(variablePath + "-" + key + "-" + value, DEFAULT_COLOR);
                } else if (COLOR_HEX_WITHOUT_HASHTAG.test(value)) {
                    values.put(key, Provided.nestedOptionalOf("#" + value));
                    fixedBrokenColors.put(variablePath + "-" + key + "-" + value, "#" + value);
                } else if ("transparent".equalsIgnoreCase(value)) {
                    values.put(key, Provided.nestedOptionalOf(TRANSPARENT_RGBA));
                    fixedBrokenColors.put(variablePath + "-" + key + "-" + value, TRANSPARENT_RGBA);
                } else if (value.matches("^#[0-9a-fA-F]{3}$")) {
                    StringBuilder colorBuilder = new StringBuilder("#");
                    for (char c : value.substring(1).toCharArray()) {
                        colorBuilder.append(c).append(c);
                    }
                    values.put(key, Provided.nestedOptionalOf(colorBuilder.toString()));
                    fixedBrokenColors.put(variablePath + "-" + key + "-" + value, colorBuilder.toString());
                } else {
                    values.put(key, Provided.nestedOptionalOf(value));
                }
            } else if (oldVariable.getType() == CreativeVariable.Type.SWITCH) {
                values.put(key, Provided.nestedOptionalOf(Boolean.valueOf(value)));
            } else if (oldVariable.getType() == IMAGE) {
                mapImageVariable(value)
                    .ifPresent(mappedImageValue -> values.put(key, mappedImageValue));
            } else {
                if (StringUtils.isBlank(value)) {
                    values.put(key, Provided.nestedOptionalOf(value));
                } else {
                    boolean containsRendertimeBars = containsRendertimeBars(value);
                    if (containsHandlebars(value) || containsRendertimeBars) {
                        values.put(key, handlebars(value,
                            containsRendertimeBars || containsBuildtimeWithRendertime(key, value, allOldVariables)));
                    } else {
                        values.put(key, Provided.nestedOptionalOf(value));
                    }
                }
            }
        }
        if (isTranslateable) {
            for (String variant : builtCampaign.getVariants()) {
                if (!values.containsKey(variant)) {
                    values.put(variant, values.get("en"));
                }
            }
        }
        return values;
    }

    private boolean containsRendertimeBars(String value) {
        return value.contains("{[") && value.contains("]}");
    }

    private boolean containsBuildtimeWithRendertime(String key, String value,
        Collection<CreativeVariable> allOldVariables) throws JsonProcessingException, EvaluationException {
        String sanitized = value
            .replaceAll("\n", "\\\\\\n")
            .replaceAll("\t", "\\\\\\t")
            .replaceAll("\"", "\\\\\"");
        BuildtimeEvaluatable<Map<String, Object>, String> evaluatable = ObjectMapperProvider.getConfiguredInstance()
            .readValue("\"handlebars@buildtime:" + sanitized + "\"", new TypeReference<>() {});

        Map<String, Object> context = Maps.newHashMap();

        for (CreativeVariable variable : allOldVariables) {
            boolean isTranslateable = mapTags(variable).contains(TRANSLATABLE_TAG);
            String referencedKey = isTranslateable && key.equals("default") ? "en" : key;
            context.put(variable.getName(), variable.getValues().get(referencedKey));
        }
        String evaluated = evaluationService.evaluate(evaluatable, new LazyLoadingSupplier<>(() -> context));
        return containsRendertimeBars(evaluated);
    }

    private Optional<BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        mapImageVariable(String value) throws JsonProcessingException {

        boolean isFromCreativeRoot = value.startsWith("creative-root://");
        boolean isAnImageLinkWithInvalidExtension =
            value.startsWith("https://") && ALLOWED_IMAGE_EXTENSIONS.stream().noneMatch(value::endsWith);

        if (isFromCreativeRoot || isAnImageLinkWithInvalidExtension) {
            String filename = StringUtils.substringAfterLast(value, "/");
            filename = filename.replaceAll("\\.+$", StringUtils.EMPTY);
            filename = StringUtils.substringBeforeLast(filename, ".");
            String name = cleanFromUnacceptableImageNameCharacters(filename);
            return Optional.of(createAssetUrl(name));
        } else if (StringUtils.isNotBlank(value)) {
            if (containsHandlebars(value)) {
                return Optional.of(handlebars(value, false));
            } else {
                return Optional.of(Provided.nestedOptionalOf(value));
            }
        }

        return Optional.empty();
    }

    private BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>
        createAssetUrl(String assetName) throws JsonProcessingException {
        return ObjectMapperProvider.getConfiguredInstance()
            .readValue("\"spel@buildtime:context.getAsset('" + assetName + "').getUrl()\"", new TypeReference<>() {});
    }

    private BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>
        handlebars(String expression, boolean runtime) throws JsonProcessingException {
        String sanitized = expression
            .replaceAll("\n", "\\\\\\n")
            .replaceAll("\t", "\\\\\\t")
            .replaceAll("\"", "\\\\\"");
        return ObjectMapperProvider.getConfiguredInstance()
            .readValue((runtime ? "\"handlebars@runtime:" : "\"handlebars@buildtime:") + sanitized + "\"",
                new TypeReference<>() {});
    }

    private VariableSource mapSource(CreativeVariable oldVariable) {
        return oldVariable.getScope() == CreativeVariable.Scope.CAMPAIGN ? VariableSource.INHERITED
            : VariableSource.LOCAL;
    }

    private Set<String> mapTags(CreativeVariable oldVariable) {
        Set<String> tags = new HashSet<>();
        for (String tag : oldVariable.getTags()) {
            tags.add(tag.toLowerCase());
        }
        if (!oldVariable.isVisible().booleanValue()) {
            tags.add(HIDDEN_TAG);
        }
        if (oldVariable.getName().equals("campaign_image")) {
            tags.add(HIDDEN_TAG);
            tags.add("internal:ui-display");
        }
        if (VARIABLE_NAMES_TO_BE_MIGRATED.contains(oldVariable.getName())) {
            tags.add(EXPERT_TAG);
        }
        if (oldVariable.getType() == CreativeVariable.Type.IMAGE) {
            tags.add("category:content:image");
            tags.add("category:styling:image");
            tags.remove(TRANSLATABLE_TAG);
        }
        return tags;
    }

    private SettingType mapType(CreativeVariable.Type type) {
        switch (type) {
            case COLOR:
                return SettingType.COLOR;
            case TEXT:
            case SHORT_TEXT:
                return SettingType.STRING;
            case SWITCH:
                return SettingType.BOOLEAN;
            case IMAGE:
                return SettingType.IMAGE;
            default:
                throw new RuntimeException("Can't map type " + type);
        }
    }

    interface ProcessedZip {
        Map<String, ByteSource> getImages();

        Set<String> getRendertimeVariableNames();

        Optional<String> getScheduleName(String variableName);
    }

    private ProcessedZip writeNewArchiveZipAndExtractImages(CreativeArchive oldCreativeArchive,
        Supplier<CreativeArchiveBuilder> creativeBuilderSupplier, Authorization authorization) throws Exception {
        Map<String, ByteSource> images = Maps.newHashMap();
        Set<String> rendertimeVariableNames = Sets.newHashSet();

        ByteSource creativeArchiveData =
            creativeArchiveService.getData(authorization, oldCreativeArchive.getCreativeArchiveId());
        List<Map<String, Object>> rawVariables = Lists.newArrayList();
        try (FileBackedOutputStream newZip = new FileBackedOutputStream(THRESHOLD, true)) {
            try (ZipOutputStream zipOutput = new ZipOutputStream(newZip)) {
                try (ZipInputStream zip = new ZipInputStream(creativeArchiveData.openBufferedStream())) {
                    while (true) {
                        ZipEntry entry = zip.getNextEntry();
                        if (entry == null) {
                            break;
                        }

                        if (entry.getName().equals("/variables/runtime-variables.js")) {
                            zipOutput.putNextEntry(new ZipEntry(entry.getName()));
                            byte[] allBytes = zip.readAllBytes();
                            zipOutput.write(allBytes);
                            rendertimeVariableNames.addAll(extractVariableNames(allBytes));
                        } else if (entry.getName().equals("/variables/rendertime-variables.js")) {
                            zipOutput.putNextEntry(new ZipEntry(entry.getName()));
                            byte[] allBytes = zip.readAllBytes();
                            zipOutput.write(allBytes);
                            rendertimeVariableNames.addAll(extractVariableNames(allBytes));
                        } else if (entry.getName().equals("/variables/static-variables.json")) {
                            rawVariables.addAll(ObjectMapperProvider.getConfiguredInstance()
                                .readValue(zip.readAllBytes(), new TypeReference<>() {}));
                        } else if (entry.getName().startsWith("/images") || entry.getName().startsWith("/img")) {
                            String filename = StringUtils.substringAfterLast(entry.getName(), "/");
                            try (FileBackedOutputStream assetContent = new FileBackedOutputStream(THRESHOLD, true)) {
                                assetContent.write(zip.readAllBytes());
                                images.put(filename, assetContent.asByteSource());
                            }
                        } else if (V8_REMOVED_FILES.contains(entry.getName())) {
                            LOG.debug("File {} is removed from creative {} of campaign {} of client {}",
                                entry.getName(), oldCreativeArchive.getCreativeArchiveId(), campaign.getId(),
                                campaign.getClientId());
                        } else {
                            zipOutput.putNextEntry(new ZipEntry(entry.getName()));
                            zipOutput.write(zip.readAllBytes());
                        }
                    }
                }
            }
            creativeBuilderSupplier.get().withData(newZip.asByteSource());
        }
        return new ProcessedZip() {
            @Override
            public Map<String, ByteSource> getImages() {
                return images;
            }

            @Override
            public Set<String> getRendertimeVariableNames() {
                return rendertimeVariableNames;
            }

            @Override
            public Optional<String> getScheduleName(String variableName) {
                Optional<Map<String, Object>> rawVariable = rawVariables.stream()
                    .filter(candidate -> variableName.equalsIgnoreCase(String.valueOf(candidate.get("name"))))
                    .findFirst();

                if (rawVariable.isPresent()) {
                    Object readWrite = rawVariable.get().get("readWrite");
                    if (readWrite instanceof Map) {
                        Object scheduleName = ((Map<?, ?>) readWrite).get("scheduleName");
                        if (scheduleName != null) {
                            return Optional.of(scheduleName.toString());
                        }
                    }
                }
                return Optional.empty();
            }
        };
    }

    private Set<String> extractVariableNames(byte[] fileContent) {
        Set<String> variableNames = new HashSet<>();

        String content = new String(fileContent);

        Pattern pattern = Pattern.compile("\\s+name:\\s+['\"](\\w+)['\"],");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            variableNames.add(variableName);
        }

        return variableNames;
    }

    private String prettify(String value) {
        String noHyphens = StringUtils.replace(value, "-", "_", -1);
        String spacedUnderscores = StringUtils.replace(noHyphens, "_", " _", -1);
        String noUnderscores = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, spacedUnderscores);
        return noUnderscores;
    }

    private CampaignComponent lookupRoot(List<CampaignComponent> components) {
        return components.stream()
            .filter(component -> ROOT.equalsIgnoreCase(component.getName()))
            .findFirst()
            .get();
    }

    private Map<String, CreativeVariable> fixHandlebarsSafeHtmlMethodsToClearHtmlHelpers(
        Map<String, CreativeVariable> creativeVariables) {

        return creativeVariables.entrySet().stream()
            .map(entry -> {
                CreativeVariable variable = entry.getValue();
                if (!variable.getType().equals(CreativeVariable.Type.TEXT)) {
                    return Pair.of(entry.getKey(),
                        new MigrationCreativeVariable(variable.getName(), variable.getLabel(),
                            variable.getScope(), variable.getDefaultScope(), variable.getType(), variable.getTags(),
                            variable.getValues(), variable.isVisible().booleanValue(), variable.getCreativeArchiveId(),
                            variable.getOutput()));
                }

                ImmutableMap.Builder<String, String> newValuesBuilder = ImmutableMap.builder();

                for (Map.Entry<String, String> valueEntry : variable.getValues().entrySet()) {
                    if (containsHandlebars(valueEntry.getValue())) {
                        String value = convertSpecificFunctionsToHelpersIfNeeded(valueEntry.getValue());
                        newValuesBuilder.put(valueEntry.getKey(), value);
                    } else {
                        newValuesBuilder.put(valueEntry.getKey(), valueEntry.getValue());
                    }
                }

                return Pair.of(entry.getKey(), new MigrationCreativeVariable(variable.getName(), variable.getLabel(),
                    variable.getScope(), variable.getDefaultScope(), variable.getType(), variable.getTags(),
                    newValuesBuilder.build(), variable.isVisible().booleanValue(), variable.getCreativeArchiveId(),
                    variable.getOutput()));
            })
            .collect(Collectors.toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
    }

    private String convertSpecificFunctionsToHelpersIfNeeded(String value) {
        String result = value;

        for (String helper : HANDLEBARS_HELPERS) {
            result = result.replaceAll("\\{\\{\\s*" + helper + "\\((.*?)\\)\\s*}}", "{{" + helper + " $1}}");
        }

        return result;
    }

    private boolean containsHandlebars(String value) {
        return value.contains("{{") && value.contains("}}");
    }

    private <T> T extractValueFromProvidedNestedOptional(
        BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>> evaluatable,
        Class<T> resultType) {
        RuntimeEvaluatable<Object, Optional<Object>> runtimeEvaluatable =
            (RuntimeEvaluatable<Object, Optional<Object>>) ((Provided) evaluatable).getValue();
        Optional<Object> optionalProvidedValue = (Optional<Object>) ((Provided) runtimeEvaluatable).getValue();
        return resultType.cast(optionalProvidedValue.orElseThrow());
    }

    private boolean isValidURL(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String cleanFromUnacceptableImageNameCharacters(String input) {
        return input.replaceAll(IMAGE_NAME_CHARACTERS_TO_BE_REPLACED, IMAGE_NAME_REPLACEMENT);
    }

    private Optional<byte[]> fetchImage(String url) {
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error("Was unable to download image with URL={}. Response status was={}", url,
                    response.getStatusLine());

                return Optional.empty();
            }
            byte[] responseBodyBytes = EntityUtils.toByteArray(response.getEntity());
            if (responseBodyBytes != null && responseBodyBytes.length > 0) {
                return Optional.of(responseBodyBytes);
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while downloading image with URL={}", url, e);
        }

        return Optional.empty();
    }

    private interface FilenameAndExtensionData {
        String getFilenameWithoutExtension();

        String getFileExtension();
    }

    private VariableBuilder getVariableBuilder(CampaignComponentBuilder componentBuilder, String variableName,
        SettingType settingType)
        throws SettingNameLengthException, SettingInvalidNameException {

        try {
            return componentBuilder.updateSetting(dummyVariableForUpdate(variableName));
        } catch (RuntimeException e) {
            return (VariableBuilder) componentBuilder.addSetting(settingType).withName(variableName);
        }
    }

    private Variable dummyVariableForUpdate(String variableName) {
        return new Variable() {
            @Override
            public String getName() {
                return variableName;
            }

            @Override
            public Optional<String> getDisplayName() {
                return Optional.empty();
            }

            @Override
            public SettingType getType() {
                return null;
            }

            @Override
            public
                Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                    RuntimeEvaluatable<Object, Optional<Object>>>>
                getValues() {
                return null;
            }

            @Override
            public VariableSource getSource() {
                return null;
            }

            @Override
            public BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> getDescription() {
                return null;
            }

            @Override
            public Set<String> getTags() {
                return null;
            }

            @Override
            public DeweyDecimal getPriority() {
                return null;
            }
        };
    }

    private void applyEng21748NewDefinition(NewVariableDefinition definition,
        VariableBuilder variableBuilder,
        Set<String> tags,
        boolean hasDefaultVariant) throws SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, SettingTagLengthException {

        variableBuilder.withPriority(definition.getPriority());

        if (StringUtils.isNotBlank(definition.getProposedNewVariableDisplayName())) {
            variableBuilder.withDisplayName(definition.getProposedNewVariableDisplayName());
        }

        if (StringUtils.isNotBlank(definition.getProposedNewVariableDescription())) {
            variableBuilder.withDescription(Provided.optionalOf(definition.getProposedNewVariableDescription()));
        }

        Set<String> modifiedTags = Sets.newHashSet(tags);

        if (definition.getTranslatable().equals("YES") && !hasDefaultVariant) {
            modifiedTags.add("translatable");
        } else {
            modifiedTags.remove("translatable");
        }

        String category = definition.getCategory().toLowerCase().replaceAll(" ", "_");
        String subCategory = definition.getSubCategory().toLowerCase().replaceAll(" ", "_");

        modifiedTags.removeIf(tag -> tag.startsWith("importance:"));
        modifiedTags.removeIf(tag -> tag.startsWith("category:"));
        modifiedTags.add("importance:" + definition.getImportance().toLowerCase());
        modifiedTags.add("category:" + category + ":" + subCategory);

        variableBuilder.withTags(modifiedTags);
    }

    private List<Variable> getVariables(List<Setting> settings) {
        return settings.stream()
            .filter(setting -> setting instanceof Variable)
            .map(setting -> (Variable) setting)
            .collect(Collectors.toList());
    }

    private Optional<BuiltCampaignControllerTriggerEvent>
        lookupForInputEventTriggerWithNonEmptyEventNames(BuiltFrontendController builtFrontendController) {
        return builtFrontendController.getTriggers().stream()
            .filter(trigger -> trigger.getType() == CampaignControllerTriggerType.EVENT)
            .map(BuiltCampaignControllerTriggerEvent.class::cast)
            .filter(trigger -> trigger.getEventType() == CampaignControllerTriggerEventType.INPUT)
            .filter(trigger -> !trigger.getEventNames().isEmpty())
            .findFirst();
    }

}
