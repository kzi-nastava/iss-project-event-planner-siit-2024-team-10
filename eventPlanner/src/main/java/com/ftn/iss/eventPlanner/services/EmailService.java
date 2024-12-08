package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.model.EmailDetails;

public interface EmailService {

    String sendSimpleEmail(EmailDetails details);

    String sendEmailWithAttachment(EmailDetails details);
}