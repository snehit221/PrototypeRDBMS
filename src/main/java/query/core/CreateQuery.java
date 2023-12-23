package query.core;

import utils.ConfigUtils;
import utils.ConstantKeywords;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * create a new database
 */
public class CreateQuery extends CoreDatabaseQuery {

    private static ConfigUtils configUtils = ConfigUtils.getInstance();
    private String databaseName;


    /**
     * @param databaseName name of the database to be created
     * @return
     * @implNote create a new database with specified name
     */
    public Boolean createDatabase(String databaseName) {
        this.databaseName = databaseName;
//        ConfigUtils configUtils = ConfigUtils.getInstance();
        String databaseDirPath = configUtils.getDatabaseDirPath(databaseName);
        Path path = Paths.get(databaseDirPath);

        if (super.isNoValidDatabaseCreated()) {
            try {
                Files.createDirectory(path);
                System.out.println("Successfully created database : " + databaseName);
                return true;

            } catch (IOException e) {
                System.out.println("Unable to create new database " + e);
            }
        } else {
            System.out.println("ERROR: Database already exist.");
        }
        return false;
    }


    public void createTable(String tableName, List<String> sanitizedColumnNameTokens) {

        if (!isNoValidDatabaseCreated()) {

            String fileName = tableName + ".txt";
            Path filePath = Paths.get(getExistingDatabaseFullPath(), fileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))) {
                // adding column names to the file
                for (int i = 0; i < sanitizedColumnNameTokens.size(); i++) {
                    writer.write(sanitizedColumnNameTokens.get(i));
                    if (i < sanitizedColumnNameTokens.size() - 1) {
                        writer.write(ConstantKeywords.columnCreationDelimiter);  // column-delimiter
                    }
                }
                writer.write("\n");
                System.out.println("Query OK, CREATE success");
                writer.close();
            } catch (IOException e) {
                System.err.println("Error creating the table file: " + e.getMessage());
            }
        } else {
            System.out.println(ConstantKeywords.databaseNotExistErrorMessage);
        }
    }

}

