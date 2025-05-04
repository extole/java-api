package com.extole.client.rest.impl.person;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.person.shareables.PersonShareableContentResponse;
import com.extole.client.rest.person.shareables.PersonShareableDataResponse;
import com.extole.client.rest.person.shareables.PersonShareableResponse;
import com.extole.client.rest.person.v4.PersonShareableContentV4Response;
import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableContent;

@Component
public class PersonShareableRestMapper {
    private final ProgramDomainCache programCache;

    @Autowired
    public PersonShareableRestMapper(ProgramDomainCache programCache) {
        this.programCache = programCache;
    }

    public PersonShareableV4Response toPersonShareableV4Response(Shareable shareable) throws ProgramNotFoundException {
        URI shareableLink = getShareableLink(shareable.getProgramId(), shareable.getClientId(), shareable.getCode());
        return new PersonShareableV4Response(
            shareable.getCode(),
            shareable.getKey(),
            shareable.getLabel().orElse(null),
            shareableLink.toString(),
            shareable.getPersonId().getValue(),
            shareable.getContent().map(this::toShareableContentV4Response).orElse(null),
            shareable.getData());
    }

    public PersonShareableV4Response toPersonShareableV4Response(ClientShareable shareable) {
        return new PersonShareableV4Response(
            shareable.getCode(),
            shareable.getKey(),
            shareable.getLabel().orElse(null),
            shareable.getLink().toString(),
            shareable.getPersonId().getValue(),
            shareable.getContent().map(this::toShareableContentV4Response).orElse(null),
            shareable.getData());
    }

    public PersonShareableResponse toPersonShareableResponse(Shareable shareable, ZoneId timeZone)
        throws ProgramNotFoundException {
        URI link = getShareableLink(shareable.getProgramId(), shareable.getClientId(), shareable.getCode());
        return PersonShareableResponse.builder()
            .withCode(shareable.getCode())
            .withKey(shareable.getKey())
            .withLabel(shareable.getLabel().orElse(null))
            .withLink(link.toString())
            .withContent(shareable.getContent().map(this::toShareableContentResponse).orElse(null))
            .withData(shareable.getData().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> new PersonShareableDataResponse(entry.getKey(), entry.getValue()))))
            .withCreatedDate(ZonedDateTime.ofInstant(shareable.getCreatedDate(), timeZone))
            .withUpdatedDate(ZonedDateTime.ofInstant(shareable.getUpdatedDate(), timeZone))
            .build();
    }

    public PersonShareableResponse toPersonShareableResponse(ClientShareable shareable, ZoneId timeZone)
        throws ProgramNotFoundException {
        URI link = getShareableLink(shareable.getProgramId(), shareable.getClientId(), shareable.getCode());
        return PersonShareableResponse.builder()
            .withCode(shareable.getCode())
            .withKey(shareable.getKey())
            .withLabel(shareable.getLabel().orElse(null))
            .withLink(link.toString())
            .withContent(shareable.getContent().map(this::toShareableContentResponse).orElse(null))
            .withData(shareable.getData().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> new PersonShareableDataResponse(entry.getKey(), entry.getValue()))))
            .withCreatedDate(ZonedDateTime.ofInstant(shareable.getCreatedDate(), timeZone))
            .withUpdatedDate(ZonedDateTime.ofInstant(shareable.getUpdatedDate(), timeZone))
            .build();
    }

    private PersonShareableContentV4Response toShareableContentV4Response(ShareableContent content) {
        return new PersonShareableContentV4Response(
            content.getPartnerContentId().orElse(null),
            content.getTitle().orElse(null),
            content.getImageUrl().map(URI::toString).orElse(null),
            content.getDescription().orElse(null),
            content.getUrl().map(URI::toString).orElse(null));
    }

    private PersonShareableContentResponse toShareableContentResponse(ShareableContent content) {
        return PersonShareableContentResponse.builder()
            .withTitle(content.getTitle().orElse(null))
            .withDescription(content.getDescription().orElse(null))
            .withPartnerContentId(content.getPartnerContentId().orElse(null))
            .withUrl(content.getUrl().map(URI::toString).orElse(null))
            .withImageUrl(content.getImageUrl().map(URI::toString).orElse(null))
            .build();
    }

    private URI getShareableLink(Id<ProgramHandle> programId, Id<ClientHandle> clientId, String shareableCode)
        throws ProgramNotFoundException {
        PublicProgram forwardedProgram = programCache.getForwardedById(programId, clientId);
        URI shareUri = forwardedProgram.getShareUri();
        String baseUrl;
        try {
            baseUrl = new URIBuilder(shareUri.toString()).build().toString();
        } catch (URISyntaxException e) {
            throw new PersonShareableRuntimeException("Unable to build link for URI: " + shareUri, e);
        }
        try {
            return new URIBuilder(baseUrl + "/" + shareableCode).build();
        } catch (URISyntaxException e) {
            throw new PersonShareableRuntimeException(
                "Unable to build shareable link. Base URL: " + baseUrl + ". Shareable code: " + shareableCode, e);
        }
    }
}
