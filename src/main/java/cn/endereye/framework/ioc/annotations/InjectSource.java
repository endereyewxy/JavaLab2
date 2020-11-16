package cn.endereye.framework.ioc.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Inherited
@Documented
public @interface InjectSource {
    String name() default "";
}
