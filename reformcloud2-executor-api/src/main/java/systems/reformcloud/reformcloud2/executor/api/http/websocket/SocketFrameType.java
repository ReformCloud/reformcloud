/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.executor.api.http.websocket;

/**
 * The type of a frame sent by the client. For further information please see
 * <a href="https://tools.ietf.org/html/rfc6455">The WebSocket Protocol</a>.
 *
 * @author derklaro
 * @since 27. October 2020
 */
public enum SocketFrameType {
    /**
     * The "Payload data" is arbitrary binary data whose interpretation
     * is solely up to the application layer.
     */
    BINARY,
    /**
     * The "Payload data" is text data encoded as UTF-8. Note that a
     * particular text frame might include a partial UTF-8 sequence;
     * however, the whole message MUST contain valid UTF-8.
     */
    TEXT,
    /**
     * The Ping frame contains an opcode of 0x9. A ping frame may include
     * "Application data". Upon receipt of a Ping frame, an endpoint must
     * send a pong frame in response, unless it already received a close frame.
     * It should respond with pong frame as soon as is practical. An endpoint
     * may send a ping frame any time after the connection is established and
     * before the connection is closed. NOTE: A Ping frame may serve either as
     * a keepalive or as a means to verify that the remote endpoint is still responsive.
     */
    PING,
    /**
     * The Pong frame contains an opcode of 0xA. A Pong frame sent in response to
     * a Ping frame must have identical "Application data" as found in the message
     * body of the Ping frame being replied to. If an endpoint receives a ping frame
     * and has not yet sent pong frame(s) in response to previous ping frame(s), the
     * endpoint may elect to send a Pong frame for only the most recently processed Ping
     * frame.
     */
    PONG,
    /**
     * The Close frame contains an opcode of 0x8. The Close frame may contain
     * a body (the "Application data" portion of the frame) that indicates a reason
     * for closing, such as an endpoint shutting down, an endpoint having received
     * a frame too large, or an endpoint having received a frame that does
     * not conform to the format expected by the endpoint. If there is a body, the
     * first two bytes of the body must be a 2-byte unsigned integer (in network byte order)
     * representing a status code. Following the 2-byte integer, the body may contain
     * UTF-8-encoded data  with value /reason/, the interpretation of which is not defined by
     * this specification. This data is not necessarily human readable but may be useful
     * for debugging or passing information relevant to the script that opened the connection.
     * As the data is not guaranteed to be human readable, clients must not show it to
     * end users. Close frames sent from client to server must have the field frame-masked
     * set to 1. The application must not send any more data frames after sending a
     * close frame. If an endpoint receives a close frame and did not previously send a
     * Close frame, the endpoint MUST send a Close frame in response.  (When
     * sending a Close frame in response, the endpoint typically echos the
     * status code it received.)  It SHOULD do so as soon as practical.  An
     * endpoint MAY delay sending a Close frame until its current message is
     * sent (for instance, if the majority of a fragmented message is
     * already sent, an endpoint MAY send the remaining fragments before
     * sending a Close frame).  However, there is no guarantee that the
     * endpoint that has already sent a Close frame will continue to process
     * data. After both sending and receiving a close message, an endpoint
     * considers the WebSocket connection closed and must close the
     * underlying TCP connection. The server must close the underlying TCP
     * connection immediately; the client should wait for the server to
     * close the connection but may close the connection at any time after
     * sending and receiving a Close message, e.g., if it has not received a
     * TCP Close from the server in a reasonable time period. If a client and server
     * both send a Close message at the same time, both endpoints will have sent
     * and received a Close message and should consider the WebSocket connection closed
     * and close the underlying TCP connection.
     */
    CLOSE,
    /**
     * The "Payload data" is arbitrary binary data which is the resumed data of
     * the last payload sent to the client or server.
     */
    CONTINUATION
}
