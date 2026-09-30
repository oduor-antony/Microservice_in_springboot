package com.vidat.order_services.service;


import com.vidat.order_services.dto.InventoryResponse;
import com.vidat.order_services.dto.OrderLineItemsDTO;
import com.vidat.order_services.dto.OrderRequest;
import com.vidat.order_services.model.Order;
import com.vidat.order_services.model.OrderLineItems;
import com.vidat.order_services.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private  final WebClient.Builder webClientBuilder;
    private final OrderRepository orderRepository;
    public void placeOrder(OrderRequest orderRequest){
        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems=orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDto)
                .toList();


        order.setOrderLineItems(orderLineItems);

       List<String> skuCode=  order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();
        //call inventory service and place order when the product is in the stock

        InventoryResponse[] inventoryResponsesArray = webClientBuilder
                .build()
                .get()
                .uri("http://INVENTORY-SERVICE/api/inventory",
                        uriBuilder -> uriBuilder
                                .queryParam("skuCode", skuCode)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
      boolean allProductsInStock=  Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock){
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDTO orderLineItemsDTO) {
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());
        orderLineItems.setQuantity((orderLineItemsDTO.getQuantity()));
        orderLineItems.setSkuCode(orderLineItemsDTO.getSkuCode());

        return orderLineItems;

    }
}
