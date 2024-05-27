package com.hronous.services;

import com.hronous.annotations.Service;
import com.hronous.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DBConnection {

    private int connectionCount = 0;//Количество соединени в БД
    private int maxCountConnection = 5;//Макисмальное количество Подключений к БД
    private Map<String, List<Connection>> connections = new HashMap<>();
    private Connection connection;//"jdbc:postgresql://127.0.0.1:8090/Library?user=postgres&password=1234"

    public DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(String connectionString ) {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            List<Connection> connections1 = connections.get(connectionString);
            if (connections1 == null){
                connections1 = new ArrayList<>();
                connections.put(connectionString, connections1);
            }else{
                if (connections1.size() > maxCountConnection  )
                    throw new DBConnectionException(
                            String.format("Превишено количество подключений - [%d]", maxCountConnection));
            }
            connections1.add(connection);
            connectionCount++;
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Закрыть все соединения, которые есть в нашей MAP
     */
    public void closeAllConnection(){

    }

    /**
     * Закрывать отдно соединение и убарить его из нашей MAP
     * @param connection
     */
    public void closeConnection(Connection connection){

    }
}
