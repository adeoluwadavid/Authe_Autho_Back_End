/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.JWTAuth.security.jwt;


import com.example.JWTAuth.service.UserDS;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Adewole
 */
public class TokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtility jwtUtil;
    
    @Autowired
    private UserDS userDS;
    
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         
        try{
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtil.validateJwtToken(jwt)){
                String username = jwtUtil.getUserNameFromJwtToken(jwt);
                
                UserDetails userD = userDS.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authe = new UsernamePasswordAuthenticationToken(
                    userD, null, userD.getAuthorities());
                authe.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authe);
            }
        }catch(Exception e){
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request,response);
    }
     private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
          return headerAuth.substring(7, headerAuth.length());  

        }
            return null;
    }
    
}
