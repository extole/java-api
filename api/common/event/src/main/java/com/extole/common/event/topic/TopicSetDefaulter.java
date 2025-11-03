package com.extole.common.event.topic;

import com.extole.common.event.Topic;

public interface TopicSetDefaulter {

    TopicSetBuilder create(Topic topic);

    boolean isInterested(Topic topic);

}
