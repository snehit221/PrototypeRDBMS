package query.validator;

import query.core.SelectQuery;
import query.request.CoreQueryOperationRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectQueryValidator implements IQueryValidator<SelectQuery> {


    /**
     * @param coreQueryOperationRequest uses to execute the query
     * @implNote executes select query if it is valid
     */
    @Override
    public void validateQuery(CoreQueryOperationRequest coreQueryOperationRequest, SelectQuery selectQuery) {

        String selectQueryPattern = "^SELECT\\s+\\*\\s+FROM\\s+[\\w_]+(\\s+WHERE\\s+.+)?$|^SELECT\\s+.+\\s+FROM\\s+[\\w_]+(\\s+WHERE\\s+.+)?$";

        Pattern regexPatternForSelect = Pattern.compile(selectQueryPattern);
        String userProvidedSQL = coreQueryOperationRequest.getUserProvidedSQL();
        Matcher matcherForSelectQuery = regexPatternForSelect.matcher(userProvidedSQL);
        if (matcherForSelectQuery.matches()) {
            //System.out.println("select matched");
            List<String> extractedUserInputTokens = coreQueryOperationRequest.getExtractedUserInputTokens();
            selectQuery.executeSelectQuery(extractedUserInputTokens, userProvidedSQL);
            //return true;
        } else {
            System.out.println("ERROR: Invalid Select Query Syntax");
            // Query does not match any expected patterns
        }

    }

}
