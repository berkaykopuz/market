package com.toyota.product.service;

import com.toyota.product.dto.ProductDto;
import com.toyota.product.entity.Product;
import com.toyota.product.exception.BadProductRequestException;
import com.toyota.product.exception.ProductNotFoundException;
import com.toyota.product.repository.ProductRepository;

import org.springframework.stereotype.Service;

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
                .orElseThrow(()-> new ProductNotFoundException("product not found with id: " + productId));

        return ProductDto.convert(product);
    }

    public ProductDto createProduct(ProductDto productDto){
        if (    productDto.name() == null ||
                productDto.amount() == null ||
                productDto.price() == null ||
                productDto.category() == null) {
            throw new BadProductRequestException("Invalid product data. All fields are required.");
        }

        if (    productDto.name().isEmpty() ||
                productDto.amount() <= 0 ||
                productDto.price() <= 0 ||
                productDto.category().isEmpty()) {
            throw new BadProductRequestException("Invalid product data. All fields must be valid.");
        }


        Product product = new Product();
        product.setName(productDto.name());
        product.setAmount(productDto.amount());
        product.setPrice(productDto.price());
        product.setCategory(productDto.category());
        product.setUpdatedDate(LocalDateTime.now());

        return ProductDto.convert(productRepository.save(product));
    }

    public ProductDto updateProduct(Long productId, ProductDto updatedProductDto){
        if (    updatedProductDto.name() == null ||
                updatedProductDto.amount() == null ||
                updatedProductDto.price() == null ||
                updatedProductDto.category() == null) {
            throw new BadProductRequestException("Invalid product data. All fields are required.");
        }

        if (    updatedProductDto.name().isEmpty() ||
                updatedProductDto.amount() <= 0 ||
                updatedProductDto.price() <= 0 ||
                updatedProductDto.category().isEmpty()) {
            throw new BadProductRequestException("Invalid product data. All fields must be valid.");
        }

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
            throw new ProductNotFoundException("Product not found with id: " + productId);
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
