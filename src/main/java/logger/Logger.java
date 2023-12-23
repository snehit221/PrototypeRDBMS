package logger;

import query.core.CoreDatabaseQuery;
import utils.ConfigUtils;
import utils.ConstantKeywords;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger implements ILogger {

    /**
     * logs the query entered by the user in a log file
     *
     * @param userProvidedQuery
     */
    @Override
    public void logQueryExecutionOperation(String userProvidedQuery) {

        try {
            ConfigUtils configUtils = ConfigUtils.getInstance();
            String fileName = configUtils.getLoggerFileFullPath();

            LocalDateTime currentTimestamp = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTimestamp = currentTimestamp.format(formatter);
            BufferedWriter bufferedWriterToUpdateTable = new BufferedWriter(new FileWriter(fileName, true));

            String logEntry = formattedTimestamp + "  QUERY  " + userProvidedQuery;
            bufferedWriterToUpdateTable.write(logEntry);
            bufferedWriterToUpdateTable.newLine();
            bufferedWriterToUpdateTable.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
