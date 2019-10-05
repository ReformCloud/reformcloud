package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter;

import systems.reformcloud.reformcloud2.executor.api.common.logger.FormatterBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.Colours;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

public final class LogFileFormatter extends FormatterBase {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss:SS");

    public LogFileFormatter(LoggerBase loggerBase) {
        super(loggerBase);
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[")
                .append(DATE_FORMAT.format(record.getMillis()))
                .append(" ")
                .append(record.getLevel().getLocalizedName())
                .append("] ")
                .append(Colours.stripColor(formatMessage(record)))
                .append("\n");
        if (record.getThrown() != null) {
            stringBuilder.append(format(record.getThrown()));
        }
        return stringBuilder.toString();
    }
}
