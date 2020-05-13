package com.example.feast.core.entities;

import java.io.Serializable;

public class Ingredient implements Serializable {

    private String name;
    private long amount;

    public Ingredient(String name, Long amount) {
        this.amount = amount;
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
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
        return name + ", " + amount + " Gram";

    }
}
