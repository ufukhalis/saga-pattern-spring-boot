package io.github.ufukhalis.orderservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.TopicBuilder

@Configuration
@EnableKafka
class TopicConfig {

    @Bean
    fun orderTopic(): NewTopic {
        return TopicBuilder.name("order-topic")
            .partitions(1)
            .compact()
            .build()
    }

    @Bean
    fun orderResultTopic(): NewTopic {
        return TopicBuilder.name("order-result-topic")
            .partitions(1)
            .compact()
            .build()
    }

}
