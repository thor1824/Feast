package com.example.feast.Models;

public class Ingredient {

    private String name;
    private int amount;

    public Ingredient(String name, int amount) {
        this.amount = amount;
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
