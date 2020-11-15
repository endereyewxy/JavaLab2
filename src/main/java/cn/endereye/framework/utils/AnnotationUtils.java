package cn.endereye.framework.utils;

import java.lang.annotation.Annotation;
import java.util.HashSet;

/**
 * Provide utilities for accessing annotations. Inspired by the same class from Spring Framework.
 */
public class AnnotationUtils {

    /**
     * Find a certain annotation from some class, including annotations decorating the class directly, and those who
     * decorate annotations decorating this class, and so on.
     *
     * @param <A>      Type of the annotation.
     * @param annClass Annotation class.
     * @param srcClass Decorated class.
     * @return Null if the annotation is not found, otherwise the annotation object.
     */
    public static <A extends Annotation> A findAnnotation(Class<A> annClass, Class<?> srcClass) {
        return findAnnotation(annClass, srcClass, new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A findAnnotation(Class<A> annClass,
                                                           Class<?> srcClass,
                                                           HashSet<Annotation> visited) {
        final Annotation[] annotations = srcClass.getDeclaredAnnotations();
        for (Annotation ann : annotations) {
            if (ann.annotationType() == annClass)
                return (A) ann;
        }
        for (Annotation ann : annotations) {
            // Do not consider annotations from the default java.lang.annotation package.
            if (!ann.getClass().getName().startsWith("java.lang.annotation") && visited.add(ann)) {
                final A result = findAnnotation(annClass, ann.annotationType(), visited);
                if (result != null)
                    return result;
            }
        }
        // TODO Consider interfaces and super classes.
        return null;
    }
}
