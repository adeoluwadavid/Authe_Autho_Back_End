/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.JWTAuth.repository;

import com.example.JWTAuth.model.ERole;
import com.example.JWTAuth.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Adewole
 */
@Repository
public interface RoleRep extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
