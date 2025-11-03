package com.extole.common.persistence;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteSource;
import net.sf.persist.SizeAware;

/**
 * An input stream wrapper which provides an additional size method
 */
public class SizeAwareInputStream extends InputStream implements SizeAware {

    private final ByteSource byteSource;
    private final InputStream inputStream;

    public SizeAwareInputStream(ByteSource byteSource) throws IOException {
        this.byteSource = byteSource;
        this.inputStream = byteSource.openStream();
    }

    @Override
    public int size() throws IOException {
        return Long.valueOf(byteSource.size()).intValue();
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return inputStream.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        return inputStream.read(bytes, offset, length);
    }

    @Override
    public long skip(long bytes) throws IOException {
        return inputStream.skip(bytes);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readLimit) {
        inputStream.mark(readLimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
}
