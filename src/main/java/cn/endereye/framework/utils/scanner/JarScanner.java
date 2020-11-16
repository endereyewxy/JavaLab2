package cn.endereye.framework.utils.scanner;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;

public class JarScanner implements Scanner {
    @Override
    public HashSet<Class<?>> scan(String pkg) throws ClassNotFoundException, IOException {
        HashSet<Class<?>> result = new HashSet<>();
        final Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pkg.replace(".", "/"));
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                final Enumeration<JarEntry> entries = ((JarURLConnection) url.openConnection()).getJarFile().entries();
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.endsWith(CLASS_SUFFIX) && name.startsWith(pkg.replace(".", "/")))
                        result.add(Class.forName(name.substring(0, name.length() - CLASS_SUFFIX.length()).replace("/", ".")));
                }
            } else if ("file".equalsIgnoreCase(url.getProtocol())) {
                result.addAll(new DirScanner(url.getPath().replace(pkg.replace(".", "/"), "")).scan(pkg));
            }
        }
        return result;
    }
}
