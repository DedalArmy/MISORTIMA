package LoggerUtils;

import fr.imt.ales.msr.LoggerUtils.LaunchBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LaunchBarTest {
    private File propertiesLogFile;
    private URL logFileUrl;
    private Logger logger;

    @BeforeEach
    public void setUp() throws FileNotFoundException, MalformedURLException, URISyntaxException {
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        propertiesLogFile = new File(Thread.currentThread().getContextClassLoader().getResource("log4j2-test.properties").getPath());

        // this will force a reconfiguration
        context.setConfigLocation(propertiesLogFile.toURI());
        logFileUrl = new URL(System.getProperty("java.io.tmpdir") + "/logs/trace.log");
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        //System.out.println(logFileUrl);

        logger = LogManager.getLogger(this.getClass());
        LaunchBar.displayLaunchBar(logger,"Test launch bar empty",0,100);
        LaunchBar.displayLaunchBar(logger,"Test launch bar empty",50,100);
        LaunchBar.displayLaunchBar(logger,"Test launch bar empty",100,100);


        String logString = new String(Files.readAllBytes(Paths.get(logFileUrl.toURI())), Charset.forName("UTF-8"));

        assertTrue(logString.contains("[                                        ] 0.0%"));
        assertTrue(logString.contains("[====================                    ] 50.0%"));
        assertTrue(logString.contains("[========================================] 100.0%"));
    }

    @AfterEach
    private void tearDown() throws URISyntaxException {
        new File(logFileUrl.toURI()).delete();
    }
}
