package com.extole.evaluation;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.GuavaCacheMetrics;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.ValidEvaluatableModule;
import com.extole.evaluateable.ecma.Js2025Evaluatable;
import com.extole.evaluateable.handlebars.HandlebarsEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsEvaluatableDeserializationException;
import com.extole.evaluateable.javascript.JavascriptEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelEvaluatable;
import com.extole.evaluateable.spel.SpelEvaluatableDeserializationException;

@Component
public class EvaluationServiceImpl implements EvaluationService {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private static final EvaluationResultConverter EVALUATION_RESULT_CONVERTER = new EvaluationResultConverter() {
        private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .registerModule(new SimpleModule().addDeserializer(String.class, new StdDeserializer<String>(String.class) {
                @Override
                public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                    if (jsonParser.getCurrentToken() != JsonToken.VALUE_STRING) {
                        throw deserializationContext.wrongTokenException(jsonParser,
                            deserializationContext.getTypeFactory().constructSimpleType(String.class, null),
                            JsonToken.VALUE_STRING,
                            "Only Strings into Strings are allowed");
                    }
                    return jsonParser.getValueAsString();
                }
            }))
            .registerModule(new ExtoleTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new GuavaModule())
            .registerModule(new ValidEvaluatableModule());

        @Override
        public <INPUT, OUTPUT> OUTPUT convert(INPUT input, JavaType expectedOutputType) throws Exception {
            if (input == null) {
                JavaType deserializationType = objectMapper.getTypeFactory()
                    .constructParametricType(NullValueRepresentation.class, expectedOutputType);
                return objectMapper.<NullValueRepresentation<OUTPUT>>readValue("{\"value\":null}",
                    deserializationType).value;
            }

            if (input instanceof String) {
                if (expectedOutputType.isContainerType() ||
                    expectedOutputType.isJavaLangObject() ||
                    expectedOutputType.isTypeOrSubTypeOf(Number.class) ||
                    expectedOutputType.isTypeOrSubTypeOf(Boolean.class)) {
                    return objectMapper.readValue((String) input, expectedOutputType);
                }
            }

            return objectMapper.readValue(objectMapper.writeValueAsString(input), expectedOutputType);
        }
    };

    private final LoadingCache<Evaluatable<?, ?>, EvaluatableExecutor<?, ?>> compiledEvaluatableCache;

    @Autowired
    public EvaluationServiceImpl(ExtoleMetricRegistry metricRegistry) {
        compiledEvaluatableCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public EvaluatableExecutor<?, ?> load(Evaluatable<?, ?> evaluatable) throws EvaluationException {
                    return compile(evaluatable);
                }
            });
        metricRegistry.registerAll(
            GuavaCacheMetrics.metricsFor("compiledEvaluatableCache", compiledEvaluatableCache));
    }

    @WithSpan
    @Override
    public <CONTEXT, RESULT> RESULT evaluate(Evaluatable<CONTEXT, RESULT> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier)
        throws EvaluationException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            if (CURRENT_EVALUATABLE.get() == null) {
                CURRENT_EVALUATABLE.set(evaluatable);
            }
            return internalEvaluate(evaluatable, contextSupplier);
        } finally {
            CURRENT_EVALUATABLE.remove();
            long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
            if (elapsed > 1) {
                LOG.warn("Evaluation for {} took {} seconds", evaluatable, Long.valueOf(elapsed));
            }
        }
    }

    private <CONTEXT, RESULT> RESULT internalEvaluate(Evaluatable<CONTEXT, RESULT> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier) throws EvaluationException {
        if (evaluatable == null) {
            throw new EvaluationException("null cannot be evaluated", evaluatable);
        }

        if (contextSupplier == null) {
            throw new EvaluationException("context supplier should be non null", evaluatable);
        }

        if (evaluatable == Provided.voided()) {
            return null;
        }

        if (isVoidType(evaluatable.getExpectedResultType())) {
            evaluateRaw(evaluatable, contextSupplier);
            return null;
        }

        RESULT evaluated = getEvaluated(evaluatable, contextSupplier);
        validate(evaluatable, evaluated);
        return evaluated;
    }

    private boolean isVoidType(JavaType expectedResultType) {
        return Optional.ofNullable(expectedResultType)
            .map(javaType -> javaType.getRawClass())
            .filter(type -> Boolean.valueOf(isVoidType(type)).booleanValue())
            .isPresent();
    }

    private <RESULT> void validate(Evaluatable<?, RESULT> evaluatable, RESULT evaluated)
        throws EvaluationException {
        checkNonNull(evaluatable, evaluated);

        RESULT unwrap = unwrap(evaluated);
        if (unwrap instanceof Collection) {
            for (Object element : (Collection<?>) unwrap) {
                checkNonNull(evaluatable, element);
            }
        }
    }

    @Nullable
    private <CONTEXT, RESULT> RESULT getEvaluated(Evaluatable<CONTEXT, RESULT> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier)
        throws EvaluationException {
        if (Provided.class == evaluatable.getClass()) {
            return ((Provided<CONTEXT, RESULT>) evaluatable).getValue();
        }

        RESULT rawEvaluationResult = evaluateRaw(evaluatable, contextSupplier);

        try {
            return EVALUATION_RESULT_CONVERTER.convert(rawEvaluationResult, evaluatable.getExpectedResultType());
        } catch (SpelEvaluatableDeserializationException e) {
            throw new SpelInvalidSyntaxEvaluationResultException(evaluatable, e.getCause());
        } catch (HandlebarsEvaluatableDeserializationException e) {
            throw new HandlebarsInvalidSyntaxEvaluationResultException(evaluatable, e.getCause());
        } catch (Exception e) {
            throw new EvaluationException("Failed to convert to expected type raw evaluated result " +
                rawEvaluationResult + " of evaluatable " + evaluatable, evaluatable, e);
        }
    }

    @Nullable
    private <CONTEXT, RESULT> RESULT evaluateRaw(Evaluatable<CONTEXT, RESULT> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier)
        throws EvaluationException {

        CONTEXT context = contextSupplier.get();
        if (context == null) {
            throw new EvaluationException("context should be non null", evaluatable);
        }

        return evaluate(evaluatable, context);
    }

    private <RESULT> RESULT unwrap(RESULT evaluationResult) {
        if (evaluationResult instanceof Optional) {
            return unwrap((RESULT) ((Optional) evaluationResult).orElse(null));
        }
        return evaluationResult;
    }

    @Nullable
    private <RESULT, CONTEXT> RESULT evaluate(Evaluatable<CONTEXT, RESULT> evaluatable, CONTEXT context)
        throws EvaluationException {
        try {
            return ((EvaluatableExecutor<CONTEXT, RESULT>) compiledEvaluatableCache.get(evaluatable)).evaluate(context);
        } catch (ExecutionException e) {
            throw new EvaluationException("Unable to get compiled evaluatable from cache", evaluatable, e);
        }
    }

    private <RESULT> void checkNonNull(Evaluatable<?, ?> evaluatable, RESULT evaluated)
        throws NullEvaluationResultException {
        if (evaluated == null) {
            throw new NullEvaluationResultException(evaluatable);
        }
    }

    private boolean isVoidType(Class<?> type) {
        return type == Void.class;
    }

    private interface EvaluationResultConverter {

        <INPUT, OUTPUT> OUTPUT convert(INPUT input, JavaType expectedOutputType) throws Exception;

    }

    /**
     * Used to deserialize null into various representations defined by the expected type
     *
     * @see StdDeserializer#getNullValue(DeserializationContext)
     */
    private static final class NullValueRepresentation<TYPE> {
        private final TYPE value;

        @JsonCreator
        private NullValueRepresentation(@JsonProperty("value") TYPE value) {
            this.value = value;
        }

    }

    private <CONTEXT, RESULT> EvaluatableExecutor<CONTEXT, RESULT> compile(Evaluatable<CONTEXT, RESULT> evaluatable)
        throws EvaluationException {
        if (JavascriptEvaluatable.class.isAssignableFrom(evaluatable.getClass())) {
            return CompiledJavascriptEvaluatableExecutorFactory
                .create((JavascriptEvaluatable<CONTEXT, RESULT>) evaluatable);
        }
        if (Js2025Evaluatable.class.isAssignableFrom(evaluatable.getClass())) {
            return CompiledJs2025EvaluatableExecutorFactory
                .create((Js2025Evaluatable<CONTEXT, RESULT>) evaluatable);
        }
        if (SpelEvaluatable.class.isAssignableFrom(evaluatable.getClass())) {
            return CompiledSpelEvaluatableExecutorFactory
                .create((SpelEvaluatable<CONTEXT, RESULT>) evaluatable);
        }
        if (HandlebarsEvaluatable.class.isAssignableFrom(evaluatable.getClass())) {
            return CompiledHandlebarsEvaluatableExecutorFactory
                .create((HandlebarsEvaluatable<CONTEXT, RESULT>) evaluatable);
        }
        throw new EvaluationException("Unknown Evaluatable type " + evaluatable.getClass(), evaluatable);
    }

}
