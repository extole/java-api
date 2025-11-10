package com.extole.client.rest.test;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.request.FileInputStreamRequest;

@Path("/v1/test/batch-kafka-event-producer")
public interface BatchKafkaEventProducerTestEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    List<ProcessEventStatus> produce(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        FileInputStreamRequest request) throws UserAuthorizationRestException;

}
