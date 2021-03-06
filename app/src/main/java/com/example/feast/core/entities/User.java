package com.example.feast.core.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String _userName;
    private String _id;
    private String _email;
    private ArrayList<UserRecipe> userRecipes;

    public User(String userName, String id, String email) {
        this._userName = userName;
        this._email = email;
        this._id = id;
    }

    public String getUserName() {
        return _userName;
    }

    public String getId() {
        return _id;
    }

    public String get_email() {
        return _email;
    }

    public ArrayList<UserRecipe> getUserRecipes() {
        return userRecipes;
    }

}
