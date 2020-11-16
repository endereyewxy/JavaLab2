package cn.endereye.framework.utils.scanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Consumer;

public interface Scanner {
    String CLASS_SUFFIX = ".class";

    HashSet<Class<?>> scan(String pkg) throws ClassNotFoundException, IOException;

    static void scan(String pkg, Consumer<Class<?>> consumer) throws ClassNotFoundException, IOException {
        // Add all scanner results to a set to prevent duplication.
        final HashSet<Class<?>> result = new HashSet<>();
        result.addAll(new DirScanner().scan(pkg));
        result.addAll(new JarScanner().scan(pkg));
        result.forEach(consumer);
    }
}
