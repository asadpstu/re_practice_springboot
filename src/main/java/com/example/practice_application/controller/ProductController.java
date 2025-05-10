package com.example.practice_application.controller;
import com.example.practice_application.dto.ProductDto;
import com.example.practice_application.dto.ProductResponseDto;
import com.example.practice_application.service.ImgBBService;
import com.example.practice_application.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Product", description = "Product's related endpoint.")
public class ProductController {

    @Autowired
    private  ProductService productService;
    @Autowired
    private ImgBBService imgbbService;


    @PostMapping("/product/image/upload")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile imageFile,
                                              @RequestParam(value = "expiration", defaultValue = "15552000") int expiration) {
        try {
            String response = imgbbService.uploadImage(imageFile, expiration);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed: " + e.getMessage());
        }
    }


    @PostMapping(value = "/product/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestPart("product") ProductDto productDto,
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            ProductResponseDto savedProduct = productService.createProduct(productDto, imageFile);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/product/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestPart("product") ProductDto productDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            ProductResponseDto  updatedProduct =  productService.updateProduct(productId, productDto, imageFile);
            if (updatedProduct != null) {
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/product/search")
    public ResponseEntity<List<ProductResponseDto>> searchProduct(
            @RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page ,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
       List<ProductResponseDto> productList  =  productService.searchProduct(keyword, page, size);
       return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/product/categories")
    public ResponseEntity<List<String>> searchProduct() {
        List<String> categories  =  productService.getCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @DeleteMapping("/product/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long productId) {
        try {
            var  result =  productService.deleteProduct(productId);
            if (result) {
                return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(false, HttpStatus.MOVED_PERMANENTLY);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}