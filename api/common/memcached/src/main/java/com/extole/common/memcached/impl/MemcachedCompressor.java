package com.extole.common.memcached.impl;

interface MemcachedCompressor {

    byte[] decompress(String key, byte[] value) throws MemcachedCompressorException;

    byte[] compress(String key, byte[] value) throws MemcachedCompressorException;
}
