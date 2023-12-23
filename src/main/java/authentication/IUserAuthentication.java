package authentication;

public interface IUserAuthentication {

    /**
     * Authenticates a user with the provided username and password.
     *
     * @param userName The username provided by the user.
     * @param password The password provided by the user.
     * @return true if the user is authenticated, false otherwise.
     */
    boolean isUserAuthenticated(String userName, String password);

    /**
     * Authenticates a security captcha.
     *
     * @return true if the captcha is authenticated, false otherwise.
     */

    Boolean authenticateSecurityQuestion(String userProvidedName, String securityQuestionAnswer);

}
