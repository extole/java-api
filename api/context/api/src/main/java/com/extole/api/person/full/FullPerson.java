package com.extole.api.person.full;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Person;
import com.extole.api.service.person.full.FullPersonDataQueryBuilder;
import com.extole.api.service.person.full.FullPersonJourneyQueryBuilder;
import com.extole.api.service.person.full.FullPersonRelationshipQueryBuilder;
import com.extole.api.service.person.full.FullPersonRewardQueryBuilder;
import com.extole.api.service.person.full.FullPersonShareQueryBuilder;
import com.extole.api.service.person.full.FullPersonShareableQueryBuilder;
import com.extole.api.service.person.full.FullPersonStepQueryBuilder;

@Schema
public interface FullPerson extends Person {
    FullPersonRewardQueryBuilder createRewardsQuery();

    FullPersonJourneyQueryBuilder createJourneysQuery();

    FullPersonStepQueryBuilder createStepsQuery();

    FullPersonShareQueryBuilder createSharesQuery();

    FullPersonShareableQueryBuilder createShareablesQuery();

    FullPersonRelationshipQueryBuilder createRelationshipsQuery();

    FullPersonDataQueryBuilder createDataQuery();
}
