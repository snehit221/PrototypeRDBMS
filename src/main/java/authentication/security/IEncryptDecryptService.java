package authentication.security;


import utils.ConfigUtils;
import utils.ConstantKeywords;

/**
 * @see <a href="https://howtodoinjava.com/java/java-security/java-aes-encryption-example"> refer for further details</a>
 */
public interface IEncryptDecryptService {

    /**
     * this method sets app secret key into the env Properties HashTable
     */
    static void loadEnvSecretKeyVariable() {
        ConfigUtils.getInstance().getProperties().
                setProperty(ConstantKeywords.applicationSecretKey, ConstantKeywords.applicationSecretKeyValue);
    }

    /**
     * generates the SHA-1 security key for encryption / decryption
     *
     * @param applicationKey
     */
    void generateSecretKey(String applicationKey);

    /**
     * responsible for encrypting the string with the secret key
     *
     * @param strToEncrypt string to be encrypted
     * @param secret       secret key
     * @return encrypted string
     */
    String encrypt(final String strToEncrypt, final String secret);

    /**
     * responsible for decrypting the string with the secret key
     *
     * @param strToDecrypt string to be decrypted
     * @param secret       secret key
     * @return decrypted string
     */
    String decrypt(String strToDecrypt, String secret);
}
