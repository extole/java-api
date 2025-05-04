package com.extole.consumer.rest.impl.validation;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.validation.Address;
import com.extole.consumer.rest.validation.AddressComponent;
import com.extole.consumer.rest.validation.AddressEndpoints;
import com.extole.consumer.rest.validation.AddressRestException;
import com.extole.consumer.rest.validation.AddressValidationRequest;
import com.extole.consumer.rest.validation.AddressValidationResponse;
import com.extole.consumer.rest.validation.ComponentName;
import com.extole.consumer.rest.validation.PostalAddress;
import com.extole.consumer.rest.validation.Verdict;
import com.extole.consumer.service.validation.AddressValidationService;
import com.extole.consumer.service.validation.ValidationException;

@Provider
public class AddressEndpointsImpl
    implements AddressEndpoints {

    private final AddressValidationService addressValidationService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final HttpServletRequest servletRequest;

    @Inject
    public AddressEndpointsImpl(AddressValidationService addressValidationService,
        ConsumerRequestContextService consumerRequestContextService,
        HttpServletRequest servletRequest) {
        this.addressValidationService = addressValidationService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.servletRequest = servletRequest;
    }

    @Override
    public AddressValidationResponse validate(String accessToken,
        AddressValidationRequest request) throws AuthorizationRestException, AddressRestException {
        consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build();
        Authorization authorization = getAuthorizationFromRequest(accessToken);

        try {
            com.extole.google.api.AddressValidationResponse validationResponse =
                addressValidationService.validateAddress(authorization, request.getRegionCode(),
                    request.getAddressLines());
            return new AddressValidationResponse(mapVerdict(validationResponse), mapAddress(validationResponse));
        } catch (ValidationException e) {
            throw RestExceptionBuilder.newBuilder(AddressRestException.class)
                .withErrorCode(AddressRestException.VALIDATION_FAILED)
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        }
    }

    private static Address mapAddress(com.extole.google.api.AddressValidationResponse validationResponse) {
        com.extole.google.api.Address validatedAddress = validationResponse.getAddress();
        return new Address(validatedAddress.getFormattedAddress(), mapPostalAddress(validatedAddress),
            validatedAddress.getAddressComponents().stream().map(component -> mapAddressComponent(component))
                .collect(Collectors.toList()),
            validationResponse.getAddress().getMissingComponentTypes(),
            validationResponse.getAddress().getUnconfirmedComponentTypes());
    }

    private static AddressComponent mapAddressComponent(com.extole.google.api.AddressComponent component) {
        return new AddressComponent(mapComponent(component), component.getComponentType(),
            component.getConfirmationLevel(), component.getInferred(), component.getReplaced(),
            component.getSpellCorrected());
    }

    private static ComponentName mapComponent(com.extole.google.api.AddressComponent component) {
        return new ComponentName(component.getComponentName()
            .getText(),
            component.getComponentName()
                .getLanguageCode());
    }

    private static PostalAddress mapPostalAddress(com.extole.google.api.Address validatedAddress) {
        return new PostalAddress(validatedAddress.getPostalAddress()
            .getRegionCode(),
            validatedAddress.getPostalAddress()
                .getRegionCode(),
            validatedAddress.getPostalAddress()
                .getAddressLines());
    }

    private static Verdict mapVerdict(com.extole.google.api.AddressValidationResponse validationResponse) {
        com.extole.google.api.Verdict validationVerdict = validationResponse.getVerdict();
        return new Verdict(validationVerdict.getInputGranularity(), validationVerdict.getValidationGranularity(),
            validationVerdict.getGeocodeGranularity(), validationVerdict.getHasUnconfirmedComponents());
    }

    private Authorization getAuthorizationFromRequest(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
    }
}
