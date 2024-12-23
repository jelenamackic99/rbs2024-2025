package com.zuehlke.securesoftwaredevelopment.domain;

import java.util.List;

public class NewBook {

    private String name;

    private String author;

    private String description;

    private double price;

    private List<Integer> tags;

    public NewBook(String name, String description, String author, double price, List<Integer> tags) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.price = price;
        this.tags = tags;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }
}
