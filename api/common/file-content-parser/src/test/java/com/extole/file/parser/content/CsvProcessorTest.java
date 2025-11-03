package com.extole.file.parser.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

class CsvProcessorTest {
    private static final CsvProcessor CSV_PROCESSOR = CsvProcessor.INSTANCE;

    @Test
    void testReadAndWriteCsvWhichContainsNullsEmptyStringsAndOmittedCellValues() throws Exception {
        String initialCsv = "\"a\",\"b\",\"c\"\n"
            + "\"\",\"null\",\n"
            + ",\"test, value\",\" \"\n";

        List<Map<String, String>> elements = CSV_PROCESSOR.read(initialCsv);
        assertThat(elements)
            .hasSize(2)
            .anySatisfy(element -> {
                Map<String, String> expecteMap = new LinkedHashMap<>();
                expecteMap.put("a", "");
                expecteMap.put("b", "null");
                expecteMap.put("c", null);
                assertThat(element).isEqualTo(expecteMap);
            })
            .anySatisfy(element -> {
                Map<String, String> expecteMap = new LinkedHashMap<>();
                expecteMap.put("a", null);
                expecteMap.put("b", "test, value");
                expecteMap.put("c", " ");
                assertThat(element).isEqualTo(expecteMap);
            });

        String newCsv = CSV_PROCESSOR.write(elements, false);
        assertThat(newCsv).isEqualTo(initialCsv);
    }

    @Test
    void testWriteWithEmptiesNullsAndValidValues() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "");
        map.put("b", null);
        List<Map<String, String>> input = ImmutableList.of(
            map,
            ImmutableMap.of("c", " ", "b", "bvc"));

        String newCsv = CSV_PROCESSOR.write(input, true);
        assertThat(newCsv).isEqualTo("\"a\",\"b\",\"c\"\n"
            + "\"\",\"null\",\n"
            + ",\"bvc\",\" \"\n");
    }

    @Test
    void testReadWithEmptiesNullsAndValidValues() throws Exception {
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("a", "");
        map1.put("b", "null");
        map1.put("c", null);
        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("a", null);
        map2.put("b", "bvc");
        map2.put("c", " ");
        List<Map<String, String>> expectedResult = ImmutableList.of(
            map1, map2);

        String csvContent = "\"a\",\"b\",\"c\"\n"
            + "\"\",\"null\",\n"
            + ",\"bvc\",\" \"\n";

        assertThat(CSV_PROCESSOR.read(csvContent)).isEqualTo(expectedResult);
    }
}
