package io.github.ufukhalis.orderservice.model

import java.util.UUID

data class OrderDTO(
    val orderId: String,
    val userId: String,
    val productId: String,
    val productCount: Int,
    val productPrice: Double,
    var orderStatus: String,
    var sourceService: String?
)

data class CreateOrderRequest(
    val userId: String,
    val productId: String,
    val productPrice: Double,
    val productCount: Int
) {
    fun toDTO(): OrderDTO {
        return OrderDTO(
            orderId = UUID.randomUUID().toString(),
            userId = this.userId,
            productId = this.productId,
            productPrice = this.productPrice,
            productCount = this.productCount,
            orderStatus = "NEW",
            sourceService = "order-service"
        )
    }
}
