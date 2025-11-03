package com.extole.evaluateable.spel;

import com.extole.evaluateable.Evaluatable;

public interface SpelEvaluatable<CONTEXT, RESULT> extends Evaluatable<CONTEXT, RESULT> {

    String getExpression();

}
