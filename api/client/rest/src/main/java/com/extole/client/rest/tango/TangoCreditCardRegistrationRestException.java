package com.extole.client.rest.tango;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoCreditCardRegistrationRestException extends ExtoleRestException {

    public static final String CAUSE = "cause";

    public static final ErrorCode<TangoCreditCardRegistrationRestException> IP_ADDRESS_INVALID =
        new ErrorCode<>("ip_address_invalid", 400, "Ip Address is invalid", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> LABEL_INVALID =
        new ErrorCode<>("label_invalid", 400, "Label is invalid", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_INVALID =
        new ErrorCode<>("billing_address_invalid", 400, "Billing address is invalid", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_FIRST_NAME_INVALID =
        new ErrorCode<>("billing_address_first_name_invalid", 400, "Invalid first name", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_LAST_NAME_INVALID =
        new ErrorCode<>("billing_address_last_name_invalid", 400, "Invalid last name", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_LINE_1_INVALID =
        new ErrorCode<>("billing_address_line_1_invalid", 400, "Invalid billing address", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_LINE_2_INVALID =
        new ErrorCode<>("billing_address_line_2_invalid", 400, "Invalid billing address", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_CITY_INVALID =
        new ErrorCode<>("billing_address_city_invalid", 400, "Invalid city", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_STATE_INVALID =
        new ErrorCode<>("billing_address_state_invalid", 400, "Invalid state", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_POSTAL_CODE_INVALID =
        new ErrorCode<>("billing_address_postal_code_invalid", 400, "Invalid postal code", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_COUNTRY_INVALID =
        new ErrorCode<>("billing_address_country_invalid", 400, "Invalid country", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BILLING_ADDRESS_EMAIL_INVALID =
        new ErrorCode<>("billing_address_email_invalid", 400, "Invalid email provided for billing address", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> CONTACT_INFORMATION_INVALID =
        new ErrorCode<>("contact_information_invalid", 400, "Invalid contact information", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> CONTACT_INFORMATION_EMAIL_INVALID =
        new ErrorCode<>("contact_information_email_invalid", 400, "Invalid email address", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> CONTACT_INFORMATION_FULL_NAME_INVALID =
        new ErrorCode<>("contact_information_full_name_invalid", 400, "Invalid full name", CAUSE);

    public static final ErrorCode<TangoCreditCardRegistrationRestException> BAD_REQUEST =
        new ErrorCode<>("bad_request", 400, "Bad request", CAUSE);

    public TangoCreditCardRegistrationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
