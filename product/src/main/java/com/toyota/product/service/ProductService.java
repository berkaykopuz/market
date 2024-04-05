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
}
