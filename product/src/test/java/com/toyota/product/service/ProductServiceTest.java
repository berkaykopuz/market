package com.toyota.product.service;

import com.toyota.product.dto.ProductDto;
import com.toyota.product.entity.Product;
import com.toyota.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ProductServiceTest {
    private ProductService productService;
    private ProductRepository productRepository;
    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);

        productService = new ProductService(productRepository);
    }

    @Test
    void getAllProducts() {
    }

    @Test
    void getProductById() {
        Product product = generateProduct();

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto result = productService.getProductById(1L);
        ProductDto expectedProductDto = ProductDto.convert(product);

        assertEquals(expectedProductDto, result);

        Mockito.verify(productRepository).findById(1L);
    }

    @Test
    void testCreateProduct_whenProductRequestIsNotNullandValid_shouldCreateProductReturnProductDto() {
        Product product = generateProduct();

        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(ProductDto.convert(product)).thenReturn(ProductDto.convert(product));

        ProductDto expectedProductDto = ProductDto.convert(product);
        ProductDto result = productService.createProduct(expectedProductDto);

        assertEquals(expectedProductDto, result);

        Mockito.verify(productRepository).save(product);
        Mockito.verify(ProductDto.convert(product));
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }

    private Product generateProduct(){
        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setCategory("GIDA");
        product.setAmount(10);
        product.setPrice(10.0);
        product.setUpdatedDate(LocalDateTime.now());

        return product;
    }
}