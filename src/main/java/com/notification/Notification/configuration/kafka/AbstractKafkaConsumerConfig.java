package com.notification.Notification.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKafkaConsumerConfig<T> {

    private final String bootstrapServers;

    protected AbstractKafkaConsumerConfig(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public abstract String getKafkaGroup();

    public abstract Class<T> getValueDeserializerClassConfig();

    protected Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, getKafkaGroup());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, getValueDeserializerClassConfig().getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    protected ConsumerFactory<String, T> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    protected ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

