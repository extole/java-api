package com.extole.evaluateable.handlebars;

import com.extole.evaluateable.Evaluatable;

public interface HandlebarsEvaluatable<CONTEXT, RESULT> extends Evaluatable<CONTEXT, RESULT> {

    String getExpression();

}
