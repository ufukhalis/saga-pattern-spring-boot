package io.github.ufukhalis.orderservice.repository

import io.github.ufukhalis.orderservice.model.OrderDTO
import org.springframework.stereotype.Repository

@Repository
class OrderRepository {

    private val orderDB = mutableMapOf<String, OrderDTO>()

    fun findById(orderId: String) = orderDB[orderId]!!

    fun save(order: OrderDTO) {
        orderDB[order.orderId] = order
    }

    fun findAll() = orderDB.values.toList()
}
