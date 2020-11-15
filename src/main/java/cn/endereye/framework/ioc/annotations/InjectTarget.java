package cn.endereye.framework.ioc.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Inherited
@Documented
public @interface InjectTarget {
    enum Policy {
        BY_TYPE,
        BY_NAME,
    }

    Policy policy() default Policy.BY_TYPE;
}
