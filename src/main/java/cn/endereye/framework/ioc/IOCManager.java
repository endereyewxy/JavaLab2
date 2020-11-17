package cn.endereye.framework.ioc;

import cn.endereye.framework.ioc.annotations.InjectSource;
import cn.endereye.framework.ioc.annotations.InjectTarget;
import cn.endereye.framework.scanner.Scanner;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class IOCManager {
    private static IOCManager instance = null;

    private final HashMap<Class<?>, Object> singletons = new HashMap<>();

    private final LinkedList<Pair<InjectSource, Class<?>>> sources = new LinkedList<>();

    private final HashSet<Class<?>> targets = new HashSet<>();

    public static IOCManager getInstance() {
        if (instance == null) {
            synchronized (IOCManager.class) {
                if (instance == null)
                    instance = new IOCManager();
            }
        }
        return instance;
    }

    /**
     * Get a singleton instance according to its class. The class itself is not required to be an injection source or
     * contains injection targets.
     *
     * @param type Class of the instance.
     * @param <T>  Generic type parameter of the instance.
     * @throws IOCFrameworkException Occurs when failed to create an instance.
     */
    @SuppressWarnings("unchecked")
    public <T> T getSingleton(Class<T> type) throws IOCFrameworkException {
        try {
            if (!singletons.containsKey(type))
                singletons.put(type, type.newInstance());
            return (T) singletons.get(type);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IOCFrameworkException("Cannot instantiate " + type.getName(), e);
        }
    }

    /**
     * Detect {@link InjectSource} and {@link InjectTarget} configurations of a class.
     *
     * @param type The class.
     */
    public void register(Class<?> type) {
        if (type.getAnnotation(Deprecated.class) == null) {
            final InjectSource annotation = type.getAnnotation(InjectSource.class);
            if (annotation != null)
                sources.add(new Pair<>(annotation, type));
            if (Arrays.stream(type.getDeclaredFields()).anyMatch(f -> f.getAnnotation(InjectTarget.class) != null))
                targets.add(type);
        }
    }

    /**
     * Perform injection. Not recommended to invoke manually, see {@link cn.endereye.framework.Loader} for more
     * information.
     */
    public void inject() throws IOCFrameworkException {
        for (Class<?> target : targets) {
            final Object instance = getSingleton(target);
            for (Field field : target.getDeclaredFields()) {
                final InjectTarget annotation = field.getAnnotation(InjectTarget.class);
                if (annotation != null) {
                    final IOCPolicy policy = IOCPolicy.policies.get(annotation.policy());
                    int maxPrior = -1;
                    Class<?> source1 = null;
                    Class<?> source2 = null;
                    for (Pair<InjectSource, Class<?>> pair : sources) {
                        int prior = policy.getPriority(pair.getKey(), pair.getValue(), field);
                        if (prior == maxPrior) {
                            if (source1 == null)
                                source1 = pair.getValue();
                            else
                                source2 = pair.getValue();
                        }
                        if (prior > maxPrior) {
                            maxPrior = prior;
                            source1 = pair.getValue();
                            source2 = null;
                        }
                    }
                    if (maxPrior != -1 && source2 != null) {
                        final String errMsg = String.format("When injecting %s: Cannot decide between %s and %s",
                                field.toGenericString(),
                                source1.toGenericString(),
                                source2.toGenericString());
                        throw new IOCFrameworkException(errMsg);
                    }
                    final Object sourceInstance = maxPrior == -1 ? null : getSingleton(source1);
                    try {
                        field.setAccessible(true);
                        field.set(instance, sourceInstance);
                    } catch (IllegalAccessException e) {
                        throw new IOCFrameworkException("Cannot inject into " + field.toGenericString(), e);
                    }
                }
            }
        }
    }

    public void scan(String pkg) throws IOCFrameworkException {
        try {
            Scanner.scan(pkg, this::register);
        } catch (Exception e) {
            throw new IOCFrameworkException(e);
        }
    }

    private IOCManager() {
    }
}
