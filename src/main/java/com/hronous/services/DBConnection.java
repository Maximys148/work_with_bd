package com.hronous.services;

import com.hronous.annotations.Service;
import com.hronous.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DBConnection {
    private static Map<String, Connection> connections = new HashMap<>();  //"jdbc:postgresql://127.0.0.1:8090/library?user=postgres&password=1234"

    public DBConnection() {
    }

    public void init(){
        System.out.println("Initializing DB connection");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection(String connectionString ) {
        try {
            Connection connections1 = connections.get(connectionString);
            if (connections1 == null){
                connections1 = DriverManager.getConnection(connectionString);
                connections.put(connectionString, connections1);
            }
            return connections1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement createStatement(String connectionString) throws SQLException {
        return getConnection(connectionString).createStatement();
    }

    /**
     * Закрыть все соединения, которые есть в нашей MAP
     */
    public void closeAllConnection(){
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            try {
                entry.getValue().close();
                connections.remove(entry.getKey());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Закрывать отдно соединение и убарить его из нашей MAP
     * @param connection
     */
    public void closeConnection(Connection connection){
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            try {
                if (entry.getValue() == connection){
                    entry.getValue().close();
                    connections.remove(entry.getKey());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
