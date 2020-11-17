package cn.endereye.framework.ioc;

import cn.endereye.framework.ioc.annotations.InjectSource;
import cn.endereye.framework.ioc.annotations.InjectTarget;

import java.lang.reflect.Field;
import java.util.HashMap;

public interface IOCPolicy {
    HashMap<InjectTarget.Policy, IOCPolicy> policies = new HashMap<InjectTarget.Policy, IOCPolicy>() {{
        put(InjectTarget.Policy.BY_TYPE, (annotation, source, target) -> {
            if (!target.getType().isAssignableFrom(source))
                return -1;
            return (target.getType() == source ? 2 : 0) + (target.getName().equals(annotation.value()) ? 1 : 0);
        });
        put(InjectTarget.Policy.BY_NAME, (annotation, source, target) -> {
            if (!target.getType().isAssignableFrom(source))
                return -1;
            return (target.getType() == source ? 1 : 0) + (target.getName().equals(annotation.value()) ? 2 : 0);
        });
        put(InjectTarget.Policy.BY_BOTH, (annotation, source, target) -> {
            if (!target.getName().equals(annotation.value()) || !target.getType().isAssignableFrom(source))
                return -1;
            return target.getType() == source ? 2 : 1;
        });
    }};

    int getPriority(InjectSource annotation, Class<?> source, Field target);
}
