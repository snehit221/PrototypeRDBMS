package query.core;

import query.request.ScheduledTransaction;
import transaction.TransactionHandler;
import utils.ConfigUtils;
import utils.ConstantKeywords;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * responsible for execution of create query
 */
public class InsertQuery extends CoreDatabaseQuery {

    private static ConfigUtils configUtils = ConfigUtils.getInstance();

    /**
     * @param insertTokens
     * @return tokensToBeInserted into the existing table structure
     */
    public String createInsertDataWithDelimiter(List<String> insertTokens) {
        StringBuilder tokensToBeInserted = new StringBuilder();
        int counter = -1;  //keeps track of the current token traversed
        for (String token : insertTokens) {
            counter++;
            if (counter < insertTokens.size() - 1) {
                tokensToBeInserted.append(token).append(ConstantKeywords.columnCreationDelimiter);
            } else {
                tokensToBeInserted.append(token);
            }
        }
        return tokensToBeInserted.toString();
    }

    public static void main(String[] args) {

        List<String> insertStatementTokens = List.of(
                "1", "John", "Doe", "2023-10-19");
        InsertQuery insertQuery = new InsertQuery();
        //insertQuery.executeInsertQuery("testTable", insertStatementTokens);
    }

    /**
     * @param tableName
     * @param insertValueTokens
     * @param isTransactionFlow
     * @implNote populates and stores in transaction queue
     */
    private void populateInsertTransactionQueue(String tableName, List<String> insertValueTokens, boolean isTransactionFlow) {
        ScheduledTransaction scheduledTransactionForInsert = new ScheduledTransaction.Builder(tableName)
                .withValueTokens(insertValueTokens)
                .withIsTransactionFlow(isTransactionFlow)
                .withTransactionType(ConstantKeywords.insertKeyword)
                .build();

        TransactionHandler.populateScheduledTransactionQueue(scheduledTransactionForInsert);
    }

    /**
     * executes the insert query operation
     *
     * @param tableName   name of the table
     * @param insertValueTokens   tokens extracted for insert value from user query
     * @param isTransactionFlow true if it is a transaction flow, false otherwise
     */
    public void executeInsertQuery(String tableName, List<String> insertValueTokens, boolean isTransactionFlow) {

        if (isTransactionFlow) {
            //System.out.println("Inside Transaction Flow: seting insert queue..");
            populateInsertTransactionQueue(tableName, insertValueTokens, true);
            return;
        }

        if (!isNoValidDatabaseCreated()) {
            String tokensToBeInserted = createInsertDataWithDelimiter(insertValueTokens);
            //System.out.println("will insert: " + tokensToBeInserted);
            String fileName = getExistingDatabaseFullPath() + ConstantKeywords.forwardSlash + tableName + ".txt";
           //System.out.println("fileName: " + fileName);
            File tableFile = new File(fileName);
            if (tableFile.exists()) {
                try {
                    FileWriter fileWriter = new FileWriter(tableFile, true);

                    BufferedWriter bufferedWriterForInsert = new BufferedWriter(fileWriter);
                    //System.out.println("writing data for insert....");
                    bufferedWriterForInsert.write(tokensToBeInserted + "\n");
                    //bufferedWriterForInsert.write("\n" + tokensToBeInserted);
                    //Closing BufferedWriter Stream
                    bufferedWriterForInsert.close();
                    System.out.println("SUCCESS: Insert Query OK, 1 row affected");
                    insertValueTokens.clear();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("name: " + tableName + " does not exist");
            }

        } else {
            System.out.println(ConstantKeywords.databaseNotExistErrorMessage);
        }
    }

}