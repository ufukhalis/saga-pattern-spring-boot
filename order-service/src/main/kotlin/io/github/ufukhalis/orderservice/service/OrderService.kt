package io.github.ufukhalis.orderservice.service

import io.github.ufukhalis.orderservice.model.CreateOrderRequest
import io.github.ufukhalis.orderservice.model.OrderDTO
import io.github.ufukhalis.orderservice.objectMapper
import io.github.ufukhalis.orderservice.repository.OrderRepository
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val template: KafkaTemplate<String, String>
) {

    private val logger = KotlinLogging.logger {}

    fun createOrder(createOrderRequest: CreateOrderRequest) {
        val order = createOrderRequest.toDTO()
        val message = objectMapper.writeValueAsString(order)
        orderRepository.save(order)
        template.send("order-topic", order.orderId, message)
        logger.info { "Order created $order" }
    }

    fun getOrders(): List<OrderDTO> {
        return orderRepository.findAll()
    }
}
