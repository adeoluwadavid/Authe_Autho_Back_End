package com.example.JWTAuth.controller;

import com.example.JWTAuth.model.ERole;
import com.example.JWTAuth.model.Role;
import com.example.JWTAuth.model.User;
import com.example.JWTAuth.repository.RoleRep;
import com.example.JWTAuth.repository.UserRep;
import com.example.JWTAuth.security.jwt.JwtUtility;
import com.example.JWTAuth.security.jwt.payload.request.LoginRequest;
import com.example.JWTAuth.security.jwt.payload.request.SignupRequest;
import com.example.JWTAuth.security.jwt.payload.response.JwtResponse;
import com.example.JWTAuth.security.jwt.payload.response.MessageResponse;
import com.example.JWTAuth.service.UserD;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.RuntimeErrorException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Adewole
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authe;

    @Autowired
    JwtUtility jwtUtil;
    
    @Autowired
    UserRep userRep;
    
    @Autowired
    RoleRep roleRep;
    
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authen = authe.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authen);
        String jwt = jwtUtil.generateJwtToken(authen);

        UserD userDetails = (UserD) authen.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getAddress(),
                userDetails.getEmail(),
                roles
        ));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRep.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if(userRep.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }
        // Create new user's account
        User user = new User(signupRequest.getFirstName(),
                                signupRequest.getLastName(),
                                 signupRequest.getUsername(),
                                 signupRequest.getAddress(),
                                 signupRequest.getEmail(),
                                encoder.encode(signupRequest.getPassword()));
        
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        
        if(strRoles == null){
            Role userRole = roleRep.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }else{
            strRoles.forEach(role -> {
                switch(role){
                    case "admin":
                        Role adminRole = roleRep.findByName(ERole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    
                    default:
                        Role userRole = roleRep.findByName(ERole.USER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRep.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
     
}
