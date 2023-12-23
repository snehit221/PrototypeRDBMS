package query.identification;

import logger.ILogger;
import logger.Logger;
import query.core.*;
import query.request.CoreQueryOperationRequest;
import query.validator.*;
import transaction.TransactionExecutor;
import utils.ConstantKeywords;

import java.util.*;

public class QueryParser implements IQueryParser {

    private IQueryValidator<CreateQuery> createQueryValidator;
    private IQueryValidator<UpdateQuery> updateQueryValidator;
    private IQueryValidator<DeleteQuery> deleteQueryValidator;
    private IQueryValidator<InsertQuery> insertQueryValidator;
    private IQueryValidator<SelectQuery> selectQueryValidator;
    private ILogger logger = new Logger();

    /**
     * @param userProvidedSQL query provided by the user
     * @return the where clause condition of the query
     */
    @Override
    public String extractWhereClause(String userProvidedSQL) {
        return userProvidedSQL.substring(userProvidedSQL.indexOf(ConstantKeywords.whereKeyword) + 5, userProvidedSQL.length());
    }

    /**
     * @param extractedTokens tokens from SQL query extracted
     * @return table name for which SQL query is being executed
     * @implNote used to extract table name for SELECT, DELETE query types
     */
    @Override
    public String extractQueryTableName(List<String> extractedTokens) {
        int indexOfTableName = extractedTokens.indexOf(ConstantKeywords.fromKeyword);
        if (indexOfTableName == -1) {
            return null;
        }
        return extractedTokens.get(indexOfTableName + 1);
    }

    /**
     * @param columnNameInfo
     * @param whereClause    user provided clause condition for which data is to be filtered
     * @return unique index of the column and its corresponding value to be matched for where clause criteria
     */
    @Override
    public Map<Integer, String> populateIndexesForWhereClause(List<String> columnNameInfo, String whereClause) {

        //System.out.println("column name info for populateIndexesForWhereClause: " + columnNameInfo);
        Map<Integer, String> columnIndexForWhereClause = new HashMap<>();
        String[] columNameValuePair = whereClause.split("=");
        // if the column name is not found
        if (!columnNameInfo.contains(columNameValuePair[0].trim())) {
            return null;
        }
        String columnValue = columNameValuePair[1].trim();
        if (columnValue.endsWith(";")) {
            columnValue = columnValue.substring(0, columnValue.length() - 1);
        }
        columnIndexForWhereClause.put(columnNameInfo.indexOf(columNameValuePair[0].trim()), columnValue);

        return columnIndexForWhereClause;
    }

    /**
     * @param keywords which compose the corresponding table name persistant file
     * @return
     */
    @Override
    public String getTableNameForQueryExecution(String... keywords) {
        StringBuilder fullPathForFileName = new StringBuilder();
        for (String segment : keywords) {
            fullPathForFileName.append(segment);
        }
        return fullPathForFileName.toString();

    }

    /**
     * raw SQL query provided by the end user
     * if the user initiates a transaction using BEGIN TRANSACTION keyword
     *
     * @implNote identifies the correct query type and passes the control to corresponding CRUD query
     */
    @Override
    public void identifyQueryType() {
        CoreQueryOperationRequest coreQueryOperationRequest = new CoreQueryOperationRequest();
        boolean isTransactionSequence = false;
        while (true) {
            System.out.print("prototype_rdbms> ");
            Scanner userInputScanner = new Scanner(System.in);
            String userProvidedSQL = userInputScanner.nextLine();
            if (userProvidedSQL.isEmpty()) {
                continue;
            }
            if (userProvidedSQL.equalsIgnoreCase("exit")) {
                System.out.println("Bye;");
                break; // Exit the loop if the user enters "bye"
            }

            //System.out.println("userProvidedSQL:" + userProvidedSQL);
            String sanitizedUserProvidedSQL = userProvidedSQL.replaceAll("\\n", " "); // Replace newline characters with spaces

            LinkedList<String> extractedUserInputTokens = new LinkedList<>(Arrays.asList(sanitizedUserProvidedSQL.trim().split(" ")));

            String userQueryType = extractedUserInputTokens.get(0).toUpperCase();

            if (ConstantKeywords.reservedValidKeywordsSet.contains(userQueryType)) {
                populateCoreQueryOperationRequest(coreQueryOperationRequest, extractedUserInputTokens, sanitizedUserProvidedSQL, isTransactionSequence);
                // log the user query
                logger.logQueryExecutionOperation(userProvidedSQL);

                switch (userQueryType) {
                    case ConstantKeywords.createKeyword -> {
                        createQueryValidator = new CreateQueryValidator();
                        createQueryValidator.validateQuery(coreQueryOperationRequest, new CreateQuery());
                    }
                    case ConstantKeywords.insertKeyword -> {
                        insertQueryValidator = new InsertQueryValidator();
                        insertQueryValidator.validateQuery(coreQueryOperationRequest, new InsertQuery());
                    }
                    case ConstantKeywords.updateKeyword -> {
                        updateQueryValidator = new UpdateQueryValidator();
                        updateQueryValidator.validateQuery(coreQueryOperationRequest, new UpdateQuery());
                    }
                    case ConstantKeywords.selectKeyword -> {
                        selectQueryValidator = new SelectQueryValidator();
                        selectQueryValidator.validateQuery(coreQueryOperationRequest, new SelectQuery());
                    }
                    case ConstantKeywords.deleteKeyword -> {
                        deleteQueryValidator = new DeleteQueryValidator();
                        deleteQueryValidator.validateQuery(coreQueryOperationRequest, new DeleteQuery());
                    }
                    case ConstantKeywords.beginKeyowrd -> {
                        System.out.println("Transaction Sequence Initiated> ");
                        isTransactionSequence = true;
                    }
                    case ConstantKeywords.endKeyword -> {
                        System.out.println("Transaction Sequence Aborted> ");
                        isTransactionSequence = false;
                    }
                    case ConstantKeywords.commitKeyword -> {
                        //System.out.println("COMMITTING NOW...");
                        TransactionExecutor.executeTransaction();
                    }
                    case ConstantKeywords.rollbackKeyword -> {
                        //System.out.println("Rollback");
                        TransactionExecutor.rollBackTransaction();
                    }
                }

            } else {
                System.out.println("ERROR: Invalid SQL Statement.");
            }
        }
    }

    private void populateCoreQueryOperationRequest(CoreQueryOperationRequest coreQueryOperationRequest,
                                                   LinkedList<String> extractedUserInputTokens,
                                                   String userProvidedSQL, boolean isTransactionFlow) {
        coreQueryOperationRequest.setExtractedUserInputTokens(extractedUserInputTokens);
        coreQueryOperationRequest.setUserProvidedSQL(userProvidedSQL);
        coreQueryOperationRequest.setTransactionFlow(isTransactionFlow);
    }
}


