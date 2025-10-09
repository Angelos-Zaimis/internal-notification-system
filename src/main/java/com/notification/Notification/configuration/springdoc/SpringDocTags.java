package com.notification.Notification.configuration.springdoc;

import io.swagger.v3.oas.models.tags.Tag;

import java.util.List;

public class SpringDocTags {
    public static final String NOTIFICATION = "API for requests to Notification";
    public static final String NOTIFICATION_DESC = "APIs to use Notification features like e.g. Notification History or deleting Notifications";

    public static List<Tag> tags() {
        return List.of(new Tag().name(NOTIFICATION).description(NOTIFICATION_DESC));
    }
}
