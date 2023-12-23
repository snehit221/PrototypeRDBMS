package query.core;

import query.identification.QueryParser;
import query.identification.UpdateQueryParser;
import query.request.ScheduledTransaction;
import transaction.TransactionExecutor;
import transaction.TransactionHandler;
import utils.ConstantKeywords;

import java.io.*;
import java.util.*;

public class UpdateQuery extends CoreDatabaseQuery {

    private static String columnNameSchema = null;
    private Map<Integer, String> columnIndexForWhereClause;
    private Map<Integer, String> columnIndexAndUpdateSetValue = new HashMap<>();
    private List<String> intermediateBufferForUpdate = new ArrayList<>();

    private QueryParser updateQueryParser = new UpdateQueryParser();


    /**
     * @param columnNameInfo    column Names retrieved from the table
     * @param setValueTokenList contains the column names and value pairs provided by the user
     * @return Map of column name and its index in the table (0 based) as key-val pair
     */
    public void populateIndexesValueForUpdate(List<String> columnNameInfo, String[] setValueTokenList) {
        //System.out.println("columnNameInfo:" + columnNameInfo);
        //System.out.println("setValLiist: " + setValueTokenList);
        for (String updateParameterExpressionPair : setValueTokenList) {
            String[] columnNameValuePair = updateParameterExpressionPair.split("=");
            String userProvidedColumnNameForUpdate = columnNameValuePair[0].trim();
            String userProvidedColumnValueForUpdate = columnNameValuePair[1].trim();
            columnIndexAndUpdateSetValue.put(columnNameInfo.indexOf(userProvidedColumnNameForUpdate), userProvidedColumnValueForUpdate);
        }
    }

    public void executeUpdateQuery(List<String> extractedTokens, String userProvidedSQL, boolean isTransactionFlow) {
        String tableName = extractedTokens.get(1);
        if (isTransactionFlow) {
            //System.out.println("Inside Transaction Flow for UPDATE: seting insert queue..");
            populateInsertTransactionQueue(extractedTokens, userProvidedSQL, tableName, true, ConstantKeywords.updateKeyword);
            return;
        }

        if (isNoValidDatabaseCreated()) {
            System.out.println(ConstantKeywords.databaseNotExistErrorMessage);
        }

        boolean isWhereClausePresent = true;
        boolean isWhereClauseInvalid = false;

        if (userProvidedSQL.contains(ConstantKeywords.setClause)) {
            int startIndexOfSet = userProvidedSQL.indexOf(ConstantKeywords.setClause) + 3;
            int endIndexOfSet = userProvidedSQL.indexOf(ConstantKeywords.whereKeyword);

            // case without a where clause
            if (endIndexOfSet == -1) {
                isWhereClausePresent = false;
                endIndexOfSet = userProvidedSQL.length() - 1;
            }

            String whereClause = null;
            if (isWhereClausePresent) {
                whereClause = updateQueryParser.extractWhereClause(userProvidedSQL);
            }

            String extractSetClause = userProvidedSQL.substring(startIndexOfSet, endIndexOfSet).trim();
            String[] setValueTokens = extractSetClause.split(ConstantKeywords.comma);


            String fileName = updateQueryParser.getTableNameForQueryExecution(getExistingDatabaseFullPath(), ConstantKeywords.forwardSlash, tableName, ".txt");

            String tempFileName = getExistingDatabaseFullPath() + ConstantKeywords.forwardSlash + tableName + "_temp.txt";

            File tableNameFile = new File(fileName);
            //File tempTableFile = new File(tempFileName);


            if (!tableNameFile.exists()) {
                System.out.println("" + tableName + "does not exist in database");
            }


            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(tableNameFile));
                //BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempTableFile));

                String eachCurrentLine = bufferedReader.readLine();
                columnNameSchema = eachCurrentLine;
                //System.out.println("eachCurrentLine: " + eachCurrentLine);
                boolean isFirstColumnInfoRow = true;
                List<String> columnNameInfo;
                boolean whereClauseSatisfied;
                boolean shouldUpdateThisTuple;


                while (eachCurrentLine != null) {
                    if (isFirstColumnInfoRow) {
                        columnNameInfo = Arrays.asList(eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter));
                        intermediateBufferForUpdate.add(eachCurrentLine);  // added column schema
                        populateIndexesValueForUpdate(columnNameInfo, setValueTokens);
                        if (isWhereClausePresent) {
                            //System.out.println("where clause found.. ");
                            columnIndexForWhereClause = updateQueryParser.populateIndexesForWhereClause(columnNameInfo, whereClause);
                            if (columnIndexForWhereClause == null) {
                                String invalidColumnName = whereClause.split("=")[0];
                                System.out.println("ERROR: Invalid Column name: " + invalidColumnName.trim() + " specified in where clause");
                                isWhereClauseInvalid = true;
                                break;
                            }
                        }
                        isFirstColumnInfoRow = false;
                        eachCurrentLine = bufferedReader.readLine();
                        continue;
                    }

                    String[] columnValueSegments = eachCurrentLine.split(ConstantKeywords.columnSplitRetrievalDelimiter);
                    shouldUpdateThisTuple = true;

                    if (isWhereClausePresent) {
                        shouldUpdateThisTuple = isCurrentTupleWhereClauseSatisfied(columnValueSegments, columnIndexForWhereClause);

                    }

                    if (shouldUpdateThisTuple) {
                        for (int i = 0; i < columnValueSegments.length; i++) {
                            // found the exact column name which needs to be updated as per new set value
                            if (columnIndexAndUpdateSetValue.containsKey(i)) {
                                columnValueSegments[i] = columnIndexAndUpdateSetValue.get(i);
                            }
                        }

                        // now create the entire new updated tuple
                        String tuplePostUpdate = String.join(ConstantKeywords.columnCreationDelimiter, columnValueSegments);
                        //System.out.println("new tuple to be inserted: " + tuplePostUpdate);
                        intermediateBufferForUpdate.add(tuplePostUpdate);
                    } else {
                        //System.out.println("existing tuple pushed to interim buffer: " + eachCurrentLine);
                        intermediateBufferForUpdate.add(eachCurrentLine);
                    }
                    eachCurrentLine = bufferedReader.readLine();

                }

                bufferedReader.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (isTransactionFlow) {
                System.out.println("Transaction Flow found... for UPDATE QUERY ");
                ScheduledTransaction scheduledTransactionUpdate = new ScheduledTransaction.Builder(tableName)
                        .withTableFile(tableNameFile)
                        .withIntermediateDataBuffer(intermediateBufferForUpdate)
                        .withTransactionType(ConstantKeywords.updateKeyword)
                        .build();

                TransactionHandler.populateScheduledTransactionQueue(scheduledTransactionUpdate);
            } else {
                if (!isWhereClauseInvalid) {
                    //System.out.println("Committing data to table file now");
                    TransactionExecutor.commitData(tableNameFile, intermediateBufferForUpdate);
                }
            }

        }
    }

}

