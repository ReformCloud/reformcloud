package systems.reformcloud.reformcloud2.executor.api.common.network.exception;

public class WrongResultTypeException extends RuntimeException {

    private static final long serialVersionUID = 6620235022709563003L;

    public static WrongResultTypeException INSTANCE = new WrongResultTypeException();
}
