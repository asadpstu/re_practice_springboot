package com.example.practice_application.service;
import com.example.practice_application.dto.ProductDto;
import com.example.practice_application.dto.ProductResponseDto;
import com.example.practice_application.model.Product;
import com.example.practice_application.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ImgBBService imgbbService;


    public ProductResponseDto createProduct(ProductDto productDto, MultipartFile imageFile) throws Exception {
        String imageUrl = imgbbService.uploadImage(imageFile, 15552000); // 180 days
        productDto.setImageUrl(imageUrl);
        Product newProduct = new Product();
        newProduct.setName(productDto.getName());
        newProduct.setDescription(productDto.getDescription());
        newProduct.setPrice(productDto.getPrice());
        newProduct.setStock(productDto.getStock());
        newProduct.setCategory(productDto.getCategory());
        newProduct.setActive(productDto.isActive());
        newProduct.setImageUrl(productDto.getImageUrl());
        Product addedProduct = productRepository.save(newProduct);
        return getProductResponse(addedProduct);
    }


    public ProductResponseDto updateProduct(Long productId, ProductDto productDto, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            return null;
        }
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = imgbbService.uploadImage(imageFile, 15552000);
                productDto.setImageUrl(imageUrl);
            } else {
                productDto.setImageUrl(existingProduct.getImageUrl());
            }
            existingProduct.setName(productDto.getName());
            existingProduct.setDescription(productDto.getDescription());
            existingProduct.setPrice(productDto.getPrice());
            existingProduct.setStock(productDto.getStock());
            existingProduct.setCategory(productDto.getCategory());
            existingProduct.setActive(productDto.isActive());
            existingProduct.setImageUrl(productDto.getImageUrl());
            Product updatedProduct = productRepository.save(existingProduct);
            return getProductResponse(updatedProduct);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }

    public List<ProductResponseDto> searchProduct(String keyword, int page, int size) {
        int limit = size > 0 ? size : 10;
        int offset = (page - 1) * limit;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Product> productPage = productRepository.searchProduct(keyword, pageable);
        return productPage.getContent().stream()
                .map(this::getProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponseDto getProductResponse(Product newProduct) {
        var productResponse = new ProductResponseDto();
        productResponse.setId(newProduct.getId());
        productResponse.setName(newProduct.getName());
        productResponse.setDescription(newProduct.getDescription());
        productResponse.setPrice(newProduct.getPrice());
        productResponse.setCategory(newProduct.getCategory());
        productResponse.setStock(newProduct.getStock());
        productResponse.setUpdatedAt(newProduct.getUpdatedAt());
        productResponse.setActive(newProduct.isActive());
        productResponse.setImageUrl(newProduct.getImageUrl());
        return productResponse;
    }

    public List<String> getCategories() {
        return productRepository.getCategories();
    }

    public Boolean deleteProduct(Long productId) {
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false;
        }
        productRepository.deleteById(productId);
        return true;
    }
}