package com.bane.asynch.service;

import com.bane.asynch.common.Constants;
import com.bane.asynch.domain.Response;
import com.bane.asynch.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.swing.text.Utilities;
import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/neiljbrown/java11-examples/blob/master/src/test/java/com/neiljbrown/java11/HttpClientTest.java
 * https://stackoverflow.com/questions/45174233/difference-between-thenaccept-and-thenapply
 * <p>
 * Class HttpClient sendAsync(HttpRequest, BodyHandler) sends the request and receives the response asynchronously.
 * The sendAsync method returns immediately with a CompletableFuture<HttpResponse>.
 * The CompletableFuture completes when the response becomes available.
 * The returned CompletableFuture can be combined in different ways to declare dependencies among several asynchronous tasks.
 * <p>
 * http://codeflex.co/java-multithreading-completablefuture-explained/
 */
class UserServiceTest {

    public static final int CONNECTION_TIMEOUT = 1000;
    private static final String GOOGLE_URL = "https://www.google.de";

    private HttpClient httpClient;
    private boolean userDefaultHttpClient;

    @BeforeEach
    void setUp() {
        this.httpClient = createHttpClient();
    }

    private HttpClient createHttpClient() {
        if (userDefaultHttpClient) {
            return HttpClient.newHttpClient();
        } else {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT))
                    .build();
        }
    }


    @DisplayName("Basic blocking HTTP GET request")
    @Test
    void getGetRequest() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(GOOGLE_URL))
                .build();

        HttpResponse<String> response = this.httpClient
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.headers().firstValue("content-type")).isPresent().isNotEmpty();
        assertThat(response.body()).isNotEmpty();
    }


    @Test
    void testAsyncGetRequest() throws ExecutionException, InterruptedException, TimeoutException {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(GOOGLE_URL))
                .build();

        // Use HttpClient.sendAsync(...) method to execute request without blocking. Returns response as CompletableFuture.
        //The sendAsync method returns immediately with a CompletableFuture<HttpResponse>.
        CompletableFuture<HttpResponse<String>> futureHttpResponse =
                this.httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(Thread.currentThread().getName());

        //Chain processing of future HttpResponse without blocking (when it's returned), subject to time-out.
        // The CompletableFuture completes when the response becomes available.
        //The runAsync(Runnable runnable) returns a new CompletableFuture that is asynchronously completed by a task running in the ForkJoinPool.commonPool()
        //after it runs the given action. (It execute tasks in a thread obtained from the global ForkJoinPool.commonPool())
        futureHttpResponse
                .thenApply(stringHttpResponse -> {
                    System.out.println(Thread.currentThread().getName());
                    return stringHttpResponse.body();
                })
                .thenAccept(body -> System.out.println("testAsyncGetRequest() - Received response body: " + body.substring(0, 80)))
                .orTimeout(1000, TimeUnit.MILLISECONDS)
                .join();

        //Alternatively, block on HttpResponse
        HttpResponse<String> response = futureHttpResponse.get(1000, TimeUnit.MILLISECONDS);

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.headers().firstValue("content-type")).isPresent().isNotEmpty();
        assertThat(response.body()).isNotEmpty();

    }


    @Test
    void testSingleHttpGetAsync() throws ExecutionException, InterruptedException {

        CompletableFuture<String> stringHttpResponse = httpGetResponse("http://httpbin.org/get");
        Response response = JsonUtils.getResponseInObject(stringHttpResponse.get(), Response.class);
        System.out.println(response);
        assertThat(response)
                .isNotNull()
                //.hasFieldOrPropertyWithValue("data", "null")
                .hasFieldOrPropertyWithValue("url", "http://httpbin.org/get");

        assertThat(response.getHeaders()).isNotNull()
                .hasFieldOrPropertyWithValue("contentLength", "0")
                .hasFieldOrPropertyWithValue("host", "httpbin.org")
                .hasFieldOrPropertyWithValue("userAgent", "Java-http-client/17.0.1");

    }

    private CompletableFuture<String> httpGetResponse(final String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    @Test
    void testMultipleHttpGetAsync() throws ExecutionException, InterruptedException {
        List<URI> uriList = List.of(URI.create(GOOGLE_URL), URI.create(GOOGLE_URL));
        List<CompletableFuture<String>> completableFutureList = concurrentCalls(uriList);

        for (CompletableFuture<String> completableFuture : completableFutureList) {
            System.out.println(completableFuture.get());
        }
    }


    /**
     * help:    https://github.com/ThakurPriyanka/demoHttpClient/blob/master/src/main/java/HttpClientASynchronous.java
     *
     * @param uriList
     * @return
     */
    private List<CompletableFuture<String>> concurrentCalls(final List<URI> uriList) {
        List<CompletableFuture<String>> completableFutureList = uriList.stream()
                .map(uri -> httpClient.sendAsync(
                        HttpRequest.newBuilder()
                                .uri(URI.create(GOOGLE_URL))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                ).thenApply(HttpResponse::body))
                .collect(Collectors.toList());

        return completableFutureList;
    }


    @Test
    void testHttpPostAsync() throws JsonProcessingException, ExecutionException, InterruptedException {
        final String requestBodyAsString = JsonUtils.getRequestBodyAsString(Constants.POST_URL);

        CompletableFuture<String> completableFuture = httpPostResponse(Constants.POST_URL, requestBodyAsString);
        Response response = JsonUtils.getResponseInObject(completableFuture.get(), Response.class);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("url", "http://httpbin.org/post")
                .hasFieldOrProperty("origin")
                .hasFieldOrProperty("data");
    }

    private CompletableFuture<String> httpPostResponse(final String uri, final String requestBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }


}