/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.node.http.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import systems.reformcloud.http.HttpStatusCode;
import systems.reformcloud.http.listener.HttpListener;
import systems.reformcloud.http.listener.RequestMethods;
import systems.reformcloud.http.reponse.ListeningHttpServerResponse;
import systems.reformcloud.http.request.HttpRequest;
import systems.reformcloud.http.request.RequestMethod;
import systems.reformcloud.http.server.HttpServer;
import systems.reformcloud.http.websocket.CloseSocketFrame;
import systems.reformcloud.http.websocket.SocketFrame;
import systems.reformcloud.http.websocket.SocketFrameType;
import systems.reformcloud.http.websocket.TextSocketFrame;
import systems.reformcloud.http.websocket.listener.FrameTypes;
import systems.reformcloud.http.websocket.listener.SocketFrameListener;
import systems.reformcloud.http.websocket.request.RequestFrameHolder;
import systems.reformcloud.http.websocket.request.SocketFrameSource;
import systems.reformcloud.http.websocket.response.ResponseFrameHolder;
import systems.reformcloud.shared.network.NetworkUtils;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DefaultHttpServerWebSocketTest {

  private static final String CLIENT_HI_MESSAGE = "Hi";
  private static final String SERVER_HI_RESPONSE = "Morning";
  private static final String SERVER_GOODBYE_MESSAGE = "ByeBye";
  private static final String CLIENT_CLOSE_REQUEST_TEXT = "Ok Bye!";
  private static final int HTTP_PORT = NetworkUtils.checkAndReplacePortIfInUse(2000);
  private static final int SERVER_CLOSE_REASON_CODE = CloseSocketFrame.CloseStatus.SERVICE_RESTART.code();

  private final HttpServer httpServer = new DefaultHttpServer();

  @Test
  @Order(1)
  void testRegisterListener() {
    Assertions.assertTrue(this.httpServer.bind("127.0.0.1", HTTP_PORT));
    this.httpServer.getListenerRegistry().registerListeners("/to/websocket", new HttpListener() {
      @Override
      @RequestMethods(RequestMethod.GET)
      public @NotNull ListeningHttpServerResponse<?> handleRequest(@NotNull HttpRequest<?> request) {
        SocketFrameSource socketFrameSource = request.source().upgrade().orElse(null);
        Assertions.assertNotNull(socketFrameSource);

        socketFrameSource.listenerRegistry().registerListeners(new SocketFrameListener() {
          @Override
          @FrameTypes(SocketFrameType.TEXT)
          public @Nullable ResponseFrameHolder<?> handleFrame(@NotNull RequestFrameHolder frame) {
            Assertions.assertTrue(frame.request() instanceof TextSocketFrame);
            if (((TextSocketFrame<?>) frame.request()).text().equals(CLIENT_HI_MESSAGE)) {
              return ResponseFrameHolder.response(SocketFrame.textFrame(SERVER_HI_RESPONSE));
            }

            return null;
          }
        }, new SocketFrameListener() {
          @Override
          @FrameTypes(SocketFrameType.TEXT)
          public @Nullable ResponseFrameHolder<?> handleFrame(@NotNull RequestFrameHolder frame) {
            Assertions.assertTrue(frame.request() instanceof TextSocketFrame);
            if (((TextSocketFrame<?>) frame.request()).text().equals(CLIENT_CLOSE_REQUEST_TEXT)) {
              return ResponseFrameHolder.response(SocketFrame.closeFrame(SERVER_CLOSE_REASON_CODE, SERVER_GOODBYE_MESSAGE))
                .lastHandler(true)
                .closeAfterSent(true);
            }

            return null;
          }
        });

        Assertions.assertEquals(2, socketFrameSource.listenerRegistry().getListeners().size());
        return ListeningHttpServerResponse.response(request);
      }
    });
    Assertions.assertEquals(1, this.httpServer.getListenerRegistry().getListeners().size());
    Assertions.assertEquals(1, this.httpServer.getListenerRegistry().getListeners().get("/to/websocket").size());
  }

  @Test
  @Order(2)
  @Timeout(10)
  void testHandlePostRequest() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + HTTP_PORT + "/to/websocket").openConnection();
    connection.setRequestMethod("POST");
    connection.setDoInput(true);
    connection.connect();
    // Check if the request was discarded because it's only handling get requests
    Assertions.assertEquals(HttpStatusCode.NOT_FOUND.code(), connection.getResponseCode());
  }

  @Test
  @Order(3)
  @Timeout(10)
  void testUpgradeConnection() throws IOException, InterruptedException, DeploymentException {
    Session session = ContainerProvider.getWebSocketContainer().connectToServer(
      ClientHandler.class,
      URI.create("ws://127.0.0.1:" + HTTP_PORT + "/to/websocket")
    );
    while (session.isOpen()) {
      Thread.sleep(50);
    }

    Assertions.assertEquals(3, ClientHandler.STATE.get());
  }

  @AfterAll
  void closeHttpServer() {
    this.httpServer.closeAll();
  }

  @ClientEndpoint
  public static final class ClientHandler {

    private static final AtomicInteger STATE = new AtomicInteger();
    private final AtomicBoolean expectingServerHiResponse = new AtomicBoolean();
    private final AtomicBoolean expectingServerCloseConnection = new AtomicBoolean();

    @OnOpen
    public void onOpen(@NotNull Session session) {
      Assertions.assertFalse(this.expectingServerHiResponse.getAndSet(true));
      session.getAsyncRemote().sendText(CLIENT_HI_MESSAGE);
      STATE.incrementAndGet();
    }

    @OnMessage
    public void onMessage(@NotNull Session session, String message) {
      Assertions.assertTrue(this.expectingServerHiResponse.getAndSet(false));
      Assertions.assertEquals(SERVER_HI_RESPONSE, message);
      Assertions.assertFalse(this.expectingServerCloseConnection.getAndSet(true));
      session.getAsyncRemote().sendText(CLIENT_CLOSE_REQUEST_TEXT);
      STATE.incrementAndGet();
    }

    @OnClose
    public void onClose(@NotNull CloseReason closeReason) {
      Assertions.assertTrue(this.expectingServerCloseConnection.getAndSet(false));
      Assertions.assertEquals(SERVER_CLOSE_REASON_CODE, closeReason.getCloseCode().getCode());
      Assertions.assertEquals(SERVER_GOODBYE_MESSAGE, closeReason.getReasonPhrase().trim());
      STATE.incrementAndGet();
    }
  }
}
