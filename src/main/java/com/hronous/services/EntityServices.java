package com.hronous.services;


import com.hronous.annotations.InjectDBClasses;
import com.hronous.annotations.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Сервис который обрабатывает наши Сущности БД (помеченные аннотацией Table)
 * Перебирать все классы существуюшие внашем проекте
 * Создавать таблицы если их нет в ДБ
 */
@Service
public class EntityServices {

    @InjectDBClasses
    private Map<String, Class<?>> entities = new HashMap<>();

    private String ddd;
    private DBConnection dbConnection = null;

    public EntityServices() {
    }

    /**
     * При инициализации ищет все классы аннотированные Table,
     * и создает таблицу в БД если ее там не было.
     */
    public void init(){
        System.out.println("Initializing EntityServices");
        try {
            Statement statement = dbConnection.createStatement("jdbc:postgresql://127.0.0.1:5432/library?user=postgres&password=Max.2005");
            ResultSet tableSearch = statement.executeQuery("SELECT table_name\n" +
                    "FROM information_schema.tables\n" +
                    "WHERE table_schema = 'public'\n" +
                    "AND table_type = 'BASE TABLE';\n");
            Set<String> tableNames = new HashSet();
            while (tableSearch.next()){
                tableNames.add(tableSearch.getString(1));
                System.out.println(tableSearch.getString(1));
            }
            for (Map.Entry<String, Class<?>> entry: entities.entrySet()) {
                boolean presence = tableNames.contains(entry.getKey());
                if(!presence){
                    statement.execute("CREATE TABLE public."+entry.getKey() +
                            "(Id SERIAL PRIMARY KEY)");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
