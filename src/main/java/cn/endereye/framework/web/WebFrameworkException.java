package cn.endereye.framework.web;

public class WebFrameworkException extends Exception {
    public WebFrameworkException(Throwable cause) {
        super(cause);
    }

    public WebFrameworkException(String reason) {
        super(reason);
    }
}
