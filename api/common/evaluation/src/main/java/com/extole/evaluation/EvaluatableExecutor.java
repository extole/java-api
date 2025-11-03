package com.extole.evaluation;

interface EvaluatableExecutor<CONTEXT, RESULT> {

    RESULT evaluate(CONTEXT context) throws EvaluationException;

}
