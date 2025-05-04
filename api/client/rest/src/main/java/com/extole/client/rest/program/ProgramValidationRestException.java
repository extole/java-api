package com.extole.client.rest.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProgramValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ProgramValidationRestException> INVALID_PROGRAM_DOMAIN =
        new ErrorCode<>("invalid_program_domain", 400, "Invalid program domain", "program_domain");

    public static final ErrorCode<ProgramValidationRestException> PROGRAM_DOMAIN_CONTAINS_INVALID_SITE_PATTERN =
        new ErrorCode<>("program_domain_contains_invalid_site_pattern", 400,
            "Program domain contains an invalid site pattern", "site_pattern", "type");

    public static final ErrorCode<ProgramValidationRestException> PROGRAM_REDIRECT_GENERATES_LOOP =
        new ErrorCode<>("program_redirect_generates_loop", 400, "Program redirect generates loop", "redirect_program");

    public static final ErrorCode<ProgramValidationRestException> NON_EXISTENT_REDIRECT_PROGRAM =
        new ErrorCode<>("non_existent_redirect_program_id", 400, "Redirect program doesn't exist");

    public static final ErrorCode<ProgramValidationRestException> INVALID_NAME =
        new ErrorCode<>("invalid_name", 400, "The program name is invalid", "program_name");

    public static final ErrorCode<ProgramValidationRestException> SSL_PUBLIC_KEY_NOT_YET_VALID = new ErrorCode<>(
        "ssl_public_key_not_yet_valid", 400, "Public Key not yet valid", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_CHAIN_KEY_NOT_YET_VALID = new ErrorCode<>(
        "ssl_chain_key_not_yet_valid", 400, "Chain key chain not yet valid", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_KEY_INVALID = new ErrorCode<>(
        "ssl_key_invalid", 400, "Invalid format for public key", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_KEY_CHAIN_OR_PRIVATE_KEY_MISSING_OR_INVALID =
        new ErrorCode<>(
            "ssl_key_chain_or_private_key_missing_or_invalid", 400,
            "One of the SSL keys (public, chain, private) is missing or invalid",
            "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_PRIVATE_KEY_MISSING = new ErrorCode<>(
        "ssl_private_key_missing", 400, "SSL private key is missing");

    public static final ErrorCode<ProgramValidationRestException> SSL_PRIVATE_KEY_INVALID = new ErrorCode<>(
        "ssl_private_key_invalid", 400, "Invalid format for SSL private key", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_PRIVATE_KEY_INVALID_FORMAT = new ErrorCode<>(
        "ssl_private_key_invalid_format", 400, "Invalid format for SSL private key", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_PUBLIC_PRIVATE_KEYS_NOT_MATCHING =
        new ErrorCode<>("ssl_public_private_keys_mismatch", 400, "Key pair public/private does not match");

    public static final ErrorCode<ProgramValidationRestException> SSL_PUBLIC_KEY_EXPIRED = new ErrorCode<>(
        "ssl_public_key_expired", 400, "Public key expired", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_CHAIN_KEY_EXPIRED = new ErrorCode<>(
        "ssl_chain_key_expired", 400, "Chain key expired", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_SELF_SIGNED_CERTIFICATE_NOT_ACCEPTED =
        new ErrorCode<>(
            "ssl_self_signed_certificate_not_accepted", 400, "Self-signed certificates cannot be used", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_CERTIFICATE_NOT_SIGNED_BY_CHAIN = new ErrorCode<>(
        "ssl_certificate_not_signed_by_chain", 400, "Certificate not signed by the chain", "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_KEYS_NOT_ALLOWED_FOR_EXTOLE_DOMAIN =
        new ErrorCode<>(
            "ssl_keys_not_allowed_for_extole_domain", 400,
            "Cannot specify SSL attributes for extole.io or extole.com domains",
            "client_id");

    public static final ErrorCode<ProgramValidationRestException> SSL_CERTIFICATE_INVALID_DOMAIN = new ErrorCode<>(
        "ssl_certificate_invalid_domain", 400,
        "SSL Certificate does not match program domain",
        "client_id");

    public static final ErrorCode<ProgramValidationRestException> CNAME_TARGET_NOT_APPLICABLE_FOR_EXTOLE_DOMAINS =
        new ErrorCode<>(
            "cname_target_not_applicable_for_extole_domains", 400, "Can't set CNAME target for Extole domains",
            "client_id");

    public static final ErrorCode<ProgramValidationRestException> CNAME_TARGET_INVALID_FORMAT = new ErrorCode<>(
        "cname_target_invalid_format", 400, "CNAME target has invalid format. Expected: *.extole.io or *.extole.com",
        "client_id");

    public static final ErrorCode<ProgramValidationRestException> CNAME_TARGET_INVALID = new ErrorCode<>(
        "cname_target_invalid", 400, "Invalid CNAME target domain",
        "client_id");

    public static final ErrorCode<ProgramValidationRestException> CNAME_TARGET_DUPLICATE = new ErrorCode<>(
        "cname_target_duplicate", 400, "The CNAME target is being used by another program",
        "client_id");

    public ProgramValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
