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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import systems.reformcloud.http.listener.HttpListenerRegistry;

public class DefaultHttpServerChannelInitializer extends ChannelInitializer<Channel> {

  private final HttpListenerRegistry listenerRegistry;

  public DefaultHttpServerChannelInitializer(HttpListenerRegistry listenerRegistry) {
    this.listenerRegistry = listenerRegistry;
  }

  @Override
  protected void initChannel(Channel ch) {
    ch.pipeline()
      .addLast(ServerConstants.HTTP_SERVER_CODEC, new HttpServerCodec())
      .addLast(ServerConstants.HTTP_OBJECT_AGGREGATOR, new HttpObjectAggregator(Short.MAX_VALUE))
      .addLast(ServerConstants.HTTP_CORS_HANDLER, new CorsHandler(CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build()))
      .addLast(ServerConstants.HTTP_HANDLER, new DefaultHttpHandler(this.listenerRegistry));
  }
}
