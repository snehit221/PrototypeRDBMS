package query.request;


import query.core.CoreDatabaseQuery;

import java.util.LinkedList;

/**
 * stores the objects required to execute create operation
 * extractedUserInputTokens tokens extracted from user input sql query
 * createQuery              used to execute CREATE DDL statement
 * userProvidedSQL          true if Database operation is successful, false otherwise
 */
public class CoreQueryOperationRequest {
    private LinkedList<String> extractedUserInputTokens;
    private String userProvidedSQL;
    private boolean isTransactionFlow;

    public LinkedList<String> getExtractedUserInputTokens() {
        return extractedUserInputTokens;
    }

    public void setExtractedUserInputTokens(LinkedList<String> extractedUserInputTokens) {
        this.extractedUserInputTokens = extractedUserInputTokens;
    }

    public String getUserProvidedSQL() {
        return userProvidedSQL;
    }

    public void setUserProvidedSQL(String userProvidedSQL) {
        this.userProvidedSQL = userProvidedSQL;
    }

    public boolean isTransactionFlow() {
        return isTransactionFlow;
    }

    public void setTransactionFlow(boolean isTransactionFlow) {
        this.isTransactionFlow = isTransactionFlow;
    }


    @Override
    public String toString() {
        return "CoreQueryOperationRequest{" +
                "extractedUserInputTokens=" + extractedUserInputTokens +
                ", userProvidedSQL='" + userProvidedSQL + '\'' +
                ", isTransactionFlow=" + isTransactionFlow +
                '}';
    }
}
