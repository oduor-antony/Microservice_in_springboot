package com.vidat.inventory_service.controller;


import com.vidat.inventory_service.dto.InventoryResponse;
import com.vidat.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inventory")
public class InventotyController {
    private final InventoryService inventoryService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public  List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){

       return inventoryService.isInStock(skuCode);
    }
}
