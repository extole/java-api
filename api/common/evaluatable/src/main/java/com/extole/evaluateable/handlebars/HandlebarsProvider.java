package com.extole.evaluateable.handlebars;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;

import com.extole.evaluateable.handlebars.helpers.RawBlockHelper;
import com.extole.evaluateable.handlebars.helpers.SafeCssStringHelper;
import com.extole.evaluateable.handlebars.helpers.SafeCssUrlHelper;
import com.extole.evaluateable.handlebars.helpers.SafeHtmlAttributeHelper;
import com.extole.evaluateable.handlebars.helpers.SafeHtmlContentHelper;
import com.extole.evaluateable.handlebars.helpers.SafeHtmlHelper;
import com.extole.evaluateable.handlebars.helpers.SafeHtmlUnquotedAttributeHelper;
import com.extole.evaluateable.handlebars.helpers.SafeJsAttributeHelper;
import com.extole.evaluateable.handlebars.helpers.SafeJsBlockHelper;
import com.extole.evaluateable.handlebars.helpers.SafeJsHelper;
import com.extole.evaluateable.handlebars.helpers.SafeUriComponentHelper;

public final class HandlebarsProvider {
    private HandlebarsProvider() {
    }

    private static final Handlebars HANDLEBARS = new Handlebars() {}
        .with(EscapingStrategy.NOOP)
        .registerHelper(RawBlockHelper.INSTANCE.getName(), RawBlockHelper.INSTANCE)
        .registerHelper(SafeHtmlHelper.INSTANCE.getName(), SafeHtmlHelper.INSTANCE)
        .registerHelper(SafeCssStringHelper.INSTANCE.getName(), SafeCssStringHelper.INSTANCE)
        .registerHelper(SafeCssUrlHelper.INSTANCE.getName(), SafeCssUrlHelper.INSTANCE)
        .registerHelper(SafeHtmlAttributeHelper.INSTANCE.getName(), SafeHtmlAttributeHelper.INSTANCE)
        .registerHelper(SafeHtmlContentHelper.INSTANCE.getName(), SafeHtmlContentHelper.INSTANCE)
        .registerHelper(SafeHtmlUnquotedAttributeHelper.INSTANCE.getName(), SafeHtmlUnquotedAttributeHelper.INSTANCE)
        .registerHelper(SafeJsAttributeHelper.INSTANCE.getName(), SafeJsAttributeHelper.INSTANCE)
        .registerHelper(SafeJsBlockHelper.INSTANCE.getName(), SafeJsBlockHelper.INSTANCE)
        .registerHelper(SafeJsHelper.INSTANCE.getName(), SafeJsHelper.INSTANCE)
        .registerHelper(SafeUriComponentHelper.INSTANCE.getName(), SafeUriComponentHelper.INSTANCE);

    public static Handlebars getInstance() {
        return HANDLEBARS;
    }
}
