package fr.imt.ales.msr;

import fr.imt.ales.msr.Facade.MisortimaFacade;
import fr.imt.ales.msr.FileWritersReaders.FileReaderJSON;
import fr.imt.ales.msr.FileWritersReaders.FileWriterJSON;
import fr.imt.ales.msr.GithubClient.GitRepositoryNotInitializedException;
import fr.imt.ales.msr.GithubClient.GithubGitClient;
import fr.imt.ales.msr.GithubClient.GithubHttpClient;
import fr.imt.ales.msr.RawDataFilters.RawDataFilter;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException, ParseException {

        /*try {
            MisortimaFacade misortimaFacade  = new MisortimaFacade();
            misortimaFacade.cloneRepositories("/home/qperez/mining/results_with_commit.json","/home/qperez/mining/repositories","qperez","*xq8yp724");
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (GitRepositoryNotInitializedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/


        FileReaderJSON fileReaderJSON = new FileReaderJSON();
        RawDataFilter rawDataFilter = new RawDataFilter();
        try {
            List<String> listFields = new ArrayList<>();
            listFields.add("full_name");
            listFields.add("stargazers_count");
            listFields.add("watchers_count");
            listFields.add("forks_count");
            listFields.add("open_issues_count");
            listFields.add("score");
            listFields.add("size");
            listFields.add("created_at");

            JSONObject jsonObjectExtracted = rawDataFilter.extractSpecificFieldsFromJSONFile("/home/quentin/mining/file_raw.json",listFields);
            rawDataFilter.deleteDuplicateEntriesJSONObject(jsonObjectExtracted);


            FileWriterJSON fileWriterJSON = new FileWriterJSON();
            fileWriterJSON.writeJsonFile(jsonObjectExtracted, "/home/quentin/mining","results_filtered.json");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        //e28c3de2fbc60097c02f17fc30f2786a3fb56817
        /*try {
            GithubGitClient githubGitClient = new GithubGitClient();
            githubGitClient.checkoutWithCommitID("e9f75667096e6ed7634331b9d3d32797a286e00e");
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (GitRepositoryNotInitializedException e) {
            e.printStackTrace();
        }*/

        /*Logger logger = LogManager.getLogger(Main.class);

        Options options = new Options();
        options.addOption("u", "url", true, "Set the URL to use for the request on Github API")
                .addOption("d", "directory", true, "Set the directory use to store the results of mining : JSON files and repositories")
                .addOption("fn", "filenames", true,"Set the filename prefix for JSON files")
                .addOption("fe", "fields",true,"List of fields to extract")
                .addOption("h", "help", false,"Display the help");

        options.getOption("fe").setArgs(Option.UNLIMITED_VALUES);

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args, true);

        if(cmd.hasOption("h") || cmd.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar MiSoRTIMA-1.0-SNAPSHOT-jar-with-dependencies.jar <option> <value>", options);
            System.exit(1);
        }

        String url       = cmd.getOptionValue("u");
        String directory = cmd.getOptionValue("d");
        String filename  = cmd.getOptionValue("fn");
        List<String> fieldsToExtract = new ArrayList<>(Arrays.asList(cmd.getOptionValues("fe")));

        try {
            MisortimaFacade misortimaFacade = new MisortimaFacade();
            misortimaFacade.extractAndSaveJSONDataFromURL(url,directory,filename + "_raw.json");
            misortimaFacade.filterData(fieldsToExtract,
                    directory + "/" + filename + "_raw.json",
                    directory,
                    filename + "_filtered.json");
            misortimaFacade.associatedRepositoriesListToLastCommit(directory + "/" + filename + "_filtered.json",
                    directory,filename + "_with_commit.json");

        } catch (GitAPIException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/

        /*GithubHttpClient githubHttpClient = new GithubHttpClient();

         java -jar MiSoRTIMA-1.0-SNAPSHOT-jar-with-dependencies.jar -u "https://api.github.com/search/repositories?q=Spring+language:java+stars:>=100+created:>=2010-01-01+forks:>=20" -d /home/quentin/mining -fn results -fe commits_url ssh_url clone_url html_url full_name name

        try {
            JSONObject jsonObjectRaw = githubHttpClient.getRawDataJson("https://api.github.com/search/repositories?q=Spring+language:java+stars:>=100+created:>=2010-01-01+forks:>=20", new JSONObject());
            FileWriterJSON fileWriterJSON = new FileWriterJSON();
            fileWriterJSON.writeJsonFile(jsonObjectRaw, "/home/qperez/mining","file_raw.json");

            FileReaderJSON fileReaderJSON = new FileReaderJSON();
            JSONObject filteredJsonObject = fileReaderJSON.readJSONFile("/home/qperez/mining/file_raw.json");
            fileWriterJSON.writeJsonFile(jsonObjectRaw, "/home/qperez/mining","file_filtered.json");
            JSONObject obj = githubHttpClient.getLastCommitForRepositoriesList(filteredJsonObject);
            fileWriterJSON.writeJsonFile(obj, "/home/qperez/mining","file_filtered_with_commits.json");

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }*/

    }
}
