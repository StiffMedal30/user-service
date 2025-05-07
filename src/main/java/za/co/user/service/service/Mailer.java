package za.co.user.service.service;

public interface Mailer {

    void sendAcountActivationEmail(String to, String token);

    void sendPasswordResetEmail(String to, String token);
}
