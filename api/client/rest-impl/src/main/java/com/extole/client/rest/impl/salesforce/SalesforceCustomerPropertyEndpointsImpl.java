package com.extole.client.rest.impl.salesforce;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.client.ClientType;
import com.extole.client.rest.salesforce.SalesforceCustomerPropertyCreationRequest;
import com.extole.client.rest.salesforce.SalesforceCustomerPropertyEndpoints;
import com.extole.client.rest.salesforce.SalesforceCustomerPropertyResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.salesforce.SalesforceCustomerPropertyBuilder;
import com.extole.model.service.client.salesforce.SalesforceCustomerPropertyService;
import com.extole.model.service.client.salesforce.SalesforceProjectStatus;
import com.extole.model.service.client.salesforce.SalesforceResult;

@Provider
public class SalesforceCustomerPropertyEndpointsImpl implements SalesforceCustomerPropertyEndpoints {

    public static final String CURRENCY_PREFIX = "US$";
    private final SalesforceCustomerPropertyService salesforceCustomerPropertyService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public SalesforceCustomerPropertyEndpointsImpl(SalesforceCustomerPropertyService salesforceCustomerPropertyService,
        ClientAuthorizationProvider authorizationProvider) {
        this.salesforceCustomerPropertyService = salesforceCustomerPropertyService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<SalesforceCustomerPropertyResponse> createOrUpdate(String accessToken,
        SalesforceCustomerPropertyCreationRequest request) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<SalesforceCustomerPropertyResponse> updateResults = new ArrayList<>();

            List<SalesforceCustomerPropertyBuilder> builders = new ArrayList<>();
            if (request.getSalesforceAccountId().isPresent()
                && request.getSalesforceAccountId().getValue().isPresent()) {
                request.getSalesforceAccountId().ifPresent(salesforceAccountId -> {
                    if (salesforceAccountId.isPresent()) {
                        Optional<Id<ClientHandle>> clientId =
                            request.getClientId().map(value -> value.filter(Predicate.not(Strings::isNullOrEmpty))
                                .map(id -> Id.<ClientHandle>valueOf(id))).orElse(Optional.empty());
                        try {
                            builders.addAll(salesforceCustomerPropertyService.createOrUpdate(authorization,
                                salesforceAccountId.get(), clientId));
                        } catch (AuthorizationException e) {
                            if (e.getCause() instanceof ClientNotFoundException) {
                                SalesforceResult.Builder builder = SalesforceResult.builder();
                                clientId.ifPresent(builder::withClientId);
                                updateResults.add(convertToResponse(
                                    builder.withSalesforceAccountId(salesforceAccountId.get()).build()));
                            } else {
                                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                                    .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                                    .withCause(e)
                                    .build();
                            }
                        }
                    }
                });
            } else {
                Id<ClientHandle> clientId;
                if (request.getClientId().isPresent()
                    && request.getClientId().getValue().filter(Predicate.not(Strings::isNullOrEmpty)).isPresent()) {
                    clientId = Id.valueOf(request.getClientId().getValue().get());
                } else {
                    clientId = authorization.getClientId();
                }
                try {
                    builders.add(salesforceCustomerPropertyService.createOrUpdate(authorization, clientId));
                } catch (AuthorizationException e) {
                    if (e.getCause() instanceof ClientNotFoundException) {
                        updateResults.add(convertToResponse(SalesforceResult.builder().withClientId(clientId).build()));
                    } else {
                        throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                            .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                            .withCause(e)
                            .build();
                    }
                }
            }

            for (SalesforceCustomerPropertyBuilder builder : builders) {
                try {
                    updateResults.add(applyUpdate(builder, request));
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof ClientNotFoundException) {
                        SalesforceResult.Builder resultBuilder = SalesforceResult.builder();
                        request.getSalesforceAccountId()
                            .ifPresent(accountId -> accountId.ifPresent(resultBuilder::withSalesforceAccountId));
                        request.getClientId().ifPresent(
                            clientId -> clientId.map(Id::<ClientHandle>valueOf).ifPresent(resultBuilder::withClientId));
                        updateResults.add(convertToResponse(resultBuilder.build()));
                    } else {
                        throw e;
                    }
                }
            }

            return updateResults;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private SalesforceCustomerPropertyResponse applyUpdate(SalesforceCustomerPropertyBuilder builder,
        SalesforceCustomerPropertyCreationRequest request) throws AuthorizationException {
        request.getType().ifPresent(type -> type.map(String::toUpperCase).map(builder::withType));
        request.getProjectStatus()
            .map(status -> new SalesforceProjectStatus(status.getId(), status.getStatus()))
            .ifPresent(builder::withProjectStatus);
        request.getVertical()
            .ifPresent(vertical -> vertical.filter(Predicate.not(Strings::isNullOrEmpty)).map(builder::withVertical)
                .orElseGet(builder::clearVertical));
        request.getMrr()
            .ifPresent(mrr -> mrr.filter(Predicate.not(Strings::isNullOrEmpty))
                .map(value -> value.replace(CURRENCY_PREFIX, ""))
                .map(value -> {
                    if (value.contains(",")) {
                        return value.replace(".", "").replace(",", ".");
                    } else {
                        return value;
                    }
                })
                .map(BigDecimal::new)
                .map(builder::withMrr).orElseGet(builder::clearMrr));
        request.getCsmEmail()
            .ifPresent(csmEmail -> csmEmail.filter(Predicate.not(Strings::isNullOrEmpty)).map(builder::withCsmEmail)
                .orElseGet(builder::clearCsmEmail));
        request.getUsOnlySupport().ifPresent(usOnlySupport -> usOnlySupport.map(builder::withUsOnlySupport)
            .orElseGet(() -> builder.withUsOnlySupport(Boolean.FALSE)));
        request.getRiskCategory()
            .ifPresent(riskCategory -> riskCategory.filter(Predicate.not(Strings::isNullOrEmpty))
                .map(builder::withRiskCategory)
                .orElseGet(builder::clearRiskCategory));
        request.getAtRisk().ifPresent(atRisk -> atRisk.map(builder::withAtRisk)
            .orElseGet(builder::clearAtRisk));
        request.getClientPriority()
            .ifPresent(clientPriority -> clientPriority.filter(Predicate.not(Strings::isNullOrEmpty))
                .map(builder::withClientPriority)
                .orElseGet(builder::clearClientPriority));
        request.getNvp().ifPresent(nvp -> nvp.map(builder::withNvp)
            .orElseGet(builder::clearNvp));
        request.getNextRenewalDate().ifPresent(nextRenewalDate -> nextRenewalDate.map(builder::withNextRenewalDate)
            .orElseGet(builder::clearNextRenewalDate));
        request.getInitialLaunchDate()
            .ifPresent(initialLaunchDate -> initialLaunchDate.map(builder::withInitialLaunchDate)
                .orElseGet(builder::clearInitialLaunchDate));
        request.getSsd().ifPresent(ssd -> ssd.map(builder::withSsd)
            .orElseGet(builder::clearSsd));
        request.getParentOrgName()
            .ifPresent(parentOrgName -> parentOrgName.filter(Predicate.not(Strings::isNullOrEmpty))
                .map(builder::withParentOrgName)
                .orElseGet(builder::clearParentOrgName));
        request.getCustomerJourneyStage()
            .ifPresent(customerJourneyStage -> customerJourneyStage.filter(Predicate.not(Strings::isNullOrEmpty))
                .map(builder::withCustomerJourneyStage)
                .orElseGet(builder::clearCustomerJourneyStage));
        request.getSupportOnlyServices()
            .ifPresent(supportOnlyServices -> supportOnlyServices.map(builder::withSupportOnlyServices)
                .orElseGet(builder::clearSupportOnlyServices));
        request.getClientTenure()
            .ifPresent(clientTenure -> clientTenure.filter(Predicate.not(Strings::isNullOrEmpty))
                .map(builder::withClientTenure)
                .orElseGet(builder::clearClientTenure));
        SalesforceResult result = builder.save();
        return convertToResponse(result);
    }

    private static SalesforceCustomerPropertyResponse convertToResponse(SalesforceResult result) {
        return new SalesforceCustomerPropertyResponse(
            result.getSalesforceAccountId(),
            Id.valueOf(result.getClientId().getValue()),
            result.getType().map(Enum::name).map(ClientType::valueOf),
            result.getVertical(),
            result.getMrr(),
            result.getProjectStatuses().stream()
                .map(status -> new com.extole.client.rest.salesforce.SalesforceProjectStatus(status.getId(),
                    status.getStatus()))
                .collect(Collectors.toList()),
            result.getCsmId(), result.getCsmEmail(), result.getSupportEngineerId(),
            result.getSupportEngineerEmail(),
            result.getSupportEngineerName(),
            result.getUsOnlySupport(),
            result.getRiskCategory(),
            result.getAtRisk(),
            result.getClientPriority(),
            result.getNvp(),
            result.getNextRenewalDate(),
            result.getInitialLaunchDate(),
            result.getSsd(),
            result.getParentOrgName(),
            result.getCustomerJourneyStage(),
            result.getSupportOnlyServices(),
            result.getClientTenure());
    }
}
