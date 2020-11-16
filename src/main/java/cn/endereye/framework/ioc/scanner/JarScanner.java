package cn.endereye.framework.ioc.scanner;

import cn.endereye.framework.ioc.IOCFrameworkException;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;

public class JarScanner implements Scanner {
    @Override
    public HashSet<Class<?>> scan(String pkg) throws IOCFrameworkException {
        HashSet<Class<?>> result = new HashSet<>();
        final Enumeration<URL> urls;
        try {
            urls = Thread.currentThread().getContextClassLoader().getResources(pkg.replace(".", "/"));
        } catch (IOException e) {
            throw new IOCFrameworkException("Cannot load package " + pkg);
        }
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                final Enumeration<JarEntry> entries;
                try {
                    entries = ((JarURLConnection) url.openConnection()).getJarFile().entries();
                } catch (IOException e) {
                    throw new IOCFrameworkException("Cannot open JAR " + url.toString());
                }
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.endsWith(CLASS_SUFFIX) && name.startsWith(pkg.replace(".", "/"))) {
                        final String fullName = name.substring(0, name.length() - CLASS_SUFFIX.length()).replace("/", ".");
                        try {
                            result.add(Class.forName(fullName));
                        } catch (ClassNotFoundException e) {
                            throw new IOCFrameworkException("Cannot load class " + fullName);
                        }
                    }
                }
            } else if ("file".equalsIgnoreCase(url.getProtocol())) {
                result.addAll(new DirScanner(url.getPath().replace(pkg.replace(".", "/"), "")).scan(pkg));
            }
        }
        return result;
    }
}
