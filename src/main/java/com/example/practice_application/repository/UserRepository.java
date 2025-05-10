package com.example.practice_application.repository;

import com.example.practice_application.dto.UserResponseDto;
import com.example.practice_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    @Query("SELECT s FROM User s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.details) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(s.age AS string) LIKE CONCAT('%', :search, '%')")
    List<User> searchUsers(String search);
}
