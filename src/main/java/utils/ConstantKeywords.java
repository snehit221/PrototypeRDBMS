package utils;

import java.util.HashSet;
import java.util.Set;

public class ConstantKeywords {

    public static final String username = "username";

    public static final String password = "password";
    public static final String securityQuestion = "securityQuestion";
    public static final String securityQuestionAnswer = "securityQuestionAnswer";
    public static final String applicationSecretKey = "applicationSecretKey";
    public static final String applicationSecretKeyValue = "BC#!@#";
    public static final String enterUserNameInput = "Enter your username: ";
    public static final String enterPasswordInput = "Enter your password: ";
    public static final String columnCreationDelimiter = "|$|";
    public static final String columnSplitRetrievalDelimiter = "\\|\\$\\|";
    public static final String forwardSlash = "/";
    public static final String comma = ",";
    public static final String whereKeyword = "WHERE";
    public static final String fromKeyword = "FROM";
    public static final String setClause = "SET";
    public static final String createKeyword = "CREATE";
    public static final String selectKeyword = "SELECT";
    public static final String insertKeyword = "INSERT";
    public static final String deleteKeyword = "DELETE";
    public static final String updateKeyword = "UPDATE";
    public static final String beginKeyowrd = "BEGIN";
    public static final String endKeyword = "END";
    public static final String commitKeyword = "COMMIT";
    public static final String rollbackKeyword = "ROLLBACK";

    public static final String loggerFileName = "log.txt";

    public static final String asterisk = "*";  // to denote all columns in select query

    public static final String databaseNotExistErrorMessage = "ERROR: Cannot execute query without a valid database";

    public static Set<String> reservedValidKeywordsSet = new HashSet<>();

    static {
        reservedValidKeywordsSet.add("CREATE");
        reservedValidKeywordsSet.add("INSERT");
        reservedValidKeywordsSet.add("SELECT");
        reservedValidKeywordsSet.add("UPDATE");
        reservedValidKeywordsSet.add("DELETE");
        reservedValidKeywordsSet.add("PRIMARY");
        reservedValidKeywordsSet.add("KEY");
        reservedValidKeywordsSet.add("DATE");
        reservedValidKeywordsSet.add("INT");
        reservedValidKeywordsSet.add("VARCHAR");
        reservedValidKeywordsSet.add("(");
        reservedValidKeywordsSet.add(")");
        reservedValidKeywordsSet.add("BEGIN");
        reservedValidKeywordsSet.add("END");
        reservedValidKeywordsSet.add("COMMIT");
        reservedValidKeywordsSet.add("ROLLBACK");
    }


    public static void generatePostLoginMessages() {
        System.out.println("Login Success: Welcome to Prototype Database Server");
    }
}
