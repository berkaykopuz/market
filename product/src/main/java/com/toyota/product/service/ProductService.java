package com.toyota.product.service;

import com.toyota.product.dto.ProductDto;
import com.toyota.product.entity.Product;
import com.toyota.product.exception.BadProductRequestException;
import com.toyota.product.exception.ProductNotFoundException;
import com.toyota.product.repository.ProductRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static Logger logger = LogManager.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Function saves user infos with role parameter.
     * @return productdto list
     */
    public List<ProductDto> getAllProducts(){
        logger.info("Getting all products");
        return productRepository.findAll().stream().map(ProductDto::convert).collect(Collectors.toList());
    }

    /**
     * Function returns requested product
     * @param productId productId
     * @return product
     */
    public ProductDto getProductById(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("product not found with id: " + productId));

        logger.info("Getting product with id: " + product.getId());
        return ProductDto.convert(product);
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
}
