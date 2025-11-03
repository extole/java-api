package com.extole.evaluateable;

import com.extole.common.lang.ThrowingConsumer;

public interface BuildtimeEvaluatable<CONTEXT, RESULT> extends Evaluatable<CONTEXT, RESULT> {

    default <EXCEPTION extends Exception> void
        ifDefined(BuildtimeEvaluatableThrowingConsumer<EXCEPTION, CONTEXT, RESULT> consumer) throws EXCEPTION {
        if (Evaluatable.isDefined(this)) {
            consumer.accept(this);
        }
    }

    @FunctionalInterface
    interface BuildtimeEvaluatableThrowingConsumer<EXCEPTION extends Exception, CONTEXT, RESULT>
        extends ThrowingConsumer<BuildtimeEvaluatable<CONTEXT, RESULT>, EXCEPTION> {

        @Override
        void accept(BuildtimeEvaluatable<CONTEXT, RESULT> type) throws EXCEPTION;

    }

}
