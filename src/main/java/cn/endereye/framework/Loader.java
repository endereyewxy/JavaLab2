package cn.endereye.framework;

import cn.endereye.framework.ioc.IOCFrameworkException;
import cn.endereye.framework.ioc.IOCManager;
import cn.endereye.framework.web.WebFrameworkException;
import cn.endereye.framework.web.WebManager;

public abstract class Loader {
    public static void load(String pkg) throws IOCFrameworkException, WebFrameworkException {
        IOCManager.getInstance().scan(pkg);
        WebManager.getInstance().scan(pkg);
        IOCManager.getInstance().inject();
    }
}
