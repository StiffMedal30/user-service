package za.co.auth.records;

public record JwtResponseRecord(String token, String type, String username) {

    public JwtResponseRecord(String token, AppUserRecord dto) {
        this(token, "Bearer", dto.username());
    }
}
