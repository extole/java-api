package com.extole.api.impl.campaign;

import static com.extole.api.campaign.VariableUnavailabilityCause.NAME_NOT_FOUND;
import static com.extole.api.campaign.VariableUnavailabilityCause.RUNTIME;
import static com.extole.api.campaign.VariableUnavailabilityCause.VARIANT_NOT_FOUND;
import static com.extole.model.entity.campaign.Setting.COMPONENT_DESCRIPTION_SETTING_NAME;
import static com.extole.model.entity.campaign.Setting.COMPONENT_DISPLAY_NAME_SETTING_NAME;
import static com.extole.model.entity.campaign.Setting.COMPONENT_ID_SETTING_NAME;
import static com.extole.model.entity.campaign.Setting.COMPONENT_NAME_SETTING_NAME;
import static com.extole.model.entity.campaign.Variable.DEFAULT_VALUE_KEY;

import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.ClientContext;
import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.Component;
import com.extole.api.campaign.ComponentAsset;
import com.extole.api.campaign.Element;
import com.extole.api.campaign.ElementsQueryBuilder;
import com.extole.api.campaign.VariableContext;
import com.extole.api.impl.ClientContextImpl;
import com.extole.api.impl.campaign.LoopPreventingEvaluator.EvaluationLogicException;
import com.extole.api.impl.campaign.LoopPreventingEvaluator.LoopDetectedException;
import com.extole.api.impl.campaign.summary.ResolvedVariable;
import com.extole.api.impl.service.GlobalServicesFactory;
import com.extole.api.service.GlobalServices;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.log.execution.ExecutionLoggerFactory;
import com.extole.evaluateable.handlebars.ShortVariableSyntaxContext;
import com.extole.evaluation.EvaluationService;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.client.PublicClient;
import com.extole.spring.ServiceLocator;

public final class CampaignBuildtimeContextImpl implements CampaignBuildtimeContext, ShortVariableSyntaxContext {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignBuildtimeContextImpl.class);

    private final Campaign campaign;
    private final ClientContext clientContext;
    private final PublicClient client;
    private final Map<String, ComponentAsset> assets;
    private final ServiceLocator serviceLocator;
    private final Map<VariableKey, FlatVariable> variables;
    private final LoopPreventingEvaluator<FlatVariable, ResolvedVariable> loopPreventingEvaluator;
    private final GlobalServices globalServices;
    private final ExecutionLogger logger;
    private final Map<Id<CampaignComponent>, List<Element>> elements;
    private final List<String> fallbackKeys;
    private final Set<Id<Campaign>> exclusionList = new HashSet<>() {
        {
            add(Id.valueOf("6833485240468705616"));
            add(Id.valueOf("6833519423060832922"));
            add(Id.valueOf("6836382347958751855"));
            add(Id.valueOf("6847526453247087105"));
            add(Id.valueOf("6875685835073617340"));
            add(Id.valueOf("6875685950868263807"));
            add(Id.valueOf("6880158589424312383"));
            add(Id.valueOf("6896622894657530331"));
            add(Id.valueOf("6934788142892551528"));
            add(Id.valueOf("6938369829472289500"));
            add(Id.valueOf("6954104301292295207"));
            add(Id.valueOf("6956603715274916939"));
            add(Id.valueOf("6958913563162617103"));
            add(Id.valueOf("6971433284273852092"));
            add(Id.valueOf("6987854053059396618"));
            add(Id.valueOf("6987869692264700904"));
            add(Id.valueOf("7016014619452021839"));
            add(Id.valueOf("7018176692722745385"));
            add(Id.valueOf("7024215131975850317"));
            add(Id.valueOf("7026337451712498582"));
            add(Id.valueOf("7035948237075903220"));
            add(Id.valueOf("7046526000266118239"));
            add(Id.valueOf("7049432924036827608"));
            add(Id.valueOf("7050172486005367392"));
            add(Id.valueOf("7057249665884438798"));
            add(Id.valueOf("7060234544701565523"));
            add(Id.valueOf("7062471554810067562"));
            add(Id.valueOf("7062479397638607139"));
            add(Id.valueOf("7063570947441499880"));
            add(Id.valueOf("7069678977794258703"));
            add(Id.valueOf("7072304309726327720"));
            add(Id.valueOf("7082854625371734002"));
            add(Id.valueOf("7084276350415078428"));
            add(Id.valueOf("7085756251803838370"));
            add(Id.valueOf("7085756489198540105"));
            add(Id.valueOf("7089476271193926316"));
            add(Id.valueOf("7091636033097124815"));
            add(Id.valueOf("7093864962233014100"));
            add(Id.valueOf("7099189476574109894"));
            add(Id.valueOf("7106626302173698441"));
            add(Id.valueOf("7124497417933854507"));
            add(Id.valueOf("7125430557560795244"));
            add(Id.valueOf("7130601120389180398"));
            add(Id.valueOf("7133258543409566700"));
            add(Id.valueOf("7138083651459063173"));
            add(Id.valueOf("7143291474221983197"));
            add(Id.valueOf("7143313116766618370"));
            add(Id.valueOf("7150295156411875365"));
            add(Id.valueOf("7151811811365454294"));
            add(Id.valueOf("7160752123676363535"));
            add(Id.valueOf("7169257975567109149"));
            add(Id.valueOf("7174145378527555184"));
            add(Id.valueOf("7174722175550460816"));
            add(Id.valueOf("7179233585262191728"));
            add(Id.valueOf("7193017461434872420"));
            add(Id.valueOf("7197822038187266682"));
            add(Id.valueOf("7200043767702707570"));
            add(Id.valueOf("7200096858867570256"));
            add(Id.valueOf("7200104759840167570"));
            add(Id.valueOf("7202953355764128346"));
            add(Id.valueOf("7202954115430709220"));
            add(Id.valueOf("7202956834832949887"));
            add(Id.valueOf("7204908426195065939"));
            add(Id.valueOf("7210771849012405006"));
            add(Id.valueOf("7210772062333666664"));
            add(Id.valueOf("7210772192668772476"));
            add(Id.valueOf("7213801148848808033"));
            add(Id.valueOf("7213862756473044675"));
            add(Id.valueOf("7218451585994944718"));
            add(Id.valueOf("7218600159042115326"));
            add(Id.valueOf("7218992417149748775"));
            add(Id.valueOf("7223442355431113567"));
            add(Id.valueOf("7225578893208199674"));
            add(Id.valueOf("7226155412482686695"));
            add(Id.valueOf("7226427544154993191"));
            add(Id.valueOf("7229373020128050222"));
            add(Id.valueOf("7229416742095794300"));
            add(Id.valueOf("7229816729708831132"));
            add(Id.valueOf("7231542029474992911"));
            add(Id.valueOf("7231544878997777259"));
            add(Id.valueOf("7234884160563679265"));
            add(Id.valueOf("7236080437098163955"));
            add(Id.valueOf("7236700143851555599"));
            add(Id.valueOf("7238690110196761696"));
            add(Id.valueOf("7238962661334621182"));
            add(Id.valueOf("7239415780352393312"));
            add(Id.valueOf("7241158419935787681"));
            add(Id.valueOf("7241238043028892144"));
            add(Id.valueOf("7241405952037878796"));
            add(Id.valueOf("7242035290052526413"));
            add(Id.valueOf("7242379791809907985"));
            add(Id.valueOf("7242398347278080779"));
            add(Id.valueOf("7242826870462341950"));
            add(Id.valueOf("7243954304920503810"));
            add(Id.valueOf("7244917473385087572"));
            add(Id.valueOf("7245035152549472917"));
            add(Id.valueOf("7245261748491105429"));
            add(Id.valueOf("7247118476249955686"));
            add(Id.valueOf("7247972237387288031"));
            add(Id.valueOf("7250198594473388467"));
            add(Id.valueOf("7251646302313485457"));
            add(Id.valueOf("7252215665472892616"));
            add(Id.valueOf("7254265959849953652"));
            add(Id.valueOf("7254526507104162628"));
            add(Id.valueOf("7255345395357415448"));
            add(Id.valueOf("7257302703622237869"));
            add(Id.valueOf("7257858853122147750"));
            add(Id.valueOf("7259465281482863723"));
            add(Id.valueOf("7259722532493854384"));
            add(Id.valueOf("7259724076153773152"));
            add(Id.valueOf("7260514968059303448"));
            add(Id.valueOf("7260515849452265886"));
            add(Id.valueOf("7260515902082169815"));
            add(Id.valueOf("7261957419485753016"));
            add(Id.valueOf("7262475835510483448"));
            add(Id.valueOf("7263475935059255409"));
            add(Id.valueOf("7264552604594364238"));
            add(Id.valueOf("7265049487941984871"));
            add(Id.valueOf("7265396750309541843"));
            add(Id.valueOf("7265409734091009380"));
            add(Id.valueOf("7266155363121092292"));
            add(Id.valueOf("7267643268006624984"));
            add(Id.valueOf("7267687051356593295"));
            add(Id.valueOf("7267967023177118889"));
            add(Id.valueOf("7268331329407150460"));
            add(Id.valueOf("7268682785904162968"));
            add(Id.valueOf("7269874809245267954"));
            add(Id.valueOf("7270139491219962987"));
            add(Id.valueOf("7270195061224241245"));
            add(Id.valueOf("7271310910484984079"));
            add(Id.valueOf("7272809207305779182"));
            add(Id.valueOf("7272822248490237111"));
            add(Id.valueOf("7273486488799222139"));
            add(Id.valueOf("7273511327866708450"));
            add(Id.valueOf("7273895581824537855"));
            add(Id.valueOf("7273896282107055449"));
            add(Id.valueOf("7275494121991531209"));
            add(Id.valueOf("7275784649529291415"));
            add(Id.valueOf("7276184499757866328"));
            add(Id.valueOf("7276476155481968169"));
            add(Id.valueOf("7277938304967278381"));
            add(Id.valueOf("7277950037803298947"));
            add(Id.valueOf("7278257233410951222"));
            add(Id.valueOf("7278281193841345638"));
            add(Id.valueOf("7278288683813088717"));
            add(Id.valueOf("7278402858679710066"));
            add(Id.valueOf("7280295880073581501"));
            add(Id.valueOf("7280358480800082662"));
            add(Id.valueOf("7281312681343098873"));
            add(Id.valueOf("7283000117567339340"));
            add(Id.valueOf("7283132051242732742"));
            add(Id.valueOf("7284243063014815563"));
            add(Id.valueOf("7285490702791917100"));
            add(Id.valueOf("7288423390751906150"));
            add(Id.valueOf("7288719765653609975"));
            add(Id.valueOf("7290694241002869666"));
            add(Id.valueOf("7293283618119583418"));
            add(Id.valueOf("7293521609753609177"));
            add(Id.valueOf("7293868038697789366"));
            add(Id.valueOf("7293874062421298983"));
            add(Id.valueOf("7293874376568492501"));
            add(Id.valueOf("7293874712465751367"));
            add(Id.valueOf("7293874967014083364"));
            add(Id.valueOf("7293875761208768718"));
            add(Id.valueOf("7296177603317834817"));
            add(Id.valueOf("7296962167645589319"));
            add(Id.valueOf("7298837890464942464"));
            add(Id.valueOf("7301368144347997282"));
            add(Id.valueOf("7301775169532758267"));
            add(Id.valueOf("7302546446165946019"));
            add(Id.valueOf("7304679697744298390"));
            add(Id.valueOf("7306250735571141498"));
            add(Id.valueOf("7307695953299611561"));
            add(Id.valueOf("7308802227854191114"));
            add(Id.valueOf("7310199829262195271"));
            add(Id.valueOf("7310657303419588761"));
            add(Id.valueOf("7311429496608674209"));
            add(Id.valueOf("7311432832376738642"));
            add(Id.valueOf("7311434683156371086"));
            add(Id.valueOf("7311779023148176011"));
            add(Id.valueOf("7314078896093945904"));
            add(Id.valueOf("7315553673378002531"));
            add(Id.valueOf("7316899826851772484"));
            add(Id.valueOf("7317035505295895194"));
            add(Id.valueOf("7317361068981099950"));
            add(Id.valueOf("7319599429859235800"));
            add(Id.valueOf("7322215501850207950"));
            add(Id.valueOf("7322252103044326744"));
            add(Id.valueOf("7322912449515058924"));
            add(Id.valueOf("7324353440376752428"));
            add(Id.valueOf("7324361949363661974"));
            add(Id.valueOf("7324398988813700744"));
            add(Id.valueOf("7324786620453850537"));
            add(Id.valueOf("7325472916963833986"));
            add(Id.valueOf("7325480187358640013"));
            add(Id.valueOf("7326948662673427753"));
            add(Id.valueOf("7327277970075766322"));
            add(Id.valueOf("7327355302557805672"));
            add(Id.valueOf("7328618347105329200"));
            add(Id.valueOf("7329634905389411085"));
            add(Id.valueOf("7329636746340692868"));
            add(Id.valueOf("7332385071476773177"));
            add(Id.valueOf("7332554228631040643"));
            add(Id.valueOf("7332842245912599870"));
            add(Id.valueOf("7332865776717656520"));
            add(Id.valueOf("7332866820089429980"));
            add(Id.valueOf("7332902658070405956"));
            add(Id.valueOf("7332934920675544538"));
            add(Id.valueOf("7333162285775727810"));
            add(Id.valueOf("7333252715881155714"));
            add(Id.valueOf("7333322049113054824"));
            add(Id.valueOf("7333630002785823422"));
            add(Id.valueOf("7333641755441227500"));
            add(Id.valueOf("7334798480179965661"));
            add(Id.valueOf("7334813098210887855"));
            add(Id.valueOf("7334825240339503295"));
            add(Id.valueOf("7334828080667552249"));
            add(Id.valueOf("7334863846947201698"));
        }
    };

    public CampaignBuildtimeContextImpl(
        Campaign campaign,
        PublicClient client,
        Map<VariableKey, FlatVariable> variables,
        Map<String, ComponentAsset> assets,
        GlobalServicesFactory globalServicesFactory,
        ServiceLocator serviceLocator,
        LoopPreventingEvaluator<FlatVariable, ResolvedVariable> loopPreventingEvaluator,
        ZoneId clientTimezone,
        Map<Id<CampaignComponent>, List<Element>> elements,
        List<String> fallbackKeys) {
        this.campaign = campaign;
        this.elements = elements;
        this.fallbackKeys = fallbackKeys;
        this.clientContext = new ClientContextImpl(client.getId().getValue(), client.getShortName(),
            clientTimezone.getId());
        this.client = client;
        this.assets = assets;
        this.serviceLocator = serviceLocator;
        this.variables = ImmutableMap.copyOf(variables);
        this.loopPreventingEvaluator = loopPreventingEvaluator;
        this.globalServices = globalServicesFactory.createBuilder(client.getId(), "campaign", campaign.getId(),
            clientTimezone)
            .build();
        this.logger = ExecutionLoggerFactory.newInstance(getClass());
    }

    @Nullable
    @Override
    public Component getComponent() {

        String componentId = String.valueOf(getVariableContext().get(COMPONENT_ID_SETTING_NAME));

        if (componentId == null) {
            return null;
        }

        return new Component() {

            @Override
            public String getId() {
                return componentId;
            }

            @Override
            public String getName() {
                return String.valueOf(getVariableContext().get(COMPONENT_NAME_SETTING_NAME));
            }

            @Override
            public String getDisplayName() {
                return String.valueOf(getVariableContext().get(COMPONENT_DISPLAY_NAME_SETTING_NAME));
            }

            @Nullable
            @Override
            public String getDescription() {
                return String.valueOf(getVariableContext().get(COMPONENT_DESCRIPTION_SETTING_NAME));
            }

            @Override
            public ElementsQueryBuilder createElementsQuery() {
                return new ElementsQueryBuilderImpl(
                    elements.getOrDefault(Id.valueOf(componentId), Collections.emptyList()));
            }

        };
    }

    @Override
    public VariableContext getVariableContext() {
        return getVariableContext(Variable.DEFAULT_VALUE_KEY);
    }

    @Override
    public VariableContext getVariableContext(String defaultKey) {
        return getVariableContext(new String[] {defaultKey});
    }

    @Override
    public VariableContext getVariableContext(String... defaultKeys) {
        VariableContext target = new VariableContext() {

            @Override
            public Object get(String name) {
                return get(name, new String[] {});
            }

            @Override
            public Object get(String name, String key) {
                return get(name, new String[] {key});
            }

            @Override
            public Object get(String name, String... keys) {
                List<String> evaluatingVariants = computeEvaluatingKeys(keys);
                Optional<FlatVariable> firstCandidate = evaluatingVariants.stream()
                    .map(key -> VariableKey.of(name, key))
                    .map(key -> variables.get(key))
                    .filter(value -> Objects.nonNull(value))
                    .findFirst();
                Optional<ResolvedVariable> result = firstCandidate.map(flatVariable -> {
                    try {
                        return loopPreventingEvaluator.evaluate(flatVariable);
                    } catch (EvaluationLogicException | LoopDetectedException e) {
                        String messageTemplate = "Failed to evaluate variable name = %s," +
                            " key = %s, value = %s, client = %s, campaign = %s ";
                        String message = String.format(messageTemplate, flatVariable.getVariableKey().getName(),
                            flatVariable.getVariableKey().getKey(), flatVariable.getValue(), client.getId(),
                            campaign.getId());
                        throw new FlatVariableEvaluationRuntimeException(e, message);
                    }
                });

                if (firstCandidate.isEmpty() || (result.isPresent() && result.get() == ResolvedVariable.RUNTIME)) {
                    String stacktrace = null;
                    if (loopPreventingEvaluator instanceof CachedLoopPreventingEvaluatorImpl) {
                        stacktrace = ((CachedLoopPreventingEvaluatorImpl<FlatVariable, ?>) loopPreventingEvaluator)
                            .getStack()
                            .stream()
                            .map(flatVariable -> flatVariable.getVariableKey().getName() + "["
                                + flatVariable.getVariableKey().getKey() + "]" + " = " + flatVariable.getValue())
                            .collect(Collectors.joining("\n"));
                    }

                    List<String> availableVariants = variables.keySet()
                        .stream()
                        .filter(variableKey -> variableKey.getName().equalsIgnoreCase(name))
                        .map(VariableKey::getKey)
                        .collect(Collectors.toList());
                    if (firstCandidate.isEmpty()) {
                        if (!exclusionList.contains(campaign.getId())) {
                            throw new UnavailableReferencedVariableRuntimeException(name, evaluatingVariants,
                                availableVariants, availableVariants.isEmpty() ? NAME_NOT_FOUND : VARIANT_NOT_FOUND);
                        }
                        LOG.warn(
                            "Dry run ENG-21229. Variable with name = {}, keys = {} not found. While evaluating {}" +
                                " The campaign {} for client {} build would fail with enabled validation!" +
                                " Full path : \n{}\n",
                            name, evaluatingVariants, EvaluationService.CURRENT_EVALUATABLE.get(),
                            campaign.getId(), client.getId(), stacktrace);
                    } else {
                        if (!exclusionList.contains(campaign.getId())) {
                            throw new UnavailableReferencedVariableRuntimeException(name, evaluatingVariants,
                                availableVariants, RUNTIME);
                        }
                        LOG.warn(
                            "Dry run ENG-21229. Variable with name = {}, keys = {} is runtime. While evaluating {}" +
                                " The campaign {} for client {} build would fail with enabled validation!" +
                                " Full path : \n{}\n",
                            name, evaluatingVariants, EvaluationService.CURRENT_EVALUATABLE.get(),
                            campaign.getId(), client.getId(), stacktrace);
                    }
                }

                return result.map(ResolvedVariable::get).orElse(null);
            }

            private List<String> computeEvaluatingKeys(String[] keys) {
                Set<String> evaluatingVariants = Sets.newLinkedHashSet(List.of(keys.length == 0 ? defaultKeys : keys));
                evaluatingVariants.add(DEFAULT_VALUE_KEY);
                evaluatingVariants.addAll(fallbackKeys);
                return Lists.newArrayList(evaluatingVariants);
            }

            @Override
            public Object getVariable(String name, String... keys) {
                return get(name, keys);
            }
        };

        return serviceLocator.create(InitializableVariableContext.class).initialize(client.getId(), target);
    }

    @Override
    public ComponentAsset getAsset(String assetName) {
        return assets.get(assetName);
    }

    @Override
    public String getProgramLabel() {
        return campaign.getProgramLabel().getName();
    }

    @Override
    public Object getVariable(String name, String... keys) {
        return getVariableContext().get(name, keys);
    }

    @Override
    public ClientContext getClientContext() {
        return clientContext;
    }

    @Override
    public GlobalServices getGlobalServices() {
        return globalServices;
    }

    @Override
    public void log(String message) {
        logger.log(message);
    }

}
