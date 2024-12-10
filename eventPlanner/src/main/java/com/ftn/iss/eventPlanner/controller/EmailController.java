package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.model.EmailDetails;
import com.ftn.iss.eventPlanner.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send_email")
    public ResponseEntity<String> sendMail(@RequestBody EmailDetails details) {
        try {
            String status = emailService.sendSimpleEmail(details);
            if (status.contains("Successfully")) {
                return ResponseEntity.ok(status);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending email: " + e.getMessage());
        }
    }

    @PostMapping("/send_email_with_attachment")
    public ResponseEntity<String> sendEmailWithAttachment(@RequestBody EmailDetails details) {
        try {
            String status = emailService.sendEmailWithAttachment(details);
            if (status.contains("Successfully")) {
                return ResponseEntity.ok(status);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending email with attachment: " + e.getMessage());
        }
    }
}
