order-ms is server repo and stock-ms is a client repo
Circuit Breaker & Retry 

==================
validate the circuit breaker
http://localhost:8080/api/orders
method type: Post
Request : {
    "item":"Item1",
    "quantity":2,
    "amount":33.2,
    "orderId":123123,
    "paymentMode": "cash",
    "address": "Bangalore"
}
====================
validate the the circuit breaker status with actuator
method type: Get
http://localhost:8080/actuator/health
==================
validate the retry 
method type: Get
http://localhost:8080/api/getStock
==================

