package ru.ballo.projects.email.service;

import ru.ballo.projects.email.EmailContext;
import jakarta.mail.MessagingException;

public interface MailSender {

    void send(EmailContext context) throws MessagingException;
}
