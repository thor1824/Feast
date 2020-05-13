package com.example.feast.core.entities;

import java.io.Serializable;
import java.util.ArrayList;

public interface IRecipe extends Serializable {
    String getId();

    void setId(String id);

    ArrayList<Ingredient> getIngredients();

    void setIngredients(ArrayList<Ingredient> ingredients);

    long getEstimatedTime();

    void setEstimatedTime(long estimatedTime);

    String getName();

    void setName(String name);

    String getImageUrl();

    void setImageUrl(String imageUrl);
}
