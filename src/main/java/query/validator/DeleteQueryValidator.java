package query.validator;

import query.core.DeleteQuery;
import query.request.CoreQueryOperationRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteQueryValidator implements IQueryValidator<DeleteQuery> {


    /**
     * @param coreQueryOperationRequest
     * @implNote executes update query if it is valid
     */
    @Override
    public void validateQuery(CoreQueryOperationRequest coreQueryOperationRequest, DeleteQuery deleteQuery) {
        String deleteQueryPattern = "^DELETE\\s+FROM\\s+[\\w_]+(?:\\s+WHERE\\s+.+)?$";

        Pattern regexDeletePattern = Pattern.compile(deleteQueryPattern);
        String userProvidedSQL = coreQueryOperationRequest.getUserProvidedSQL();
        Matcher matcherForDelete = regexDeletePattern.matcher(userProvidedSQL);

        if (matcherForDelete.matches()) {
            List<String> extractedUserInputTokens = coreQueryOperationRequest.getExtractedUserInputTokens();
            deleteQuery.executeDeleteQuery(extractedUserInputTokens, userProvidedSQL, coreQueryOperationRequest.isTransactionFlow());
        } else {
            System.out.println("ERROR: Invalid Delete Query Syntax");
        }
    }
}


