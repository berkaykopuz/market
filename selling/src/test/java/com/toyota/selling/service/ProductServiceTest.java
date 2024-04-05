package com.toyota.selling.service;

import com.toyota.selling.dto.CampaignDto;
import com.toyota.selling.dto.ProductDto;
import com.toyota.selling.entity.Product;
import com.toyota.selling.exception.BadProductRequestException;
import com.toyota.selling.exception.ProductNotFoundException;
import com.toyota.selling.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    private ProductRepository productRepository;
    private ProductService productService;
    private MockedStatic<ProductDto> mockStatic;


    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        mockStatic = Mockito.mockStatic(ProductDto.class);

        productService = new ProductService(productRepository);
    }

    @Test
    void testCreateProduct_whenProductRequestIsNotNullandValid_shouldCreateProductReturnProductDto() {
        Product product = generateProduct();
        ProductDto expectedProductDto = generateProductDto(product);

        Mockito.when(ProductDto.convert(productRepository.save(product))).thenReturn(expectedProductDto);

        ProductDto result = productService.createProduct(expectedProductDto);

        assertEquals(expectedProductDto, result);

        Mockito.verify(productRepository).save(product);

    }

    @Test
    void testCreateProduct_whenProductRequestHasNullProperties_shouldThrowBadProductRequestException() {
        ProductDto productDto = new ProductDto(null, null, null, null, null, null);

        assertThrows(BadProductRequestException.class, () -> productService.createProduct(productDto));

        Mockito.verifyNoInteractions(productRepository);
    }

    @Test
    void testCreateProduct_whenProductRequestIsNotValid_shouldThrowBadProductRequestException() {
        ProductDto productDto = new ProductDto(1L,
                "product",
                -1,
                -5.0,
                "",
                LocalDateTime.now());

        assertThrows(BadProductRequestException.class, () -> productService.createProduct(productDto));

        Mockito.verifyNoInteractions(productRepository);
    }

    @Test
    void testUpdateProduct_whenProductIsExist_shouldUpdateProductandReturnProductDto() {
        Product product = generateProduct();
        Long productId = 1L;

        ProductDto updatedProductDto = new ProductDto(
                productId,
                "updatedProduct",
                10,
                100.0,
                "updatedCategory",
                LocalDateTime.now());

        product.setId(updatedProductDto.id());
        product.setName(updatedProductDto.name());
        product.setAmount(updatedProductDto.amount());
        product.setPrice(updatedProductDto.price());
        product.setCategory(updatedProductDto.category());
        product.setUpdatedDate(updatedProductDto.updatedDate());

        Product updatedProduct = product;

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        Mockito.when(ProductDto.convert(updatedProduct)).thenReturn(updatedProductDto);

        ProductDto result = productService.updateProduct(productId, updatedProductDto);

        assertEquals(updatedProductDto, result);

        Mockito.verify(productRepository).findById(productId);
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void testUpdateProduct_whenProductIsNotExist_shouldUpdateProductandReturnProductDto() {

        ProductDto productDto = new ProductDto(1L,
                "product",
                10,
                10.0,
                "GIDA",
                LocalDateTime.now());

        Mockito.when(productRepository.findById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(2L, productDto));

    }

    @Test
    void testDeleteProduct_whenProductExists_shouldDeleteProduct() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        String result = productService.deleteProduct(productId);
        String expected = "Product deleted";

        assertEquals(expected, result);
    }

    @Test
    void testDeleteCampaign_whenCampaignDoesNotExist_shouldReturnExceptionMessage() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(false);


        String result = productService.deleteProduct(productId);
        String expected = "Product not found with id: " + productId;

        assertEquals(expected, result);
    }

    private Product generateProduct() {
        Product product = new Product();

        product.setId(1L);
        product.setName("product");
        product.setAmount(10);
        product.setPrice(10.0);
        product.setCategory("GIDA");
        product.setUpdatedDate(LocalDateTime.now());

        return product;
    }

    private ProductDto generateProductDto (Product product){

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
