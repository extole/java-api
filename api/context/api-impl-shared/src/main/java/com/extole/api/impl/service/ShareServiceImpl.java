package com.extole.api.impl.service;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.service.ShareService;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonPublicShare;
import com.extole.person.service.share.PersonShareNotFoundException;
import com.extole.person.service.share.PersonShareService;

public class ShareServiceImpl implements ShareService {
    private static final Logger LOG = LoggerFactory.getLogger(ShareServiceImpl.class);
    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";

    private final Id<ClientHandle> clientId;
    private final PersonShareService personShareService;

    public ShareServiceImpl(Id<ClientHandle> clientId, PersonShareService personShareService) {
        this.clientId = clientId;
        this.personShareService = personShareService;
    }

    @Override
    public Share getShareByPartnerShareId(String partnerShareId) {
        PartnerEventId partnerEventId = PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId);
        try {
            return new ShareImpl(personShareService.getPublicShare(clientId, partnerEventId));
        } catch (PersonShareNotFoundException e) {
            // TODO include logger context in global services https://extole.atlassian.net/browse/ENG-23737
            LOG.debug("Unable to get share with partner id: {}", partnerEventId, e);
            return null;
        }
    }

    @Override
    public Share getShare(String shareId) {
        try {
            return new ShareImpl(personShareService.getPublicShare(clientId, Id.valueOf(shareId)));
        } catch (PersonShareNotFoundException e) {
            // TODO include logger context in global services https://extole.atlassian.net/browse/ENG-23737
            LOG.debug("Unable to get share with id: {}", shareId, e);
            return null;
        }
    }

    private static final class ShareImpl implements Share {

        private final String id;
        private final Map<String, String> data;
        private final String channel;

        private ShareImpl(PersonPublicShare personPublicShare) {
            this.id = personPublicShare.getId().getValue();
            this.data = Collections.unmodifiableMap(personPublicShare.getData());
            this.channel = personPublicShare.getChannel().map(candidate -> candidate.getName()).orElse(null);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Map<String, String> getData() {
            return data;
        }

        @Override
        public String getChannel() {
            return channel;
        }

    }
}
