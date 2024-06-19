package pl.noteally.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) throws MailSendException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("noteally@demomailtrap.com");
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            logger.info("EmailService.sendEmail(): Email sent successfully to: {}", to);
        } catch (MailSendException e) {
            logger.error("EmailService.sendEmail(): Failed to send email to {}: {}", to, e.getMessage());
            throw e;
        }
    }
}
