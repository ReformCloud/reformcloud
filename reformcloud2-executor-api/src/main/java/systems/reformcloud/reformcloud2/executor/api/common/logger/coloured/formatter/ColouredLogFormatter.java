package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter;

import systems.reformcloud.reformcloud2.executor.api.common.logger.FormatterBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.Colours;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class ColouredLogFormatter extends FormatterBase {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss:SS");

    public ColouredLogFormatter(LoggerBase loggerBase) {
        super(loggerBase);
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder()
                .append(Colours.GRAY.toString())
                .append("[")
                .append(Colours.WHITE.toString())
                .append(DATE_FORMAT.format(record.getMillis()))
                .append(Colours.GRAY.toString())
                .append(" ")
                .append(getByLevel(record.getLevel()).toString())
                .append(record.getLevel().getLocalizedName())
                .append(Colours.GRAY.toString())
                .append("] ")
                .append(Colours.RESET.toString())
                .append(formatMessage(record))
                .append("\n");
        if (record.getThrown() != null) {
            stringBuilder.append(format(record.getThrown()));
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
