package com.extole.common.javascript;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;

public class RepostPostHandlersDataFormatTests {

    @Test
    public void testDataFromReportIsConvertedToArrayAndIncludedInNotification() throws Exception {
        ScriptEngine scriptEngine = new JavascriptScriptEngineFactory()
            .create(JavascriptBuildtimeEvaluatable.class.getSimpleName())
            .withGlobalPerEngine(true)
            .withStrict(true)
            .withLazyCompilation(false)
            .withClassCacheSize(100)
            .build();

        Bindings bindings = scriptEngine.createBindings();
        bindings.put("context", new ReportPostHandlersDataFormatContext());

        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        List<String> result =
            (List<String>) unwrapScriptObjectMirror(
                scriptEngine.eval(
                    "load('src/test/resources/report-posthandlers/report-data-is-converted-to-array.js')",
                    scriptContext));

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writeValueAsString(result);
        JsonNode jsonNode = mapper.readTree(jsonResult);

        assertThat(jsonNode).isNotEmpty();
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(4);
        assertThat(jsonNode.get(0).isObject()).isTrue();
        assertThat(jsonNode.get(0).get("campaign_name").toString()).isEqualTo("\"Slide\"");
        assertThat(jsonNode.get(0).get("summary_rows").isArray()).isTrue();
        assertThat(jsonNode.get(0).get("summary_rows").size()).isEqualTo(14);
        assertThat(jsonNode.get(0).get("summary_rows").get(0).get("time_period").toString()).isEqualTo("\"WEEK\"");
    }

    private Object unwrapScriptObjectMirror(Object object) {
        if (object instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) object;
            if (mirror.isFunction()) {
                return mirror.toString();
            }
            if (mirror.isArray()) {
                return mirror.values()
                    .stream()
                    .map(element -> unwrapScriptObjectMirror(element))
                    .collect(Collectors.toList());
            }
            return mirror.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> unwrapScriptObjectMirror(entry.getValue())));
        }
        return object;
    }
}
