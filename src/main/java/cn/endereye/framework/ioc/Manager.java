package cn.endereye.framework.ioc;

import cn.endereye.framework.ioc.annotations.InjectSource;
import cn.endereye.framework.ioc.annotations.InjectTarget;
import cn.endereye.framework.ioc.scanner.DirScanner;
import cn.endereye.framework.ioc.scanner.JarScanner;
import cn.endereye.framework.utils.AnnotationUtils;
import com.google.common.collect.HashBasedTable;

import java.lang.reflect.Field;
import java.util.*;

public class Manager {
    private final HashMap<Class<?>, Object> sharedObjects = new HashMap<>();

    private final HashBasedTable<Class<?>, String, LinkedList<Class<?>>> sources = HashBasedTable.create();

    private final ArrayList<Class<?>> targets = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> T getOrCreateSharedObject(Class<T> type) throws IOCFrameworkException {
        try {
            if (!sharedObjects.containsKey(type))
                sharedObjects.put(type, type.newInstance());
            return (T) sharedObjects.get(type);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IOCFrameworkException("Cannot instantiate class " + type.getName());
        }
    }

    public void register(Class<?> type) {
        if (type.getAnnotation(Deprecated.class) == null) {
            final InjectSource annotation = AnnotationUtils.findAnnotation(InjectSource.class, type);
            if (annotation != null) {
                registerSource(type, annotation.name(), type);
                for (Class<?> inf : type.getInterfaces())
                    registerSource(inf, annotation.name(), type);
            }
            if (Arrays.stream(type.getDeclaredFields()).anyMatch(f -> f.getAnnotation(InjectTarget.class) != null))
                targets.add(type);
        }
    }

    public void inject() throws IOCFrameworkException {
        for (Class<?> target : targets) {
            final Object instance = getOrCreateSharedObject(target);
            for (Field field : target.getDeclaredFields()) {
                final InjectTarget annotation = field.getAnnotation(InjectTarget.class);
                if (annotation != null) {
                    final LinkedList<Class<?>> dependencies;
                    if (sources.contains(field.getType(), field.getName())) {
                        // 1st priority
                        // Search for sources matching both type and name.
                        dependencies = sources.get(field.getType(), field.getName());
                    } else {
                        // 2nd priority
                        // Search for sources matching the corresponding key specified by policy.
                        dependencies = new LinkedList<>();
                        (annotation.policy() == InjectTarget.Policy.BY_TYPE
                                ? sources.row(field.getType())
                                : sources.column(field.getName())).values().forEach(dependencies::addAll);
                    }
                    if (dependencies.size() < 1)
                        throw new IOCFrameworkException("No matching source of " + field.toGenericString());
                    if (dependencies.size() > 1)
                        throw new IOCFrameworkException("Too many matching source of " + field.toGenericString());
                    try {
                        field.setAccessible(true);
                        field.set(instance, getOrCreateSharedObject(dependencies.getFirst()));
                    } catch (IllegalAccessException e) {
                        throw new IOCFrameworkException("Cannot inject into " + field.toGenericString());
                    }
                }
            }
        }
    }

    public void scan(String pkg) throws IOCFrameworkException {
        final HashSet<Class<?>> classes = new HashSet<>();
        classes.addAll(new DirScanner().scan(pkg));
        classes.addAll(new JarScanner().scan(pkg));
        for (Class<?> aClass : classes)
            register(aClass);
    }

    private void registerSource(Class<?> type, String name, Class<?> source) {
        if (!sources.contains(type, name))
            sources.put(type, name, new LinkedList<>(Collections.singletonList(source)));
        else
            sources.get(type, name).addLast(source);
    }

}
