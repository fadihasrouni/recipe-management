package com.teamrockstars.fadihasrouni.recipesmanagement.controller;

import com.teamrockstars.fadihasrouni.recipesmanagement.api.IngredientsApi;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.IngredientRequest;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.SimpleIngredientResponse;
import com.teamrockstars.fadihasrouni.recipesmanagement.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientController implements IngredientsApi {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Override
    public ResponseEntity<List<SimpleIngredientResponse>> getIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @Override
    public ResponseEntity<SimpleIngredientResponse> createIngredient(IngredientRequest ingredientRequest){
        return new ResponseEntity<>(ingredientService.createIngredient(ingredientRequest), HttpStatus.CREATED);
    }
}
