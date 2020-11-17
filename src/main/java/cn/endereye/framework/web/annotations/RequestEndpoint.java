package cn.endereye.framework.web.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestEndpoint {
    String path() default "";

    String method() default "GET";
}
