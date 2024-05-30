package com.hronous.services;


import com.hronous.annotations.Table;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassFinder {

    private Map<String, Class<?>> dbTables = new HashMap<>();

    public void findClasses(String packageName, Class<?> clazz){
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replace('.', '/'));
        if (resourceAsStream == null){
            System.err.println("Resource not found " + packageName);
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        Set<? extends Class<?>> collect = bufferedReader
                .lines()
                .filter(it -> it.endsWith(".class"))
                .map(it -> getClass(it, packageName))
                .collect(Collectors.toSet());
        collect.forEach(System.out::println);
    }

    private Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName( packageName + "." +className.substring(0, className.lastIndexOf('.')) );
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found " + className);
            return null;
        }
    }

    public void getClasses(String packageName) {
       Reflections reflections = new Reflections(packageName , new SubTypesScanner(false));
        Set<Class<?>> subTypesOf = reflections.getSubTypesOf(Object.class);
        for (Class<?> clazz: subTypesOf) {
            if (clazz.isAnnotationPresent(Table.class)){
                Table annotation = clazz.getAnnotation(Table.class);
                dbTables.put(annotation.tableName(), clazz);
            }
        }
    }
}
