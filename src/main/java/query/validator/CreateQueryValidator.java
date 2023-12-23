package query.validator;

import query.core.CreateQuery;
import query.request.CoreQueryOperationRequest;
import transaction.TransactionHandler;
import utils.ConstantKeywords;

import java.util.LinkedList;
import java.util.List;

public class CreateQueryValidator implements IQueryValidator<CreateQuery> {

    private String tableName;
    private List<String> sanitizedColumnNameTokens = new LinkedList<>();


    public void extractMetaDataFromTokens(List<String> tokens) {
        //System.out.println("tokens: "+ tokens);
        int indexOfTokens = 0;

        if (tokens.size() >= 3 && tokens.get(indexOfTokens).equalsIgnoreCase("CREATE") &&
                tokens.get(indexOfTokens + 1).equalsIgnoreCase("TABLE")) {
            indexOfTokens += 2;

            // Extract the table name
            if (indexOfTokens < tokens.size()) {
                tableName = tokens.get(indexOfTokens);
                //System.out.println("Table Name: " + tableName);
                indexOfTokens++;
            } else {
                System.out.println("No table name was provided");
                return;
            }

            while (indexOfTokens < tokens.size()) {
                String token = tokens.get(indexOfTokens);
                //System.out.println("token: "+ token);
                if (token.equals("(")) {
                    indexOfTokens++;
                    continue;
                } else if (token.equals(")")) {
                    break;
                } else {
                    boolean isThisKeywordReserved = false;
                    for (String reservedKeyword : ConstantKeywords.reservedValidKeywordsSet) {
                        if (token.contains(reservedKeyword)) {
                            isThisKeywordReserved = true;
                        }
                    }
                    if (!isThisKeywordReserved) {
                        sanitizedColumnNameTokens.add(token);
                    }
                }
                indexOfTokens++;
            }
        } else {
            System.out.println("No valid CREATE TABLE statement found.");
        }
    }


    /**
     * validates the create query
     *
     * @param coreQueryOperationRequest
     */
    @Override
    public void validateQuery(CoreQueryOperationRequest coreQueryOperationRequest, CreateQuery createQuery) {

        String createActionType = coreQueryOperationRequest.getExtractedUserInputTokens().get(1).toUpperCase();

        // DB creation scenario
        if (createActionType.equals("DATABASE")) {
            String extractedDatabaseName = coreQueryOperationRequest.getExtractedUserInputTokens().get(2);
            if (coreQueryOperationRequest.isTransactionFlow()) {
                System.out.println("Transaction Flow. will not create database yet");
                TransactionHandler.populateCreateDatabaseOperationQueue(coreQueryOperationRequest);
                return;
            } else {
                createQuery.createDatabase(extractedDatabaseName);
            }
        }
        // TABLE creation sql statement scenario
        else if (createActionType.equals("TABLE")) {

            if (coreQueryOperationRequest.isTransactionFlow()) {
                System.out.println("Transaction Flow for create table. will not table database yet");
                TransactionHandler.populateCreateDatabaseOperationQueue(coreQueryOperationRequest);
                return;
            }
            extractMetaDataFromTokens(coreQueryOperationRequest.getExtractedUserInputTokens());
            createQuery.createTable(tableName, sanitizedColumnNameTokens);
        }
    }
}


