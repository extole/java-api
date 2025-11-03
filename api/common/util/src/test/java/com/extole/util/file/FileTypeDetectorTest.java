package com.extole.util.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class FileTypeDetectorTest {

    private static final String JPEG_FILE_NAME = "red_pixel.jpeg";
    private static final String PNG_FILE_NAME = "red_pixel.png";
    private static final String GIF_FILE_NAME = "red_pixel.gif";

    @Test
    public void detectValidFileMustReturnValidFileType() throws Exception {
        FileTypeDetector fileTypeDetector = new FileTypeDetector();
        ClassLoader currentClassLoader = FileTypeDetectorTest.class.getClassLoader();
        try (InputStream jpgInputStream = currentClassLoader.getResourceAsStream(JPEG_FILE_NAME);
            InputStream pngInputStream = currentClassLoader.getResourceAsStream(PNG_FILE_NAME);
            InputStream gifInputStream = currentClassLoader.getResourceAsStream(GIF_FILE_NAME)) {

            assertThat(fileTypeDetector.detect(jpgInputStream).getMimeType()).isEqualTo("image/jpeg");
            assertThat(fileTypeDetector.detect(pngInputStream.readAllBytes()).getMimeType()).isEqualTo("image/png");
            assertThat(fileTypeDetector.detect(gifInputStream, GIF_FILE_NAME).getMimeType()).isEqualTo("image/gif");
        }
    }
}
