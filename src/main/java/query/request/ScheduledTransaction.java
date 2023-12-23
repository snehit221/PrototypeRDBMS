package query.request;

import java.io.File;
import java.util.List;

/**
 * Builder design pattern implemented
 * This class is responsible for storing the scheduled objects inside a transaction
 */
public class ScheduledTransaction {
    private String tableName;
    private List<String> valueTokens;
    private boolean isTransactionFlow;
    private String transactionType;
    private File tableFile;
    private List<String> intermediateDataBuffer;
    private String userProvidedSQL;

    private ScheduledTransaction() {
        // Private constructor to prevent direct instantiation
    }

    public String getTableName() {
        return tableName;
    }

    // Getter methods for ScheduledTransaction fields

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getValueTokens() {
        return valueTokens;
    }

    public void setValueTokens(List<String> valueTokens) {
        this.valueTokens = valueTokens;
    }

    public boolean isTransactionFlow() {
        return isTransactionFlow;
    }

    public void setTransactionFlow(boolean isTransactionFlow) {
        this.isTransactionFlow = isTransactionFlow;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public File getTableFile() {
        return tableFile;
    }

    public void setTableFile(File tableFile) {
        this.tableFile = tableFile;
    }

    public List<String> getIntermediateDataBuffer() {
        return intermediateDataBuffer;
    }

    public void setIntermediateDataBuffer(List<String> intermediateDataBuffer) {
        this.intermediateDataBuffer = intermediateDataBuffer;
    }

    public String getUserProvidedSQL() {
        return userProvidedSQL;
    }

    public void setUserProvidedSQL(String userProvidedSQL) {
        this.userProvidedSQL = userProvidedSQL;
    }

    public static class Builder {
        private String tableName;
        private List<String> valueTokens;
        private boolean isTransactionFlow;
        private String transactionType;
        private File tableFile;
        private List<String> intermediateDataBuffer;
        private String userProvidedSQL;

        public Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder withValueTokens(List<String> insertValueTokens) {
            this.valueTokens = insertValueTokens;
            return this;
        }

        public Builder withIsTransactionFlow(boolean isTransactionFlow) {
            this.isTransactionFlow = isTransactionFlow;
            return this;
        }

        public Builder withTransactionType(String transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder withTableFile(File tableFile) {
            this.tableFile = tableFile;
            return this;
        }

        public Builder withIntermediateDataBuffer(List<String> intermediateDataBuffer) {
            this.intermediateDataBuffer = intermediateDataBuffer;
            return this;
        }

        public Builder withUserProvidedSQL(String userProvidedSQL) {
            this.userProvidedSQL = userProvidedSQL;
            return this;
        }

        public ScheduledTransaction build() {
            ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
            scheduledTransaction.tableName = this.tableName;
            scheduledTransaction.valueTokens = this.valueTokens;
            scheduledTransaction.isTransactionFlow = this.isTransactionFlow;
            scheduledTransaction.transactionType = this.transactionType;
            scheduledTransaction.tableFile = this.tableFile;
            scheduledTransaction.intermediateDataBuffer = this.intermediateDataBuffer;
            scheduledTransaction.userProvidedSQL = this.userProvidedSQL;
            return scheduledTransaction;
        }
    }
}