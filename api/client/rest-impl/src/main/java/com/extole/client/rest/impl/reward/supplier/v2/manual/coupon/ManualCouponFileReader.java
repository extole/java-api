package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.reward.supplier.v2.ManualCouponRequest;
import com.extole.util.file.FileTypeDetector;
import com.extole.util.file.MimeTypeException;

@Component
class ManualCouponFileReader {
    private static final int FILE_BACKED_OUTPUT_STREAM_THRESHOLD = 256 * 1024;
    private static final FileTypeDetector FILE_TYPE_DETECTOR = new FileTypeDetector();

    private final ManualCouponFileParserFactory manualCouponFileParserFactory;

    @Autowired
    ManualCouponFileReader(ManualCouponFileParserFactory manualCouponFileParserFactory) {
        this.manualCouponFileParserFactory = manualCouponFileParserFactory;
    }

    public List<ManualCouponRequest> readCoupons(InputStream fileInputStream, Optional<String> fileName,
        ZoneId timeZone) throws ManualCouponFileReadException {
        try (FileBackedOutputStream fileBackedInputStream = newFileBackedInputStream(fileInputStream)) {
            return parse(fileBackedInputStream, fileName.orElse(null), timeZone);
        } catch (IOException | ManualCouponInvalidFileExtensionException | NotOfficeXmlFileException
            | MimeTypeException e) {
            throw new ManualCouponFileReadException(
                "Coupon file had an unsupported extension or was not properly formatted", e);
        }
    }

    private static boolean isPlainText(InputStream inputStream, String fileName)
        throws IOException, MimeTypeException {
        String fileExtension = FilenameUtils.getExtension(fileName);

        if (StringUtils.isNotBlank(fileExtension)) {
            return fileExtension.equalsIgnoreCase(ManualCouponFormat.TEXT.getExtension())
                || fileExtension.equalsIgnoreCase(ManualCouponFormat.CSV.getExtension());
        }
        return ManualCouponFormat.TEXT.getMimeType()
            .equalsIgnoreCase(FILE_TYPE_DETECTOR.detect(inputStream).getMimeType());
    }

    private static FileBackedOutputStream newFileBackedInputStream(InputStream inputStream) throws IOException {
        final FileBackedOutputStream fileBackedOutputStream =
            new FileBackedOutputStream(FILE_BACKED_OUTPUT_STREAM_THRESHOLD, true);

        InputStream bomInputStream = new BOMInputStream(inputStream);
        ByteStreams.copy(bomInputStream, fileBackedOutputStream);

        return fileBackedOutputStream;
    }

    private List<ManualCouponRequest> parse(FileBackedOutputStream inputStream, String fileName, ZoneId timeZone)
        throws ManualCouponFileReadException, ManualCouponInvalidFileExtensionException, IOException,
        MimeTypeException {
        ManualCouponFormat fileFormat =
            isPlainText(inputStream.asByteSource().openStream(), fileName) ? ManualCouponFormat.TEXT
                : ManualCouponFormat.XLSX;

        return manualCouponFileParserFactory.getParser(fileFormat).parse(inputStream.asByteSource().openStream(),
            timeZone);
    }
}
