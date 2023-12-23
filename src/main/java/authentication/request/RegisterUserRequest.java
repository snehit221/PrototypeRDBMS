package authentication.request;

/**
 * used for register a new user
 */
public class RegisterUserRequest {

    private String userName;
    private String password;
    private String securityQuestion;
    private String securityQuestionAnswer;

    public RegisterUserRequest(String userName, String password, String securityQuestion, String securityQuestionAnswer) {
        this.userName = userName;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }
}
