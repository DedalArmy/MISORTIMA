package fr.imt.ales.msr.Facade;

import fr.imt.ales.msr.GithubClient.GitRepositoryNotInitializedException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FacadeTest {

    private final String NAME_FILE_JSON_REPO_OCTOCAT = "json-response-repo-octocat-helloworld.json";
    private final String NAME_FILE_JSON_EXAMPLE = "json-example.json";
    private final String NAME_FILE_JSON_EXAMPLE_FILTERED = "json-example-filtered.json";


    private JSONObject jsonObjectOctocatRepoExpected;
    private String fileContentJsonObjectOctocatRepoExpected;
    private String fileContentJsonObjectFiltered;

    private String pathTmpDir;
    private String pathTestResources;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException {
        URL urlJsonFileRepoOctocat = Thread.currentThread().getContextClassLoader().getResource(NAME_FILE_JSON_REPO_OCTOCAT);
        URL urlJsonFileFiltered = Thread.currentThread().getContextClassLoader().getResource(NAME_FILE_JSON_EXAMPLE_FILTERED);

        fileContentJsonObjectOctocatRepoExpected = new String(Files.readAllBytes(Paths.get(urlJsonFileRepoOctocat.toURI())), Charset.forName("UTF-8"));
        fileContentJsonObjectFiltered = new String(Files.readAllBytes(Paths.get(urlJsonFileFiltered.toURI())), Charset.forName("UTF-8"));

        jsonObjectOctocatRepoExpected = new JSONObject(fileContentJsonObjectOctocatRepoExpected);

        pathTmpDir = System.getProperty("java.io.tmpdir");// + "/test-clone";
    }

    @Test
    public void testExtractAndSaveJSONDataFromURLNominalCase() throws IOException, GitAPIException, URISyntaxException, InterruptedException {
        MisortimaFacade misortimaFacade = new MisortimaFacade();
        misortimaFacade.extractAndSaveJSONDataFromURL("https://api.github.com/repos/octocat/Hello-World",pathTmpDir, NAME_FILE_JSON_REPO_OCTOCAT);

        File fileActual = new File(pathTmpDir + "/" + NAME_FILE_JSON_REPO_OCTOCAT);
        assertTrue(fileActual.isFile());

        JSONObject jsonObjectActual = new JSONObject(new String(Files.readAllBytes(Paths.get(pathTmpDir + "/" + NAME_FILE_JSON_REPO_OCTOCAT)), Charset.forName("UTF-8")));
        assertEquals(jsonObjectOctocatRepoExpected.getString("git_url"), jsonObjectActual.getString("git_url"));
        assertEquals(jsonObjectOctocatRepoExpected.getString("full_name"), jsonObjectActual.getString("full_name"));
        assertEquals(jsonObjectOctocatRepoExpected.getJSONObject("owner").getString("login"), jsonObjectActual.getJSONObject("owner").getString("login"));
    }

    @Test
    public void testExtractAndSaveJSONDataFromURLWithMalformedURL() throws GitAPIException, InterruptedException, IOException, URISyntaxException {
        MisortimaFacade misortimaFacade = new MisortimaFacade();

        assertThrows(UnknownHostException.class, ()->{ misortimaFacade.extractAndSaveJSONDataFromURL("https://api.github.m/repos/ocat/Hello-World",pathTmpDir, NAME_FILE_JSON_REPO_OCTOCAT);} );
    }

    @Test
    public void testFilterDataNominalCase() throws GitAPIException, IOException, URISyntaxException {
        List<String> fieldsToExtract = new ArrayList<>();
        fieldsToExtract.add("git_url");
        fieldsToExtract.add("commits_url");
        fieldsToExtract.add("stargazers_count");
        fieldsToExtract.add("owner");

        MisortimaFacade misortimaFacade = new MisortimaFacade();

        misortimaFacade.filterData(fieldsToExtract,     "src/test/resources/json-example-filtered.json", pathTmpDir,"filtered.json");

        File fileActual = new File(pathTmpDir + "/" + "filtered.json");
        assertTrue(fileActual.isFile());

        String fileContentActualJsonFiltered = new String(Files.readAllBytes(Paths.get(pathTmpDir + "/" + "filtered.json")), Charset.forName("UTF-8"));
        assertEquals(new JSONObject(fileContentJsonObjectFiltered).toString(2),new JSONObject(fileContentActualJsonFiltered).toString(2));
    }

    @Test
    public void testCloneRepositoriesNominalCase() throws GitAPIException, URISyntaxException, IOException, GitRepositoryNotInitializedException {
        MisortimaFacade misortimaFacade = new MisortimaFacade();

        URL urlJsonFileReposWithCommits= Thread.currentThread().getContextClassLoader().getResource("json-with-commits.json");
        //JSONObject jsonObjectReposWithCommits = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileReposWithCommits.toURI())), Charset.forName("UTF-8")));

        misortimaFacade.cloneRepositories(Paths.get(urlJsonFileReposWithCommits.toURI()).toString(),pathTmpDir + "/test-clone","","");

        File fileRepo = new File(pathTmpDir + "/test-clone");
        ArrayList<String> listFiles = new ArrayList<>(Arrays.asList(fileRepo.list()));
        assertTrue(fileRepo.list().length == 2);
        assertTrue(listFiles.contains("peholmst"));
        assertTrue(listFiles.contains("spring-projects"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        File fileRepo = new File(pathTmpDir + "/test-clone");
        FileUtils.deleteDirectory(fileRepo);
    }
}
