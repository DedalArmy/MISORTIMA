package fr.imt.ales.msr.Facade;

import fr.imt.ales.msr.FileWritersReaders.FileReaderJSON;
import fr.imt.ales.msr.FileWritersReaders.FileWriterJSON;
import fr.imt.ales.msr.GithubClient.GitRepositoryNotInitializedException;
import fr.imt.ales.msr.GithubClient.GithubGitClient;
import fr.imt.ales.msr.GithubClient.GithubHttpClient;
import fr.imt.ales.msr.RawDataFilters.RawDataFilter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Facade class to simplify the using of features in MiSo
 */
public class MisortimaFacade {
    private FileReaderJSON fileReaderJSON;
    private FileWriterJSON fileWriterJSON;
    private GithubGitClient githubGitClient;
    private GithubHttpClient githubHttpClient;
    private RawDataFilter rawDataFilter;

    /**
     * Constructor of facade to encapsulate the other constructors of the attribute
     * @throws GitAPIException
     */
    public MisortimaFacade() throws GitAPIException {
        fileReaderJSON = new FileReaderJSON();
        fileWriterJSON = new FileWriterJSON();
        githubHttpClient = new GithubHttpClient();
        githubGitClient   = new GithubGitClient();
        rawDataFilter = new RawDataFilter();
    }

    /**
     * Gets the data from the Github API and writes the result in a json file at the specific path given
     * @param URL URL to request
     * @param path Path to store the file
     * @param filename Filename of the JSON file
     * @throws InterruptedException
     * @throws IOException IOException thrown when an error occurred during the writing of the JSON file
     * @throws URISyntaxException URISyntaxException thrown when the URL is malformed
     */
    public void extractAndSaveJSONDataFromURL(String URL, String path, String filename)
            throws InterruptedException, IOException, URISyntaxException {
        //fileWriterJSON.writeJsonFile(githubHttpClient.getRawDataJson(URL, new JSONObject()),path,filename);
        fileWriterJSON.writeJsonFile(githubHttpClient.getRawDataJson(URL, new JSONObject(),path,filename),path,filename);
    }

    /**
     * Filters the fields stored in a JSON file and store the result in a new JSON file
     * @param fieldsToExtract List of String which contains the fields to extract
     * @param pathToJsonFileToFilter Path to the Json file to filter
     * @param pathToStoreJsonFileFiltered Path to store the Json file filtered
     * @param filenameJsonFileFiltered Filename of the JSON file filtered
     * @throws IOException
     * @throws URISyntaxException
     */
    public void filterData(List<String> fieldsToExtract,
                           String pathToJsonFileToFilter,
                           String pathToStoreJsonFileFiltered,
                           String filenameJsonFileFiltered) throws IOException, URISyntaxException {
        JSONObject filteredJsonObject = rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileToFilter, fieldsToExtract);
        fileWriterJSON.writeJsonFile(filteredJsonObject, pathToStoreJsonFileFiltered,filenameJsonFileFiltered);
    }

    /**
     * Associates the latest commit to each repositories stored in JSON file and write a new JSON file
     * @param pathJsonFileToRead
     * @param pathWriteJsonFileWithAssociatedCommit
     * @param filenameJsonFileWithAssociatedCommit
     */
    public void associatedRepositoriesListToLastCommit(String pathJsonFileToRead,
                                                       String pathWriteJsonFileWithAssociatedCommit,
                                                       String filenameJsonFileWithAssociatedCommit) throws IOException, URISyntaxException, InterruptedException {
        JSONObject filteredJsonObject = fileReaderJSON.readJSONFile(pathJsonFileToRead);
        JSONObject jsonOjectWithAssociatedCommit = githubHttpClient.getLastCommitForRepositoriesList(
                fileReaderJSON.readJSONFile(pathJsonFileToRead),fileWriterJSON,pathWriteJsonFileWithAssociatedCommit,filenameJsonFileWithAssociatedCommit);

        fileWriterJSON.writeJsonFile(jsonOjectWithAssociatedCommit,
                pathWriteJsonFileWithAssociatedCommit,
                filenameJsonFileWithAssociatedCommit);
    }

    public void cloneRepositories(String pathToJsonFile,String pathDirectoryToStoreProjects, String githubUsername, String githubPassword) throws IOException, URISyntaxException, GitAPIException, GitRepositoryNotInitializedException {
        JSONObject jsonObjectRepositories = fileReaderJSON.readJSONFile(pathToJsonFile);
        githubGitClient.cloneRepositoriesFromList(jsonObjectRepositories,pathDirectoryToStoreProjects,githubUsername,githubPassword);
    }

}
