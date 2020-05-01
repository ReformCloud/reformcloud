package systems.reformcloud.reformcloud2.executor.api.common.network.exception;

public class SilentNetworkException extends RuntimeException {

    public SilentNetworkException(String message) {
        super(message);
    }

    public SilentNetworkException(Throwable cause) {
        super(cause.getClass().getSimpleName() + ":" + cause.getMessage());
    }

    public SilentNetworkException(String message, Throwable cause) {
        super(message + "@" + cause.getClass().getSimpleName() + ":" + cause.getMessage());
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
