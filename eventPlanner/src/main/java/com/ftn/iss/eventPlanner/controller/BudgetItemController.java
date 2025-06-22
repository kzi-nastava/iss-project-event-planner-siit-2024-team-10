package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.budgetitem.*;
import com.ftn.iss.eventPlanner.services.BudgetItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/budget-items")
public class BudgetItemController {
    @Autowired
    private BudgetItemService budgetItemService;

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedBudgetItemDTO> createBudgetItem(@RequestBody CreateBudgetItemDTO createBudgetItemDTO) {
        try{
            CreatedBudgetItemDTO createdBudgetItemDTO = budgetItemService.create(createBudgetItemDTO);
            return new ResponseEntity<>(createdBudgetItemDTO, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetBudgetItemDTO>> getAllBudgetItems() {
        List<GetBudgetItemDTO> budgetItems = budgetItemService.findAll();
        return new ResponseEntity<>(budgetItems, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetBudgetItemDTO> getBudgetItemById(@PathVariable int id) {
        try {
            GetBudgetItemDTO budgetItem = budgetItemService.findById(id);
            return new ResponseEntity<>(budgetItem, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
/*
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedEventTypeDTO> updateEventType(@PathVariable int id, @Valid @RequestBody UpdateEventTypeDTO updateEventTypeDTO) {
        try {
            UpdatedEventTypeDTO updatedEventType = eventTypeService.update(id, updateEventTypeDTO);
            return new ResponseEntity<>(updatedEventType, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable int id) {
        try {
            eventTypeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

 */
}
