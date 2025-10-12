package com.notification.Notification.configuration.kafka;

public class KafkaTopics {
    public static final String NOTIFICATION_TOPIC = "notification-events";
    public static final String NOTIFICATION_DISPATCH_TOPIC = "notification-dispatch";
    public static final String TOPIC_NOTIFICATION_TOPIC = "topic-notifications";
    public static final String TOPIC_PUSH_NOTIFICATION = "topic-push-notifications";
    public static final String INTERNAL_TOPIC_PUSH_NOTIFICATION = "internal-topic-push-notifications";
    
    private KafkaTopics() {}
}

