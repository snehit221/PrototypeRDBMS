package query.validator;

import query.core.UpdateQuery;
import query.request.CoreQueryOperationRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateQueryValidator implements IQueryValidator<UpdateQuery> {

    /**
     * @param coreQueryOperationRequest
     * @param updateQuery
     * @implNote executes UPDATE query if it is valid, else generates system error message
     */
    @Override
    public void validateQuery(CoreQueryOperationRequest coreQueryOperationRequest, UpdateQuery updateQuery) {

        String pattern1 = "^UPDATE\\s+[\\w_]+\\s+SET\\s+(\\w+\\s*=\\s*\\w+(,\\s*\\w+\\s*=\\s*\\w+)*)\\s+WHERE\\s+.+;$";
        String pattern2 = "^UPDATE\\s+[\\w_]+\\s+SET\\s+(\\w+\\s*=\\s*\\w+(,\\s*\\w+\\s*=\\s*\\w+)*)\\s*;$";

        Pattern regexPattern1 = Pattern.compile(pattern1);
        Pattern regexPattern2 = Pattern.compile(pattern2);
        String userProvidedSQL = coreQueryOperationRequest.getUserProvidedSQL();

        Matcher matcher1 = regexPattern1.matcher(userProvidedSQL);
        Matcher matcher2 = regexPattern2.matcher(userProvidedSQL);

        if (matcher1.matches() || matcher2.matches()) {
            // Query matches one of the expected patterns
            List<String> extractedUserInputTokens = coreQueryOperationRequest.getExtractedUserInputTokens();

            updateQuery.executeUpdateQuery(extractedUserInputTokens,
                    userProvidedSQL, coreQueryOperationRequest.isTransactionFlow());
        } else {
            System.out.println("ERROR: Invalid Update Query Syntax");
            // Query does not match any expected patterns
        }

    }
}
