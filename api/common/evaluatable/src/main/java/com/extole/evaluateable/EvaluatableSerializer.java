package com.extole.evaluateable;

import static com.extole.evaluateable.Evaluatable.BUILDTIME;
import static com.extole.evaluateable.Evaluatable.EXPRESSION_DELIMITER;
import static com.extole.evaluateable.Evaluatable.HANDLEBARS;
import static com.extole.evaluateable.Evaluatable.INSTALLTIME;
import static com.extole.evaluateable.Evaluatable.JAVASCRIPT;
import static com.extole.evaluateable.Evaluatable.JS2025;
import static com.extole.evaluateable.Evaluatable.PHASE_DELIMITER;
import static com.extole.evaluateable.Evaluatable.RUNTIME;
import static com.extole.evaluateable.Evaluatable.SPEL;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.extole.evaluateable.ecma.Js2025Evaluatable;
import com.extole.evaluateable.handlebars.HandlebarsEvaluatable;
import com.extole.evaluateable.javascript.JavascriptEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelEvaluatable;

public class EvaluatableSerializer<CONTEXT, RESULT> extends StdSerializer<Evaluatable<CONTEXT, RESULT>>
    implements ContextualSerializer {

    private BeanProperty property;
    private boolean serializeAsFieldName = false;

    public EvaluatableSerializer() {
        super(EvaluatableSerializer.class, false);
    }

    public EvaluatableSerializer(boolean writeAsFieldName) {
        super(EvaluatableSerializer.class, false);
        this.serializeAsFieldName = writeAsFieldName;
    }

    public EvaluatableSerializer(BeanProperty property) {
        super(EvaluatableSerializer.class, false);
        this.property = property;
    }

    @Override
    public void serialize(Evaluatable<CONTEXT, RESULT> value, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws IOException {
        Object serializable = extractSerializable(value);

        if (serializable == null) {
            serializerProvider.defaultSerializeNull(jsonGenerator);
        } else {
            if (serializeAsFieldName) {
                jsonGenerator.writeFieldName(serializable.toString());
            } else {
                JsonSerializer<Object> serializer =
                    serializerProvider.findValueSerializer(serializable.getClass(), property);
                serializer.serialize(serializable, jsonGenerator, serializerProvider);
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) {
        return new EvaluatableSerializer<>(beanProperty);
    }

    private Object extractSerializable(Evaluatable<CONTEXT, RESULT> value) {
        if (value instanceof JavascriptEvaluatable) {
            return serializeJavascriptEvaluatable((JavascriptEvaluatable<CONTEXT, RESULT>) value);
        }

        if (value instanceof SpelEvaluatable) {
            return serializeSpelEvaluatable((SpelEvaluatable<CONTEXT, RESULT>) value);
        }

        if (value instanceof HandlebarsEvaluatable) {
            return serializeHandlebarsEvaluatable((HandlebarsEvaluatable<CONTEXT, RESULT>) value);
        }

        if (value instanceof Js2025Evaluatable) {
            return serializeJs2025Evaluatable((Js2025Evaluatable<CONTEXT, RESULT>) value);
        }

        if (value instanceof Provided) {
            return ((Provided<CONTEXT, RESULT>) value).getValue();
        }
        throw new RuntimeException("Unsupported Evaluatable " + value);
    }

    private String serializeJavascriptEvaluatable(JavascriptEvaluatable<CONTEXT, RESULT> evaluatable) {
        return JAVASCRIPT + PHASE_DELIMITER + phase(evaluatable) + EXPRESSION_DELIMITER + evaluatable.getExpression();
    }

    private String serializeSpelEvaluatable(SpelEvaluatable<CONTEXT, RESULT> evaluatable) {
        return SPEL + PHASE_DELIMITER + phase(evaluatable) + EXPRESSION_DELIMITER + evaluatable.getExpression();
    }

    private String serializeHandlebarsEvaluatable(HandlebarsEvaluatable<CONTEXT, RESULT> evaluatable) {
        return HANDLEBARS + PHASE_DELIMITER + phase(evaluatable) + EXPRESSION_DELIMITER + evaluatable.getExpression();
    }

    private String serializeJs2025Evaluatable(Js2025Evaluatable<CONTEXT, RESULT> evaluatable) {
        return JS2025 + PHASE_DELIMITER + phase(evaluatable) + EXPRESSION_DELIMITER
            + evaluatable.getExpression();
    }

    private String phase(Evaluatable<CONTEXT, RESULT> evaluatable) {
        if (RuntimeEvaluatable.class.isAssignableFrom(evaluatable.getClass())) {
            return RUNTIME;
        } else if (InstalltimeEvaluatable.class.isAssignableFrom(evaluatable.getClass())) {
            return INSTALLTIME;
        } else {
            return BUILDTIME;
        }
    }

}
