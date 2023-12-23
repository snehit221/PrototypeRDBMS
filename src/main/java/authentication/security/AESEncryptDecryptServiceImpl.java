package authentication.security;

import utils.ConfigUtils;
import utils.ConstantKeywords;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESEncryptDecryptServiceImpl implements IEncryptDecryptService {

    public static final String ALGORITHM = "AES";
    private static SecretKeySpec secretKey;

    public static void main(String[] args) {
        IEncryptDecryptService.loadEnvSecretKeyVariable();
        String appSecretKey = ConfigUtils.getInstance().getProperties().getProperty(ConstantKeywords.applicationSecretKey);
        System.out.println(appSecretKey);
        AESEncryptDecryptServiceImpl aesEncryptDecryptService = new AESEncryptDecryptServiceImpl();
        String encrypted = aesEncryptDecryptService.encrypt("a", appSecretKey);
        String decrypted = aesEncryptDecryptService.decrypt(encrypted, appSecretKey);
    }

    /**
     * generates the SHA-1 security key for encryption / decryption
     *
     * @param applicationKey
     */
    @Override
    public void generateSecretKey(String applicationKey) {
        MessageDigest sha = null;
        try {
            byte[] key = applicationKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * responsible for encrypting the string with the secret key
     *
     * @param strToEncrypt string to be encrypted
     * @param secret       secret key
     * @return encrypted string
     */
    @Override
    public String encrypt(String strToEncrypt, String secret) {
        try {
            generateSecretKey(secret);
            Cipher aesCipher = Cipher.getInstance(ALGORITHM);
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getEncoder().encodeToString(aesCipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error during encryption: " + e);
        }
        return null;
    }

    /**
     * responsible for decrypting the string with the secret key
     *
     * @param strToDecrypt string to be decrypted
     * @param secret       secret key
     * @return decrypted string
     */
    @Override
    public String decrypt(String strToDecrypt, String secret) {
        try {
            generateSecretKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error during decryption " + e);
        }
        return null;
    }
}


