package query.validator;

import query.core.InsertQuery;
import query.request.CoreQueryOperationRequest;
import utils.ConstantKeywords;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertQueryValidator implements IQueryValidator<InsertQuery> {
    private static String tableName;

    private List<String> insertValueTokens = new LinkedList<>();


    /**
     * @param userProvidedSQL
     */
    private void extractDataForInsertQuery(List<String> extractedTokens, String userProvidedSQL) {
        //System.out.println("extracted tokens: " + extractedTokens);
        int indexOfValue = -1;
        if (extractedTokens.contains("VALUES")) {
            indexOfValue = extractedTokens.indexOf("VALUES");
        }
        if (extractedTokens.contains("values")) {
            indexOfValue = extractedTokens.indexOf("values");
        }

        if (indexOfValue > -1) {
            //  now get all the tokens excluding ( and ) to store
            tableName = extractedTokens.get(2);
            int indexOFOpenParen = userProvidedSQL.indexOf("(");
            int indexOfCloseParen = userProvidedSQL.indexOf(")");

            String insertValues = userProvidedSQL.substring(indexOFOpenParen + 1, indexOfCloseParen).trim();
            //System.out.println("insertValues V2: " + insertValues);
            String[] interValuesArr = insertValues.split(ConstantKeywords.comma);
            // populate the inserValue tokens
            for (String token : interValuesArr) {
                insertValueTokens.add(token.trim());
            }
        } else {
            System.out.println("ERROR: Missing values keyword in insert statement");

        }
    }

    /**
     * @param coreQueryOperationRequest
     */
    @Override
    public void validateQuery(CoreQueryOperationRequest coreQueryOperationRequest, InsertQuery insertQuery) {
        String insertQueryPattern = "INSERT\\s+INTO\\s+\\w+\\s+VALUES\\s*\\([^)]*\\);";

        Matcher patternForInsertQuery = Pattern.compile(insertQueryPattern).matcher(coreQueryOperationRequest.getUserProvidedSQL());
        if (patternForInsertQuery.matches()) {
            extractDataForInsertQuery(coreQueryOperationRequest.getExtractedUserInputTokens(), coreQueryOperationRequest.getUserProvidedSQL());
            // call insert query
            insertQuery.executeInsertQuery(tableName, insertValueTokens, coreQueryOperationRequest.isTransactionFlow());
        } else {
            System.out.println("ERROR: Invalid Insert Statement");
        }
    }

}
