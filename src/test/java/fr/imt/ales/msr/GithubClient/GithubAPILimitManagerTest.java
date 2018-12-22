package fr.imt.ales.msr.GithubClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GithubAPILimitManagerTest {
    private GithubAPILimitManager githubAPILimitManager;
    private File propertiesLogFile;
    private URL logFileUrl;
    private Logger logger;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        githubAPILimitManager = new GithubAPILimitManager();

        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        propertiesLogFile = new File(Thread.currentThread().getContextClassLoader().getResource("log4j2-test.properties").getPath());

        // this will force a reconfiguration
        context.setConfigLocation(propertiesLogFile.toURI());
    }

    @Test
    public void testHandleLimitAPIGithubNominalCase() throws InterruptedException {
        assertEquals(5000.0,githubAPILimitManager.handleLimitAPIGithub(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()+2000),0));
    }

    @Test
    public void testHandleLimitAPIGithubNominalCaseWithRateLimitSupToZero() throws InterruptedException {
        assertEquals(0.0,githubAPILimitManager.handleLimitAPIGithub(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),13));
    }

    @Test
    public void testHandleLimitAPIGithubNominalCaseWithRateLimitInfToZero() throws InterruptedException {
        assertEquals(0.0,githubAPILimitManager.handleLimitAPIGithub(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),-13));
    }

    @Test
    public void testPrintRateLimitAPINominalCase() throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("json-rate-limit-example.json");
        String fileContentJsonObjectRateLimit = new String(Files.readAllBytes(Paths.get(url.toURI())), Charset.forName("UTF-8"));
        JSONObject jsonObjectRateLimit = new JSONObject(fileContentJsonObjectRateLimit);

        githubAPILimitManager.printRateLimitAPI(jsonObjectRateLimit);

        logFileUrl = new URL("file://" + System.getProperty("java.io.tmpdir") + "/logs/trace.log");
        String logString = new String(Files.readAllBytes(Paths.get(logFileUrl.toURI())), Charset.forName("UTF-8"));

        /*assertEquals(logString,
                "|-----------------------------------------------|\n" +
                "| Requests on core API limit : 60               |\n" +
                "| Requests on core API remaining : 59           |\n" +
                "| Reset core API at  : 1970-01-18 22:00:31.163  |\n" +
                "| Requests on search API limit : 10             |\n" +
                "| Requests on search API remaining : 6          |\n" +
                "| Reset search API at  : 1970-01-18 22:00:28.98 |\n" +
                "|-----------------------------------------------|\n\n");*/
    }

    @AfterEach
    public void afterTest() throws URISyntaxException {
        if(logFileUrl != null)
            new File(logFileUrl.toURI()).delete();
    }
}
