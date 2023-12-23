import authentication.IUserAuthentication;
import authentication.UserAuthentication;
import authentication.UserRegistration;
import authentication.request.RegisterUserRequest;
import authentication.security.AESEncryptDecryptServiceImpl;
import authentication.security.IEncryptDecryptService;
import query.identification.IQueryParser;
import query.identification.QueryParser;
import utils.ConstantKeywords;
import utils.ValidationUtils;

import java.util.Scanner;

public class Main {

    static IEncryptDecryptService encryptDecryptService = new AESEncryptDecryptServiceImpl();

    public static void main(String[] args) {
        //loads the encryption key into env properties
        IEncryptDecryptService.loadEnvSecretKeyVariable();
        System.out.println("Please Choose from below options to use Prototype Database");
        System.out.println("1. Login");
        System.out.println("2. Register User");
        System.out.print("Select 1 to login or 2 to register a new user. Enter \"exit\" to terminate the session: ");

        String providedUserName;
        String providedPassword;
        String securityQuestion;   // used for two-factor authentication (2FA)
        String securityQuestionAnswer;


        while (true) {
            Scanner userInput = new Scanner(System.in);
            String sanitizedInput = userInput.nextLine().trim();
            System.out.println();
            switch (sanitizedInput) {
                case "1" -> {
                    System.out.print(ConstantKeywords.enterUserNameInput);
                    providedUserName = userInput.nextLine().trim();
                    System.out.print(ConstantKeywords.enterPasswordInput);
                    providedPassword = userInput.nextLine().trim();
                    IUserAuthentication userAuthentication = new UserAuthentication(encryptDecryptService);

                    boolean loginResult = userAuthentication.isUserAuthenticated(providedUserName, providedPassword);
                    if (loginResult) {
                        securityQuestionAnswer = userInput.nextLine().trim();
                        boolean finalAuthResult = userAuthentication.authenticateSecurityQuestion(providedUserName, securityQuestionAnswer);
                        if (finalAuthResult) {
                            ConstantKeywords.generatePostLoginMessages();

                            //String userProvidedSQL = userInput.nextLine().trim();
                            //userInput.close();
                            IQueryParser queryParser = new QueryParser();
                            queryParser.identifyQueryType();

                        } else {
                            System.out.println("ERROR : Access denied for user " + providedUserName + " due to invalid captcha (using password: YES)");
                        }

                        return;
                    }
                    System.out.println("ERROR : Access denied for user " + providedUserName + " (using password: YES)");
                }
                case "2" -> {
                    System.out.print("Enter your username: ");
                    providedUserName = userInput.nextLine().trim();
                    System.out.print("Enter your password: ");
                    providedPassword = userInput.nextLine().trim();
                    System.out.print("Enter your security question: ");
                    securityQuestion = userInput.nextLine().trim();
                    System.out.print("Enter your answer for security question: ");
                    securityQuestionAnswer = userInput.nextLine().trim();

                    // return if user input is invalid
                    if (ValidationUtils.isUserInputInvalid(providedUserName, providedPassword,
                            securityQuestion, securityQuestionAnswer)) {
                        return;
                    }
                    RegisterUserRequest registerUserRequest = new RegisterUserRequest(providedUserName, providedPassword, securityQuestion, securityQuestionAnswer);
                    UserRegistration userRegistration = new UserRegistration(encryptDecryptService);
                    userRegistration.registerUser(registerUserRequest);
                }
                case "exit" -> System.out.println("Bye");

                default -> {
                }
            }


        }


    }


}