package logger;

public interface ILogger {
    /**
     * logs the query entered by the user in a log file
     *
     * @param userProvidedQuery
     */
    void logQueryExecutionOperation(String userProvidedQuery);
}
