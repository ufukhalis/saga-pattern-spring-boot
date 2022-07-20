package io.github.ufukhalis.paymentservice.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.ufukhalis.paymentservice.model.OrderDTO
import io.github.ufukhalis.paymentservice.objectMapper
import io.github.ufukhalis.paymentservice.service.PaymentService
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class KafkaConsumer(
    private val paymentService: PaymentService
) {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(id = "order-topic", topics = ["order-topic"], groupId = "payment-service")
    fun onOrderEventReceived(message: String) {
        val o = objectMapper.readValue<OrderDTO>(message)
        logger.info { "Order received $o" }

        if (o.orderStatus == "NEW") {
            paymentService.reservePayment(o)
        } else {
            paymentService.completePayment(o)
        }
    }
}
