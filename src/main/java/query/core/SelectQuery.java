package query.core;

import query.identification.QueryParser;
import query.identification.SelectQueryParser;
import utils.ConstantKeywords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SelectQuery extends CoreDatabaseQuery {

    private Map<Integer, String> columnIndexForWhereClause;
    private List<String> selectQueryInterimBuffer = new LinkedList<>();
    private List<Integer> selectColumnIndexes = new LinkedList<>();

    private QueryParser selectQueryParser = new SelectQueryParser();


    /**
     * @param extractedTokens sanitized tokens parsed from user's query
     * @param userProvidedSQL query provided by the user
     * @implNote executes the select sql query based on the validated input tokens
     */
    public void executeSelectQuery(List<String> extractedTokens, String userProvidedSQL) {
        if (isNoValidDatabaseCreated()) {
            System.out.println(ConstantKeywords.databaseNotExistErrorMessage);
        }

        String tableName = selectQueryParser.extractQueryTableName(extractedTokens);

        boolean isWhereClausePresent = false;
        String whereClause = null;
        String[] columnsForSelect = null;


        if (extractedTokens.contains(ConstantKeywords.whereKeyword)) {
            if (extractedTokens.get(extractedTokens.indexOf(tableName) + 1).equals(ConstantKeywords.whereKeyword)) {
                isWhereClausePresent = true;
                whereClause = selectQueryParser.extractWhereClause(userProvidedSQL);
                //System.out.println("found whereClause for select: " + whereClause);
            }
        }

        boolean isAllColumnsSelect = false;
        // select * case
        if (extractedTokens.get(extractedTokens.indexOf(ConstantKeywords.selectKeyword) + 1).
                equals(ConstantKeywords.asterisk)) {
            isAllColumnsSelect = true;
        }
        // select a,b case
        else {
            int indexOfSelectClause = userProvidedSQL.indexOf(ConstantKeywords.selectKeyword);
            int indexOfFromClause = userProvidedSQL.indexOf(ConstantKeywords.fromKeyword);
            String userProvidedCommaSepColumns = userProvidedSQL.substring(indexOfSelectClause + 6, indexOfFromClause);
            columnsForSelect = SelectQueryParser.extractColumnNames(userProvidedCommaSepColumns.trim());
            //System.out.println("columnsForSelect: " + Arrays.deepToString(columnsForSelect));
            isAllColumnsSelect = false;
        }

        String fileName = selectQueryParser.getTableNameForQueryExecution(getExistingDatabaseFullPath(), ConstantKeywords.forwardSlash, tableName, ".txt");

        //System.out.println("fileName for select query:" + fileName);

        File tableFile = new File(fileName);

        if (!tableFile.exists()) {
            System.out.println("" + tableName + " table does not exist in database");
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFile));

            String eachCurrentLine = bufferedReader.readLine();

            boolean isFirstRow = true;

            List<String> columnNameInfo;
            boolean shouldSelectThisTuple = true;

            while (eachCurrentLine != null) {
                if (isFirstRow) {
                    columnNameInfo = Arrays.asList(eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter));
                    if (isAllColumnsSelect) {
                        selectQueryInterimBuffer.add(eachCurrentLine);
                    } else {
                        populateIndexesForSelect(columnNameInfo, columnsForSelect);
                        System.out.println("select column first row: " + selectColumnIndexes);
                        String tupleWithFilteredColumns = getTupleWithFilteredColumns(eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter), selectColumnIndexes);
                        //System.out.println("tupleWithFilteredColumns first Values: " + tupleWithFilteredColumns);
                        selectQueryInterimBuffer.add(tupleWithFilteredColumns);

                    }

                    if (isWhereClausePresent) {
                        //System.out.println("where clause found.. ");
                        columnIndexForWhereClause = selectQueryParser.populateIndexesForWhereClause(columnNameInfo, whereClause);
                        if (columnIndexForWhereClause == null) {
                            String invalidColumnName = whereClause.split("=")[0];
                            System.out.println("ERROR: Invalid Column name: " + invalidColumnName.trim() + " specified in WHERE clause");
                            //isWhereClauseInvalid = true;
                            break;
                        }

                        //System.out.println("columnIndexForWhereClause: " + columnIndexForWhereClause);
                    }
                    // System.out.println("columnIndexAndUpdateSetValue: " + columnIndexAndUpdateSetValue);
                    isFirstRow = false;
                    eachCurrentLine = bufferedReader.readLine();
                    continue;
                }
                //System.out.println("reading each tuple now...");
                String[] columnValueSegments = eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter);

                if (isWhereClausePresent) {
                    shouldSelectThisTuple = isCurrentTupleWhereClauseSatisfied(columnValueSegments, columnIndexForWhereClause);
                    //System.out.println("current tuple should be selected " + shouldSelectThisTuple);
                }

                if (shouldSelectThisTuple) {
                    if (!isAllColumnsSelect) {
                        String tupleWithFilteredColumns = getTupleWithFilteredColumns(columnValueSegments, selectColumnIndexes);
                        //System.out.println("tupleWithFilteredColumns Values: " + tupleWithFilteredColumns);
                        selectQueryInterimBuffer.add(tupleWithFilteredColumns);
                    } else {
                        selectQueryInterimBuffer.add(eachCurrentLine);
                    }
                }
                eachCurrentLine = bufferedReader.readLine();
            }
            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("ERROR: table does not exist");
        }
        SelectQueryPrinter selectQueryPrinter = new SelectQueryPrinter();
        selectQueryPrinter.printSelectQueryResult(selectQueryInterimBuffer);
        //SelectQueryPrinter.printSelectQueryResult(selectQueryInterimBuffer);
    }

    /**
     * @param columnNameInfo
     * @param columnsForSelect columns for select provided by the user in query
     */
    private void populateIndexesForSelect(List<String> columnNameInfo, String[] columnsForSelect) {
        for (String selectQueryColumn : columnsForSelect) {
            selectColumnIndexes.add(columnNameInfo.indexOf(selectQueryColumn.trim()));
        }
    }
}