package com.example.feast.client.internal.model;

import com.example.feast.core.data.adapter.IRecipeRepo;
import com.example.feast.core.data.adapter.IUserRecipeRepo;
import com.example.feast.core.services.IImageService;
import com.example.feast.core.services.IRecipeService;
import com.example.feast.core.services.IUserRecipeService;
import com.example.feast.core.services.impl.ImageService;
import com.example.feast.core.services.impl.RecipeService;
import com.example.feast.core.services.impl.UserRecipeService;
import com.example.feast.data.external.firebase.repository.IImageRepo;
import com.example.feast.data.external.firebase.repository.ImageRepo;
import com.example.feast.data.external.firebase.repository.RecipeRepo;
import com.example.feast.data.external.firebase.repository.UserRecipeRepo;

public class BuildFactory {

    protected static Model BuildModel() {

        //Image Store
        IImageRepo imageRepo = new ImageRepo();
        IImageService imageService = new ImageService(imageRepo);

        //Recipe
        IRecipeRepo reRepo = new RecipeRepo();
        IRecipeService reServ = new RecipeService(reRepo);

        //UserRecipe
        IUserRecipeRepo urRepo = new UserRecipeRepo();
        IUserRecipeService urServ = new UserRecipeService(urRepo);

        Model model = new Model(urServ, reServ, imageService);
        return model;
    }
}
