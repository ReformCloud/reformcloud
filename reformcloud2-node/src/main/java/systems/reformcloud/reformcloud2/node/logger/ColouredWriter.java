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
package systems.reformcloud.reformcloud2.node.logger;

import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ColouredWriter extends Handler {

  private final LineReader lineReader;

  ColouredWriter(LineReader lineReader) {
    this.lineReader = lineReader;
  }

  @Override
  public void publish(LogRecord record) {
    if (super.isLoggable(record)) {
      this.publish(super.getFormatter().format(record));
    }
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() throws SecurityException {
  }

  private void publish(@NotNull String record) {
    record = ConsoleColour.toColouredString('&', record);

    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
    this.lineReader.getTerminal().puts(InfoCmp.Capability.clr_eol);
    this.lineReader.getTerminal().writer().print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + record + Ansi.ansi().reset().toString());
    this.lineReader.getTerminal().writer().flush();

    this.redisplay();
  }

  private void redisplay() {
    if (!this.lineReader.isReading()) {
      return;
    }

    this.lineReader.callWidget(LineReader.REDRAW_LINE);
    this.lineReader.callWidget(LineReader.REDISPLAY);
  }
}
