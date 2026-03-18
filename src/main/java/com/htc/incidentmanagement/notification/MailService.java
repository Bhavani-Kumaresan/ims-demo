package com.htc.incidentmanagement.notification;

import java.io.File;

public interface MailService {
    void sendMail(String to, String subject, String body);
     void sendMailWithAttachment(
            String to,
            String subject,
            String body,
            File attachment
    );
}
