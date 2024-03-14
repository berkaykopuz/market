package com.toyota.product.service;

import com.toyota.product.dto.ProductDto;
import com.toyota.product.entity.Product;
import com.toyota.product.exception.BadProductRequestException;
import com.toyota.product.exception.ProductNotFoundException;
import com.toyota.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


class ProductServiceTest {
    private ProductService productService;
    private ProductRepository productRepository;
    private MockedStatic<ProductDto> mockStatic;
    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        mockStatic = Mockito.mockStatic(ProductDto.class);

        productService = new ProductService(productRepository);
    }

    @Test
    void testGetAllProducts_whenProductExists_shouldReturnProductsWithProductDto() {
        List<Product> products = new ArrayList<>();
        Product product = generateProduct();
        products.add(product);
        ProductDto productDto = generateProductDto(product);
        List<ProductDto> expected = new ArrayList<>();
        expected.add(productDto);

        when(productRepository.findAll()).thenReturn(products);
        when(ProductDto.convert(product)).thenReturn(productDto);

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(expected, result);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_whenProductIsNotExist_shouldReturnEmptyList() {
        List<ProductDto> expected = new ArrayList<>();

        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(expected, result);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_whenProductExists_shouldReturnProductDto() {
        Product product = generateProduct();
        Long productId = 1L;

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDto result = productService.getProductById(productId);
        ProductDto expectedProductDto = ProductDto.convert(product);

        assertEquals(expectedProductDto, result);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_whenProductIsNotExist_shouldThrowProductNotFoundException() {
        Long productId = 1L;

        Mockito.when(productRepository.findById(productId)).thenThrow(new ProductNotFoundException(
                "product not found with id: " + productId));

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(productId);
        });

        verify(productRepository, times(1)).findById(productId);
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

    private ProductDto generateProductDto(Product product){

        ProductDto productDto = new ProductDto(
                1L,
                product.getName(),
                product.getAmount(),
                product.getPrice(),
                product.getCategory(),
                product.getUpdatedDate()
        );
        return productDto;
    }

    @AfterEach
    public void afterEach() {
        mockStatic.close();
    }
}