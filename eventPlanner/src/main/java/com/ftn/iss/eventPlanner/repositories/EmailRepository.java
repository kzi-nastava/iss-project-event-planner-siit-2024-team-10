package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.EmailDetails;

public interface EmailRepository {

    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);
}