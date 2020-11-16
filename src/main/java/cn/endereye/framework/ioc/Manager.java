package cn.endereye.framework.ioc;

import cn.endereye.framework.ioc.annotations.InjectSource;
import cn.endereye.framework.ioc.annotations.InjectTarget;
import cn.endereye.framework.ioc.scanner.ScannerOfDir;
import cn.endereye.framework.ioc.scanner.ScannerOfJar;
import cn.endereye.framework.utils.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Manager {
    private final HashMap<Class<?>, LinkedList<Object>> sourcesByType = new HashMap<>();

    private final HashMap<String, LinkedList<Object>> sourcesByName = new HashMap<>();

    private final ArrayList<Object> targets = new ArrayList<>();

    /**
     * Register a class to be an injection source or an injection target container, or maybe both.
     *
     * @param type Class to be registered.
     * @throws IOCFrameworkException Occurred when failed to create an instance of the class.
     */
    public void register(Class<?> type) throws IOCFrameworkException {
        Object instance = null;
        if (type.getAnnotation(Deprecated.class) == null) {
            final InjectSource source = AnnotationUtils.findAnnotation(InjectSource.class, type);
            if (source != null) {
                instance = createInstance(type);
                // Step 1. Add registry of its name.
                //         This step is skipped if the source does not have a explicitly assigned name.
                if (!"".equals(source.name())) {
                    sourcesByName.putIfAbsent(source.name(), new LinkedList<>());
                    sourcesByName.get(source.name()).addLast(instance);
                }
                // Step 2. Add registry for its own class.
                //         It is guaranteed that the place is not occupied since we can only register every class once.
                sourcesByType.putIfAbsent(type, new LinkedList<>());
                sourcesByType.get(type).addLast(instance);
                // Step 3. Add registries for implemented interfaces.
                for (Class<?> inf : type.getInterfaces()) {
                    sourcesByType.putIfAbsent(inf, new LinkedList<>());
                    sourcesByType.get(inf).addLast(instance);
                }
            }
        }
        for (Field field : type.getDeclaredFields()) {
            final InjectTarget target = field.getAnnotation(InjectTarget.class);
            if (target != null) {
                // Add this object into the target list.
                // Actual injection is performed later, in order to ensure every injection source is registered.
                if (instance == null)
                    instance = createInstance(type);
                targets.add(instance);
                break;
            }
        }
    }

    /**
     * Perform injection of all registered injection target containers.
     *
     * @throws IOCFrameworkException Occurred when the manager cannot find a proper source to inject, or failed to
     *                               inject the source object into the corresponding field.
     */
    public void inject() throws IOCFrameworkException {
        for (Object instance : targets) {
            for (Field field : instance.getClass().getDeclaredFields()) {
                final InjectTarget target = field.getAnnotation(InjectTarget.class);
                if (target != null) {
                    final Object sourceObject = target.policy() == InjectTarget.Policy.BY_TYPE
                            ? getSourceObject(field, sourcesByType, field.getType())
                            : getSourceObject(field, sourcesByName, field.getName());
                    try {
                        field.setAccessible(true);
                        field.set(instance, sourceObject);
                    } catch (IllegalAccessException e) {
                        throw new IOCFrameworkException("Cannot inject into " + field.getName());
                    }
                }
            }
        }
    }

    /**
     * Scan all classes under a specific package and register them all.
     *
     * @param pkg Package URL.
     */
    public void scan(String pkg) throws IOCFrameworkException {
        final HashSet<Class<?>> classes = new HashSet<>();
        classes.addAll(new ScannerOfDir().scan(pkg));
        classes.addAll(new ScannerOfJar().scan(pkg));
        for (Class<?> aClass : classes)
            register(aClass);
    }

    private Object createInstance(Class<?> type) throws IOCFrameworkException {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IOCFrameworkException("Cannot create instance of " + type.getName());
        }
    }

    private <K> Object getSourceObject(Field field, HashMap<K, LinkedList<Object>> map, K key) throws IOCFrameworkException {
        if (!map.containsKey(key))
            throw new IOCFrameworkException(field.toGenericString() + ": No sources found");
        final LinkedList<Object> list = map.get(key);
        if (list.size() > 1)
            throw new IOCFrameworkException(field.toGenericString() + ": Cannot decide which to inject.");
        return list.getFirst();
    }
}
