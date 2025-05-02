package za.co.auth.records;

public record NewPasswordRecord(String username, String email, String newPassword, String confirmPassword) {
}
