package com.hronous;

import com.hronous.services.ClassFinder;

public class Main {
    public static void main(String[] args) {
        ClassFinder finder = new ClassFinder();
       // finder.findClasses("com.hronous.services", Table.class);
       // finder.getClasses("com.hronous").forEach(System.out::println);
        finder.getClasses("com.hronous");
    }
}