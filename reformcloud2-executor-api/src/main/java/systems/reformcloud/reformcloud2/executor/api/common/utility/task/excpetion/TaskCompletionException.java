package systems.reformcloud.reformcloud2.executor.api.common.utility.task.excpetion;

public final class TaskCompletionException extends RuntimeException {

    public TaskCompletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskCompletionException(Throwable cause) {
        super("An exception occurred", cause);
    }
}
