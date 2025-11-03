package com.extole.evaluateable;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.evaluateable.provided.Provided;

@Schema
@JsonDeserialize(using = EvaluatableDeserializer.class, keyUsing = EvaluatableKeyDeserializer.class)
@JsonSerialize(using = EvaluatableSerializer.class, keyUsing = EvaluatableKeySerializer.class)
public interface Evaluatable<CONTEXT, RESULT> {

    String SPEL = "spel";
    String JAVASCRIPT = "javascript";
    String JS2025 = "js2025";
    String HANDLEBARS = "handlebars";
    String PHASE_DELIMITER = "@";
    String BUILDTIME = "buildtime";
    String RUNTIME = "runtime";
    String INSTALLTIME = "installtime";
    String EXPRESSION_DELIMITER = ":";

    static boolean isDefined(Evaluatable<?, ?> evaluatable) {
        if (evaluatable instanceof Provided) {
            Object value = ((Provided<?, ?>) evaluatable).getValue();
            if (value instanceof Evaluatable) {
                return isDefined((Evaluatable<?, ?>) value);
            }
        }
        return !Provided.nullified().equals(evaluatable) && !Provided.optionalEmpty().equals(evaluatable);
    }

    static <CONTEXT, RESULT, EVALUATABLE extends Evaluatable<CONTEXT, RESULT>> EVALUATABLE
        defaultIfUndefined(EVALUATABLE evaluatable, EVALUATABLE defaultEvaluatable) {
        return isDefined(evaluatable) ? evaluatable : defaultEvaluatable;
    }

    JavaType getExpectedResultType();

}
