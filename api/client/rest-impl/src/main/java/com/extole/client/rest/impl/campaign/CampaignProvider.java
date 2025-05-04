package com.extole.client.rest.impl.campaign;

import java.util.Optional;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.BuiltCampaignService;
import com.extole.model.service.campaign.CampaignIdentity;
import com.extole.model.service.campaign.CampaignIdentityService;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionNotFoundException;
import com.extole.model.service.campaign.CampaignVersionState;

@Component
public class CampaignProvider {
    public static final String VERSION_PUBLISHED = "published";
    public static final String VERSION_LATEST = "latest";
    /** @see #VERSION_LATEST */
    @Deprecated // TODO to be removed in ENG-7950
    public static final String VERSION_DRAFT = "draft";
    /** @see #VERSION_PUBLISHED */
    @Deprecated // TODO to be removed in ENG-7950
    public static final String VERSION_LIVE = "live";
    private static final Logger LOG = LoggerFactory.getLogger(CampaignProvider.class);
    private final CampaignService campaignService;

    private final CampaignIdentityService campaignIdentityService;
    private final BuiltCampaignService builtCampaignService;

    @Autowired
    public CampaignProvider(CampaignService campaignService,
        CampaignIdentityService campaignIdentityService,
        BuiltCampaignService builtCampaignService) {
        this.campaignService = campaignService;
        this.campaignIdentityService = campaignIdentityService;
        this.builtCampaignService = builtCampaignService;
    }

    public CampaignVersionState getCampaignVersionState(String version) throws CampaignRestException {
        return parseState(version).orElseThrow(() -> newInvalidCampaignVersionRestException(version, Optional.empty()));
    }

    public CampaignVersion getCampaignVersion(Id<Campaign> campaignId, String version) throws CampaignRestException {
        Optional<CampaignVersion> campaignVersion = parseCampaignVersion(version);

        if (campaignVersion.isPresent()) {
            return campaignVersion.get();
        }

        Optional<CampaignVersionState> versionState = parseState(version);
        if (versionState.isPresent()) {
            try {
                CampaignIdentity campaignIdentity = campaignIdentityService.getByCampaignId(campaignId);

                CampaignVersionState campaignVersionState = versionState.get();

                if (campaignVersionState == CampaignVersionState.LATEST) {
                    return new CampaignVersion(campaignIdentity.getLatestVersion());
                }

                return new CampaignVersion(campaignIdentity.getPublishedVersion()
                    .orElseThrow(() -> newInvalidCampaignVersionRestException(version, Optional.empty())));
            } catch (CampaignNotFoundException e) {
                throw newInvalidCampaignRestException(campaignId.getValue(), Optional.of(e));
            }
        } else {
            throw newInvalidCampaignVersionRestException(version, Optional.empty());
        }
    }

    public Campaign getCampaign(Authorization authorization, Id<Campaign> campaignId, String version)
        throws CampaignRestException {
        Optional<CampaignVersionState> versionState = parseState(version);
        if (versionState.isPresent()) {
            return getCampaignByIdAndVersionState(authorization, campaignId, versionState.get());
        }
        return getCampaignByIdAndVersion(authorization, campaignId, getCampaignVersion(campaignId, version));
    }

    public Campaign getCampaignIncludeArchived(Authorization authorization, Id<Campaign> campaignId, String version)
        throws CampaignRestException {
        Optional<CampaignVersionState> versionState = parseState(version);
        if (versionState.isPresent()) {
            return getCampaignByIdAndVersionStateIncludeArchived(authorization, campaignId, versionState.get());
        }
        return getCampaignByIdAndVersionIncludeArchived(authorization, campaignId,
            getCampaignVersion(campaignId, version));
    }

    public BuiltCampaign getBuiltCampaign(Authorization authorization, Id<Campaign> campaignId, String version)
        throws CampaignRestException, BuildCampaignRestException {
        Campaign campaign = getCampaign(authorization, campaignId, version);
        return buildCampaign(campaign);
    }

    public Campaign getLatestCampaign(Authorization authorization,
        Id<Campaign> campaignId)
        throws CampaignRestException {
        return getCampaign(authorization, campaignId, VERSION_LATEST);
    }

    private Campaign getCampaignByIdAndVersion(Authorization authorization, Id<Campaign> campaignId,
        CampaignVersion campaignVersion) throws CampaignRestException {
        try {
            return campaignService.getCampaignByIdAndVersion(authorization, campaignId, campaignVersion);
        } catch (CampaignNotFoundException e) {
            throw newInvalidCampaignRestException(campaignId.getValue(), Optional.of(e));
        } catch (CampaignVersionNotFoundException e) {
            throw newInvalidCampaignVersionRestException(campaignVersion.getValue().toString(), Optional.of(e));
        }
    }

    private Campaign getCampaignByIdAndVersionIncludeArchived(Authorization authorization, Id<Campaign> campaignId,
        CampaignVersion campaignVersion) throws CampaignRestException {
        try {
            return campaignService.getCampaignByIdAndVersionIncludeArchived(authorization, campaignId, campaignVersion);
        } catch (CampaignNotFoundException e) {
            throw newInvalidCampaignRestException(campaignId.getValue(), Optional.of(e));
        } catch (CampaignVersionNotFoundException e) {
            throw newInvalidCampaignVersionRestException(campaignVersion.getValue().toString(), Optional.of(e));
        }
    }

    private Campaign getCampaignByIdAndVersionState(Authorization authorization, Id<Campaign> campaignId,
        CampaignVersionState campaignVersionState) throws CampaignRestException {
        try {
            return campaignService.getCampaignByIdAndVersionState(authorization, campaignId, campaignVersionState);
        } catch (CampaignNotFoundException e) {
            throw newInvalidCampaignRestException(campaignId.getValue(), Optional.of(e));
        }
    }

    private Campaign getCampaignByIdAndVersionStateIncludeArchived(Authorization authorization, Id<Campaign> campaignId,
        CampaignVersionState campaignVersionState) throws CampaignRestException {
        try {
            return campaignService.getCampaignByIdAndVersionStateIncludeArchived(authorization, campaignId,
                campaignVersionState);
        } catch (CampaignNotFoundException e) {
            throw newInvalidCampaignRestException(campaignId.getValue(), Optional.of(e));
        }
    }

    public BuiltCampaign buildCampaign(Campaign campaign) throws BuildCampaignRestException {
        try {
            return builtCampaignService.buildCampaign(campaign);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    private Optional<CampaignVersion> parseCampaignVersion(String version) {
        String sanitizedVersion = sanitize(version);
        try {
            return Optional.of(new CampaignVersion(Integer.valueOf(sanitizedVersion)));
        } catch (NumberFormatException e) {
            LOG.debug("The value {} is not a valid integer", version);
            return Optional.empty();
        }
    }

    public Optional<CampaignVersionState> parseState(String version) {
        if (Strings.isNullOrEmpty(version)) {
            return Optional.of(CampaignVersionState.LATEST);
        }

        String campaignVersion = sanitize(version);

        if (VERSION_PUBLISHED.equalsIgnoreCase(campaignVersion)) {
            return Optional.of(CampaignVersionState.PUBLISHED);
        }
        if (VERSION_LIVE.equalsIgnoreCase(campaignVersion)) {
            LOG.warn("ENG-7950 usage of deprecated campaignVersion={}", campaignVersion);
            return Optional.of(CampaignVersionState.PUBLISHED);
        }
        if (VERSION_LATEST.equalsIgnoreCase(campaignVersion)) {
            return Optional.of(CampaignVersionState.LATEST);
        }
        if (VERSION_DRAFT.equalsIgnoreCase(campaignVersion)) {
            LOG.warn("ENG-7950 usage of deprecated campaignVersion={}", campaignVersion);
            return Optional.of(CampaignVersionState.LATEST);
        }
        return Optional.empty();
    }

    private String sanitize(String version) {
        String[] versionArray = version.split("/");
        return versionArray[versionArray.length - 1];
    }

    public Optional<Integer> parseVersion(String version) throws CampaignRestException {
        if (StringUtils.isEmpty(version)) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.valueOf(sanitize(version)));
        } catch (NumberFormatException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_VERSION_MALFORMED)
                .withCause(e).build();
        }
    }

    private CampaignRestException newInvalidCampaignVersionRestException(String version,
        Optional<Throwable> optionalCause) {
        RestExceptionBuilder<CampaignRestException> exceptionBuilder =
            RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION).addParameter("version", version);
        optionalCause.ifPresent(exceptionBuilder::withCause);
        return exceptionBuilder.build();
    }

    private CampaignRestException newInvalidCampaignRestException(String campaignId,
        Optional<Throwable> optionalCause) {
        RestExceptionBuilder<CampaignRestException> exceptionBuilder =
            RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId);
        optionalCause.ifPresent(exceptionBuilder::withCause);
        return exceptionBuilder.build();
    }
}
