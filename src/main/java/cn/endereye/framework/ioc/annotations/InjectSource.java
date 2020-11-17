package cn.endereye.framework.ioc.annotations;

import java.lang.annotation.*;

/**
 * Indicating that an annotated class is a candidate injection source.
 * <p>
 * Because the framework uses singleton pattern to organize its components, an injection source can only have at most
 * one instance regardless of how many times it is required. See {@link cn.endereye.framework.ioc.IOCManager} for more
 * information.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface InjectSource {
    /**
     * Represents the name of this injection source.
     */
    String value() default "";
}
