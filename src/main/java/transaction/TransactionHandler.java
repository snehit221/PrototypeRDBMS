package transaction;

import query.request.CoreQueryOperationRequest;
import query.request.ScheduledTransaction;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @implNote manages the transaction queue life cycle for CRUD operations
 */
public class TransactionHandler {

    private static Queue<CoreQueryOperationRequest> createDatabaseOperationQueue = new LinkedList<>();

    private static Queue<ScheduledTransaction> scheduledTransactionQueue = new LinkedList<>();

    public static void populateCreateDatabaseOperationQueue(CoreQueryOperationRequest coreQueryOperationRequest) {
        createDatabaseOperationQueue.add(coreQueryOperationRequest);
    }

    public static void populateScheduledTransactionQueue(ScheduledTransaction scheduledTransaction) {
        scheduledTransactionQueue.add(scheduledTransaction);
    }

    public static Queue<CoreQueryOperationRequest> getCreateDatabaseOperationQueue() {
        return createDatabaseOperationQueue;
    }

    public static Queue<ScheduledTransaction> getScheduledTransactionQueue() {
        return scheduledTransactionQueue;
    }
}
