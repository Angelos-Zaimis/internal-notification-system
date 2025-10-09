package com.notification.Notification.configuration.kafka;

import com.notification.Notification.configuration.kafka.AbstractKafkaConsumerConfig;
import com.notification.Notification.configuration.kafka.KafkaGroups;
import com.notification.Notification.dto.InternalNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class NotificationListenerConfig extends AbstractKafkaConsumerConfig<InternalNotificationDTO> {

    @Autowired
    public NotificationListenerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        super(bootstrapAddress);
    }

    @Override
    public String getKafkaGroup() {
        return KafkaGroups.GROUP_NOTIFICATION;
    }

    @Override
    public Class<InternalNotificationDTO> getValueDeserializerClassConfig() {
        return InternalNotificationDTO.class;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InternalNotificationDTO> notificationListenerFactory() {
        return kafkaListenerContainerFactory();
    }
}
