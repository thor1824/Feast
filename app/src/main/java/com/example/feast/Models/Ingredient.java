package com.example.feast.Models;

import java.io.Serializable;

public class Ingredient implements Serializable {

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

    @Override
    public String toString() {
        return name + ", " + Integer.toString(amount) + " Gram";

    }
}
