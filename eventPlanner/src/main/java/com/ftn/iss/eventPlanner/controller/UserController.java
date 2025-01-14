package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.company.CreatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.user.*;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

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
        if(savedUser.getRole()==Role.PROVIDER){
            savedUser.setCompany(new CreatedCompanyDTO(1,user.getCompany().getEmail(),user.getCompany().getName(),user.getCompany().getPhoneNumber(),user.getCompany().getDescription(),user.getCompany().getPhotos(), null));
        }

        return new ResponseEntity<CreatedUserDTO>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUserDTO> getUserDetails(@PathVariable("accountId") int accountId) {
        GetUserDTO user = userService.getUserDetails(accountId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable int id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedUserDTO> updateUser(@RequestBody UpdateUserDTO user, @PathVariable int id) {
        UpdatedUserDTO updatedUser = new UpdatedUserDTO();
        updatedUser.setId(id);
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        updatedUser.setProfilePhoto(user.getProfilePhoto());
        if(user.getCompany()!=null){
            updatedUser.setCompany(new UpdatedCompanyDTO(user.getCompany().getId(),user.getCompany().getPhoneNumber(),user.getCompany().getDescription(),user.getCompany().getPhotos(), null));
        }

        return new ResponseEntity<UpdatedUserDTO>(updatedUser, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/changePassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,@PathVariable int id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{userID}/favorites")
    public ResponseEntity<CreatedFavoriteDTO> addToFavorites(@PathVariable int userID, @RequestBody CreateFavoriteDTO createFavoriteDTO) {
        CreatedFavoriteDTO createdFavoriteDTO = new CreatedFavoriteDTO();
        createdFavoriteDTO.setUserID(userID);
        createdFavoriteDTO.setOfferingID(createFavoriteDTO.getOfferingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavoriteDTO);
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Integer>> getFavorites(@PathVariable int userId) {
        ArrayList<Integer> favorites = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<?> suspendUser(@PathVariable("id") int id) {
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
