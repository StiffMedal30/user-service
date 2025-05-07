package za.co.user.service.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import za.co.user.service.properties.MailProperties;
import za.co.user.service.service.Mailer;

@Service
@AllArgsConstructor
public class MailerImpl implements Mailer {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final LinkDecoderServiceImpl linkDecoderService;

    @Override
    public void sendAcountActivationEmail(String to, String token) {
        String subject = "Verify your NovaFlow account";
        String messageBody = mailProperties.getActivateAccountMessageBody();
        String url = mailProperties.getAccountActivationUrl();

        sendEmail(to, token, subject, messageBody, url);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Reset your NovaFlow password";
        String messageBody = mailProperties.getResetPasswordMessageBody();
        String url = mailProperties.getResetPasswordUrl();
        sendEmail(to, token, subject, messageBody, url);
    }

    private void sendEmail(String to, String token, String subject, String htmlBodyTemplate, String url) {
        try {
            String encodedLink = createEncodedRedirectUrl(token, url);
            String formattedHtml = htmlBodyTemplate.formatted(encodedLink);
            buildMail(to, subject, formattedHtml);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    private String createEncodedRedirectUrl(String token, String url) {
        String encoded = linkDecoderService.encode(url + "?token=" + token);
        return mailProperties.getBaseRedirectUrl() + encoded;
    }

    private void buildMail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

}
