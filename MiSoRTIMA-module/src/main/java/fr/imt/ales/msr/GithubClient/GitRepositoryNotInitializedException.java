package fr.imt.ales.msr.GithubClient;

/**
 * Class of exception to handle the case when the git repository is not initialized
 * @author Quentin PEREZ
 * @version 1.0
 */
public class GitRepositoryNotInitializedException extends Exception {

    /**
     * Constructor of GitRepositoryNotInitializedException with a specific message
     * @param message Message to display
     */
    public GitRepositoryNotInitializedException(String message){
        super(message);
    }
}
