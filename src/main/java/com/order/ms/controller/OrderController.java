package com.order.ms.controller;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.order.ms.dto.CustomerOrder;
import com.order.ms.dto.OrderStatus;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/api")
public class OrderController {

	private RestTemplate restTemplate;

	private static final String STOCK_API = "http://localhost:8082/api/updateStock";
	private static final String GET_STOCK_API = "http://localhost:8082/api/getStocks";
	public OrderController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	int attempt =0;

	@PostMapping("/orders")
	@CircuitBreaker(name ="OrderMs",fallbackMethod = "fallback")
	public OrderStatus doOrder(@RequestBody CustomerOrder customerOrder) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CustomerOrder> request = new HttpEntity<>(customerOrder, headers);
		System.out.println("Request Attempt "+attempt++);
		ResponseEntity<OrderStatus> responseEntity = restTemplate.postForEntity(STOCK_API, request, OrderStatus.class);
		OrderStatus orderStatus = responseEntity.getBody();
		System.out.println("Service Connected!!");
		System.out.println("Order Status::" + orderStatus);

		return orderStatus;
	}

	public OrderStatus fallback(){
		OrderStatus  orderStatus = new OrderStatus();
		orderStatus.setStatus("Service Unavailable!!!!");
		return new OrderStatus();
	}

	@GetMapping("/getStock")
	@Retry(name ="OrderRetry", fallbackMethod = "fallbackto")
	public ResponseEntity<String> getOrder() {

		System.out.println("Request Attempt "+attempt++);
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(GET_STOCK_API, String.class);

		System.out.println("Service Connected!!");

		return responseEntity;
	}

	public ResponseEntity<String> fallbackto(Exception e) {
		attempt=0;
		System.out.println("Service Disconnected Completedly");
		return new ResponseEntity<>("Service Disconnected Completedly!!", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

