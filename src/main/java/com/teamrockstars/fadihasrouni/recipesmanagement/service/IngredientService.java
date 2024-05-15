package com.teamrockstars.fadihasrouni.recipesmanagement.service;

import com.teamrockstars.fadihasrouni.recipesmanagement.model.Ingredient;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.IngredientRequest;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.SimpleIngredientResponse;
import com.teamrockstars.fadihasrouni.recipesmanagement.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Get all available ingredients
     *
     * @return
     */
    public List<SimpleIngredientResponse> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return convertToSimpleIngredientResponseList(ingredients);
    }

    /**
     * create an ingredient and returns it
     *
     * @param ingredientRequest
     * @return
     */
    public SimpleIngredientResponse createIngredient(IngredientRequest ingredientRequest) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientRequest.getName());

        try {
            ingredient = ingredientRepository.save(ingredient);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Ingredient with name " + ingredientRequest.getName() + " already exists");
        }

        return convertToSimpleIngredientResponse(ingredient);
    }

    /**
     * Converts from an ingredient (database model) to simple ingredient rest response
     *
     * @param ingredient
     * @return
     */
    private SimpleIngredientResponse convertToSimpleIngredientResponse(Ingredient ingredient) {
        SimpleIngredientResponse response = new SimpleIngredientResponse();
        response.setId(ingredient.getId());
        response.setName(ingredient.getName());

        return response;
    }
    /**
     * Converts from a list of ingredients (database model) to simple ingredient rest response list
     *
     * @param ingredients
     * @return
     */
    private List<SimpleIngredientResponse> convertToSimpleIngredientResponseList(List<Ingredient> ingredients) {
        List<SimpleIngredientResponse> simpleIngredientList = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            simpleIngredientList.add(convertToSimpleIngredientResponse(ingredient));
        }
        return simpleIngredientList;
    }
}
