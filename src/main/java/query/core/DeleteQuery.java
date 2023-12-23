package query.core;

import query.identification.DeleteQueryParser;
import query.identification.QueryParser;
import query.request.ScheduledTransaction;
import transaction.TransactionExecutor;
import transaction.TransactionHandler;
import utils.ConstantKeywords;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeleteQuery extends CoreDatabaseQuery {

    private Map<Integer, String> columnIndexForWhereClause;
    private List<String> intermediateBufferForDelete = new ArrayList<>();
    private QueryParser deleteQueryParser = new DeleteQueryParser();

    /**
     * executes the delete query operation
     *
     * @param extractedTokens   extracted from the user request
     * @param userProvidedSQL   query provided by the user
     * @param isTransactionFlow true if it is a transaction flow, false otherwise
     */
    public void executeDeleteQuery(List<String> extractedTokens, String userProvidedSQL, boolean isTransactionFlow) {

        String tableNameForDeleteQuery = extractedTokens.get(2);

        if (isTransactionFlow) {
            //System.out.println("Inside Transaction Flow for DELETE: setting deleter queue..");
            populateInsertTransactionQueue(extractedTokens, userProvidedSQL, tableNameForDeleteQuery, true, ConstantKeywords.deleteKeyword);
            return;
        }

        if (isNoValidDatabaseCreated()) {
            System.out.println(ConstantKeywords.databaseNotExistErrorMessage);
        }

        boolean isWhereClausePresent = false;
        String whereClause = null;


        if (extractedTokens.contains(ConstantKeywords.whereKeyword)) {
            if (extractedTokens.get(extractedTokens.indexOf(tableNameForDeleteQuery) + 1).equals(ConstantKeywords.whereKeyword)) {
                isWhereClausePresent = true;
                whereClause = deleteQueryParser.extractWhereClause(userProvidedSQL);
                //System.out.println("found whereClause for delete: " + whereClause);
            }
        }


        String fileName = deleteQueryParser.getTableNameForQueryExecution(getExistingDatabaseFullPath(), ConstantKeywords.forwardSlash, tableNameForDeleteQuery, ".txt");
        File tableNameFile = new File(fileName);


        File tableFile = new File(fileName);

        if (!tableFile.exists()) {
            System.out.println("" + tableNameForDeleteQuery + " does not exist in database");
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFile));

            String eachCurrentLine = bufferedReader.readLine();

            boolean isFirstRow = true;

            List<String> columnNameInfo;
            boolean shouldDeleteThisTuple = true;


            while (eachCurrentLine != null) {

                if (isFirstRow) {
                    columnNameInfo = Arrays.asList(eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter));
                    intermediateBufferForDelete.add(eachCurrentLine);
                    if (isWhereClausePresent) {
                        //System.out.println("where clause found.. ");
                        columnIndexForWhereClause = deleteQueryParser.populateIndexesForWhereClause(columnNameInfo, whereClause);
                        if (columnIndexForWhereClause == null) {
                            String invalidColumnName = whereClause.split("=")[0];
                            System.out.println("ERROR: Invalid Column name: " + invalidColumnName.trim() + " specified in WHERE clause");
                            break;
                        }
                        //System.out.println("columnIndexForWhereClause for delete statement: " + columnIndexForWhereClause);
                    }
                    isFirstRow = false;
                    eachCurrentLine = bufferedReader.readLine();
                    continue;
                }
                //System.out.println("reading each tuple now...");
                String[] columnValueSegments = eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter);

                if (isWhereClausePresent) {
                    shouldDeleteThisTuple = isCurrentTupleWhereClauseSatisfied(columnValueSegments, columnIndexForWhereClause);
                    //System.out.println("current tuple should be deteted " + shouldDeleteThisTuple);
                }

                if (!shouldDeleteThisTuple) {
                    intermediateBufferForDelete.add(eachCurrentLine);
                }

                eachCurrentLine = bufferedReader.readLine();
            }
            bufferedReader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (isTransactionFlow) {
            System.out.println("Transaction Flow found...");
            ScheduledTransaction scheduledTransactionUpdate = new ScheduledTransaction.Builder(tableNameForDeleteQuery)
                    .withTableFile(tableNameFile)
                    .withIntermediateDataBuffer(intermediateBufferForDelete)
                    .withTransactionType(ConstantKeywords.deleteKeyword)
                    .build();

            TransactionHandler.populateScheduledTransactionQueue(scheduledTransactionUpdate);
        } else {
            //System.out.println("Committing data to table file now");
            TransactionExecutor.commitData(tableNameFile, intermediateBufferForDelete);
        }

    }

}