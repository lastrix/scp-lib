package com.lastrix.scp.lib.reflect;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@Getter
public class ClassRegistryConfiguration {
    private final Set<Class<? extends Annotation>> annotationMetaClasses = new HashSet<>();
    private final Set<Class<? extends Annotation>> classAnnotations = new HashSet<>();
    private final Set<Class<? extends Annotation>> methodAnnotations = new HashSet<>();
    private final Set<Class<? extends Annotation>> fieldAnnotations = new HashSet<>();

    {
        annotationMetaClasses.add(Reflected.class);
    }

    /**
     * Add annotation that marks other annotations as
     *
     * @param annotationClass
     */
    public void addAnnotationMetaClass(Class<? extends Annotation> annotationClass) {
        annotationMetaClasses.add(annotationClass);
    }

    public void addClassAnnotation(Class<? extends Annotation> annotationClass) {
        classAnnotations.add(annotationClass);
    }

    public void addMethodAnnotation(Class<? extends Annotation> annotationClass) {
        methodAnnotations.add(annotationClass);
    }

    public void addFieldAnnotation(Class<? extends Annotation> annotationClass) {
        fieldAnnotations.add(annotationClass);
    }
}
