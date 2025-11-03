package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentDuplicateRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentDuplicateRestException> COMPONENT_COLLISION = new ErrorCode<>(
        "component_collision", 400, "Could not avoid collision by suffix the name of component",
        "component_name");

    public static final ErrorCode<ComponentDuplicateRestException> ROOT_DUPLICATION_ATTEMPT = new ErrorCode<>(
        "root_component_duplication", 400, "Root component is not allowed for duplication");

    public static final ErrorCode<ComponentDuplicateRestException> MISSING_TARGET_COMPONENT_BY_ABSOLUTE_NAME =
        new ErrorCode<>(
            "missing_target_component_by_absolute_name", 400, "Target component by absolute name is missing",
            "absolute_name");

    public static final ErrorCode<ComponentDuplicateRestException> UNIQUE_COMPONENT_ELEMENT_REQUIRED = new ErrorCode<>(
        "component_element_uniqueness_required", 400, "Unique component element is required",
        "element_type", "reference_value");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_SOURCE_ELEMENT_ID_MISSING = new ErrorCode<>(
        "missing_anchor_source_element_id", 400, "Anchor source element id is required");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_TARGET_ELEMENT_ID_MISSING = new ErrorCode<>(
        "missing_anchor_target_element_id", 400, "Anchor target element id is required");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_MISSING = new ErrorCode<>(
        "missing_anchor", 400, "Expected anchor is not defined", "source_element_id", "source_element_type");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_NO_DEFAULT_CANDIDATE = new ErrorCode<>(
        "no_default_anchor", 400, "No default anchor candidate could be identified in target component",
        "source_element_id", "source_element_type");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_MANY_DEFAULT_CANDIDATES = new ErrorCode<>(
        "many_default_anchors", 400, "No default anchor candidate could be identified in target component",
        "source_element_id", "source_element_type", "candidates");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_INVALID = new ErrorCode<>(
        "invalid_anchor", 400, "Invalid anchor. Target element has wrong type or not found", "source_element_id",
        "source_element_type", "target_element_id", "expected_target_element_types");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_UNRECOGNIZED = new ErrorCode<>(
        "unrecognized_anchors", 400, "Some anchors are not expected", "source_element_ids");

    public static final ErrorCode<ComponentDuplicateRestException> ANCHOR_AMBIGUOUS = new ErrorCode<>(
        "ambiguous_anchor", 400, "Anchor definition is ambiguous", "source_element_id");

    public static final ErrorCode<ComponentDuplicateRestException> SOCKET_FILTER_TYPE_MISMATCH = new ErrorCode<>(
        "socket_filter_type_mismatch", 400, "Socket filter type mismatch", "source_component_types",
        "filter_component_type");

    public static final ErrorCode<ComponentDuplicateRestException> SOCKET_FILTER_COMPONENT_FACET_MISMATCH =
        new ErrorCode<>(
            "socket_filter_component_facet_mismatch", 400, "Socket filter component facet mismatch",
            "source_component_facets", "filter_component_facet_name", "filter_component_facet_value");

    public static final ErrorCode<ComponentDuplicateRestException> MISSING_SOURCE_COMPONENT_TYPE = new ErrorCode<>(
        "missing_source_component_type", 400, "Source component type is missing", "component_id", "socket_name",
        "expected_component_type");

    public static final ErrorCode<ComponentDuplicateRestException> COMPONENT_INSTALL_FAILED = new ErrorCode<>(
        "component_install_failed", 400, "Component install failed", "component_id", "error_message");

    public ComponentDuplicateRestException(String uniqueId, ErrorCode<ComponentDuplicateRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
