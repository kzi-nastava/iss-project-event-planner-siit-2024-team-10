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
import java.util.List;

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
            savedUser.setCompany(new CreatedCompanyDTO(1,user.getCompany().getEmail(),user.getCompany().getName(),user.getCompany().getPhoneNumber(),user.getCompany().getDescription(),user.getCompany().getPhotos(), user.getCompany().getLocation()));
        }

        return new ResponseEntity<CreatedUserDTO>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetUserDTO>> getUsers() {
        Collection<GetUserDTO> users = new ArrayList<>() ;

        GetUserDTO user1 = new GetUserDTO();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setRole(Role.PROVIDER);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPhoneNumber("123-456-7890");
        user1.setProfilePhoto("https://example.com/photos/user1.jpg");
        user1.setLocation(new LocationDTO("New York", "USA", "5th Avenue", "10A"));
        user1.setCompany(new GetCompanyDTO("info@company1.com", "Tech Corp", "123-555-7890", "Tech solutions company",
                Arrays.asList("https://example.com/photos/company1.jpg"), new LocationDTO("San Francisco", "USA", "Market St", "101")));

        users.add(user1);

        // User 2
        GetUserDTO user2 = new GetUserDTO();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setRole(Role.PROVIDER);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setPhoneNumber("234-567-8901");
        user2.setProfilePhoto("https://example.com/photos/user2.jpg");
        user2.setLocation(new LocationDTO("London", "UK", "Baker Street", "221B"));
        user2.setCompany(new GetCompanyDTO("contact@company2.com", "Health Solutions", "345-666-8901", "Healthcare products company",
                Arrays.asList("https://example.com/photos/company2.jpg"), new LocationDTO("Bristol", "UK", "Queen St", "42")));

        users.add(user2);

        // User 3
        GetUserDTO user3 = new GetUserDTO();
        user3.setId(3);
        user3.setEmail("user3@example.com");
        user3.setRole(Role.EVENT_ORGANIZER);
        user3.setFirstName("Alice");
        user3.setLastName("Johnson");
        user3.setPhoneNumber("345-678-9012");
        user3.setProfilePhoto("https://example.com/photos/user3.jpg");
        user3.setLocation(new LocationDTO("Paris", "France", "Champs-Élysées", "33"));
        user3.setCompany(null);

        users.add(user3);

        // User 4
        GetUserDTO user4 = new GetUserDTO();
        user4.setId(4);
        user4.setEmail("user4@example.com");
        user4.setRole(Role.PROVIDER);
        user4.setFirstName("Bob");
        user4.setLastName("Brown");
        user4.setPhoneNumber("456-789-0123");
        user4.setProfilePhoto("https://example.com/photos/user4.jpg");
        user4.setLocation(new LocationDTO("Berlin", "Germany", "Kurfürstendamm", "101"));
        user4.setCompany(new GetCompanyDTO("hello@company3.com", "Green Energy", "789-555-0123", "Renewable energy company",
                Arrays.asList("https://example.com/photos/company3.jpg"), new LocationDTO("Hamburg", "Germany", "Jungfernstieg", "22")));

        users.add(user4);

        // User 5
        GetUserDTO user5 = new GetUserDTO();
        user5.setId(5);
        user5.setEmail("user5@example.com");
        user5.setRole(Role.PROVIDER);
        user5.setFirstName("Charlie");
        user5.setLastName("Williams");
        user5.setPhoneNumber("567-890-1234");
        user5.setProfilePhoto("https://example.com/photos/user5.jpg");
        user5.setLocation(new LocationDTO("Tokyo", "Japan", "Shibuya", "109"));
        user5.setCompany(null);

        users.add(user5);


        return new ResponseEntity<Collection<GetUserDTO>>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUserDTO> getUser(@PathVariable("id") int id) {

        GetUserDTO user = new GetUserDTO();
        user.setId(id);
        user.setEmail("user4@example.com");
        user.setRole(Role.PROVIDER);
        user.setFirstName("Bob");
        user.setLastName("Brown");
        user.setPhoneNumber("456-789-0123");
        user.setProfilePhoto("https://example.com/photos/user4.jpg");
        user.setLocation(new LocationDTO("Berlin", "Germany", "Kurfürstendamm", "101"));
        user.setCompany(new GetCompanyDTO("hello@company3.com", "Green Energy", "789-555-0123", "Renewable energy company",
                Arrays.asList("https://example.com/photos/company3.jpg"), new LocationDTO("Hamburg", "Germany", "Jungfernstieg", "22")));

        return new ResponseEntity<GetUserDTO>(user, HttpStatus.OK);
    }

    @PostMapping(value="/login",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse=new LoginResponseDTO();
        loginResponse.setToken("token for "+loginRequest.getEmail());

        return new ResponseEntity<LoginResponseDTO>(loginResponse, HttpStatus.CREATED);
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
        updatedUser.setLocation(user.getLocation());
        if(user.getCompany()!=null){
            updatedUser.setCompany(new UpdatedCompanyDTO(user.getCompany().getId(),user.getCompany().getPhoneNumber(),user.getCompany().getDescription(),user.getCompany().getPhotos(), user.getCompany().getLocation()));
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
    @PostMapping("/{userID}/favorites/{offeringID}")
    public ResponseEntity<List<Integer>> addToFavorites(@PathVariable int userID, @PathVariable int offeringID) {
        ArrayList<Integer> favorites = new ArrayList<>();
        favorites.add(offeringID);
        return ResponseEntity.ok(favorites);
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
