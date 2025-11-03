package com.extole.evaluateable;

import static com.extole.evaluateable.Evaluatable.BUILDTIME;
import static com.extole.evaluateable.Evaluatable.HANDLEBARS;
import static com.extole.evaluateable.Evaluatable.INSTALLTIME;
import static com.extole.evaluateable.Evaluatable.JAVASCRIPT;
import static com.extole.evaluateable.Evaluatable.JS2025;
import static com.extole.evaluateable.Evaluatable.RUNTIME;
import static com.extole.evaluateable.Evaluatable.SPEL;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.lang.deserializer.CacheableStdDeserializer;
import com.extole.evaluateable.ecma.Js2025BuildtimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025EvaluatableDeserializationException;
import com.extole.evaluateable.ecma.Js2025ExpressionCompileTimeException;
import com.extole.evaluateable.ecma.Js2025ExpressionInvalidLengthException;
import com.extole.evaluateable.ecma.Js2025InstalltimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025RuntimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsBuildtimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsEvaluatableDeserializationException;
import com.extole.evaluateable.handlebars.HandlebarsExpressionCompileTimeException;
import com.extole.evaluateable.handlebars.HandlebarsInstalltimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsRuntimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptInstalltimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptRuntimeEvaluatable;
import com.extole.evaluateable.normalization.EvaluatableExpressionNormalizer;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
import com.extole.evaluateable.spel.SpelEvaluatableDeserializationException;
import com.extole.evaluateable.spel.SpelExpressionCompileTimeException;
import com.extole.evaluateable.spel.SpelInstalltimeEvaluatable;
import com.extole.evaluateable.spel.SpelRuntimeEvaluatable;
import com.extole.evaluateable.validation.EvaluatableValidator;

class EvaluatableDeserializer extends CacheableStdDeserializer<Evaluatable<?, ?>>
    implements ContextualDeserializer {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluatableDeserializer.class);

    private static final Set<String> LANGUAGE_SUPPORTS_EMPTY_EXPRESSIONS = ImmutableSet.of(HANDLEBARS);

    private static final Map<String, Set<Class<?>>> SUPPORTED_EVALUATABLE_TYPES = ImmutableMap.of(
        BUILDTIME, ImmutableSet.of(Evaluatable.class, BuildtimeEvaluatable.class, JavascriptBuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class, HandlebarsBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class),
        RUNTIME, ImmutableSet.of(Evaluatable.class, RuntimeEvaluatable.class, JavascriptRuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class, HandlebarsRuntimeEvaluatable.class, Js2025RuntimeEvaluatable.class),
        INSTALLTIME, ImmutableSet.of(Evaluatable.class, InstalltimeEvaluatable.class,
            JavascriptInstalltimeEvaluatable.class, SpelInstalltimeEvaluatable.class,
            HandlebarsInstalltimeEvaluatable.class, Js2025InstalltimeEvaluatable.class));

    private JavaType evaluatableResultType;
    private Class<?> evaluatableType;
    private boolean isOptional;
    private EvaluatableExpressionNormalizer normalizer;
    private EvaluatableValidator validator;

    protected EvaluatableDeserializer() {
        super((JavaType) null);
    }

    protected EvaluatableDeserializer(DeserializationContext deserializationContext,
        BeanProperty beanProperty, EvaluatableExpressionNormalizer normalizer, EvaluatableValidator validator)
        throws JsonMappingException {
        this(deserializationContext.getContextualType().getBindings().getBoundType(1),
            deserializationContext.getContextualType().getRawClass(),
            beanProperty != null && Optional.class == beanProperty.getType().getRawClass(), normalizer, validator);

        JavaType contextualType = deserializationContext.getContextualType();

        if (!Evaluatable.class.isAssignableFrom(contextualType.getRawClass())) {
            throw InvalidDefinitionException.from(deserializationContext,
                "This deserializer supports only " + Evaluatable.class.getSimpleName());
        }

        if (contextualType.getBindings().size() != 2) {
            throw InvalidDefinitionException.from(deserializationContext,
                "Raw Evaluatable is not supported. Both type parameters must be specified.");
        }
    }

    protected EvaluatableDeserializer(JavaType evaluatableResultType, Class<?> evaluatableType, boolean isOptional,
        EvaluatableExpressionNormalizer normalizer, EvaluatableValidator validator) {
        this();
        this.evaluatableResultType = evaluatableResultType;
        this.evaluatableType = evaluatableType;
        this.isOptional = isOptional;
        this.normalizer = normalizer;
        this.validator = validator;
    }

    @Override
    public Evaluatable<?, ?> deserialize(JsonParser parser, DeserializationContext deserializationContext)
        throws IOException {
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
            Optional<Evaluatable<?, ?>> parsedEvaluatable = tryParseEvaluatable(deserializationContext, value);
            if (parsedEvaluatable.isPresent()) {
                Evaluatable<?, ?> evaluatable = parsedEvaluatable.get();
                validate(evaluatable, deserializationContext);
                return evaluatable;
            }
        }

        return providedEvaluatable(parser);
    }

    private Evaluatable<?, ?> validate(Evaluatable<?, ?> evaluatable, DeserializationContext deserializationContext)
        throws IOException {
        try {
            validator.validate(evaluatable);
        } catch (SpelExpressionCompileTimeException e) {
            throw new SpelEvaluatableDeserializationException(deserializationContext.getParser(), e);
        } catch (Js2025ExpressionInvalidLengthException | Js2025ExpressionCompileTimeException e) {
            throw new Js2025EvaluatableDeserializationException(deserializationContext.getParser(), e);
        } catch (HandlebarsExpressionCompileTimeException e) {
            throw new HandlebarsEvaluatableDeserializationException(deserializationContext.getParser(), e);
        }
        return evaluatable;
    }

    Evaluatable<?, ?> deserializeKey(String key, DeserializationContext deserializationContext) throws IOException {
        return tryParseEvaluatable(deserializationContext, key)
            .orElseGet(() -> Provided.of(key));
    }

    private Evaluatable<?, ?> providedEvaluatable(JsonParser parser) throws IOException {
        return Provided.of(parser.getCodec().readValue(parser, evaluatableResultType));
    }

    private Optional<Evaluatable<?, ?>> tryParseEvaluatable(DeserializationContext deserializationContext, String value)
        throws IOException {

        Optional<ExpressionEvaluatableType> detectedExpressionEvaluatableType = ExpressionEvaluatableType.detect(value);

        if (detectedExpressionEvaluatableType.isEmpty()) {
            return Optional.empty();
        }

        ExpressionEvaluatableType expressionEvaluatableType = detectedExpressionEvaluatableType.get();
        String language = expressionEvaluatableType.getLanguage();
        String phase = expressionEvaluatableType.getPhase();
        String expression = StringUtils.substringAfter(value, expressionEvaluatableType.getPrefixHeader());

        if (expression.isEmpty() && !LANGUAGE_SUPPORTS_EMPTY_EXPRESSIONS.contains(language)) {
            return Optional.empty();
        }

        expression = normalizer.normalize(expression, expressionEvaluatableType);
        if (SUPPORTED_EVALUATABLE_TYPES.get(phase).contains(this.evaluatableType)) {
            switch (language) {
                case SPEL:
                    if (BUILDTIME.equals(phase)) {
                        return Optional.of(SpelBuildtimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (RUNTIME.equals(phase)) {
                        return Optional.of(SpelRuntimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (INSTALLTIME.equals(phase)) {
                        return Optional.of(SpelInstalltimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    break;
                case JAVASCRIPT:
                    if (BUILDTIME.equals(phase)) {
                        return Optional.of(JavascriptBuildtimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (RUNTIME.equals(phase)) {
                        return Optional.of(JavascriptRuntimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (INSTALLTIME.equals(phase)) {
                        return Optional.of(JavascriptInstalltimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    break;
                case JS2025:
                    if (BUILDTIME.equals(phase)) {
                        return Optional.of(Js2025BuildtimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (RUNTIME.equals(phase)) {
                        return Optional.of(Js2025RuntimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (INSTALLTIME.equals(phase)) {
                        return Optional.of(Js2025InstalltimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    break;
                case HANDLEBARS:
                    if (BUILDTIME.equals(phase)) {
                        return Optional.of(HandlebarsBuildtimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (RUNTIME.equals(phase)) {
                        return Optional.of(HandlebarsRuntimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    if (INSTALLTIME.equals(phase)) {
                        return Optional.of(HandlebarsInstalltimeEvaluatable.of(expression, evaluatableResultType));
                    }
                    break;
                default:
                    throw MismatchedInputException.from(deserializationContext, "This should never happen");
            }
            throw MismatchedInputException.from(deserializationContext, "This should never happen");
        } else {
            return Optional.empty();
        }
    }

    @Override
    public EvaluatableDeserializer createContextual(DeserializationContext deserializationContext,
        BeanProperty beanProperty) throws JsonMappingException {
        return new EvaluatableDeserializer(deserializationContext, beanProperty,
            EvaluatableExpressionNormalizer.DEFAULT_NOOP, EvaluatableValidator.DEFAULT_NOOP);
    }

    private JsonDeserializer<?> getDefaultDeserializer(DeserializationContext deserializationContext)
        throws JsonMappingException {
        return deserializationContext.findRootValueDeserializer(evaluatableResultType);
    }

    @Override
    public Evaluatable<?, ?> getNullValue(DeserializationContext deserializationContext) throws JsonMappingException {
        if (isOptional) {
            return null;
        }
        if (evaluatableResultType.isTypeOrSubTypeOf(Evaluatable.class)) {
            return Provided.of(getDefaultDeserializer(deserializationContext).getNullValue(deserializationContext));
        }
        if (Void.class == evaluatableResultType.getRawClass()) {
            return Provided.voided();
        }
        if (Optional.class == evaluatableResultType.getRawClass()) {
            return Provided.optionalEmpty();
        }
        return Provided.nullified();
    }
}
