package com.extole.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

public final class FileTypeDetector {
    private static final String SUFFIX_REGEX = "\\s*\\(\\d+\\)";
    private static final Pattern SUFFIX_REGEX_PATTERN = Pattern.compile(SUFFIX_REGEX);
    private final Tika tika;
    private final TikaConfig configuration;

    public FileTypeDetector() {
        this.configuration = TikaConfig.getDefaultConfig();
        this.tika = new Tika(configuration);
    }

    public MimeType detect(String fileName) throws MimeTypeException {
        return detect(fileName, MimeType.BINARY);
    }

    public MimeType detect(String fileName, MimeType defaultMimeType) throws MimeTypeException {
        String mimeType = tika.detect(fileName);
        if (MimeType.BINARY.getMimeType().equalsIgnoreCase(mimeType)) {
            String detectedMimeType = defaultMimeType.getMimeType();

            String[] suffixes = fileName.split("\\.");
            for (int i = suffixes.length - 1; i > 0; i--) {
                Matcher matcher = SUFFIX_REGEX_PATTERN.matcher(suffixes[i]);
                String suffix = matcher.find() ? matcher.group() : suffixes[i];
                String type = tika.detect(StringUtils.removeEnd(fileName, suffix));
                if (!MimeType.BINARY.getMimeType().equalsIgnoreCase(type)) {
                    detectedMimeType = type;
                    break;
                } else {
                    fileName = StringUtils.removeEnd(fileName, "." + suffixes[i]);
                }
            }
            mimeType = detectedMimeType;
        }
        return computeMimeType(mimeType);
    }

    public MimeType detect(byte[] prefix) throws MimeTypeException {
        return computeMimeType(tika.detect(prefix));
    }

    public MimeType detect(InputStream stream) throws IOException, MimeTypeException {
        return computeMimeType(tika.detect(stream));
    }

    public MimeType detect(InputStream stream, String name) throws IOException, MimeTypeException {
        return computeMimeType(tika.detect(stream, name));
    }

    private MimeType computeMimeType(String mediaType) throws MimeTypeException {
        try {
            org.apache.tika.mime.MimeType mimeType = configuration.getMimeRepository().forName(mediaType);
            return new MimeType(mimeType.getName(), mimeType.getExtension().replaceFirst("\\.", "").toUpperCase());
        } catch (org.apache.tika.mime.MimeTypeException e) {
            throw new MimeTypeException("Could not parse media type", e);
        }
    }
}
