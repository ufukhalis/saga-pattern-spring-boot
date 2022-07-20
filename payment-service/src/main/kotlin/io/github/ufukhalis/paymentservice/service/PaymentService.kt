package io.github.ufukhalis.paymentservice.service

import io.github.ufukhalis.paymentservice.model.OrderDTO
import io.github.ufukhalis.paymentservice.model.User
import io.github.ufukhalis.paymentservice.objectMapper
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.stream.IntStream
import javax.annotation.PostConstruct

@Service
class PaymentService(
    private val template: KafkaTemplate<String, String>
) {

    private val logger = KotlinLogging.logger {}

    private val userDB = mutableMapOf<String, User>()

    @PostConstruct
    fun initUsers() {
        IntStream.range(0, 10)
            .mapToObj {
                User(
                    it.toString(), "$it-name", 100.0, 0.0
                )
            }.forEach {
                logger.info { "User created $it" }
                userDB[it.id] = it
            }
    }

    fun reservePayment(order: OrderDTO) {
        val user = userDB[order.userId]!!

        if(order.productPrice <= user.availableAmount) {
            order.orderStatus = "ACCEPT"
            user.reservedAmount = user.reservedAmount + order.productPrice
            user.availableAmount = user.availableAmount - order.productPrice
            userDB[order.userId] = user

            logger.info { "Payment accepted $user" }
        } else {
            order.orderStatus = "REJECT"

            logger.info { "Payment rejected $user" }
        }

        order.sourceService = "payment-service"
        val message = objectMapper.writeValueAsString(order)
        template.send("order-result-topic", order.orderId, message)

        logger.info { "Payment reserve operation completed $user" }
    }

    fun completePayment(order: OrderDTO) {
        val user = userDB[order.userId]!!

        if (order.orderStatus == "COMPLETED") {
            user.reservedAmount = user.reservedAmount - order.productPrice
            userDB[order.userId] = user

            logger.info { "Order completed for payment $user" }
        } else if (order.orderStatus == "ROLLBACK") {
            if (user.reservedAmount != 0.0) {
                user.reservedAmount = user.reservedAmount - order.productPrice
                user.availableAmount = user.availableAmount + order.productPrice
                userDB[order.userId] = user
            } else {
                logger.info { "No need to roll back reserved amount $user" }
            }

            logger.info { "Order rolled back for payment $user" }
        }
    }
}
