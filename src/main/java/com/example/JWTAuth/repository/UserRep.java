/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.JWTAuth.repository;

import com.example.JWTAuth.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Adewole
 */
@Repository
public interface UserRep extends JpaRepository<User, Integer> {
     Optional<User> findByUsername(String username);
     Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
