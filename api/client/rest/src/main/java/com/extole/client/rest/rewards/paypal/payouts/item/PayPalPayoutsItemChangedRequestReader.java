package com.extole.client.rest.rewards.paypal.payouts.item;

import static com.extole.client.rest.rewards.paypal.payouts.item.PayPalPayoutsItemChangedRequest.TransactionStatus.valueOf;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.client.rest.rewards.paypal.payouts.item.PayPalPayoutsItemChangedRequest.TransactionStatus;
import com.extole.common.lang.ObjectMapperProvider;

/**
 * Example of raw data content this reader translates to {@link PayPalPayoutsItemChangedRequest}
 *
 * <pre>
 * {@code
 *  {
 *   "id": "WH-68L35871MU875914X-8DM79009TB591331B",
 *   "create_time": "2016-06-06T03:44:51Z",
 *   "resource_type": "payouts_item",
 *   "event_type": "PAYMENT.PAYOUTS-ITEM.SUCCEEDED",
 *   "summary": "A payout item has succeeded",
 *   "resource": {
 *     "transaction_id": "57J64166G9424913F",
 *     "payout_item_fee": {
 *       "currency": "USD",
 *       "value": "0.25"
 * },
 * "transaction_status": "SUCCESS",
 * "time_processed": "2016-06-06T03:44:51Z",
 * "payout_item": {
 * "recipient_type": "EMAIL",
 * "amount": {
 * "currency": "USD",
 * "value": "1.0"
 * },
 * "note": "First payout",
 * "receiver": "beamdaddy@paypal.com",
 * "sender_item_id": "Item1"
 * },
 * "links": [
 * {
 * "href": "https://api.paypal.com/v1/payments/payouts-item/AYNYWNCHBD8KS",
 * "rel": "self",
 * "method": "GET"
 * },
 * {
 * "href": "https://api.paypal.com/v1/payments/payouts/Q8B9WFS7ZZJ4Q",
 * "rel": "batch",
 * "method": "GET"
 * }
 * ],
 * "payout_item_id": "AYNYWNCHBD8KS",
 * "payout_batch_id": "Q8B9WFS7ZZJ4Q"
 * },
 * "links": [
 * {
 * "href": "https://api.paypal.com/v1/notifications/webhooks-events/WH-68L35871MU875914X",
 * "rel": "self",
 * "method": "GET",
 * "encType": "application/json"
 * },
 * {
 * "href": "https://api.paypal.com/v1/notifications/webhooks-events/WH-68L35871MU875914X/resend",
 * "rel": "resend",
 * "method": "POST",
 * "encType": "application/json"
 * }
 * ],
 * "event_version": "1.0"
 * }}
 * </pre>
 *
 * Only some JSON nodes are translated to {@link PayPalPayoutsItemChangedRequest} fields
 * <br>
 *
 * The whole content is stored in {@link PayPalPayoutsItemChangedRequest#getPayload()} though which is required for
 * authenticity validation.
 * https://developer.paypal.com/docs/integration/direct/webhooks/rest-webhooks/#verify-event-notifications
 *
 * @see <a href="https://developer.paypal.com/docs/integration/direct/webhooks/event-names/#batch-payouts">
 *      Event types
 *      </a>
 * @see <a href="https://developer.paypal.com/developer/webhooksSimulator/">
 *      Simulate events and see request body content
 *      </a>
 * @see PayPalPayoutsItemChangedRequest
 */
@Consumes(MediaType.APPLICATION_JSON)
public class PayPalPayoutsItemChangedRequestReader implements MessageBodyReader<PayPalPayoutsItemChangedRequest> {
    private static final Logger LOG = LoggerFactory.getLogger(PayPalPayoutsItemChangedRequestReader.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    private static final String DASH = "-";
    private static final String EVENT_ID = "id";
    private static final String RESOURCE = "resource";
    private static final String PAYOUT_ITEM = "payout_item";
    private static final String SENDER_ITEM_ID = "sender_item_id";
    private static final String RECEIVER = "receiver";
    private static final String PAYOUT_BATCH_ID = "payout_batch_id";
    private static final String TRANSACTION_STATUS = "transaction_status";
    private static final String EVENT_TYPE = "event_type";
    private static final String SUMMARY = "summary";
    private static final String CREATE_TIME = "create_time";
    private static final String ERRORS = "errors";
    private static final String ERROR_NAME = "name";
    private static final String ERROR_MESSAGE = "message";

    private static final String PAY_PAL_AUTH_ALGO_HEADER_NAME = "PAYPAL-AUTH-ALGO";
    private static final String PAY_PAL_CERT_URL_HEADER_NAME = "PAYPAL-CERT-URL";
    private static final String PAY_PAL_TRANSMISSION_ID_HEADER_NAME = "PAYPAL-TRANSMISSION-ID";
    private static final String PAY_PAL_TRANSMISSION_SIG_HEADER_NAME = "PAYPAL-TRANSMISSION-SIG";
    private static final String PAY_PAL_TRANSMISSION_TIME_HEADER_NAME = "PAYPAL-TRANSMISSION-TIME";
    private static final String ERROR_UNDEFINED = "undefined";

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == PayPalPayoutsItemChangedRequest.class;
    }

    @Override
    public PayPalPayoutsItemChangedRequest readFrom(Class<PayPalPayoutsItemChangedRequest> type, Type genericType,
        Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        try {
            return readPayPalPayoutsItemChangedRequest(httpHeaders, entityStream);
        } catch (Exception e) {
            LOG.error("Failed to interpret paypal webhook callback " + entityStream.toString(), e);
            throw new BadRequestException(e);
        }
    }

    private PayPalPayoutsItemChangedRequest readPayPalPayoutsItemChangedRequest(
        MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(entityStream);

        Function<String, String> extractor = (key) -> jsonNode.get(key).textValue();

        JsonNode resourceNode = jsonNode.get(RESOURCE);

        String batchId = resourceNode.get(PAYOUT_BATCH_ID).textValue();
        String senderItemId = resourceNode.get(PAYOUT_ITEM).get(SENDER_ITEM_ID).textValue();
        String receiver = resourceNode.get(PAYOUT_ITEM).get(RECEIVER).textValue();
        TransactionStatus transactionStatus = valueOf(resourceNode.get(TRANSACTION_STATUS).textValue());
        JsonNode errors = resourceNode.get(ERRORS);

        Map<String, Object> map = OBJECT_MAPPER.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
        return PayPalPayoutsItemChangedRequest.Builder.newBuilder()
            .withEventId(extractor.apply(EVENT_ID))
            .withBatchId(batchId)
            .withReceiver(receiver)
            .withRewardId(StringUtils.substringAfter(senderItemId, DASH))
            .withClientId(StringUtils.substringBefore(senderItemId, DASH))
            .withEventType(PayPalPayoutsItemChangedRequest.Type.from(extractor.apply(EVENT_TYPE)))
            .withTransactionStatus(transactionStatus)
            .withSummary(extractor.apply(SUMMARY))
            .withErrorCode(errors == null || errors.isMissingNode() || errors.isNull() ? ERROR_UNDEFINED
                : errors.get(ERROR_NAME).asText())
            .withErrorMessage(errors == null || errors.isMissingNode() || errors.isNull() ? ERROR_UNDEFINED
                : errors.get(ERROR_MESSAGE).asText())
            .withCreatedAt(Instant.parse(extractor.apply(CREATE_TIME)))
            .withAuthenticationAlgorithm(httpHeaders.getFirst(PAY_PAL_AUTH_ALGO_HEADER_NAME))
            .withPublicKeyCertificateUrl(httpHeaders.getFirst(PAY_PAL_CERT_URL_HEADER_NAME))
            .withTransmissionId(httpHeaders.getFirst(PAY_PAL_TRANSMISSION_ID_HEADER_NAME))
            .withTransmissionSignature(httpHeaders.getFirst(PAY_PAL_TRANSMISSION_SIG_HEADER_NAME))
            .withTransmissionTime(httpHeaders.getFirst(PAY_PAL_TRANSMISSION_TIME_HEADER_NAME))
            .withPayload(map)
            .build();
    }
}
