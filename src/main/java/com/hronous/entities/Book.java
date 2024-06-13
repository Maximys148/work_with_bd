package com.hronous.entities;

import com.hronous.annotations.Column;
import com.hronous.annotations.Id;
import com.hronous.annotations.Table;

@Table( tableName = "book")
public class Book {

    @Id
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String author;

    private String secretField;


    public Book(String name, String author) {
        this.name = name;
        this.author = author;
        this.secretField = " ";
    }

    public Book() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
