package io.github.ufukhalis.inventoryservice.service

import io.github.ufukhalis.inventoryservice.model.Inventory
import io.github.ufukhalis.inventoryservice.model.OrderDTO
import io.github.ufukhalis.inventoryservice.objectMapper
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.stream.IntStream
import javax.annotation.PostConstruct

@Service
class InventoryService(
    private val template: KafkaTemplate<String, String>
) {

    private val logger = KotlinLogging.logger {}

    private val inventoryDB = mutableMapOf<String, Inventory>()

    @PostConstruct
    fun initInventories() {
        IntStream.range(0, 10)
            .mapToObj {
                Inventory(
                    it.toString(), 100, 0
                )
            }.forEach {
                logger.info { "Inventory created $it" }
                inventoryDB[it.productId] = it
            }
    }

    fun reserveInventory(order: OrderDTO) {
        val inventory = inventoryDB[order.productId]!!

        if (order.productCount <= inventory.availableItems) {
            inventory.reservedItems = inventory.reservedItems + order.productCount
            inventory.availableItems = inventory.availableItems - order.productCount
            order.orderStatus = "ACCEPT"
            inventoryDB[order.productId] = inventory

            logger.info { "Inventory accepted $inventory" }
        } else {
            order.orderStatus = "REJECT"

            logger.info { "Inventory rejected $inventory" }
        }

        order.sourceService = "inventory-service"
        val message = objectMapper.writeValueAsString(order)
        template.send("order-result-topic", order.orderId, message)

        logger.info { "Inventory reserve operation completed $inventory" }
    }

    fun confirmInventory(order: OrderDTO) {
        val inventory = inventoryDB[order.productId]!!

        if (order.orderStatus == "COMPLETED") {
            inventory.reservedItems = inventory.reservedItems - order.productCount
            inventoryDB[order.productId] = inventory

            logger.info { "Order completed for inventory $inventory" }
        } else if (order.orderStatus == "ROLLBACK") {
            if (inventory.reservedItems != 0) {
                inventory.reservedItems = inventory.reservedItems - order.productCount
                inventory.availableItems = inventory.availableItems + order.productCount
                inventoryDB[order.productId] = inventory
            } else {
                logger.info { "No need to roll back reserve item $inventory" }
            }

            logger.info { "Order rolled back for payment $inventory" }
        }
    }
}
