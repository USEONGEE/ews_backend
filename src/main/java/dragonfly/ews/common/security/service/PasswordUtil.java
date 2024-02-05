package dragonfly.ews.common.security.service;

import java.util.UUID;

public class PasswordUtil {
    public static String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }
}
