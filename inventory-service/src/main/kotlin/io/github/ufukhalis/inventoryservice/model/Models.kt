package io.github.ufukhalis.inventoryservice.model

data class OrderDTO(
    val orderId: String,
    val userId: String,
    val productId: String,
    val productCount: Int,
    val productPrice: Double,
    var orderStatus: String,
    var sourceService: String
)

data class Inventory(
    val productId: String,
    var availableItems: Int,
    var reservedItems: Int
)
