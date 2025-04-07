package com.ecommerce.infrastructure.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topics.order-paid}")
    private String orderPaidTopic;

    @Bean
    public NewTopic orderPaidTopic() {
        return TopicBuilder.name(orderPaidTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
