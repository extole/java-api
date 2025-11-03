package com.extole.common.lang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnicodeUtilsTest {
    private static final String UNICODE_SAMPLES_JAVA_FILE_NAME = "unicode_samples_java.txt";

    private static final List<String> SAMPLE_LANGUAGE_NAMES =
        Arrays.asList("russian", "turkish", "japanese", "chinese", "korean", "hebrew", "armenian", "greek");

    private Properties properties;

    @BeforeEach
    public void setup() throws IOException {
        properties = new Properties();
        properties.load(UnicodeUtilsTest.class.getClassLoader().getResourceAsStream(UNICODE_SAMPLES_JAVA_FILE_NAME));
    }

    @Test
    public void replaceNonUTF8CharactersInBMPUnicodeSamplesMustNotChangeTheInput() {
        for (String languageName : SAMPLE_LANGUAGE_NAMES) {
            String unicodeText = properties.getProperty(languageName);
            assertNotNull(unicodeText);
            assertThat(UnicodeUtils.replaceNonUTF8Characters(unicodeText)).isEqualTo(unicodeText);
        }
    }

    @Test
    public void replaceNonUTF8CharactersInUnicodeEmojiChangesInput() {
        assertThat(UnicodeUtils.replaceNonUTF8Characters("Alien \uD83D\uDC7D")).isEqualTo("Alien ?");
        assertThat(UnicodeUtils.replaceNonUTF8Characters("Broken heart \uD83D\uDC94")).isEqualTo("Broken heart ?");
    }

    @Test
    public void replaceNonUTF8CharactersThrowsNPE() {
        assertThrows(NullPointerException.class, () -> UnicodeUtils.replaceNonUTF8Characters(null));
    }

}
