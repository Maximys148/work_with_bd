package com.hronous;

import com.hronous.entities.Book;
import com.hronous.services.DBConnection;
import com.hronous.services.EntityServices;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Book book = new Book("Маугли", "Олег");
        EntityServices services = new EntityServices();
    }
}