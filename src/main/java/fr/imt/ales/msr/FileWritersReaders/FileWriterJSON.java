package fr.imt.ales.msr.FileWritersReaders;

import fr.imt.ales.msr.GithubClient.GithubHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Class used to write a JSON Object in a file
 */
public class FileWriterJSON {
    final static Logger logger = LogManager.getLogger(FileWriterJSON.class);
    private final int INDENT_FACTOR = 2;
    /**
     * Default constructor
     */
    public FileWriterJSON(){}

    /**
     * Writes a file with the given name and containing the given JSON Object
     * @param jsonObjectToWrite JSON Object to write in the file
     * @param filename Name of the file to write
     */
    public void writeJsonFile(JSONObject jsonObjectToWrite, String path, String filename) {
        if(!new File(path).isDirectory())
            throw new InvalidPathException(path, "Error path to write JSON file is invalid or is not a directory");

        try (FileWriter fileWriter = new FileWriter(path + "/" + filename)) {
            fileWriter.write(jsonObjectToWrite.toString(INDENT_FACTOR));
            logger.info("Successfully wrote JSON Object");
        } catch (IOException e) {
            logger.error("Error during writing JSON object " +filename+ " : " + e.getMessage());
        }

    }
}
