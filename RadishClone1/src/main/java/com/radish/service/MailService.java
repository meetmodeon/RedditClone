package com.radish.service;

import com.radish.exceptions.SpringRedditException;
import com.radish.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    @Async
    void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator mimeMessagePreparator= mimeMessage -> {
            MimeMessageHelper messageHelper= new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springreddit@email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody());

//            String processedMessage = mailContentBuilder.build(notificationEmail.getBody());
//            messageHelper.setText(processedMessage, true);
        };
        try{
            mailSender.send(mimeMessagePreparator);
            log.info("Activation email send !!");
        }catch (MailException e){
            log.error("Exception occurred when sending mail",e);
            throw new SpringRedditException("Exception occurred when sending mail to "+notificationEmail.getRecipient(),e);
        }
    }
}
