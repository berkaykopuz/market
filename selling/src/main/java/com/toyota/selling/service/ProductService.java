package com.toyota.selling.service;

import com.toyota.selling.dto.ProductDto;
import com.toyota.selling.entity.Product;
import com.toyota.selling.exception.BadProductRequestException;
import com.toyota.selling.exception.ProductNotFoundException;
import com.toyota.selling.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService {
    private static Logger logger = LogManager.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Function saves product entity by given request
     * @param productDto productDto
     * @return product
     */
    public ProductDto createProduct(ProductDto productDto){
        if (    productDto.name() == null ||
                productDto.amount() == null ||
                productDto.price() == null ||
                productDto.category() == null) {
            logger.warn("Invalid product data. All fields are required.");
            throw new BadProductRequestException("Invalid product data. All fields are required.");
        }

        if (    productDto.name().isEmpty() ||
                productDto.amount() <= 0 ||
                productDto.price() <= 0 ||
                productDto.category().isEmpty()) {
            logger.warn("Invalid product data. All fields must be valid.");
            throw new BadProductRequestException("Invalid product data. All fields must be valid.");
        }


        Product product = new Product();
        product.setName(productDto.name());
        product.setAmount(productDto.amount());
        product.setPrice(productDto.price());
        product.setCategory(productDto.category());
        product.setUpdatedDate(LocalDateTime.now());

        logger.info("Creating new product object");
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
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
    }

    public String deleteProduct(Long productId) {
        if(productRepository.existsById(productId)){
            productRepository.deleteById(productId);
            logger.info("Product deleted");
            return "Product deleted";
        }
        else{
            logger.warn("Product not found with id: " + productId);
            return "Product not found with id: " + productId;
        }
    }
}
