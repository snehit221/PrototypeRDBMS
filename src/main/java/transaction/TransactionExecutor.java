package transaction;

import query.core.DeleteQuery;
import query.core.InsertQuery;
import query.core.UpdateQuery;
import query.request.ScheduledTransaction;
import utils.ConstantKeywords;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

public class TransactionExecutor {


    /**
     * commits the data to the respective table
     *
     * @param tableFile                     file name for the table name
     * @param intermediateDataRecordsBuffer
     */
    public static void commitData(File tableFile, List<String> intermediateDataRecordsBuffer) {
        try {
            BufferedWriter bufferedWriterToUpdateTable = new BufferedWriter(new FileWriter(tableFile));
            for (String updatedLine : intermediateDataRecordsBuffer) {

                bufferedWriterToUpdateTable.write(updatedLine);
                bufferedWriterToUpdateTable.newLine();
            }
            bufferedWriterToUpdateTable.close();
            System.out.println("Query OK, DML Operation success");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * commits the transaction upon encountering commit keyword using the queue
     */
    public static void executeTransaction() {
        System.out.println("Committing transaction now...");
        Queue<ScheduledTransaction> transactionQueue = TransactionHandler.getScheduledTransactionQueue();

        if (transactionQueue.isEmpty()) {
            System.out.println("ERROR: Nothing to Commit.");
            return;
        }

        while (!transactionQueue.isEmpty()) {
            ScheduledTransaction transactionToBeExecuted = transactionQueue.poll();
            if (transactionToBeExecuted.getTransactionType().equals(ConstantKeywords.insertKeyword)) {
                InsertQuery insertQuery = new InsertQuery();
                insertQuery.executeInsertQuery(transactionToBeExecuted.getTableName(), transactionToBeExecuted.getValueTokens(), false);
            }
            if (transactionToBeExecuted.getTransactionType().equals(ConstantKeywords.updateKeyword)) {
                UpdateQuery updateQuery = new UpdateQuery();
                updateQuery.executeUpdateQuery(transactionToBeExecuted.getValueTokens(), transactionToBeExecuted.getUserProvidedSQL(), false);

            }
            if (transactionToBeExecuted.getTransactionType().equals(ConstantKeywords.deleteKeyword)) {
                DeleteQuery deleteQuery = new DeleteQuery();
                deleteQuery.executeDeleteQuery(transactionToBeExecuted.getValueTokens(), transactionToBeExecuted.getUserProvidedSQL(), false);
            }
        }
    }


    /**
     * clears the transaction queue upon rollback
     */
    public static void rollBackTransaction() {
        TransactionHandler.getScheduledTransactionQueue().clear();
        System.out.println("ROLLBACK Success");
    }
}
