package fr.imt.ales.msr.GithubClient;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GithubHttpClientTest {

    @Disabled
    @Test
    public void testGetRawDataNominalCase() throws IOException, URISyntaxException, InterruptedException {
//given:
        String url = "https://api.github.com/search/repositories?q=Spring+created:>=2010-01-01+language:java+stars:>=100+forks:>=20&page=2";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        HttpGet mockHttpGet = mock(HttpGet.class);
        CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
        HttpEntity mockHttpEntity = mock(HttpEntity.class);

        //Creation of headerElement for Link
        HeaderElement headerElementLinkPrev = new BasicHeaderElement("Link","<https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=1>; rel=\"prev\", <https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=3>; rel=\"next\", <https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=22>; rel=\"last\"");
        when(mockHttpClient.execute(mockHttpGet)).thenReturn(mockHttpResponse);

        HeaderElement[] headerElementTabLink = new HeaderElement[2];

        when(mockHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getFirstHeader("Link"));

        when(mockHttpEntity.getContent()).thenReturn(getClass().getClassLoader().getResourceAsStream("json-example.json"));

        when(mockHttpClient.execute((HttpGet) any())).thenReturn(mockHttpResponse);

        GithubHttpClient githubHttpClient = new GithubHttpClient();
        githubHttpClient.setHttpClient(mockHttpClient);

        assertEquals("0",githubHttpClient.getRawDataJson(url,new JSONObject()));
    }

    @Test
    public void testHandleLimitAPIGithubNominalCase() throws InterruptedException {
        GithubHttpClient githubHttpClient = new GithubHttpClient();

        assertEquals(2000.0,githubHttpClient.handleLimitAPIGithub(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()+2000),0));
    }

    @Test
    public void testHandleLimitAPIGithubNominalCaseWithRateLimitSupToZero() throws InterruptedException {
        GithubHttpClient githubHttpClient = new GithubHttpClient();

        assertEquals(0.0,githubHttpClient.handleLimitAPIGithub(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),13));
    }

    @Test
    public void testHandleLimitAPIGithubNominalCaseWithRateLimitInfToZero() throws InterruptedException {
        GithubHttpClient githubHttpClient = new GithubHttpClient();

        assertEquals(0.0,githubHttpClient.handleLimitAPIGithub(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),-13));
    }

    @Test
    public void testUrlEncodeSpecificCharsNominalCase(){
        String urlToEncode = "https://api.github.com/search/repositories?q=Spring+created:>=2010-01-01+language:java+stars:>=100+forks:>=20&page=2";
        String urlEncodedExpected = "https://api.github.com/search/repositories?q=Spring+created:%3E=2010-01-01+language:java+stars:%3E=100+forks:%3E=20&page=2";

        GithubHttpClient githubHttpClient = new GithubHttpClient();

        assertEquals(urlEncodedExpected,githubHttpClient.urlEncodeSpecificChars(urlToEncode));
    }

    @Test
    public void testUrlEncodeSpecificCharsWithNull(){
        String urlToEncode = null;
        GithubHttpClient githubHttpClient = new GithubHttpClient();

        assertThrows(NullPointerException.class,
                ()->{githubHttpClient.urlEncodeSpecificChars(urlToEncode);} );
    }
}
