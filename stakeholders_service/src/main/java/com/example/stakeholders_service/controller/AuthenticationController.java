package com.example.stakeholders_service.controller;

import com.example.stakeholders_service.dto.AuthenticationRequestDTO;
import com.example.stakeholders_service.dto.AuthenticationResponseDTO;
import com.example.stakeholders_service.dto.RegistrationDTO;
import com.example.stakeholders_service.model.User;
import com.example.stakeholders_service.service.UserService;
import com.example.stakeholders_service.util.TokenUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationDTO registrationDTO) {

        User existUser = userService.findByEmail(registrationDTO.getEmail());
        if (existUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        existUser = userService.findByUsername(registrationDTO.getUsername());
        if (existUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }



        User user = userService.registerFromDTO(registrationDTO);
        if(user == null) {
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponseDTO> signin(@RequestBody AuthenticationRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getEmail());
        long expiresIn = tokenUtils.getExpiredIn();
        String role = user.getRole().getName();

        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt, expiresIn,role));
    }





}
