/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.JWTAuth.controller;

import javax.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Adewole
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess(){
        return "Content for All";
    }
    
    @GetMapping("/user")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
     //@Secured("ROLE_USER")
    //@RolesAllowed("USER")
    public String userAccess(){
        return "Content for only the User";
    }
    
    @GetMapping("/admin")
    //@PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(){
        return "Content for only the Admin";
    }
}
