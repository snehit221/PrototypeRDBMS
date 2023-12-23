package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * singleton class ConfigUtils used to load properties from file
 */

public class ConfigUtils {
    private static final String rootDirectory = System.getProperty("user.dir");
    private static ConfigUtils instance;
    private final Properties properties;

    private ConfigUtils() {
        properties = new Properties();
    }

    public static ConfigUtils getInstance() {
        if (instance == null) {
            instance = new ConfigUtils();
        }
        return instance;
    }



    public Properties readPropertyConfigFile(String fileName) throws IOException {
        if (fileName == null) {
            return null;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            properties.load(fileInputStream);
        } catch (IOException e) {
            //System.out.println("exception: " + e);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return properties;
    }

    public String getUserConfigFullPath(String userName) {
        if (rootDirectory == null || userName == null) {
            System.out.println("invalid rootDirectory: " + rootDirectory + " or userName: " + userName);
        }
        return rootDirectory + "/src/main/resources/" + userName + ".properties";
    }

    public String getLoggerFileFullPath() {
        if (rootDirectory == null) {
            System.out.println("invalid rootDirectory: " + null);
        }
        return rootDirectory + "/src/main/resources/" +  "log.txt";
    }
    public String getDatabaseDirPath(String databaseName) {
        if (rootDirectory == null || databaseName == null) {
            System.out.println("invalid rootDirectory: " + rootDirectory + " or databaseName: " + databaseName);
        }
        return rootDirectory + "/src/main/resources/" + databaseName + "/";
    }

    public String getRootDatabaseDirectory() {
        return rootDirectory + "/src/main/resources/";
    }

    public Properties getProperties() {
        return properties;
    }
}
