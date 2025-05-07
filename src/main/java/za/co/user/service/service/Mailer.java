package za.co.user.service.service;

public interface Mailer {

    void sendVerificationEmail(String to, String token);
}
