package com.teamrockstars.fadihasrouni.recipesmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.IngredientItem;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.RecipeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RecipeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndRetrieveRecipe() throws Exception {
        // Create a new Recipe
        RecipeRequest newRecipe = createRecipeRequest("Test Recipe", "A test recipe", RecipeRequest.DietaryTypeEnum.VEGAN,
                4, 30, "Test instructions", 100f, 1L, 1L);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRecipe)))
                .andExpect(status().isCreated());

        // Retrieve the newly created Recipe
        assertExpectResponse(mockMvc.perform(get("/recipes/1")));
    }

    /**
     * Create recipe request
     *
     * @param name
     * @param description
     * @param dietaryType
     * @param numberOfServings
     * @param prepTimeMinutes
     * @param instructions
     * @param ingredientQuantity
     * @param unitId
     * @param ingredientId
     * @return
     */
    public RecipeRequest createRecipeRequest(String name, String description, RecipeRequest.DietaryTypeEnum dietaryType,
                                             int numberOfServings, int prepTimeMinutes, String instructions,
                                             float ingredientQuantity, Long unitId, Long ingredientId) {
        // Create a new Recipe
        RecipeRequest newRecipe = new RecipeRequest();
        newRecipe.setName(name);
        newRecipe.setDescription(description);
        newRecipe.setDietaryType(dietaryType);
        newRecipe.setNumberOfServings(numberOfServings);
        newRecipe.setPrepTimeMinutes(prepTimeMinutes);
        newRecipe.setInstructions(instructions);

        IngredientItem ingredient = new IngredientItem();
        ingredient.setQuantity(ingredientQuantity);
        ingredient.setUnitId(unitId);
        ingredient.setIngredientId(ingredientId);

        newRecipe.setIngredients(Arrays.asList(ingredient));

        return newRecipe;
    }

    /**
     * Assert that the response is correct
     *
     * @param resultActions
     * @throws Exception
     */
    private void assertExpectResponse(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Recipe"))
                .andExpect(jsonPath("$.description").value("A test recipe"))
                .andExpect(jsonPath("$.dietaryType").value("VEGAN"))
                .andExpect(jsonPath("$.numberOfServings").value(4))
                .andExpect(jsonPath("$.prepTimeMinutes").value(30))
                .andExpect(jsonPath("$.instructions").value("Test instructions"))
                .andExpect(jsonPath("$.ingredients[0].quantity").value(100f))
                .andExpect(jsonPath("$.ingredients[0].unit").value("Cup"))
                .andExpect(jsonPath("$.ingredients[0].ingredient").value("Cucumber"));
    }
}
