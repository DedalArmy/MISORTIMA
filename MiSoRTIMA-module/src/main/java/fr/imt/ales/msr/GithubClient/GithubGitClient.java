package fr.imt.ales.msr.GithubClient;

import fr.imt.ales.msr.LoggerUtils.LoggerPrintUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Github client using JGit to checkout a project
 * @author Quentin Perez
 * @version 1.0
 */
public class GithubGitClient {
    private Git git;
    private final static Logger logger = LogManager.getLogger(GithubGitClient.class);

    /**
     * Default constructor to create a Github client
     */
    public GithubGitClient() throws GitAPIException {
    }

    /**
     * Method the set the Git object attribute
     * @param gitURL Url of the path
     * @param absolutePathDirectory
     * @throws GitAPIException
     */
    public void cloneRepository(String gitURL, String absolutePathDirectory, String username, String password) throws GitAPIException {
        git = Git.cloneRepository()
                .setURI(gitURL)
                .setDirectory(new File(absolutePathDirectory))
                .setCredentialsProvider( new UsernamePasswordCredentialsProvider(username, password))
                .call();
    }

    /**
     * Method the set the Git object attribute
     * @param gitURL Url of the path
     * @param absolutePathDirectory
     * @throws GitAPIException
     */
    public void cloneRepository(String gitURL, String absolutePathDirectory) throws GitAPIException {
        git = Git.cloneRepository()
                .setURI(gitURL)
                .setDirectory(new File(absolutePathDirectory))
                .call();
    }

    /**
     *
     * @param jsonObjectListRepositories
     * @param pathDirectory
     * @param username
     * @param password
     */
    public void cloneRepositoriesFromList(JSONObject jsonObjectListRepositories, String pathDirectory, String username, String password) throws GitAPIException, GitRepositoryNotInitializedException {
        JSONArray jsonArrayItems = jsonObjectListRepositories.getJSONArray("items");

        if(!pathDirectory.endsWith("/"))
            pathDirectory+= "/";

        for (int i = 0; i < jsonArrayItems.length(); i++) {

            LoggerPrintUtils.printLaunchBar(logger,"==< Clone repositories >==",i+1,jsonArrayItems.length());
            if(jsonArrayItems.get(i) instanceof JSONObject){
                JSONObject jsonObjectRepo = jsonArrayItems.getJSONObject(i);

                if(jsonObjectRepo.has("last_commit") && jsonObjectRepo.get("last_commit") instanceof JSONObject){
                    JSONObject jsonObjectCommit = jsonObjectRepo.getJSONObject("last_commit");

                    if (jsonObjectCommit.has("sha") && jsonObjectCommit.get("sha") instanceof String){
                        String cloneUrl = jsonObjectRepo.getString("clone_url");
                        String fullname = jsonObjectRepo.getString("full_name");
                        String sha = jsonObjectCommit.getString("sha");

                        logger.info("== Clone: " + fullname + "==");
                        if((username != null && !username.equals("") &&
                                (password != null && !password.equals("")))){
                            cloneRepository(cloneUrl,pathDirectory + fullname, username, password);
                        }else{
                            cloneRepository(cloneUrl,pathDirectory + fullname);
                        }
                        checkoutWithCommitID(sha);
                    }

                }
                else{
                    logger.warn("Unknown object type");
                }
            }

        }
    }

    /**
     * Checkouts a project from a specific commit ID
     * @param commitSha Sha of the commit
     * @throws GitAPIException Exception
     */
    public void checkoutWithCommitID(String commitSha) throws GitAPIException, GitRepositoryNotInitializedException {
        if(git == null){
            throw new GitRepositoryNotInitializedException("The repository is not initialized, you must call before the method \"cloneRepository\"");
        }
        git.checkout().setName(commitSha).call();
    }

}
