package LoggerUtils;

import fr.imt.ales.msr.LoggerUtils.LoggerPrintUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggerPrintUtilsTest {
    private File propertiesLogFile;
    private URL logFileUrl;
    private Logger logger;

    @BeforeEach
    public void setUp() throws FileNotFoundException, MalformedURLException, URISyntaxException {
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        propertiesLogFile = new File(Thread.currentThread().getContextClassLoader().getResource("log4j2-test.properties").getPath());

        // this will force a reconfiguration
        context.setConfigLocation(propertiesLogFile.toURI());
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        //System.out.println(logFileUrl);

        logger = LogManager.getLogger(this.getClass());
        LoggerPrintUtils.printLaunchBar(logger,"Test launch bar empty",0,100);
        LoggerPrintUtils.printLaunchBar(logger,"Test launch bar 50%",50,100);
        LoggerPrintUtils.printLaunchBar(logger,"Test launch bar 100%",100,100);

        logFileUrl = new URL("file://" + System.getProperty("java.io.tmpdir") + "/logs/trace.log");
        String logString = new String(Files.readAllBytes(Paths.get(logFileUrl.toURI())), Charset.forName("UTF-8"));

        assertTrue(logString.contains("Test launch bar empty"));
        assertTrue(logString.contains("[                                        ] 0.0%"));
        assertTrue(logString.contains("Test launch bar 50%"));
        assertTrue(logString.contains("[====================                    ] 50.0%"));
        assertTrue(logString.contains("Test launch bar 100%"));
        assertTrue(logString.contains("[========================================] 100.0%"));
    }

    @AfterEach
    private void tearDown() throws URISyntaxException {
        new File(logFileUrl.toURI()).delete();
    }
}
