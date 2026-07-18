package com.example.librarymanagement;

import java.io.Serializable;

/**
 * Model class representing a Book entity in the Library Management System.
 */
public class Book implements Serializable {
    private long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private int publicationYear;
    private int quantity;
    private int availableQuantity;
    private String description;

    public Book() {
    }

    public Book(long id, String title, String author, String isbn, String category,
                String publisher, int publicationYear, int quantity, int availableQuantity,
                String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.description = description;
    }

    public Book(String title, String author, String isbn, String category,
                String publisher, int publicationYear, int quantity, int availableQuantity,
                String description) {
        this(-1, title, author, isbn, category, publisher, publicationYear, quantity, availableQuantity, description);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return title + " (Avail: " + availableQuantity + "/" + quantity + ")";
    }
}
