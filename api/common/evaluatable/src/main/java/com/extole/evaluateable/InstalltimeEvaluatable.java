package com.extole.evaluateable;

import com.extole.common.lang.ThrowingConsumer;

public interface InstalltimeEvaluatable<CONTEXT, RESULT> extends Evaluatable<CONTEXT, RESULT> {

    default <EXCEPTION extends Exception> void
        ifDefined(InstalltimeEvaluatableThrowingConsumer<EXCEPTION, CONTEXT, RESULT> consumer) throws EXCEPTION {
        if (Evaluatable.isDefined(this)) {
            consumer.accept(this);
        }
    }

    @FunctionalInterface
    interface InstalltimeEvaluatableThrowingConsumer<EXCEPTION extends Exception, CONTEXT, RESULT>
        extends ThrowingConsumer<InstalltimeEvaluatable<CONTEXT, RESULT>, EXCEPTION> {

        @Override
        void accept(InstalltimeEvaluatable<CONTEXT, RESULT> type) throws EXCEPTION;

    }

}
