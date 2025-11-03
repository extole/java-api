package com.extole.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.lang.DeploymentEnvironment;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.model.entity.blocks.Block;
import com.extole.model.entity.blocks.ListType;

public final class BlockProvider {
    private static final Logger LOG = LoggerFactory.getLogger(BlockProvider.class);

    private static final JsonArrayDeserializer INVALID_JSON_ARRAY_DESERIALIZER = new JsonArrayDeserializer();
    private static final String BLOCK_SOURCE_URLS_JSON = "native_block_source_urls.json";
    private static final int MINIMUM_USER_AGENT_BLOCK_LENGTH = 3;
    private static final int MAXIMUM_BLOCK_VALUE_LENGTH = 500;
    private static final String ZERO_TO_255 = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    public static final Predicate<String> CIDR_V4_VALIDATOR = Pattern
        .compile("^(" + ZERO_TO_255 + "\\.){3}" + ZERO_TO_255 + "/(3[0-2]|[12]?[0-9])$")
        .asMatchPredicate();

    private static final List<BlockResourceReference> RESOURCE_REFERENCES = readBlockResourceReferences();

    private BlockProvider() {
    }

    public static Stream<Block> provideAll() {
        LOG.debug("All global blocks were provided");
        List<Pair<BlockResourceReference, Supplier<InputStream>>> lazyResourceProviders = RESOURCE_REFERENCES.stream()
            .map(resourceReference -> Pair.of(resourceReference,
                createLazyResourceProvider("/block_files/" + resourceReference.getResourceName())))
            .collect(Collectors.toUnmodifiableList());

        return lazyResourceProviders
            .stream()
            .flatMap(pair -> {
                Instant now = Instant.now();
                BlockResourceReference blockResourceReference = pair.getLeft();
                Supplier<InputStream> contentProvider = pair.getRight();
                List<String> parsedBlocks =
                    parseBlocks(contentProvider.get(), blockResourceReference.getBlockContentType());
                return parsedBlocks.stream()
                    .filter(Objects::nonNull)
                    .map(value -> value.toLowerCase())
                    .map(value -> new BlockValue(
                        blockResourceReference.getBlockResourceType(),
                        blockResourceReference.getBlockContentType(),
                        blockResourceReference.getResourceName(),
                        blockResourceReference.getUrl(),
                        now,
                        value))
                    .filter(
                        block -> valueIsValid(block.getValue(), block.getUrl(), block.getBlockResourceType().name()))
                    .map(block -> buildBlockValue(block.getBlockResourceType().name(), block.getUrl(), block.getValue(),
                        block.getCreatedDate()));
            });
    }

    private static boolean valueIsValid(String value, String url, String type) {
        boolean valid = !StringUtils.isBlank(value)
            && value.length() <= MAXIMUM_BLOCK_VALUE_LENGTH
            && !StringUtils.containsWhitespace(value)
            && !StringUtils.containsAny(value, '#', '$', '%', '^', '&', '<', '>');
        if (type.equals(ListType.USER_AGENT.name()) && value.length() < MINIMUM_USER_AGENT_BLOCK_LENGTH) {
            valid = false;
            LOG.warn("Block for {} from {} skipped because of being too short", value, url);
        }
        if (type.equals(ListType.CIDR.name()) && CIDR_V4_VALIDATOR.negate().test(cidrfy(value))) {
            valid = false;
            LOG.warn("Block for {} from {} skipped because not a valid cidr", value, url);
        }
        if (!valid) {
            LOG.warn("Block for {} from {} not created as value is not valid", value, url);
        }
        return valid;

    }

    private static Block buildBlockValue(String type, String url, String value, Instant createdDate) {
        ListType listType = ListType.valueOf(type.toUpperCase());
        if (listType == ListType.CIDR) {
            value = cidrfy(value);
        }
        return new BlockImpl(listType, url, value, createdDate);
    }

    private static String cidrfy(String value) {
        return StringUtils.contains(value, "/") ? value : value.concat("/32");
    }

    private static Supplier<InputStream> createLazyResourceProvider(String resourceName) {
        return () -> readResource(resourceName);
    }

    private static List<String> parseBlocks(InputStream content, BlockFileContentType contentType) {
        try {
            if (BlockFileContentType.JSON == contentType) {
                String blocksAsString = IOUtils.toString(content);
                return deserializeArray(blocksAsString);
            }
            if (BlockFileContentType.TEXT == contentType) {
                List<String> lines = new LinkedList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(content))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
                return Collections.unmodifiableList(lines);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        } finally {
            IOUtils.closeQuietly(content);
        }
        throw new IllegalArgumentException("Not handled resource type " + contentType);
    }

    private static List<BlockResourceReference> readBlockResourceReferences() {
        byte[] fileContent;
        try (InputStream inputStream = readResource("/" + BLOCK_SOURCE_URLS_JSON)) {
            fileContent = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Map<String, String>> records = deserialize(fileContent, new TypeReference<>() {});
        return records.stream()
            .filter(map -> {
                String environmentAsString = map.get("environment");
                if (environmentAsString == null) {
                    return false;
                }
                if ("ALL".equals(environmentAsString)) {
                    return true;
                }
                return DeploymentEnvironment.valueOf(
                    StringUtils.upperCase(environmentAsString)) == DeploymentEnvironment.getDeploymentEnvironment();
            })
            .map(map -> {
                String type = map.get("type");
                String format = map.get("format");
                String url = map.get("url");
                String resourceName = StringUtils.substringAfterLast(url, "/");
                return new BlockResourceReference(BlockResourceType.valueOf(type), BlockFileContentType.valueOf(format),
                    url, resourceName);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<String> deserializeArray(String serialized) {
        try {
            return OBJECT_MAPPER.readValue(serialized, new TypeReference<>() {});
        } catch (IOException e) {
            return INVALID_JSON_ARRAY_DESERIALIZER.deserialize(serialized);
        }
    }

    private static <T> T deserialize(byte[] src, TypeReference<T> valueTypeRef) {
        try {
            return OBJECT_MAPPER.readValue(src, valueTypeRef);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static InputStream readResource(String path) {
        return Objects.requireNonNull(
            BlockProvider.class.getResourceAsStream(path), "Can't read resource: " + path);
    }

}
