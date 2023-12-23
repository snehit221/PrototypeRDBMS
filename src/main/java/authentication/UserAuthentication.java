package authentication;

import authentication.security.IEncryptDecryptService;
import utils.ConfigUtils;
import utils.ConstantKeywords;

import java.io.IOException;
import java.util.Properties;

public class UserAuthentication implements IUserAuthentication {

    private final IEncryptDecryptService encryptDecryptService;

    private final ConfigUtils configUtils = ConfigUtils.getInstance();

    private Properties userProperties;

    // dependency inversion applied here
    public UserAuthentication(IEncryptDecryptService encryptDecryptService) {
        this.encryptDecryptService = encryptDecryptService;
    }

    /**
     * @param userProvidedName     provided by the user
     * @param userProvidedPassword provided by the user
     * @return true / false after authenticating user's credentials and security captcha
     */
    public boolean isUserAuthenticated(String userProvidedName, String userProvidedPassword) {
        // get the credentials for file storage

        try {
            // read the userProperties file containing user auth creds and captcha
            this.userProperties = configUtils.readPropertyConfigFile(configUtils.getUserConfigFullPath(userProvidedName));
            // case when auth is success

            String storedEncryptedPassword = userProperties.getProperty(ConstantKeywords.password);

            String encryptUserProvidedPassword = encryptDecryptService.encrypt(userProvidedPassword, ConstantKeywords.applicationSecretKeyValue);

            boolean result = storedEncryptedPassword.equals(encryptUserProvidedPassword);

            if (result) {
                System.out.print("Please answer the security question: ");
                System.out.println(userProperties.getProperty(ConstantKeywords.securityQuestion));
            }
            return result;

        } catch (Exception e) {
            System.out.println("ERROR: Invalid Credentials");
        }

        return false;
    }


    /**
     * @param userProvidedName       authenticated post login userName
     * @param securityQuestionAnswer answer provided by the user
     * @return true if security question answer matches, false otherwise
     */
    @Override
    public Boolean authenticateSecurityQuestion(String userProvidedName, String securityQuestionAnswer) {
        String storedAnswer = userProperties.getProperty(ConstantKeywords.securityQuestionAnswer);
        String encryptUserSecurityAnswer = encryptDecryptService.encrypt(securityQuestionAnswer, ConstantKeywords.applicationSecretKeyValue);
        return storedAnswer.trim().equals(encryptUserSecurityAnswer);
    }


}
