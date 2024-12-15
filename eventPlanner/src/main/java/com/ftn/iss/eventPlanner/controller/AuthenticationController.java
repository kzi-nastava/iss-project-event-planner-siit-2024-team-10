package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.authentication.LoginRequestDTO;
import com.ftn.iss.eventPlanner.dto.authentication.LoginResponseDTO;
import com.ftn.iss.eventPlanner.dto.authentication.RegisterDTO;
import com.ftn.iss.eventPlanner.exception.ResourceConflictException;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.services.AccountService;
import com.ftn.iss.eventPlanner.util.TokenUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(value = "api/auth")
public class AuthenticationController {
    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;


    @PostMapping(value="/login",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> createAuthenticationToken(
            @RequestBody LoginRequestDTO authenticationRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Account account = (Account) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(account);
        int expiresIn = tokenUtils.getExpiresIn();

        return ResponseEntity.ok(new LoginResponseDTO(jwt, expiresIn));
    }
}
