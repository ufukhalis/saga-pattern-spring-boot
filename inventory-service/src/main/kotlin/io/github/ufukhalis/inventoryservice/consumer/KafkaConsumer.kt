package io.github.ufukhalis.inventoryservice.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.ufukhalis.inventoryservice.model.OrderDTO
import io.github.ufukhalis.inventoryservice.objectMapper
import io.github.ufukhalis.inventoryservice.service.InventoryService
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class KafkaConsumer(
    private val inventoryService: InventoryService
) {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(id = "order-topic", topics = ["order-topic"], groupId = "inventory-service")
    fun onOrderEventReceived(message: String) {
        val o = objectMapper.readValue<OrderDTO>(message)
        logger.info { "Order received $o" }

        if (o.orderStatus == "NEW") {
            inventoryService.reserveInventory(o)
        } else {
            inventoryService.confirmInventory(o)
        }
    }
}
