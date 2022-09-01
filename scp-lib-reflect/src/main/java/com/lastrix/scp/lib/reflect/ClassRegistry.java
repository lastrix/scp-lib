package com.lastrix.scp.lib.reflect;


import io.github.classgraph.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassRegistry {
    private static final ClassRegistry INSTANCE = new ClassPathScanner().scan();

    private final Map<Class<? extends Annotation>, Set<Class<?>>> classMap = new HashMap<>();
    private final Map<Class<? extends Annotation>, Map<Class<?>, Set<Method>>> methodMap = new HashMap<>();
    private final Map<Class<? extends Annotation>, Map<Class<?>, Set<Field>>> fieldMap = new HashMap<>();

    public static Set<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotationClass) {
        return INSTANCE.classMap.getOrDefault(annotationClass, Collections.emptySet());
    }

    public static Set<Class<?>> getClassesWithAnnotatedMethods(Class<? extends Annotation> annotationClass) {
        return INSTANCE.methodMap.getOrDefault(annotationClass, Collections.emptyMap()).keySet();
    }

    public static Set<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
        var result = new HashSet<Method>();
        INSTANCE.methodMap
                .getOrDefault(annotationClass, Collections.emptyMap())
                .forEach((k, v) -> result.addAll(v));
        return result;
    }

    public static Set<Class<?>> getClassesWithAnnotatedFields(Class<? extends Annotation> annotationClass) {
        return INSTANCE.fieldMap.getOrDefault(annotationClass, Collections.emptyMap()).keySet();
    }

    public static Set<Field> getAnnotatedFields(Class<? extends Annotation> annotationClass) {
        var result = new HashSet<Field>();
        INSTANCE.fieldMap
                .getOrDefault(annotationClass, Collections.emptyMap())
                .forEach((k, v) -> result.addAll(v));
        return result;
    }

    private void registerClass(Class<? extends Annotation> annotationClass, Class<?> aClass) {
        classMap.computeIfAbsent(annotationClass, ignored -> new HashSet<>()).add(aClass);
    }

    private void registerMethod(Class<? extends Annotation> annotationClass, Method method) {
        methodMap.computeIfAbsent(annotationClass, ignored -> new HashMap<>())
                .computeIfAbsent(method.getDeclaringClass(), ignored -> new HashSet<>())
                .add(method);
    }

    private void registerField(Class<? extends Annotation> annotationClass, Field field) {
        fieldMap.computeIfAbsent(annotationClass, ignored -> new HashMap<>())
                .computeIfAbsent(field.getDeclaringClass(), ignored -> new HashSet<>())
                .add(field);
    }

    private static final class ClassPathScanner extends ClassRegistryConfiguration {
        private final ClassRegistry registry = new ClassRegistry();

        private ClassRegistry scan() {
            ServiceLoader.load(ClassRegistryCustomizer.class).forEach(e -> e.customize(this));

            ClassGraph graph = new ClassGraph();
            graph.enableAllInfo().whitelistPackages("com.lastrix.scp");
            try (var result = graph.scan()) {
                getAnnotationMetaClasses().forEach(e -> processMetaAnnotations(e, result));

                getClassAnnotations().forEach(e -> processClassAnnotations(e, result));
                getMethodAnnotations().forEach(e -> processMethodAnnotations(e, result));
                getFieldAnnotations().forEach(e -> processFieldAnnotations(e, result));

            } catch (Exception e) {
                throw new IllegalStateException("Unable to initialize class registry", e);
            }

            return registry;
        }

        private void processFieldAnnotations(Class<? extends Annotation> aClass, ScanResult result) {
            for (ClassInfo classInfo : result.getClassesWithFieldAnnotation(aClass.getTypeName())) {
                for (FieldInfo info : classInfo.getDeclaredFieldInfo()) {
                    if (info.getAnnotationInfo(aClass.getTypeName()) != null) {
                        registry.registerField(aClass, info.loadClassAndGetField());
                    }
                }
            }
        }

        private void processMethodAnnotations(Class<? extends Annotation> aClass, ScanResult result) {
            for (ClassInfo classInfo : result.getClassesWithMethodAnnotation(aClass.getTypeName())) {
                for (MethodInfo info : classInfo.getDeclaredMethodAndConstructorInfo()) {
                    if (info.getAnnotationInfo(aClass.getTypeName()) != null) {
                        registry.registerMethod(aClass, info.loadClassAndGetMethod());
                    }
                }
            }
        }

        private void processClassAnnotations(Class<? extends Annotation> aClass, ScanResult result) {
            result.getClassesWithAnnotation(aClass.getTypeName())
                    .forEach(eClass -> registry.registerClass(aClass, eClass.loadClass()));
        }

        private void processMetaAnnotations(Class<? extends Annotation> aClass, ScanResult result) {
            result.getClassesWithAnnotation(aClass.getTypeName())
                    .forEach(this::processMetaAnnotation);
        }

        @SuppressWarnings("unchecked")
        private void processMetaAnnotation(ClassInfo classInfo) {
            if (classInfo.isAnnotation()) {
                var aClass = (Class<? extends Annotation>) classInfo.loadClass();
                var target = aClass.getAnnotation(Target.class);
                for (ElementType type : target.value()) {
                    switch (type) {
                        case TYPE:
                            addClassAnnotation(aClass);
                            break;

                        case FIELD:
                            addFieldAnnotation(aClass);
                            break;

                        case METHOD:
                        case CONSTRUCTOR:
                            addMethodAnnotation(aClass);
                            break;

                        case MODULE:
                        case PACKAGE:
                        case TYPE_USE:
                        case PARAMETER:
                        case LOCAL_VARIABLE:
                        case TYPE_PARAMETER:
                        case ANNOTATION_TYPE:
                        default:
                            throw new IllegalStateException("Not supported for target type: " + type);
                    }
                }

            }
        }

    }


}
