package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.company.UpdateCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdateCompanyPhotosDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdatedCompanyPhotosDTO;
import com.ftn.iss.eventPlanner.dto.user.*;
import com.ftn.iss.eventPlanner.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;


    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','ADMIN','AUTHENTICATED_USER')")
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

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value = "/{accountId}/company/photos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedCompanyPhotosDTO> updateCompanyPhotos(@Valid @RequestBody UpdateCompanyPhotosDTO updateCompanyPhotosDTO, @PathVariable int accountId) throws IOException {
        UpdatedCompanyPhotosDTO updatedCompanyPhotosDTO = userService.updateCompanyPhotos(accountId, updateCompanyPhotosDTO);
        return new ResponseEntity<>(updatedCompanyPhotosDTO, HttpStatus.OK);
    }

    @PutMapping("/{accountId}/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO, @PathVariable int accountId) {
        userService.changePassword(accountId, changePasswordDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER')")
    @PutMapping("/{accountId}/profile-photo")
    public ResponseEntity<UpdatedProfilePhotoDTO> updateProfilePhoto(@Valid @RequestBody UpdateProfilePhotoDTO updateProfilePhotoDTO, @PathVariable int accountId) throws IOException {
        UpdatedProfilePhotoDTO updatedProfilePhotoDTO = userService.updateProfilePhoto(accountId, updateProfilePhotoDTO);
        return new ResponseEntity<>(updatedProfilePhotoDTO,HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','ADMIN','AUTHENTICATED_USER')")
    @PutMapping("/{accountId}/deactivate")
    public ResponseEntity<?> deactivateAccount(@PathVariable("accountId") int accountId) {
        userService.deactivateAccount(accountId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
