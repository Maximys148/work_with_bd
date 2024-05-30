package com.hronous.services;


import com.hronous.annotations.InjectDBClasses;
import com.hronous.annotations.Service;

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

    public Map<String, Class<?>> getEntities() {
        return entities;
    }

    public EntityServices() {
        init();
    }

    /**
     * При инициализации ищет все классы аннотированные Table,
     * и создает таблицу в БД если ее там не было.
     */
    private void init(){
        /*
        try {
            DBConnection connections = new DBConnection();
            Statement statement = connections.getConnection("jdbc:postgresql://127.0.0.1:8090/Library?user=postgres&password=1234").createStatement();
            ResultSet tableSearch = statement.executeQuery("SELECT table_name\n" +
                    "FROM information_schema.tables\n" +
                    "WHERE table_schema = 'public'\n" +
                    "AND table_type = 'BASE TABLE';\n");
            Set<String> tableNames = new HashSet();
            while (tableSearch.next()){
                tableNames.add(tableSearch.getString(1));
                System.out.println(tableSearch.getString(1));
            }
            for (String tableName: tableNames) {
                boolean presence = Arrays.asList(entities).contains(tableSearch);
                if(!presence){
                    statement.executeQuery("CREATE TABLE '"+tableName+"' ();");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
    }
}
