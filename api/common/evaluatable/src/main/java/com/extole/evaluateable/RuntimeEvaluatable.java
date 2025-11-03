package com.extole.evaluateable;

import com.extole.common.lang.ThrowingConsumer;

public interface RuntimeEvaluatable<CONTEXT, RESULT> extends Evaluatable<CONTEXT, RESULT> {

    default <EXCEPTION extends Exception> void
        ifDefined(RuntimeEvaluatableThrowingConsumer<EXCEPTION, CONTEXT, RESULT> consumer) throws EXCEPTION {
        if (Evaluatable.isDefined(this)) {
            consumer.accept(this);
        }
    }

    @FunctionalInterface
    interface RuntimeEvaluatableThrowingConsumer<EXCEPTION extends Exception, CONTEXT, RESULT>
        extends ThrowingConsumer<RuntimeEvaluatable<CONTEXT, RESULT>, EXCEPTION> {

        @Override
        void accept(RuntimeEvaluatable<CONTEXT, RESULT> type) throws EXCEPTION;

    }

}
