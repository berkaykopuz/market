package com.toyota.product.service;

import com.toyota.product.dto.ProductDto;
import com.toyota.product.entity.Product;
import com.toyota.product.repository.ProductRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAllProducts(){
        return productRepository.findAll().stream().map(ProductDto::convert).collect(Collectors.toList());
    }

    public ProductDto getProductById(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("product not found with id: " + productId));

        return ProductDto.convert(product);
    }

    public ProductDto createProduct(ProductDto productDto){
        Product product = new Product();
        product.setName(productDto.name());
        product.setAmount(productDto.amount());
        product.setPrice(productDto.price());
        product.setCategory(productDto.category());
        product.setUpdatedDate(LocalDateTime.now());

        return ProductDto.convert(productRepository.save(product));
    }

    public ProductDto updateProduct(Long productId, ProductDto updatedProductDto){
        Optional<Product> product = productRepository.findById(productId);

        if(product.isPresent()){
            Product existingProduct = product.get();

            existingProduct.setName(updatedProductDto.name());
            existingProduct.setAmount(updatedProductDto.amount());
            existingProduct.setPrice(updatedProductDto.price());
            existingProduct.setCategory(updatedProductDto.category());
            existingProduct.setUpdatedDate(LocalDateTime.now());

            Product updatedProduct = productRepository.save(existingProduct);

            return ProductDto.convert(updatedProduct);
        }

        else{
            throw new NotFoundException("Product not found with id: " + productId);
        }
    }

    public String deleteProduct(Long productId) {
        if(productRepository.existsById(productId)){
            productRepository.deleteById(productId);
            return "Product deleted";
        }
        else{
            return "Product not found with id: " + productId;
        }
    }
}
