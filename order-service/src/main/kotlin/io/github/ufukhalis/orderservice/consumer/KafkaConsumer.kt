package io.github.ufukhalis.orderservice.consumer

import io.github.ufukhalis.orderservice.model.OrderDTO
import io.github.ufukhalis.orderservice.objectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.ufukhalis.orderservice.repository.OrderRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class KafkaConsumer (
    private val template: KafkaTemplate<String, String>,
    private val orderRepository: OrderRepository
) {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(id = "payment", topics = ["order-result-topic"], groupId = "order-service")
    fun onOrderResultEventReceived(message: String) {
        val order = objectMapper.readValue<OrderDTO>(message)
        logger.info { "Order result received $order" }

        if (order.orderStatus == "REJECT") {
            order.orderStatus = "ROLLBACK"
            val newMessage = objectMapper.writeValueAsString(order)
            order.orderStatus = "CANCELLED"
            orderRepository.save(order)
            template.send("order-topic", order.orderId, newMessage)

            logger.info { "Order will be cancelled $order" }
        } else if (order.orderStatus == "ACCEPT") {
            val currentOrder = orderRepository.findById(order.orderId)

            when (currentOrder.orderStatus) {
                "NEW" -> {
                    order.orderStatus = "WAITING"
                    orderRepository.save(order)

                    logger.info { "Order is waiting $order" }
                }
                "WAITING" -> {
                    order.orderStatus = "COMPLETED"
                    orderRepository.save(order)
                    val newMessage = objectMapper.writeValueAsString(order)
                    template.send("order-topic", order.orderId, newMessage)

                    logger.info { "Order is completed $order" }
                }
                else -> {
                    logger.info { "Order cancelled $order" }
                }
            }
        }
    }

}
