package com.teamrockstars.fadihasrouni.recipesmanagement.controller;

import com.teamrockstars.fadihasrouni.recipesmanagement.api.RecipesApi;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.RecipeRequest;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.RecipeResponse;
import com.teamrockstars.fadihasrouni.recipesmanagement.service.RecipeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecipeController implements RecipesApi {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // TODO
    // Get All recipes with filtering

    @Override
    public ResponseEntity<List<RecipeResponse>> getRecipes(
                @Valid @RequestParam(value = "numberOfServings", required = false) Integer numberOfServings,
                @Valid @RequestParam(value = "dietaryType", required = false) String dietaryType,
                @Valid @RequestParam(value = "instructionsInclude", required = false) String instructionsInclude,
                @Valid @RequestParam(value = "ingredientIncludes", required = false) String ingredientIncludes,
                @Valid @RequestParam(value = "ingredientExcludes", required = false) String ingredientExcludes
    ) {
        return ResponseEntity.ok(recipeService.getRecipes(numberOfServings, dietaryType, instructionsInclude, ingredientIncludes, ingredientExcludes));
    }

    @Override
    public ResponseEntity<RecipeResponse> getRecipeById(Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @Override
    public ResponseEntity<RecipeResponse> createRecipe(RecipeRequest recipeRequest) {
       return new ResponseEntity<>(recipeService.createRecipe(recipeRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<RecipeResponse> updateRecipeById(Long id, RecipeRequest recipeRequest) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeRequest));
    }

    @Override
    public ResponseEntity<Void> deleteRecipeById(Long id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
