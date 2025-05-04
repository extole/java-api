package com.extole.reporting.rest.impl.demo.data;

import static com.extole.reporting.rest.demo.data.DemoDataRestException.MISSING_DATE;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.demo.data.DemoDataDateMissingException;
import com.extole.model.service.demo.data.DemoDataEmptyFlowsException;
import com.extole.model.service.demo.data.DemoDataEventsPerDayMissingException;
import com.extole.model.service.demo.data.DemoDataFlow;
import com.extole.model.service.demo.data.DemoDataFlowDimension;
import com.extole.model.service.demo.data.DemoDataInvalidDateIntervalException;
import com.extole.model.service.demo.data.DemoDataInvalidEventsPerDayValueException;
import com.extole.model.service.demo.data.DemoDataNegativeProbabilityException;
import com.extole.model.service.demo.data.DemoDataProgramLabelMissingException;
import com.extole.model.service.demo.data.DemoDataService;
import com.extole.reporting.rest.demo.data.DemoDataEndpoints;
import com.extole.reporting.rest.demo.data.DemoDataFileAssetResponse;
import com.extole.reporting.rest.demo.data.DemoDataFlowRequest;
import com.extole.reporting.rest.demo.data.DemoDataRequest;
import com.extole.reporting.rest.demo.data.DemoDataRestException;
import com.extole.reporting.service.demo.data.DemoDataFileAsset;
import com.extole.reporting.service.demo.data.DemoDataFileAssetBuilder;
import com.extole.reporting.service.demo.data.DemoDataFileAssetService;
import com.extole.reporting.service.demo.data.DemoDataFormat;

@Provider
public class DemoDataEndpointsImpl implements DemoDataEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final DemoDataFileAssetService demoDataFileAssetService;

    @Inject
    public DemoDataEndpointsImpl(ClientAuthorizationProvider authorizationProvider, DemoDataService demoDataService,
        DemoDataFileAssetService demoDataFileAssetService) {
        this.authorizationProvider = authorizationProvider;
        this.demoDataFileAssetService = demoDataFileAssetService;
    }

    @Override
    public DemoDataFileAssetResponse generateFileAsset(String accessToken, DemoDataRequest request)
        throws UserAuthorizationRestException, DemoDataRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            DemoDataFileAssetBuilder demoDataFileAssetBuilder = demoDataFileAssetService.create(userAuthorization);
            if (request.getFormat().isPresent()) {
                DemoDataFormat format = DemoDataFormat.valueOf(request.getFormat().get().name());
                demoDataFileAssetBuilder.withFormat(format);
            }
            if (request.getFileAssetName().isPresent()) {
                demoDataFileAssetBuilder.withFileAssetName(request.getFileAssetName().get());
            }
            if (request.getStartDate().isPresent()) {
                demoDataFileAssetBuilder.withStartDate(request.getStartDate().get().toInstant());
            }
            if (request.getEndDate().isPresent()) {
                demoDataFileAssetBuilder.withEndDate(request.getEndDate().get().toInstant());
            }
            demoDataFileAssetBuilder.withEventsPerDay(request.getEventsPerDay());
            if (request.getProgramLabel().isPresent()) {
                demoDataFileAssetBuilder.withProgramLabel(request.getProgramLabel().get());
            }
            if (request.getEmailRandomPartLength().isPresent()) {
                demoDataFileAssetBuilder.withEmailRandomPartLength(request.getEmailRandomPartLength().get());
            }
            if (request.getFlows().isPresent()) {
                List<DemoDataFlow> flows = transformRequestToServiceFlows(request.getFlows().get());
                flows.forEach(flow -> demoDataFileAssetBuilder.addFlow(flow));
            }
            DemoDataFileAsset demoDataFileAsset = demoDataFileAssetBuilder.build();
            return new DemoDataFileAssetResponse(demoDataFileAsset.getFileAssetId().getValue(),
                demoDataFileAsset.getFileAssetName(), demoDataFileAsset.getEventsCount());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (DemoDataInvalidEventsPerDayValueException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(DemoDataRestException.INVALID_EVENTS_PER_DAY)
                .addParameter("max_events_per_day", Integer.valueOf(e.getMaxEventsPerDay()))
                .withCause(e)
                .build();
        } catch (DemoDataEmptyFlowsException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(DemoDataRestException.MISSING_FLOWS)
                .withCause(e)
                .build();
        } catch (DemoDataDateMissingException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(MISSING_DATE)
                .withCause(e)
                .build();
        } catch (DemoDataProgramLabelMissingException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(DemoDataRestException.INVALID_PROGRAM_LABEL)
                .withCause(e)
                .build();
        } catch (DemoDataInvalidDateIntervalException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(DemoDataRestException.INVALID_DATE_INTERVAL)
                .withCause(e)
                .build();
        } catch (DemoDataEventsPerDayMissingException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(DemoDataRestException.MISSING_EVENTS_PER_DAY)
                .withCause(e)
                .build();
        } catch (DemoDataNegativeProbabilityException e) {
            throw RestExceptionBuilder.newBuilder(DemoDataRestException.class)
                .withErrorCode(DemoDataRestException.NEGATIVE_PROBABILITY)
                .addParameter("details_message", e.getDetailsMessage())
                .withCause(e)
                .build();
        }
    }

    private List<DemoDataFlow> transformRequestToServiceFlows(List<DemoDataFlowRequest> flows) {
        return flows.stream()
            .map(flowRequest -> new DemoDataFlow(flowRequest.getEventName(),
                flowRequest.getProbability(),
                flowRequest.getJourneyType().isPresent()
                    ? DemoDataFlow.JourneyType.valueOf(flowRequest.getJourneyType().get().name())
                    : DemoDataFlow.JourneyType.FRIEND,
                flowRequest.getDimensions().stream()
                    .map(dimensionRequest -> new DemoDataFlowDimension(dimensionRequest.getName(),
                        dimensionRequest.getValue(),
                        dimensionRequest.getProbability()))
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
    }
}
