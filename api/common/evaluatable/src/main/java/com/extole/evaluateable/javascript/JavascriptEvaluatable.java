package com.extole.evaluateable.javascript;

import com.extole.evaluateable.Evaluatable;

public interface JavascriptEvaluatable<CONTEXT, RESULT> extends Evaluatable<CONTEXT, RESULT> {

    String getExpression();

}
