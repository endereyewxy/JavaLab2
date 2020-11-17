package cn.endereye.framework.web.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestEndpoint {
    String value() default "";

    String method() default "GET";
}
