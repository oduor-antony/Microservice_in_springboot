package com.microservices_in_springboot.Springboot.service;

import com.microservices_in_springboot.Springboot.dto.ProductRequest;
import com.microservices_in_springboot.Springboot.dto.ProductResponse;
import com.microservices_in_springboot.Springboot.model.Product;
import com.microservices_in_springboot.Springboot.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest){
        Product product= Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} Succesfully",product.getId());

    }

    public List<ProductResponse> getAllPtoducts() {
       List<Product> products=productRepository.findAll();
      return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
