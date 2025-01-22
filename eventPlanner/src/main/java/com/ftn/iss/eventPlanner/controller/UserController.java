package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.company.UpdateCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.user.*;
import com.ftn.iss.eventPlanner.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUserDTO> getUserDetails(@PathVariable("accountId") int accountId) {
        GetUserDTO user = userService.getUserDetails(accountId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER')")
    @PutMapping(value = "/{accountId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedUserDTO> updateUser(@Valid @RequestBody UpdateUserDTO user, @PathVariable int accountId) {
        UpdatedUserDTO updatedUserDTO = userService.updateUser(accountId, user);
        return new ResponseEntity<UpdatedUserDTO>(updatedUserDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value = "/{accountId}/company", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedCompanyDTO> updateCompany(@Valid @RequestBody UpdateCompanyDTO company, @PathVariable int accountId) {
        UpdatedCompanyDTO updatedCompanyDTO = userService.updateCompany(accountId, company);
        return new ResponseEntity<>(updatedCompanyDTO, HttpStatus.OK);
    }

    @PutMapping("/{accountId}/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO, @PathVariable int accountId) {
        userService.changePassword(accountId, changePasswordDTO);
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

    @PutMapping("/{accountId}/deactivate")
    public ResponseEntity<?> deactivateAccount(@PathVariable("accountId") int accountId) {
        userService.deactivateAccount(accountId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
