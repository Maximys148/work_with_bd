package com.hronous;

import com.hronous.services.ClassFinder;

public class Main {
    public static void main(String[] args) {
        ClassFinder finder = new ClassFinder();
        finder.init("com.hronous");
    }
}