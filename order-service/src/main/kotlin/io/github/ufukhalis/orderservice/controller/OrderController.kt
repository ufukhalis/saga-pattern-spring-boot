package io.github.ufukhalis.orderservice.controller

import io.github.ufukhalis.orderservice.model.CreateOrderRequest
import io.github.ufukhalis.orderservice.model.OrderDTO
import io.github.ufukhalis.orderservice.service.OrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping("/v1/order")
    fun createOrder(@RequestBody order: CreateOrderRequest) {
        orderService.createOrder(order)
    }

    @GetMapping("/v1/order")
    fun getOrders(): List<OrderDTO> {
        return orderService.getOrders()
    }
}
