package bg.sofia.uni.fmi.mjt.hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hasher {
    public static String hash(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(string.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Hasher cannot hash passwords", e);
        }
    }
}
