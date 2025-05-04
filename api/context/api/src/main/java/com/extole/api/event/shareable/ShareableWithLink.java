package com.extole.api.event.shareable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Shareable;

// TODO replace with service ENG-15120
@Schema
public interface ShareableWithLink extends Shareable {

    String getLink();

}
