package za.co.user.service.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import za.co.user.service.properties.MailProperties;
import za.co.user.service.service.Mailer;

@Service
public class MailerImpl implements Mailer {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final LinkDecoderServiceImpl linkDecoderService;

    public MailerImpl(JavaMailSender mailSender, MailProperties mailProperties, LinkDecoderServiceImpl linkDecoderService) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.linkDecoderService = linkDecoderService;
    }

    public void sendVerificationEmail(String to, String token) {
        // Build the actual internal verification URL with the token
        String actualUrl = mailProperties.getVerificationUrl() + "?token=" + token;

        // Encode the actual URL (e.g., using Base64)
        String encoded = linkDecoderService.encode(actualUrl);

        // Construct the public-facing redirect URL
        String redirectUrl = mailProperties.getBaseRedirectUrl() + encoded;

        // Compose and send the email with the encoded redirect link
        String subject = "Verify your NovaFlow account";
        String htmlContent = """
                <html>
                <body>
                    <h2>Welcome to NovaFlow!</h2>
                    <p>Thanks for registering. Please click the link below to activate your account:</p>
                    <a href="%s" style="display:inline-block;padding:10px 20px;background-color:#4CAF50;color:white;text-decoration:none;border-radius:5px;">Activate Account</a>
                    <p>If you didn't register, you can safely ignore this email.</p>
                    <br>
                    <small>This link will expire in 10 hours.</small>
                </body>
                </html>
                """.formatted(redirectUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Set `true` for HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

}
