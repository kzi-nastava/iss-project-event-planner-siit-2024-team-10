package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedUserDTO> createUser(@RequestBody CreateUserDTO user){
        CreatedUserDTO savedUser = new CreatedUserDTO();
        savedUser.setId(1);
        savedUser.setRole(user.getRole());
        savedUser.setEmail(user.getEmail());
        savedUser.setFirstName(user.getFirstName());
        savedUser.setLastName(user.getLastName());
        savedUser.setPhoneNumber(user.getPhoneNumber());
        savedUser.setProfilePhoto(user.getProfilePhoto());
        savedUser.setLocation(user.getLocation());
        if(savedUser.getRole()==Role.PROVIDER){
            savedUser.setCompany(user.getCompany());
        }

        return new ResponseEntity<CreatedUserDTO>(savedUser, HttpStatus.CREATED);
    }
}
