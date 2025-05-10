package com.example.practice_application.repository;

import com.example.practice_application.dto.ProductResponseDto;
import com.example.practice_application.model.Product;
import org.hibernate.annotations.processing.HQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT s FROM Product s WHERE "+
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<Product> searchProduct(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT(s.category) FROM Product s")
    List<String> getCategories();
}
