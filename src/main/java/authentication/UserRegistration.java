package authentication;

import authentication.request.RegisterUserRequest;
import authentication.security.IEncryptDecryptService;
import utils.ConfigUtils;
import utils.ConstantKeywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserRegistration {

    static ConfigUtils configUtils = ConfigUtils.getInstance();
    static String appSecretKeyValue = configUtils.getProperties().getProperty(ConstantKeywords.applicationSecretKey);
    private final IEncryptDecryptService encryptDecryptService;

    // dependency inversion applied here
    public UserRegistration(IEncryptDecryptService encryptDecryptService) {
        this.encryptDecryptService = encryptDecryptService;
    }

    /**
     * registers a new user
     * @param registerUserRequest request for regiser new user
     */
    public void registerUser(RegisterUserRequest registerUserRequest) {


        Properties registerUserProp = new Properties();
        ConfigUtils configUtils = ConfigUtils.getInstance();

        String userFilePath = configUtils.getUserConfigFullPath(registerUserRequest.getUserName());
        FileOutputStream fileOutputStream = null;

        if (new File(userFilePath).exists()) {
            System.out.println(registerUserRequest.getUserName() + " : already exists in the system!");
            return;
        }
        // store credentials in user properties file now
        registerUserProp.put(ConstantKeywords.username, registerUserRequest.getUserName());
        registerUserProp.put(ConstantKeywords.password, encryptDecryptService.encrypt(registerUserRequest.getPassword(), appSecretKeyValue));
        registerUserProp.put(ConstantKeywords.securityQuestion, registerUserRequest.getSecurityQuestion());
        registerUserProp.put(ConstantKeywords.securityQuestionAnswer, encryptDecryptService.encrypt(registerUserRequest.getSecurityQuestionAnswer(),appSecretKeyValue));
        try {
            fileOutputStream = new FileOutputStream(userFilePath);
            registerUserProp.store(fileOutputStream, "account registration details for user: " + registerUserRequest.getUserName());
            System.out.println("Successfully created account for user: " + registerUserRequest.getUserName());

        } catch (IOException e) {
            System.out.println("Unable to create new user " + e);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                System.out.println("Unable to close file");
            }
        }

    }
}
