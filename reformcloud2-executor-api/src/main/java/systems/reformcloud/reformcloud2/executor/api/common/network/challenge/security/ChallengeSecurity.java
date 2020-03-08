package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

public final class ChallengeSecurity {

    private ChallengeSecurity() {
        throw new UnsupportedOperationException();
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Nullable
    public static byte[] encryptChallengeRequest(@Nonnull String key, @Nonnull String challengeKey) {
        byte[] input = challengeKey.trim().getBytes();
        byte[] keyBytes = Arrays.copyOf(key.getBytes(), 256 / 8);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        try {
            return encryptChallengeRequest0(input, secretKeySpec);
        } catch (final GeneralSecurityException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static String decodeChallengeRequest(@Nonnull String key, @Nonnull byte[] request) {
        byte[] keyBytes = Arrays.copyOf(key.getBytes(), 256 / 8);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        try {
            return decryptChallengeRequest0(request, secretKeySpec);
        } catch (final GeneralSecurityException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static String hash(@Nonnull String plain) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(plain.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getMimeEncoder().encode(messageDigest.digest()), StandardCharsets.UTF_8);
        } catch (final NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String decryptChallengeRequest0(byte[] input, SecretKeySpec secretKeySpec) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] plainText = new byte[cipher.getOutputSize(input.length)];
        int ptLength = cipher.update(input, 0, input.length, plainText, 0);
        cipher.doFinal(plainText, ptLength);
        return new String(plainText, StandardCharsets.UTF_8).trim();
    }

    private static byte[] encryptChallengeRequest0(byte[] input, SecretKeySpec secretKeySpec) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        cipher.doFinal(cipherText, ctLength);
        return cipherText;
    }
}
