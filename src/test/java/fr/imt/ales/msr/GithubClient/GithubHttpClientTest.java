package fr.imt.ales.msr.GithubClient;

import fr.imt.ales.msr.FileWritersReaders.FileWriterJSON;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class GithubHttpClientTest {

    private String pathTmpDir = System.getProperty("java.io.tmpdir");

    @Test
    public void testGetRawDataNominalCase() throws IOException, URISyntaxException, InterruptedException {
//given:
        String url = "https://api.github.com/search/repositories?q=Spring+created:>=2010-01-01+language:java+stars:>=100+forks:>=20&page=2";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        HttpGet mockHttpGet = mock(HttpGet.class);
        CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
        HttpEntity mockHttpEntity = mock(HttpEntity.class);

        //Creation of headerElement for Link
        Header headerElementLinkPrev = new BasicHeader("Link","<https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=1>; rel=\"prev\", <https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=3>; rel=\"next\", <https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=3>; rel=\"last\"");
        Header headerElementLinkLast= new BasicHeader("Link","<https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=1>; rel=\"prev\", <https://api.github.com/search/repositories?q=Spring+created%3A%3E%3D2010-01-01+language%3Ajava+stars%3A%3E%3D100+forks%3A%3E%3D20&page=3>; rel=\"last\"");
        when(mockHttpClient.execute(mockHttpGet))
                .thenReturn(mockHttpResponse)
                .thenReturn(mockHttpResponse);

        when(mockHttpResponse.getStatusLine())
                .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"))
                .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));;
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity).thenReturn(mockHttpEntity);
        Header[] headers =  {headerElementLinkPrev};

        BasicHeader mockBasicHeaderXRateLimitReset = mock(BasicHeader.class);
        BasicHeader mockBasicHeaderXRateLimitRemaining = mock(BasicHeader.class);

        when(mockHttpResponse.getFirstHeader("Link"))
                .thenReturn(headerElementLinkPrev)
                .thenReturn(headerElementLinkLast);

        when(mockHttpResponse.getFirstHeader("X-RateLimit-Reset"))
                .thenReturn(mockBasicHeaderXRateLimitReset)
                .thenReturn(mockBasicHeaderXRateLimitReset);
        when(mockBasicHeaderXRateLimitReset.getValue())
                .thenReturn(new String("1646594047"))
                .thenReturn(new String("1646594047"));
        when(mockHttpResponse.getFirstHeader("X-RateLimit-Remaining"))
                .thenReturn(mockBasicHeaderXRateLimitRemaining)
                .thenReturn(mockBasicHeaderXRateLimitRemaining);
        when(mockBasicHeaderXRateLimitRemaining.getValue())
                .thenReturn(new String("1000"))
                .thenReturn(new String("1000"));

        when(mockHttpEntity.getContent())
                .thenReturn(getClass().getClassLoader().getResourceAsStream("json-example.json"))
                .thenReturn(getClass().getClassLoader().getResourceAsStream("json-example-filtered.json"));

        when(mockHttpClient.execute((HttpGet) any())).thenReturn(mockHttpResponse).thenReturn(mockHttpResponse);

        GithubHttpClient githubHttpClient = new GithubHttpClient();
        githubHttpClient.setHttpClient(mockHttpClient);

        URL urlJsonFileExample= Thread.currentThread().getContextClassLoader().getResource("json-example.json");
        JSONObject jsonObjectExample = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileExample.toURI())), Charset.forName("UTF-8")));
        //System.out.println(githubHttpClient.getRawDataJson(url,new JSONObject().put("items",new JSONArray())).toString(2));

        assertEquals(2, githubHttpClient.getRawDataJson(url,new JSONObject().put("items",new JSONArray())).getJSONArray("items").length());
    }

    @Test
    public void testGetterAndSetterHttpClient(){
        GithubHttpClient githubHttpClient = new GithubHttpClient();

        CloseableHttpClient closeableHttpClientExpected = HttpClients.custom().setUserAgent("My-Custom-Client").build();
        githubHttpClient.setHttpClient(closeableHttpClientExpected);

        assertEquals(closeableHttpClientExpected,githubHttpClient.getHttpClient());
    }

    @Test
    public void testGetLastCommitFromRepoNominalCase() throws URISyntaxException, IOException, InterruptedException {
        URL urlJsonFileLastCommit= Thread.currentThread().getContextClassLoader().getResource("json-commits-repo-octocat-helloworld.json");
        JSONObject jsonObjectLastCommit = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileLastCommit.toURI())), Charset.forName("UTF-8")));

        GithubHttpClient githubHttpClient = new GithubHttpClient();

        JSONObject jsonObjectLastCommitActual = githubHttpClient.getLastCommitFromRepo("https://api.github.com/repos/octocat/Hello-World/commits{/sha}","master");

        assertEquals(jsonObjectLastCommit.toString(2),jsonObjectLastCommitActual.toString(2));
    }

    @Test
    public void testGetLastCommitFromRepoWithBadUrlCommit() throws URISyntaxException, IOException, InterruptedException {
        URL urlJsonFileLastCommit= Thread.currentThread().getContextClassLoader().getResource("json-commits-repo-octocat-helloworld.json");
        JSONObject jsonObjectLastCommit = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileLastCommit.toURI())), Charset.forName("UTF-8")));

        GithubHttpClient githubHttpClient = new GithubHttpClient();

        assertNull(githubHttpClient.getLastCommitFromRepo("https://api.github.com/repos/octocat/Hello-World/commit","master"));
    }

    @Test
    public void testGetLastCommitFromRepoWithSlashInBranch() throws URISyntaxException, IOException, InterruptedException {
        URL urlJsonFileLastCommit= Thread.currentThread().getContextClassLoader().getResource("json-commits-repo-octocat-helloworld.json");
        JSONObject jsonObjectLastCommit = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileLastCommit.toURI())), Charset.forName("UTF-8")));

        GithubHttpClient githubHttpClient = new GithubHttpClient();

        JSONObject jsonObjectLastCommitActual = githubHttpClient.getLastCommitFromRepo("https://api.github.com/repos/octocat/Hello-World/commits{/sha}","/master");

        assertEquals(jsonObjectLastCommit.toString(2),jsonObjectLastCommitActual.toString(2));
    }

    @Test
    public void testGetLastCommitForRepositoriesList() throws InterruptedException, IOException, URISyntaxException {
        URL urlJsonFileRepo = Thread.currentThread().getContextClassLoader().getResource("json-example-filtered.json");
        JSONObject jsonObjectLastCommit = new JSONObject(new String(Files.readAllBytes(Paths.get(urlJsonFileRepo.toURI())), Charset.forName("UTF-8")));

        FileWriterJSON mockFileWriterJSON = mock(FileWriterJSON.class);

        GithubHttpClient githubHttpClient = new GithubHttpClient();
        JSONObject jsonObjectActualRepoWitCommits = githubHttpClient.getLastCommitForRepositoriesList(jsonObjectLastCommit,mockFileWriterJSON,pathTmpDir,"MyFile.json");

        JSONArray jsonArrayItems = jsonObjectActualRepoWitCommits.getJSONArray("items");
        System.out.println(jsonArrayItems);

        for (int i = 0; i < jsonArrayItems.length(); i++) {
            assertTrue(jsonArrayItems.getJSONObject(i).has("last_commit"));
            assertTrue(jsonArrayItems.getJSONObject(i).getJSONObject("last_commit").has("sha"));
            assertTrue(jsonArrayItems.getJSONObject(i).getJSONObject("last_commit").has("html_url"));
        }
    }

    @Test
    public void testGetLastCommitForRepositoriesListWithMalformedJSONObject() throws InterruptedException, IOException, URISyntaxException {
        FileWriterJSON mockFileWriterJSON = mock(FileWriterJSON.class);

        GithubHttpClient githubHttpClient = new GithubHttpClient();

        JSONObject jsonObjectLastCommit1 = new JSONObject();
        JSONArray jsonObjectLastCommit2 = new JSONArray();
        JSONObject jsonObjectLastCommit3 = new JSONObject().put("commits_url","Java, the better cup of coffee that I know");

        JSONArray jsonArrayItemsForTest = new JSONArray().put(jsonObjectLastCommit1).put(jsonObjectLastCommit2).put(jsonObjectLastCommit3);
        JSONObject jsonObjectLastCommit = new JSONObject().put("items",jsonArrayItemsForTest);

        JSONObject jsonObjectActualRepoWitCommits = githubHttpClient.getLastCommitForRepositoriesList(jsonObjectLastCommit,mockFileWriterJSON,pathTmpDir,"MyFile.json");

        JSONArray jsonArrayItems = jsonObjectActualRepoWitCommits.getJSONArray("items");
        System.out.println(jsonArrayItems);

        for (int i = 0; i < jsonArrayItems.length(); i++) {
            if(jsonArrayItems.get(i) instanceof JSONObject){
                assertTrue(jsonArrayItems.getJSONObject(i).has("last_commit"));
                assertFalse(jsonArrayItems.getJSONObject(i).getJSONObject("last_commit").has("sha"));
                assertFalse(jsonArrayItems.getJSONObject(i).getJSONObject("last_commit").has("html_url"));
            }
        }
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
