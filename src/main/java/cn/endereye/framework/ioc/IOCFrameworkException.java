package cn.endereye.framework.ioc;

public class IOCFrameworkException extends Exception {
    public IOCFrameworkException(Throwable cause) {
        super(cause);
    }

    public IOCFrameworkException(String reason) {
        super(reason);
    }

    public IOCFrameworkException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
