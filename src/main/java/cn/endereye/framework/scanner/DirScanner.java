package cn.endereye.framework.scanner;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;

public class DirScanner implements Scanner {
    private final String basePath;

    public DirScanner() {
        this(DirScanner.class.getResource("/").getPath());
    }

    public DirScanner(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public HashSet<Class<?>> scan(String pkg) throws ClassNotFoundException {
        final HashSet<Class<?>> result = new HashSet<>();
        scan(new File(basePath + pkg.replace(".", "/")), pkg, result);
        return result;
    }

    private void scan(File target, String pkg, HashSet<Class<?>> result) throws ClassNotFoundException {
        if (target.isDirectory()) {
            for (File file : Objects.requireNonNull(target.listFiles()))
                scan(file, pkg + "." + file.getName(), result);
        } else if (target.getName().endsWith(CLASS_SUFFIX)) {
            // Now, the argument `pkg` should be the class name, except with a `CLASS_SUFFIX`. The only thing we have to
            // do is to remove that suffix.
            result.add(Class.forName(pkg.substring(0, pkg.length() - CLASS_SUFFIX.length())));
        }
    }
}
