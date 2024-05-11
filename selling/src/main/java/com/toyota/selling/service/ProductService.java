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
        logger.debug("Received request to create a new product");

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

        logger.info("Creating new product object");
        Product product = new Product();
        product.setName(productDto.name());
        product.setAmount(productDto.amount());
        product.setPrice(productDto.price());
        product.setCategory(productDto.category());
        product.setUpdatedDate(LocalDateTime.now().plusHours(3));

        logger.info("Saving new product to the repository");
        return ProductDto.convert(productRepository.save(product));
    }

    /**
     * Updates the product with the given ID using the provided ProductDto object.
     *
     * @param productId The ID of the product to be updated.
     * @param updatedProductDto A ProductDto object containing the updated product details.
     * @return A ProductDto object of the updated product.
     * @throws ProductNotFoundException if no product with the given ID is found.
     */
    public ProductDto updateProduct(Long productId, ProductDto updatedProductDto){
        logger.debug("Received request to update product with id: " + productId);
        Optional<Product> product = productRepository.findById(productId);

        if(product.isPresent()){
            Product existingProduct = product.get();

            logger.info("Updating product object");
            existingProduct.setName(updatedProductDto.name());
            existingProduct.setAmount(updatedProductDto.amount());
            existingProduct.setPrice(updatedProductDto.price());
            existingProduct.setCategory(updatedProductDto.category());
            existingProduct.setUpdatedDate(LocalDateTime.now().plusHours(3));

            Product updatedProduct = productRepository.save(existingProduct);

            logger.info("Product updated successfully");
            return ProductDto.convert(updatedProduct);
        }

        else{
            logger.error("Product not found with id: " + productId);
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
    }

    /**
     * Deletes the product with the given ID.
     *
     * @param productId The ID of the product to be deleted.
     * @return A string message indicating the result of the deletion operation.
     */
    public String deleteProduct(Long productId) {
        logger.info("Received request to delete product with id: " + productId);

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
