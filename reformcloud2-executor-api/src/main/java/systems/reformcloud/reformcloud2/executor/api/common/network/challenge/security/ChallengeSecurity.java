/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private ChallengeSecurity() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static byte[] encryptChallengeRequest(@NotNull String key, @NotNull String challengeKey) {
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
    public static String decodeChallengeRequest(@NotNull String key, @NotNull byte[] request) {
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
    public static String hash(@NotNull String plain) {
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
