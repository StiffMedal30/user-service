package za.co.auth.service;

public interface LinkDecoderService {
    String decode(String token);
    String encode(String url);
}
