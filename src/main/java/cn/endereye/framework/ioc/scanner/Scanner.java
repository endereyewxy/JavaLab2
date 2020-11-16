package cn.endereye.framework.ioc.scanner;

import cn.endereye.framework.ioc.IOCFrameworkException;

import java.util.Set;

public interface Scanner {
    String CLASS_SUFFIX = ".class";

    Set<Class<?>> scan(String pkg) throws IOCFrameworkException;
}
