package com.extole.consumer.rest.impl.redirect;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.share.PersonShareNotFoundException;
import com.extole.person.service.share.PersonShareService;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;
import com.extole.security.backend.BackendAuthorizationProvider;

@Component
public class ShortShareLinkResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ShortShareLinkResolver.class);

    private static final Pattern PATTERN_SEPARATOR = Pattern.compile("[+!]");
    private static final String CHANNEL_SEPARATOR = "!";
    private static final String PARAM_PARTNER_SHARE_ID = "partner_share_id";
    private static final Map<String, String> SHARE_CHANNEL_MAPPINGS = ImmutableMap.<String, String>builder()
        .put("f", "facebook")
        .put("t", "twitter")
        .put("s", "sms")
        .put("e", "email")
        .put("p", "pinterest")
        .put("l", "linkedin")
        .put("n", "native_mobile")
        .put("m", "mobile")
        .put("k", "slack")
        .put("w", "whatsapp")
        .put("r", "messenger")
        .put("a", "share_link")
        .put("q", "qr_code")
        .build();

    private final ShareableService shareableService;
    private final PersonShareService personShareService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Autowired
    public ShortShareLinkResolver(ShareableService shareableService,
        PersonShareService personShareService,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.shareableService = shareableService;
        this.personShareService = personShareService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    public Optional<ShortShareLink> resolveShortShareLink(String incomingRequestPath,
        PublicProgram program) {
        Id<ClientHandle> clientId = program.getClientId();
        Authorization authorization;
        try {
            authorization = backendAuthorizationProvider.getAuthorizationForBackend(clientId);
        } catch (AuthorizationException e) {
            return Optional.empty();
        }

        URI programShareUrl = program.getShareUri();
        String pathWithShareUrlPath = incomingRequestPath;
        if (pathWithShareUrlPath.startsWith("/")) {
            pathWithShareUrlPath = pathWithShareUrlPath.substring(1);
        }
        String pathWithoutShareUrlPath = getPathWithoutShareUrlPath(incomingRequestPath, programShareUrl);

        Optional<ShortShareLink> response =
            tryHandleWholePathAsAdvocateCode(clientId, pathWithShareUrlPath, pathWithoutShareUrlPath);
        if (response.isPresent()) {
            return response;
        }

        response = tryHandleAsPathWithShareAndChannel(authorization, pathWithShareUrlPath);
        if (!response.isPresent()) {
            response = tryHandleAsPathWithShareAndChannel(authorization, pathWithoutShareUrlPath);
        }
        return response;
    }

    private Optional<ShortShareLink> tryHandleWholePathAsAdvocateCode(Id<ClientHandle> clientId,
        String pathWithShareUrlPath, String pathWithoutShareUrlPath) {
        Optional<Shareable> shareable = lookupShareableByCode(pathWithShareUrlPath, clientId);
        if (shareable.isPresent()) {
            return Optional.of(buildResponseWithoutShareData(shareable, pathWithShareUrlPath));
        }
        if (!shareable.isPresent() && !pathWithoutShareUrlPath.equals(pathWithShareUrlPath)) {
            shareable = lookupShareableByCode(pathWithoutShareUrlPath, clientId);
            if (shareable.isPresent()) {
                return Optional.of(buildResponseWithoutShareData(shareable, pathWithoutShareUrlPath));
            }
        }
        return Optional.empty();
    }

    private Optional<ShortShareLink> tryHandleAsPathWithShareAndChannel(Authorization authorization,
        String sourcePath) {
        String pathWithChannel = sourcePath;
        String pathWithoutChannel = getPathWithoutChannel(sourcePath);
        Optional<Shareable> shareable = Optional.empty();
        Optional<ShareKey> shareData = Optional.empty();

        Matcher pathMatcher = PATTERN_SEPARATOR.matcher(pathWithChannel);
        if (pathMatcher.find()) {
            int separatorPosition = pathMatcher.start();
            String codePart = pathWithChannel.substring(0, separatorPosition);
            shareable = lookupShareableByCode(codePart, authorization.getClientId());
            String sharePart = pathWithChannel.substring(separatorPosition + 1);
            shareData = resolveShareData(authorization, sharePart);
        }
        if (shareable.isPresent() || shareData.isPresent()) {
            return Optional.of(buildResponse(shareable, shareData, sourcePath));
        }

        shareable = lookupShareableByCode(pathWithoutChannel, authorization.getClientId());
        if (shareable.isPresent()) {
            return Optional.of(buildResponseWithoutShareData(shareable, sourcePath));
        }

        shareData = resolveShareData(authorization, pathWithChannel);
        if (shareData.isPresent()) {
            return Optional.of(buildResponse(Optional.empty(), shareData, sourcePath));
        }
        return shareData
            .map(shareDataValue -> buildResponse(Optional.empty(), Optional.of(shareDataValue), sourcePath));
    }

    private Optional<String> getChannelMappingValue(String channel) {
        if (StringUtils.isBlank(channel)) {
            return Optional.empty();
        }
        return Optional.ofNullable(SHARE_CHANNEL_MAPPINGS.getOrDefault(channel, channel));
    }

    private Optional<String> getChannelFromPath(String path) {
        int lastIndexOfChannelDelimiter = path.lastIndexOf(CHANNEL_SEPARATOR);
        if (lastIndexOfChannelDelimiter != -1) {
            return Optional.of(path.substring(lastIndexOfChannelDelimiter + 1));
        } else {
            return Optional.empty();
        }
    }

    private String getPathWithoutChannel(String path) {
        int lastIndexOfChannelDelimiter = path.lastIndexOf(CHANNEL_SEPARATOR);
        if (lastIndexOfChannelDelimiter != -1) {
            return path.substring(0, lastIndexOfChannelDelimiter);
        } else {
            return path;
        }
    }

    private String getPathWithoutShareUrlPath(String path, URI programShareUrl) {
        if (path.toLowerCase(Locale.ENGLISH)
            .startsWith(programShareUrl.getPath().toLowerCase(Locale.ENGLISH))) {
            path = path.substring(programShareUrl.getPath().length());
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    private Optional<Shareable> lookupShareableByCode(String advocateCode, Id<ClientHandle> clientId) {
        if (StringUtils.isBlank(advocateCode)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(shareableService.getByCode(clientId, advocateCode));
        } catch (ShareableNotFoundException e) {
            LOG.debug("Was unable to find shareable with code={} for client={}", advocateCode, clientId, e);
            return Optional.empty();
        }
    }

    private Optional<PersonShare> lookupShareByShareId(String shareId, Authorization authorization) {
        if (StringUtils.isBlank(shareId)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(personShareService.getShare(authorization, Id.valueOf(shareId)));
        } catch (PersonShareNotFoundException e) {
            LOG.debug("Was unable to find share with ID={} for client={}", shareId, authorization.getClientId(), e);
            return Optional.empty();
        }
    }

    private Optional<PersonShare> lookupShareByPartnerShareId(String partnerShareId, Authorization authorization) {
        if (StringUtils.isBlank(partnerShareId)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(personShareService.getShare(authorization,
                PartnerEventId.of(PARAM_PARTNER_SHARE_ID, partnerShareId)));
        } catch (PersonShareNotFoundException e) {
            LOG.debug("Was unable to find share with partner share ID={} for client={}", partnerShareId,
                authorization.getClientId(), e);

            return Optional.empty();
        }
    }

    private Optional<ShareKey> resolveShareData(Authorization authorization, String pathWithChannel) {
        Optional<Id<PersonShare>> shareId;
        Optional<PartnerEventId> partnerShareId;

        Optional<PersonShare> personShare = lookupShareByShareId(pathWithChannel, authorization);
        if (personShare.isPresent()) {
            shareId = Optional.of(Id.valueOf(pathWithChannel));
            return Optional.of(new ShareKey(shareId, Optional.empty(), Optional.empty()));
        }

        personShare = lookupShareByPartnerShareId(pathWithChannel, authorization);
        if (personShare.isPresent()) {
            partnerShareId = Optional.of(PartnerEventId.of(PARAM_PARTNER_SHARE_ID, pathWithChannel));
            return Optional.of(new ShareKey(Optional.empty(), partnerShareId, Optional.empty()));
        }

        String pathWithoutChannel = getPathWithoutChannel(pathWithChannel);
        if (pathWithoutChannel.equals(pathWithChannel)) {
            return Optional.empty();
        }

        Optional<String> channel = Optional.empty();
        Optional<String> channelFromPath = getChannelFromPath(pathWithChannel);
        if (channelFromPath.isPresent()) {
            channel = getChannelMappingValue(channelFromPath.get());
        }

        personShare = lookupShareByShareId(pathWithoutChannel, authorization);
        if (personShare.isPresent()) {
            shareId = Optional.of(Id.valueOf(pathWithoutChannel));
            return Optional.of(new ShareKey(shareId, Optional.empty(), channel));
        }

        personShare = lookupShareByPartnerShareId(pathWithoutChannel, authorization);
        if (personShare.isPresent()) {
            partnerShareId = Optional.of(PartnerEventId.of(PARAM_PARTNER_SHARE_ID, pathWithoutChannel));
            return Optional.of(new ShareKey(Optional.empty(), partnerShareId, channel));
        }
        return Optional.empty();
    }

    private ShortShareLink buildResponse(Optional<Shareable> shareable,
        Optional<ShareKey> shareData, String resolvingPath) {
        Optional<Id<PersonShare>> shareId = Optional.empty();
        Optional<PartnerEventId> partnerShareId = Optional.empty();
        Optional<String> channel = Optional.empty();
        if (shareable.isPresent()) {
            Optional<String> channelFromPath = getChannelFromPath(resolvingPath);
            if (channelFromPath.isPresent()) {
                channel = getChannelMappingValue(channelFromPath.get());
            }
        }
        if (shareData.isPresent()) {
            shareId = shareData.get().getShareId();
            partnerShareId = shareData.get().getPartnerShareId();
            channel = shareData.get().getChannel();
        }
        return new ShortShareLink(shareable, shareId, partnerShareId, channel, resolvingPath);
    }

    private ShortShareLink buildResponseWithoutShareData(Optional<Shareable> shareable,
        String resolvingPath) {
        Optional<String> channel = Optional.empty();
        if (shareable.isPresent()) {
            Optional<String> channelFromPath = getChannelFromPath(resolvingPath);
            if (channelFromPath.isPresent()) {
                channel = getChannelMappingValue(channelFromPath.get());
            }
        }
        return new ShortShareLink(shareable, Optional.empty(), Optional.empty(), channel, resolvingPath);
    }

}
