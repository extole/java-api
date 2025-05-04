package com.extole.api.impl.campaign;

import java.util.LinkedList;

public interface LoopPreventingEvaluator<KEY, EVALUATED> {

    EVALUATED evaluate(KEY key) throws EvaluationLogicException, LoopDetectedException;

    interface EvaluationLogic<KEY, EVALUATED> {
        EVALUATED evaluate(KEY key) throws Exception;
    }

    final class EvaluationLogicException extends Exception {
        public EvaluationLogicException(Throwable cause) {
            super(cause);
        }
    }

    final class LoopDetectedException extends Exception {

        private final LinkedList<Object> loopNodes;

        public LoopDetectedException(String message, LinkedList<Object> loopNodes) {
            super(message);
            this.loopNodes = loopNodes;
        }

        public LinkedList<Object> getLoopNodes() {
            return loopNodes;
        }
    }

}
