package com.hronous.services;


import com.hronous.annotations.*;
import com.hronous.entities.Book;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

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
                Field[] fields = entry.getValue().getDeclaredFields();
                StringBuilder SQLQuery = new StringBuilder();  //"CREATE TABLE public."+ entry.getKey()).append("(");
                for (Field field: fields) {
                    if(field.isAnnotationPresent(Column.class)){
                        if(SQLQuery.length() != 0){
                            SQLQuery.append(", ");
                        }
                        if(field.getType().equals(long.class)){
                            SQLQuery.append(field.getName()).append(" ");
                            SQLQuery.append("Integer");
                        }
                        if(field.getType().equals(String.class)){
                            SQLQuery.append(field.getName()).append(" ");
                            SQLQuery.append("CHARACTER VARYING(255)");
                        }
                        if (field.isAnnotationPresent(Id.class)){
                            SQLQuery.append(" PRIMARY KEY");
                        }
                        System.out.printf("%s %s \n",field.getName(), field.getType());
                    }
                }
                SQLQuery.insert(0, "CREATE TABLE public."+ entry.getKey() + "( ");
                SQLQuery.append(")");
                if(!presence){
                    statement.execute(String.valueOf(SQLQuery));
                    SQLQuery.reverse();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        save(new Book("Book1", "Max"));
    }
    private List<Object> findAll(Object object){
        List<Object> fieldsOfClazz = new ArrayList<>();
        Class<?> clazz = object.getClass();
        if(clazz.isAnnotationPresent(Table.class)){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field: fields) {
                if(field.isAnnotationPresent(Column.class)){
                    fieldsOfClazz.add(field.getName());
                }
            }
        }
        Collections.reverse(fieldsOfClazz);
        return  fieldsOfClazz;
    }

    public void save(Object object){
        if(object.getClass().isAnnotationPresent(Table.class)){
            StringBuilder SQLQuery = new StringBuilder();
            Class<?> clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Map<String, Object> columValue = Arrays.stream(fields).filter((field) -> field.isAnnotationPresent(Column.class)).collect(Collectors.toMap(field -> field.getName(), field -> {
                try {
                    field.setAccessible(true);
                    return field.get(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
            for (Object colum :findAll(object)){
                if(SQLQuery.length() != 0){
                    SQLQuery.append(", ");
                }
                SQLQuery.append(colum);
            }
            SQLQuery.append(") values (");
            for (String nameColum : columValue.keySet()){
                SQLQuery.append(columValue.get(nameColum));
                SQLQuery.append(", ");
            }
            SQLQuery.delete(SQLQuery.length() - 2, SQLQuery.length() - 1);
            SQLQuery.insert(0, "insert into public." + clazz.getSimpleName() +"(").append(")");
            System.out.println(" ");
        }else{
            System.out.println("Объект не является таблицей");
        }
    }
}
