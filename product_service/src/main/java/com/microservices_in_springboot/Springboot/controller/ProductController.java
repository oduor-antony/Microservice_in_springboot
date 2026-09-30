package com.microservices_in_springboot.Springboot.controller;


import com.microservices_in_springboot.Springboot.dto.ProductRequest;
import com.microservices_in_springboot.Springboot.dto.ProductResponse;
import com.microservices_in_springboot.Springboot.model.Product;
import com.microservices_in_springboot.Springboot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest){
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProcuts(){
      return  productService.getAllPtoducts();
    }
}
