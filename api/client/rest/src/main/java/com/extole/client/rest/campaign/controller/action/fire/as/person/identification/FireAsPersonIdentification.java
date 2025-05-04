package com.extole.client.rest.campaign.controller.action.fire.as.person.identification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = FireAsPersonIdentification.JSON_PERSON_IDENTIFICATION_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmailFireAsPersonIdentification.class, name = EmailFireAsPersonIdentification.TYPE),
    @JsonSubTypes.Type(value = PartnerUserIdFireAsPersonIdentification.class,
        name = PartnerUserIdFireAsPersonIdentification.TYPE),
    @JsonSubTypes.Type(value = PersonIdFireAsPersonIdentification.class,
        name = PersonIdFireAsPersonIdentification.TYPE),
    @JsonSubTypes.Type(value = PartnerEventIdFireAsPersonIdentification.class,
        name = PartnerEventIdFireAsPersonIdentification.TYPE),
})
public abstract class FireAsPersonIdentification {

    protected static final String JSON_PERSON_IDENTIFICATION_TYPE = "person_identification_type";
    protected static final String JSON_VALUE = "value";

    private final FireAsPersonIdenticationType personIdentificationType;
    private final String value;

    @JsonCreator
    protected FireAsPersonIdentification(
        @JsonProperty(JSON_PERSON_IDENTIFICATION_TYPE) FireAsPersonIdenticationType personIdentificationType,
        @JsonProperty(JSON_VALUE) String value) {
        this.personIdentificationType = personIdentificationType;
        this.value = value;
    }

    @JsonProperty(JSON_PERSON_IDENTIFICATION_TYPE)
    public FireAsPersonIdenticationType getPersonIdentificationType() {
        return personIdentificationType;
    }

    @JsonProperty(JSON_VALUE)
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null) {
            return false;
        }

        if (this == otherObject) {
            return true;
        }

        if (getClass() != otherObject.getClass()) {
            return false;
        }

        FireAsPersonIdentification otherIdentification =
            (FireAsPersonIdentification) otherObject;

        return EqualsBuilder.reflectionEquals(this, otherIdentification);
    }
}
