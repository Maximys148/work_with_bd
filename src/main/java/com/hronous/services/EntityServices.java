package com.hronous.services;


import com.hronous.annotations.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис который обрабатывает наши Сущности БД (помеченные аннотацией Table)
 * Перебирать все классы существуюшие внашем проекте
 * Создавать таблицы если их нет в ДБ
 */
@Service
public class EntityServices {

    private Map<String, Class<?>> entities = new HashMap<>();

    public EntityServices() {
        init();
    }

    /**
     * При инициализации ищет все классы аннотированные Table,
     * и создает таблицу в БД если ее там не было.
     */
    private void init(){

    }
}
