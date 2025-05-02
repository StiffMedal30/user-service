package za.co.auth.service;

public interface Mailer {

    void sendVerificationEmail(String to, String token);
}
