package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.model.EmailDetails;
import com.ftn.iss.eventPlanner.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send_email")
    public String sendMail(@RequestBody EmailDetails details)
    {
        String status
                = emailService.sendSimpleEmail(details);

        return status;
    }

    @PostMapping("/send_email_with_attachment")
    public String sendEmailWithAttachment(@RequestBody EmailDetails details)
    {
        String status
                = emailService.sendEmailWithAttachment(details);

        return status;
    }
}
