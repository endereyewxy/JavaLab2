package cn.endereye.framework.ioc.annotations;

import java.lang.annotation.*;

/**
 * Indicating that an annotated field requires injection. The injection process will ignore access modifiers and/or
 * setter methods.
 * <p>
 * Because the framework uses singleton pattern to organize its components, if a class is both an injection source and
 * has fields to be injected, the instance where all the fields are injected is the same as the instance which will be
 * used as the injection source.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface InjectTarget {
    /**
     * Represents the policy for searching injection sources. Every policy requires the source's type to be assignable
     * to the field's type.
     */
    enum Policy {
        /**
         * Represents the policy that prefers type matching. Acceptable sources are listed below from higher priority to
         * lower ones:
         * <ul>
         *     <li>Both type and name exactly match.</li>
         *     <li>Only type exactly matches.</li>
         *     <li>Only name exactly matches.</li>
         * </ul>
         */
        BY_TYPE,
        /**
         * Represents the policy that prefers type matching. Acceptable sources are listed below from higher priority to
         * lower ones:
         * <ul>
         *     <li>Both type and name exactly match.</li>
         *     <li>Only name exactly matches.</li>
         *     <li>Only type exactly matches.</li>
         * </ul>
         */
        BY_NAME,
        /**
         * Represents the policy that prefers both matching. Acceptable sources are listed below from higher priority to
         * lower ones:
         * <ul>
         *     <li>Both type and name exactly match.</li>
         *     <li>Only name exactly matches.</li>
         * </ul>
         */
        BY_BOTH,
    }

    /**
     * Represents the policy for searching injection source for this field. See {@link Policy} for more information.
     */
    Policy policy() default Policy.BY_TYPE;
}
