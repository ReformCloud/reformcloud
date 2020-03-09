package systems.reformcloud.reformcloud2.executor.api.common.logger.other.fornatter;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.logger.FormatterBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

import java.util.logging.LogRecord;

public final class DefaultLogFormatter extends FormatterBase {

    public DefaultLogFormatter(LoggerBase loggerBase) {
        super(loggerBase);
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("[")
                .append(CommonHelper.DATE_FORMAT.format(record.getMillis()))
                .append(" ")
                .append(record.getLevel().getLocalizedName())
                .append("] ")
                .append(formatMessage(record))
                .append("\n");
        if (record.getThrown() != null) {
            stringBuilder.append(format(record.getThrown()));
        }
        return stringBuilder.toString();
    }
}
