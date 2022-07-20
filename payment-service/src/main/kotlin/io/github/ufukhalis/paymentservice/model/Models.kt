package io.github.ufukhalis.paymentservice.model

data class OrderDTO(
    val orderId: String,
    val userId: String,
    val productId: String,
    val productCount: Int,
    val productPrice: Double,
    var orderStatus: String,
    var sourceService: String
)

data class User(
    val id: String,
    val name: String,
    var availableAmount: Double,
    var reservedAmount: Double
)
