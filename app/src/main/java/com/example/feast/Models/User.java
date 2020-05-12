package com.example.feast.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String _userName;
    private String _password;
    private String _id;
    private ArrayList<UserRecipes> userRecipes;

    public User(String userName, String password, String id) {
        this._userName = userName;
        this._password = password;
        this._id = id;
    }

    public String getUserName() {
        return _userName;
    }

    public String getId() {
        return _id;
    }

    public ArrayList<UserRecipes> getUserRecipes() {
        return userRecipes;
    }

}
