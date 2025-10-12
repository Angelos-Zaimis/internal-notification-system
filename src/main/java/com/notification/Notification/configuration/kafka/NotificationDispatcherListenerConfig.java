package com.notification.Notification.configuration.kafka;

import com.notification.Notification.configuration.kafka.AbstractKafkaConsumerConfig;
import com.notification.Notification.configuration.kafka.KafkaGroups;
import com.notification.Notification.dto.NotificationDeliveryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class NotificationDispatcherListenerConfig  extends AbstractKafkaConsumerConfig<NotificationDeliveryDTO> {

    @Autowired
    public NotificationDispatcherListenerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        super(bootstrapAddress);
    }

    @Override
    public String getKafkaGroup() {
        return KafkaGroups.GROUP_INTERNAL_NOTIFICATION_DISPATCHER;
    }

    @Override
    public Class<NotificationDeliveryDTO> getValueDeserializerClassConfig() {
        return NotificationDeliveryDTO.class;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationDeliveryDTO> notificationInternalDispatcherListenerFactory() {
        return kafkaListenerContainerFactory();
    }

}
