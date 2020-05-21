/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.logger.FormatterBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.Colours;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class ColouredLogFormatter extends FormatterBase {

    public ColouredLogFormatter(LoggerBase loggerBase) {
        super(loggerBase);
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder()
                .append(Colours.GRAY.toString())
                .append("[")
                .append(Colours.WHITE.toString())
                .append(CommonHelper.DATE_FORMAT.format(record.getMillis()))
                .append(Colours.GRAY.toString())
                .append(" ")
                .append(this.getByLevel(record.getLevel()).toString())
                .append(record.getLevel().getLocalizedName())
                .append(Colours.GRAY.toString())
                .append("] ")
                .append(Colours.RESET.toString())
                .append(this.formatMessage(record))
                .append("\n");
        if (record.getThrown() != null) {
            stringBuilder.append(this.format(record.getThrown()));
        }
        return stringBuilder.toString();
    }

    private Colours getByLevel(Level level) {
        if (level.equals(Level.INFO)) {
            return Colours.GREEN;
        } else if (level.equals(Level.WARNING)) {
            return Colours.YELLOW;
        } else if (level.equals(Level.SEVERE)) {
            return Colours.RED;
        }

        return Colours.GRAY;
    }
}
