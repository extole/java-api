package com.extole.client.rest.rewards.paypal.payouts;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.client.rest.rewards.RewardRestException;
import com.extole.client.rest.rewards.paypal.payouts.item.PayPalPayoutsItemChangedRequest;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/rewards/paypal-payouts")
public interface PayPalPayoutsListenerEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/webhook-listener")
    Response handle(PayPalPayoutsItemChangedRequest request)
        throws UserAuthorizationRestException, RewardRestException;

}
