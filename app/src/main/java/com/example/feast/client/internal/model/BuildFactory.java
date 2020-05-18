package com.example.feast.client.internal.model;

import com.example.feast.core.client.adapter.IAuthService;
import com.example.feast.core.client.adapter.IImageService;
import com.example.feast.core.client.adapter.IRecipeService;
import com.example.feast.core.client.adapter.IUserRecipeService;
import com.example.feast.core.client.adapter.impl.AuthService;
import com.example.feast.core.client.adapter.impl.ImageService;
import com.example.feast.core.client.adapter.impl.RecipeService;
import com.example.feast.core.client.adapter.impl.UserRecipeService;
import com.example.feast.core.data.adapter.IAuthRepo;
import com.example.feast.core.data.adapter.IImageRepo;
import com.example.feast.core.data.adapter.IRecipeRepo;
import com.example.feast.core.data.adapter.IUserRecipeRepo;
import com.example.feast.data.external.firebase.repository.AuthRepo;
import com.example.feast.data.external.firebase.repository.ImageRepo;
import com.example.feast.data.external.firebase.repository.RecipeRepo;
import com.example.feast.data.external.firebase.repository.UserRecipeRepo;

public class BuildFactory {

    /**
     * Builds the model with interfaces
     *
     * @return
     */
    protected static Model BuildModel() {

        //Auth
        IAuthRepo authRepo = new AuthRepo();
        IAuthService authService = new AuthService(authRepo);

        //Image Store
        IImageRepo imageRepo = new ImageRepo();
        IImageService imageService = new ImageService(imageRepo);

        //Recipe
        IRecipeRepo reRepo = new RecipeRepo();
        IRecipeService reServ = new RecipeService(reRepo);

        //UserRecipe
        IUserRecipeRepo urRepo = new UserRecipeRepo();
        IUserRecipeService urServ = new UserRecipeService(urRepo);

        Model model = new Model(urServ, reServ, imageService, authService);
        return model;
    }
}
