package query.core;

import query.request.ScheduledTransaction;
import transaction.TransactionHandler;
import utils.ConfigUtils;
import utils.ConstantKeywords;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * parent class for all CRUD query implementations
 */

public class CoreDatabaseQuery {

    private static ConfigUtils configUtils = ConfigUtils.getInstance();
    Path rootDirectoryPath = Paths.get(configUtils.getRootDatabaseDirectory());
    private String existingDatabaseFullPath;

    public String getExistingDatabaseFullPath() {
        return existingDatabaseFullPath;
    }

    public void setExistingDatabaseFullPath(String existingDatabaseFullPath) {
        this.existingDatabaseFullPath = existingDatabaseFullPath;
    }

    /**
     * @return true if the database creation limit is reached, false otherwise
     * @implNote current database creation limit: 1
     */
    public boolean isNoValidDatabaseCreated() {
        if (rootDirectoryPath == null) {
            return false;
        }
        if (Files.exists(rootDirectoryPath) && Files.isDirectory(rootDirectoryPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDirectoryPath)) {
                boolean hasSubdirectories = false;
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        setExistingDatabaseFullPath(entry.toString());
                        hasSubdirectories = true;
                        break;
                    }
                }
                if (hasSubdirectories) {
                    return false;
                } else {
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("The root directory does not exist!");
        }
        return false;
    }

    /**
     * @param columnValueSegments
     * @param columnIndexForWhereClause
     * @return true if where clause is satisfied for the current row, false otherwise
     */
    public boolean isCurrentTupleWhereClauseSatisfied(String[] columnValueSegments,
                                                      Map<Integer, String> columnIndexForWhereClause) {
        boolean isWhereClauseSatisfied = false;
        for (int i = 0; i < columnValueSegments.length; i++) {
            //case when where clause satisfied for that tuple
            if (columnIndexForWhereClause.containsKey(i) &&
                    columnIndexForWhereClause.get(i).equals(columnValueSegments[i])) {
                isWhereClauseSatisfied = true;
                break;
            }
        }
        return isWhereClauseSatisfied;
    }

    /**
     * @param columnValueSegments
     * @param selectColumnIndexes
     * @return tuples with filered columns
     */
    public String getTupleWithFilteredColumns(String[] columnValueSegments, List<Integer> selectColumnIndexes) {
        StringJoiner tupleWithFilteredColumns = new StringJoiner(ConstantKeywords.columnCreationDelimiter);
        for (int i = 0; i < columnValueSegments.length; i++) {
            // filtered the exact columns
            if (selectColumnIndexes.contains(i)) {
                tupleWithFilteredColumns.add(columnValueSegments[i]);
            }
        }
        return tupleWithFilteredColumns.toString();
    }

    /**
     * @param extractedTokens
     * @param userProvidedSQL
     * @param tableName
     * @param isTransactionFlow
     * @param queryType
     * @implNote populates and stores in scheduled transaction queue
     */
    public void populateInsertTransactionQueue(List<String> extractedTokens, String userProvidedSQL, String tableName, boolean isTransactionFlow, String queryType) {
        ScheduledTransaction scheduledTransactionForInsert = new ScheduledTransaction.Builder(tableName)
                .withValueTokens(extractedTokens)
                .withUserProvidedSQL(userProvidedSQL)
                .withIsTransactionFlow(isTransactionFlow)
                .withTransactionType(queryType)
                .build();

        TransactionHandler.populateScheduledTransactionQueue(scheduledTransactionForInsert);
    }
}
