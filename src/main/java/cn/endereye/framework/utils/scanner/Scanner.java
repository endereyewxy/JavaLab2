package cn.endereye.framework.utils.scanner;

import java.io.IOException;
import java.util.HashSet;

public interface Scanner {
    String CLASS_SUFFIX = ".class";

    @FunctionalInterface
    interface ScanConsumer {
        void apply(Class<?> cls) throws Exception;
    }

    HashSet<Class<?>> scan(String pkg) throws ClassNotFoundException, IOException;

    static void scan(String pkg, ScanConsumer consumer) throws Exception {
        // Add all scanner results to a set to prevent duplication.
        final HashSet<Class<?>> result = new HashSet<>();
        result.addAll(new DirScanner().scan(pkg));
        result.addAll(new JarScanner().scan(pkg));
        for (Class<?> cls : result)
            consumer.apply(cls);
    }
}
