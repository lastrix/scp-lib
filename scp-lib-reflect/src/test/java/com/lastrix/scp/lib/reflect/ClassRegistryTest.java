package com.lastrix.scp.lib.reflect;

import com.lastrix.scp.lib.reflect.ClassRegistryTest.TestAnnotation;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


@TestAnnotation
public class ClassRegistryTest {

    @TestAnnotation
    @Test
    public void testReflected() {
        var cl = ClassRegistry.getAnnotatedClasses(TestAnnotation.class);
        assertEquals(1, cl.size());
        assertSame(ClassRegistryTest.class, cl.iterator().next());

        var cml = ClassRegistry.getClassesWithAnnotatedMethods(TestAnnotation.class);
        assertEquals(1, cml.size());
        assertSame(ClassRegistryTest.class, cml.iterator().next());

        var ml = ClassRegistry.getAnnotatedMethods(TestAnnotation.class);
        assertEquals(1, ml.size());
        assertEquals("testReflected", ml.iterator().next().getName());
    }

    @Reflected
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    public @interface TestAnnotation {

    }
}
