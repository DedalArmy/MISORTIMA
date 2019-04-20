package fr.imt.ales.msr.GithubClient;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GithubGitClientTest {
    private String pathTmpDir;
    private final String FULL_NAME_REPO_OCTOCCAT = "octocat/Hello-World";

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException {
        pathTmpDir = System.getProperty("java.io.tmpdir") + "/test-clone";
        //System.out.println(pathTmpDir);
    }

    @Test
    public void cloneRepositoryNominalCase() throws GitAPIException {
        GithubGitClient githubGitClient = new GithubGitClient();

        githubGitClient.cloneRepository("https://github.com/octocat/Hello-World.git",pathTmpDir);
        File fileRepo = new File(pathTmpDir);
        assertTrue(fileRepo.list().length > 0);
        assertTrue(new ArrayList<>(Arrays.asList(fileRepo.list())).contains("README"));
    }

    @Test
    public void cloneRepositoryNominalCaseWithAuthentication() throws GitAPIException {
        GithubGitClient githubGitClient = new GithubGitClient();

        githubGitClient.cloneRepository("https://github.com/octocat/Hello-World.git",pathTmpDir,"","");
        File fileRepo = new File(pathTmpDir);
        assertTrue(fileRepo.list().length > 0);
        assertTrue(new ArrayList<>(Arrays.asList(fileRepo.list())).contains("README"));
    }


    @Test
    public void cloneRepositoryWithInvalidRemoteException() throws GitAPIException {
        GithubGitClient githubGitClient = new GithubGitClient();

        assertThrows(InvalidRemoteException.class,
                ()->{githubGitClient.cloneRepository("https://github.com/occatHelloWorld.git",pathTmpDir);});
    }

    @Test
    public void checkoutWithCommitIDNominalCase() throws GitAPIException, GitRepositoryNotInitializedException {
        GithubGitClient githubGitClient = new GithubGitClient();

        String sha = "553c2077f0edc3d5dc5d17262f6aa498e69d6f8e";
        githubGitClient.cloneRepository("https://github.com/octocat/Hello-World.git",pathTmpDir);
        githubGitClient.checkoutWithCommitID(sha);

        File fileRepo = new File(pathTmpDir);
        assertTrue(fileRepo.list().length > 0);
        assertTrue(new ArrayList<>(Arrays.asList(fileRepo.list())).contains("README"));
    }

    @Test
    public void checkoutWithCommitIDGitRepoNotInitializedException() throws GitAPIException {
        GithubGitClient githubGitClient = new GithubGitClient();

        Throwable exception = assertThrows(GitRepositoryNotInitializedException.class,
                ()->{githubGitClient.checkoutWithCommitID("553c2077f0edc3d5dc5d17262f6aa498e69d6f8e");});

        assertEquals("The repository is not initialized, you must call before the method \"cloneRepository\"",exception.getMessage());
    }

    @Test
    public void cloneRepositoriesFromListWithAuthenticationNominalCase() throws URISyntaxException, IOException, GitAPIException, GitRepositoryNotInitializedException {
        URL urlJsonFileReposWithCommits= Thread.currentThread().getContextClassLoader().getResource("json-with-commits.json");
        JSONObject jsonObjectReposWithCommits = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileReposWithCommits.toURI())), Charset.forName("UTF-8")));

        GithubGitClient githubGitClient = new GithubGitClient();
        githubGitClient.cloneRepositoriesFromList(jsonObjectReposWithCommits,pathTmpDir,"Test","Test");

        File fileRepo = new File(pathTmpDir);
        ArrayList<String> listFiles = new ArrayList<>(Arrays.asList(fileRepo.list()));
        assertTrue(fileRepo.list().length == 2);
        assertTrue(listFiles.contains("peholmst"));
        assertTrue(listFiles.contains("spring-projects"));
    }

    @Test
    public void cloneRepositoriesFromListNominalCase() throws URISyntaxException, IOException, GitAPIException, GitRepositoryNotInitializedException {
        URL urlJsonFileReposWithCommits= Thread.currentThread().getContextClassLoader().getResource("json-with-commits.json");
        JSONObject jsonObjectReposWithCommits = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileReposWithCommits.toURI())), Charset.forName("UTF-8")));

        GithubGitClient githubGitClient = new GithubGitClient();
        githubGitClient.cloneRepositoriesFromList(jsonObjectReposWithCommits,pathTmpDir,null,null);

        File fileRepo = new File(pathTmpDir);
        ArrayList<String> listFiles = new ArrayList<>(Arrays.asList(fileRepo.list()));
        assertTrue(fileRepo.list().length == 2);
        assertTrue(listFiles.contains("peholmst"));
        assertTrue(listFiles.contains("spring-projects"));
    }

    @Test
    public void cloneRepositoriesFromListNominalCaseWithSlash() throws URISyntaxException, IOException, GitAPIException, GitRepositoryNotInitializedException {
        URL urlJsonFileReposWithCommits= Thread.currentThread().getContextClassLoader().getResource("json-with-commits.json");
        JSONObject jsonObjectReposWithCommits = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileReposWithCommits.toURI())), Charset.forName("UTF-8")));

        GithubGitClient githubGitClient = new GithubGitClient();
        githubGitClient.cloneRepositoriesFromList(jsonObjectReposWithCommits,pathTmpDir+"/",null,"Test");

        File fileRepo = new File(pathTmpDir);
        ArrayList<String> listFiles = new ArrayList<>(Arrays.asList(fileRepo.list()));
        assertTrue(fileRepo.list().length == 2);
        assertTrue(listFiles.contains("peholmst"));
        assertTrue(listFiles.contains("spring-projects"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        File fileRepo = new File(pathTmpDir);
        FileUtils.deleteDirectory(fileRepo);
    }
}
