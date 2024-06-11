package com.hronous.services;


import com.hronous.annotations.InjectDBClasses;
import com.hronous.annotations.Service;
import com.hronous.annotations.Table;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ClassFinder {


    private Map<String, Class<?>> entities = new HashMap<>();
    private Map<String, Object> servises = new HashMap<>();

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

    public void init(String packageName) {
        Map<Object, Field> entitiesWithBDConnection = new HashMap<>();
        Reflections reflections = new Reflections(packageName , new SubTypesScanner(false));
        Set<Class<?>> subTypesOf = reflections.getSubTypesOf(Object.class);
        for (Class<?> clazz: subTypesOf) {
            if (clazz.isAnnotationPresent(Table.class)){
                Table annotation = clazz.getAnnotation(Table.class);
                entities.put(annotation.tableName(), clazz);
            }
            if (clazz.isAnnotationPresent(Service.class)){
                try {
                    Object o = clazz.getConstructor().newInstance();
                    servises.put(clazz.getName(), o);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                //Загружаем EntityClass
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if(declaredField.isAnnotationPresent(InjectDBClasses.class)){
                        declaredField.setAccessible(true);
                        try {
                            declaredField.set(servises.get(clazz.getName()), entities);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if(declaredField.getType().equals(DBConnection.class)){
                        entitiesWithBDConnection.put(servises.get(clazz.getName()), declaredField);
                    }
                }
                //Запускаем метод init();
            }
        }
        entitiesWithBDConnection.forEach((object, field)->{
            field.setAccessible(true);
            try {
                field.set(object, servises.get(DBConnection.class.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        servises.forEach((key, value)->{
            try {
                Method methodInit = value.getClass().getDeclaredMethod("init");
                if(methodInit != null){
                    methodInit.setAccessible(true);
                    methodInit.invoke(value, null);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Init method ClassFinder");

    }
}
