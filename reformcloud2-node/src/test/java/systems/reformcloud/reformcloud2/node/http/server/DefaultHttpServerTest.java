package systems.reformcloud.reformcloud2.node.http.server;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import systems.reformcloud.reformcloud2.executor.api.http.HttpStatusCode;
import systems.reformcloud.reformcloud2.executor.api.http.cookie.HttpCookie;
import systems.reformcloud.reformcloud2.executor.api.http.listener.HttpListener;
import systems.reformcloud.reformcloud2.executor.api.http.listener.RequestMethods;
import systems.reformcloud.reformcloud2.executor.api.http.reponse.ListeningHttpServerResponse;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequest;
import systems.reformcloud.reformcloud2.executor.api.http.request.RequestMethod;
import systems.reformcloud.reformcloud2.executor.api.http.server.HttpServer;
import systems.reformcloud.reformcloud2.shared.network.NetworkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultHttpServerTest {

    private static final String ID = "56";
    private static final String USER_ID = "google";
    private static final String TEST_BODY = "test_body";
    private static final String TEST_COOKIE_NAME = "test_cookie";
    private static final String TEST_REQUEST_HEADER_KEY = "header_key";
    private static final String TEST_COOKIE_VALUE = "test_cookie_value";
    private static final String TEST_REQUEST_HEADER_VALUE = "header_val";
    private static final int HTTP_PORT = NetworkUtils.checkAndReplacePortIfInUse(2000);

    private final HttpServer httpServer = new DefaultHttpServer();
    private final CookieManager cookieManager = new CookieManager();

    @BeforeAll
    void updateCookieManager() {
        CookieHandler.setDefault(this.cookieManager);
    }

    @Test
    @Order(1)
    void testAddListener() {
        Assertions.assertTrue(this.httpServer.bind("127.0.0.1", HTTP_PORT));
        this.httpServer.getListenerRegistry().registerListeners("/delete/{user}/work/{id}", new HttpListener() {
            @Override
            @RequestMethods(RequestMethod.POST)
            public @NotNull ListeningHttpServerResponse<?> handleRequest(@NotNull HttpRequest<?> request) {
                Assertions.assertEquals(USER_ID, request.pathParameter("user").orElse(null));
                Assertions.assertEquals(ID, request.pathParameter("id").orElse(null));
                Assertions.assertEquals(request.headers().get(TEST_REQUEST_HEADER_KEY).orElse(null), TEST_REQUEST_HEADER_VALUE);
                return ListeningHttpServerResponse.response(request)
                    .closeAfterSent(true)
                    .status(HttpStatusCode.ACCEPTED)
                    .body(TEST_BODY.getBytes(StandardCharsets.UTF_8))
                    .cookies(HttpCookie.cookie(TEST_COOKIE_NAME, TEST_COOKIE_VALUE).maxAge(5000).secure(true).httpOnly(true));
            }
        });
        Assertions.assertEquals(1, this.httpServer.getListenerRegistry().getListeners().size());
        Assertions.assertEquals(1, this.httpServer.getListenerRegistry().getListeners().get("/delete/{user}/work/{id}").size());
    }

    @Test
    @Order(2)
    void testHandleGetRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + HTTP_PORT + "/delete/" + USER_ID + "/work/" + ID).openConnection();
        connection.setRequestProperty(TEST_REQUEST_HEADER_KEY, TEST_REQUEST_HEADER_VALUE);
        connection.setDoInput(true);
        connection.connect();
        // Check if the request was discarded because it's only handling post requests
        Assertions.assertEquals(HttpStatusCode.NOT_FOUND.code(), connection.getResponseCode());
    }

    @Test
    @Order(3)
    @Timeout(10)
    void testHandlePostRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + HTTP_PORT + "/delete/" + USER_ID + "/work/" + ID).openConnection();
        connection.setRequestProperty(TEST_REQUEST_HEADER_KEY, TEST_REQUEST_HEADER_VALUE);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.connect();
        // Check if request status is correct
        Assertions.assertEquals(HttpStatusCode.ACCEPTED.code(), connection.getResponseCode());
        // Check if the cookie was added
        java.net.HttpCookie cookie = this.cookieManager.getCookieStore().getCookies()
            .stream()
            .filter(httpCookie -> httpCookie.getName().equals(TEST_COOKIE_NAME) && httpCookie.getValue().equals(TEST_COOKIE_VALUE))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(cookie);
        Assertions.assertEquals(5000, cookie.getMaxAge());
        Assertions.assertTrue(cookie.isHttpOnly());
        Assertions.assertTrue(cookie.getSecure());
        // Check if the correct body was sent
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            Assertions.assertEquals(1, lines.size());
            Assertions.assertEquals(TEST_BODY, lines.get(0));
        }
    }

    @AfterAll
    void closeHttpServer() {
        this.httpServer.closeAll();
    }
}
