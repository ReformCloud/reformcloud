package systems.reformcloud.reformcloud2.executor.api.common.network.exception;

public class SilentNetworkException extends RuntimeException {

    public SilentNetworkException(String message) {
        super(message);
    }

    public SilentNetworkException(Throwable cause) {
        super(cause);
    }

    public SilentNetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return this;
    }
}
