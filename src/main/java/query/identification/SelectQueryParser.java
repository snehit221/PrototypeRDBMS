package query.identification;

import utils.ConstantKeywords;

import java.util.List;

public class SelectQueryParser extends QueryParser {



    /**
     * @param userProvidedSQLColumns comma separated columns provided in user SQL query
     * @return
     */
    public static String[] extractColumnNames(String userProvidedSQLColumns) {
        return userProvidedSQLColumns.split(ConstantKeywords.comma);
    }


}
