package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.model.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    public String sendSimpleEmail(EmailDetails details)
    {
        try {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully to " + details.getRecipient();
        } catch (IllegalArgumentException e) {
            return "Error: Invalid email address provided: " + e.getMessage();
        } catch (Exception e) {
            return "Error while sending email: " + e.getMessage();
        }
    }

    public String sendEmailWithAttachment(EmailDetails details)
    {
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        catch (MessagingException e) {
            return "Error while sending mail!!!";
        }
    }
    public String sendHtmlEmail(EmailDetails details) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            message.setContent(details.getMsgBody(), "text/html; charset=utf-8");
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setFrom(sender);

            javaMailSender.send(message);
            return "HTML mail sent successfully";
        } catch (Exception e) {
            return "Error while sending HTML email: " + e.getMessage();
        }
    }

}
