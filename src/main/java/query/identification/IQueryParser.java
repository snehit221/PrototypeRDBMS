package query.identification;

import java.util.List;
import java.util.Map;

public interface IQueryParser {

    void identifyQueryType();

    String extractWhereClause(String userProvidedSQL);

    String extractQueryTableName(List<String> extractedTokens);

    Map<Integer, String> populateIndexesForWhereClause(List<String> columnNameInfo, String whereClause);

    String getTableNameForQueryExecution(String... keywords);


}

