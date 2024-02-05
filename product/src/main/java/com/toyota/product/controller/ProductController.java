package com.toyota.product.controller;

import com.toyota.product.dto.ProductDto;
import com.toyota.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId){
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @PostMapping("create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Long productId,
            @RequestBody ProductDto productDto){
        return new ResponseEntity<>(productService.updateProduct(productId , productDto), HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId){
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }
}
