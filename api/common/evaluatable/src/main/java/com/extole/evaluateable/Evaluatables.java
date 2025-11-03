package com.extole.evaluateable;

import java.util.Collection;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.evaluateable.provided.Provided;

public interface Evaluatables {

    static <ENUM extends Optional<?>, NEW_ENUM extends Optional<
        ?>, RUNTIME_CONTEXT, BUILDTIME_CONTEXT, RUNTIME_EVALUATABLE extends RuntimeEvaluatable<RUNTIME_CONTEXT,
            ENUM>, BUILDTIME_EVALUATABLE extends BuildtimeEvaluatable<BUILDTIME_CONTEXT,
                RUNTIME_EVALUATABLE>, NEW_RUNTIME_EVALUATABLE extends RuntimeEvaluatable<RUNTIME_CONTEXT,
                    NEW_ENUM>, NEW_BUILDTIME_EVALUATABLE extends BuildtimeEvaluatable<BUILDTIME_CONTEXT,
                        NEW_RUNTIME_EVALUATABLE>>
        NEW_BUILDTIME_EVALUATABLE remapNestedOptional(BUILDTIME_EVALUATABLE evaluatable,
            TypeReference<NEW_BUILDTIME_EVALUATABLE> newTypeRef) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(evaluatable.toString(), newTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <CONTEXT, ENUM extends Optional<?>, NEW_ENUM extends Optional<?>, EVALUATABLE extends Evaluatable<CONTEXT,
        ENUM>, NEW_EVALUATABLE extends Evaluatable<CONTEXT, NEW_ENUM>>
        NEW_EVALUATABLE remapOptional(EVALUATABLE evaluatable, TypeReference<NEW_EVALUATABLE> newTypeRef) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(evaluatable.toString(), newTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <CONTEXT, ENUM extends Enum<?>, NEW_ENUM extends Enum<?>, EVALUATABLE extends Evaluatable<CONTEXT,
        ENUM>, NEW_EVALUATABLE extends Evaluatable<CONTEXT, NEW_ENUM>>
        NEW_EVALUATABLE remapEnum(EVALUATABLE evaluatable, TypeReference<NEW_EVALUATABLE> newTypeRef) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(evaluatable.toString(), newTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <CONTEXT, ENUM extends Enum<?>, NEW_ENUM extends Enum<?>, COLLECTION extends Collection<
        ENUM>, NEW_COLLECTION extends Collection<NEW_ENUM>, EVALUATABLE extends Evaluatable<CONTEXT,
            COLLECTION>, NEW_EVALUATABLE extends Evaluatable<CONTEXT, NEW_COLLECTION>>
        NEW_EVALUATABLE remapEnumCollection(EVALUATABLE evaluatable, TypeReference<NEW_EVALUATABLE> newTypeRef) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(evaluatable.toString(), newTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <CONTEXT, TYPE, NEW_TYPE, COLLECTION extends Collection<TYPE>, NEW_COLLECTION extends Collection<
        NEW_TYPE>, EVALUATABLE extends Evaluatable<CONTEXT,
            COLLECTION>, NEW_EVALUATABLE extends Evaluatable<CONTEXT, NEW_COLLECTION>>
        NEW_EVALUATABLE remapCollection(EVALUATABLE evaluatable, TypeReference<NEW_EVALUATABLE> newTypeRef) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(evaluatable.toString(), newTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <CONTEXT, CLASS, NEW_CLASS, EVALUATABLE extends Evaluatable<CONTEXT,
        CLASS>, NEW_EVALUATABLE extends Evaluatable<CONTEXT, NEW_CLASS>>
        NEW_EVALUATABLE remapClassToClass(EVALUATABLE evaluatable, TypeReference<NEW_EVALUATABLE> newTypeRef) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(evaluatable.toString(), newTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <CONTEXT, RESULT, EVALUATABLE extends Evaluatable<CONTEXT, RESULT>> RESULT
        provided(EVALUATABLE evaluatable) {
        return ((Provided<CONTEXT, RESULT>) evaluatable).getValue();
    }

    static <OUTER_CONTEXT, INNER_CONTEXT, RESULT, INNER_EVALUATABLE extends Evaluatable<INNER_CONTEXT,
        RESULT>, OUTER_EVALUATABLE extends Evaluatable<OUTER_CONTEXT, INNER_EVALUATABLE>>
        RESULT nestedProvided(OUTER_EVALUATABLE evaluatable) {
        return provided(provided(evaluatable));
    }

    static <CONTEXT, RESULT, EVALUATABLE extends Evaluatable<CONTEXT, RESULT>>
        boolean isProvided(EVALUATABLE evaluatable) {
        return evaluatable instanceof Provided;
    }

    static <OUTER_CONTEXT, INNER_CONTEXT, RESULT, INNER_EVALUATABLE extends Evaluatable<INNER_CONTEXT,
        RESULT>, OUTER_EVALUATABLE extends Evaluatable<OUTER_CONTEXT, INNER_EVALUATABLE>>
        boolean isNestedProvided(OUTER_EVALUATABLE evaluatable) {
        if (!isProvided(evaluatable)) {
            return false;
        }

        INNER_EVALUATABLE innerEvaluatable = provided(evaluatable);

        if (!isProvided(innerEvaluatable)) {
            return false;
        }

        return true;
    }

}
