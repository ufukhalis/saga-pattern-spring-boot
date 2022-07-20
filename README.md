# Saga Pattern Implementation using Spring Boot and Kafka

---

In this example project, you will be able to find an implementation of the Saga Pattern using Spring Boot and Kafka.

As you may know or not, in the distributed systems, the transactions are not so easy to achieve. Especially, when there are a lot of services involve it. In a monolith application, it's much easier to achieve such transactions but of course, using monolith application has its downsides which most of the organizations try to get rid of them.

// TODO
Add diagram.

In this example, we have basically implemented one of the popular use case from mostly the e-commerce domain which is called payment transactions.
So, basically, a user can add a product and then try to purchase it if everything is successful then the payment must be completed and stock of that product must be decreased and the order operation must be finished.

In this manner, we have 3 microservices -> `order-service`, `payment-service` and `inventory-service`.

In the project, we didn't use any DB technology, we just stored the data in the memory(as Map) but of course, you may extend it to use any type of DB technology too.

### Flow

---

We can define the flow of the request like below(A sequence diagram would be better here but for now, we will just put items).

1. Create an order request via Swagger UI -> http://localhost:8081/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/order-controller/createOrder
> Note: userId and productId values can be 0-9, because, in the services, we are generating some dummy data.

2. Once request has been placed then it will produce an event to `order-topic`.
3. `payment-service` and `inventory-service` listen that `order-topic` and based on the `order-status`(also for other conditions, like checking stock and checking user money), they try to process that event and produce the results with new status to `order-result-topic`.
4. `order-service` listens the `order-result-topic` and it decides the final result based on the status from those events(if everything is okay then it produces `COMPLETED` otherwise `ROLLBACK`).
> Note: You may think that since `order-service` listens the `order-result-topic` and what if one of the other services sends `ACCEPT` and other with `REJECT` status. So, the `order-service` consumes that topic one by one and other services produces the event to that topic with `orderId` and this will guarantee that they will be in same partition always. You can check the implementation details.
5. Then `payment-service` and `inventory-service` will complete the process based on the new event with new status from the `order-service`.

You can easily run the project using `run.sh` file. 
Be sure that you have `mvn`, `java` and `docker` available locally. 
