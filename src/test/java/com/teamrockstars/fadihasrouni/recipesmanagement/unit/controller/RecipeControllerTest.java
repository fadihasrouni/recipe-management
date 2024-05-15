package com.teamrockstars.fadihasrouni.recipesmanagement.unit.controller;

import com.teamrockstars.fadihasrouni.recipesmanagement.controller.RecipeController;
import com.teamrockstars.fadihasrouni.recipesmanagement.exception.customException.ResourceNotFoundException;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.*;
import com.teamrockstars.fadihasrouni.recipesmanagement.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecipeController.class)
public class RecipeControllerTest {

    private final String baseUrl = "/recipes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void getRecipeTestSuccess() throws Exception {
        when(recipeService.getRecipeById(1L)).thenReturn(mockRecipeResponse(1L, "Pasta", "Delicious pasta recipe", RecipeRequest.DietaryTypeEnum.VEGAN, 4, 60, "Boil pasta. Add sauce."));

        assertExpectResponse(mockMvc.perform(get(baseUrl +"/{id}", 1L)));
    }

    @Test
    public void testGetRecipeByIdFailNotFound() throws Exception {
        when(recipeService.getRecipeById(1L)).thenThrow(new ResourceNotFoundException("Couldn't find recipe with id"));

        mockMvc.perform(get(baseUrl +"/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRecipeByIdFailInvalidId() throws Exception {
        mockMvc.perform(get(baseUrl +"/{id}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRecipeByIdSuccess () throws Exception {
        mockMvc.perform(delete(baseUrl +"/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteRecipeByIdFailureNotFound () throws Exception {
        doThrow(new ResourceNotFoundException("Couldn't find recipe with id")).when(recipeService).deleteRecipe(1L);

        mockMvc.perform(delete( baseUrl +"/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    /**
     * Assert that the response is correct
     *
     * @param resultActions
     * @throws Exception
     */
    private void assertExpectResponse(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pasta"))
                .andExpect(jsonPath("$.description").value("Delicious pasta recipe"))
                .andExpect(jsonPath("$.dietaryType").value("VEGAN"))
                .andExpect(jsonPath("$.numberOfServings").value(4))
                .andExpect(jsonPath("$.prepTimeMinutes").value(60))
                .andExpect(jsonPath("$.instructions").value("Boil pasta. Add sauce."))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients[0].quantity").value(1F))
                .andExpect(jsonPath("$.ingredients[0].unit").value("Cup"))
                .andExpect(jsonPath("$.ingredients[0].ingredient").value("Pasta"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value(2F))
                .andExpect(jsonPath("$.ingredients[1].unit").value("Kilogram"))
                .andExpect(jsonPath("$.ingredients[1].ingredient").value("Tomato"));
    }

    /**
     * Mock the response returned by the service layer
     *
     * @param id
     * @param name
     * @param description
     * @param dietaryType
     * @param numberOfServings
     * @param preTimeMinutes
     * @param instructions
     * @return
     * @throws Exception
     */
    private RecipeResponse mockRecipeResponse(Long id, String name, String description, RecipeRequest.DietaryTypeEnum dietaryType, int numberOfServings, int preTimeMinutes, String instructions)
            throws Exception {
        RecipeResponse recipeResponse = new RecipeResponse();

        recipeResponse.setId(id);
        recipeResponse.setName(name);
        recipeResponse.setDescription(description);
        recipeResponse.setDietaryType(dietaryType.toString());
        recipeResponse.setNumberOfServings(numberOfServings);
        recipeResponse.setPrepTimeMinutes(preTimeMinutes);
        recipeResponse.setInstructions(instructions);
        recipeResponse.setIngredients(createRandomIngredients());

        return recipeResponse;
    }

    /**
     * Creates random ingredients response list
     *
     * @return
     * @throws Exception
     */
    private List<IngredientResponse> createRandomIngredients() throws Exception {
        List<IngredientResponse> ingredients = new ArrayList<>();

        IngredientResponse ingredient = new IngredientResponse();

        ingredient.setIngredient("Pasta");
        ingredient.setQuantity(1F);
        ingredient.setUnit("Cup");

        ingredients.add(ingredient);

        ingredient = new IngredientResponse();

        ingredient.setIngredient("Tomato");
        ingredient.setQuantity(2F);
        ingredient.setUnit("Kilogram");

        ingredients.add(ingredient);

        return ingredients;
    }
}
