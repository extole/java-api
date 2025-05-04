package com.extole.reporting.rest.impl.batch;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.rest.client.WebResourceFactory;
import com.extole.common.rest.client.exception.translation.extole.ExtoleRestExceptionTranslationStrategy;
import com.extole.reporting.rest.batch.BatchJobLocalEndpoints;

@Component
public class BatchJobLocalEndpointsProvider {

    private final List<BatchJobLocalEndpoints> localEndpoints;

    @Autowired
    BatchJobLocalEndpointsProvider(
        @Value("${batch.job.local.domains:api-0.${extole.environment:lo}.extole.io,"
            + "api-1.${extole.environment:lo}.extole.io}") String localDomainsAsString,
        @Qualifier("batchJobApiClient") Client client) {
        List<String> localDomains = ImmutableList.copyOf(localDomainsAsString.split(","));
        ImmutableList.Builder<BatchJobLocalEndpoints> localEndpointsListBuilder =
            ImmutableList.builder();
        for (String localDomain : localDomains) {
            WebTarget webTarget = client.target("https://" + localDomain);

            BatchJobLocalEndpoints endpoints =
                WebResourceFactory.<BatchJobLocalEndpoints>builder()
                    .withResourceInterface(BatchJobLocalEndpoints.class)
                    .withTarget(webTarget)
                    .withRestExceptionTranslationStrategy(ExtoleRestExceptionTranslationStrategy.getSingleton())
                    .build();
            localEndpointsListBuilder.add(endpoints);
        }

        this.localEndpoints = localEndpointsListBuilder.build();
    }

    public List<BatchJobLocalEndpoints> getLocalEndpoints() {
        return localEndpoints;
    }

}
