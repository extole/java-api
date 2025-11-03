package com.extole.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.txt.UniversalEncodingDetector;

public final class FileEncodingDetector {

    public static final FileEncodingDetector INSTANCE = new FileEncodingDetector();

    private static final UniversalEncodingDetector ENCODING_DETECTOR = new UniversalEncodingDetector();

    public Charset detect(InputStream stream) throws IOException {
        return ENCODING_DETECTOR.detect(stream, new Metadata());
    }

    private FileEncodingDetector() {
    }
}
