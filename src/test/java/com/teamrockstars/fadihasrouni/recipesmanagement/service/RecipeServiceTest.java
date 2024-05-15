package com.teamrockstars.fadihasrouni.recipesmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.teamrockstars.fadihasrouni.recipesmanagement.enums.DietaryType;
import com.teamrockstars.fadihasrouni.recipesmanagement.exception.customException.ResourceNotFoundException;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.*;
import com.teamrockstars.fadihasrouni.recipesmanagement.repository.RecipeIngredientRepository;
import com.teamrockstars.fadihasrouni.recipesmanagement.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {


    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private EntityManager entityManager;

    Recipe recipe;
    List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    List<Recipe> recipes = new ArrayList<>();
    List<Ingredient> ingredients = new ArrayList<>();
    List<Unit> units = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ingredients = new ArrayList<>();
        units = new ArrayList<>();

        // Preparing sample ingredients to be used in the recipe model
        ingredients.add(new Ingredient(1L, "Cheese", new IngredientType(1L, "Dairy")));
        ingredients.add(new Ingredient(2L, "Tomato sauce", new IngredientType(2L, "Others")));
        ingredients.add(new Ingredient(3L, "Tomato", new IngredientType(3L, "Vegetables")));
        ingredients.add(new Ingredient(4L, "Salt", new IngredientType(4L, "Herbs and spices")));
        ingredients.add(new Ingredient(5L, "Chicken", new IngredientType(5L, "Meat, chicken, sausage...")));

        // Preparing sample units to be used in the recipe model
        units.add(new Unit(1L, "Cup"));
        units.add(new Unit(2L, "Tea Spoon"));
        units.add(new Unit(3L, "Table Spoon"));
        units.add(new Unit(4L, "milligram"));
        units.add(new Unit(5L, "Gram"));
        units.add(new Unit(6L, "Kilogram"));

        // Prepare recipe ingredients
        recipeIngredients = new ArrayList<>();

        recipeIngredients.add(new RecipeIngredient(1L, recipe, ingredients.get(0), units.get(0), 3));
        recipeIngredients.add(new RecipeIngredient(2L, recipe, ingredients.get(1), units.get(0), 2));

        recipe = returnMockedRecipe(1L, "Pizza", "Best Pizza in town", DietaryType.VEGAN, 4, 60, "1- buy it frozen, 2- put it in the oven", recipeIngredients);
        recipes.add(recipe);

        Recipe recipe2 = returnMockedRecipe(2L, "Pasta", "Best pasta in town", DietaryType.VEGAN, 4, 15, "1- boil the pasta, 2- mix it and salt and tomato paste", recipeIngredients);

        List<RecipeIngredient> recipe2Ingredients = new ArrayList<>();
        recipe2Ingredients.add(new RecipeIngredient(3L, recipe2, ingredients.get(2), units.get(3), 4));
        recipe2Ingredients.add(new RecipeIngredient(4L, recipe2, ingredients.get(3), units.get(2), 10));

        recipes.add(recipe2);
    }

    @Test
    void testGetAllRecipesSuccess() {
        Mockito.when(recipeRepository.findAll(Mockito.any(Specification.class))).thenReturn(recipes);

        List<RecipeResponse> response = recipeService.getRecipes(4, "VEGAN", "pasta", null, null);

        assertEquals(response.size(), recipes.size());
        assertRecipe(recipes.get(0), response.get(0));
        assertRecipe(recipes.get(1), response.get(1));
    }

    @Test
    void testGetRecipeByIdSuccess() {
        Mockito.when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        RecipeResponse response = recipeService.getRecipeById(recipe.getId());

        assertRecipe(recipe, response);
    }

    @Test
    void testGetRecipeByIdResourceNotFoundFail() {
        Mockito.when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(recipe.getId()));
    }

    @Test
    void testCreateRecipeSuccess() {
        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);
        Mockito.when(recipeIngredientRepository.saveAll(Mockito.anyList())).thenReturn(recipeIngredients);
        lenient().when(entityManager.getReference(Recipe.class, 1)).thenReturn(recipe);
        lenient().when(entityManager.getReference(Ingredient.class, 1)).thenReturn(ingredients.get(0));
        lenient().when(entityManager.getReference(Ingredient.class, 2)).thenReturn(ingredients.get(1));
        lenient().when(entityManager.getReference(Unit.class, 0)).thenReturn(units.get(0));

        List<IngredientItem> ingredients = new ArrayList<>();
        ingredients.add(new IngredientItem(1L, 1L, 2F));
        ingredients.add(new IngredientItem(2L, 3L, 10F));

        RecipeResponse response = recipeService.createRecipe(
                createRecipeRequest("Pizza", "Best Pizza in town", RecipeRequest.DietaryTypeEnum.VEGAN, 4, 60, "1- buy it frozen, 2- put it in the oven", ingredients));

        assertRecipe(recipe, response);
    }

    @Test
    void testCreateRecipeRuntimeExceptionFailure() {
        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () ->
                recipeService.createRecipe(createRecipeRequest("Pizza", "Best Pizza in town", RecipeRequest.DietaryTypeEnum.VEGAN, 4, 60, "1- buy it frozen, 2- put it in the oven", new ArrayList<>()))
        );
    }

    @Test
    void testUpdateRecipeSuccess() {
        Mockito.when(recipeRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(recipe));
        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);
        Mockito.when(recipeIngredientRepository.saveAll(Mockito.anyList())).thenReturn(recipeIngredients);

        lenient().when(entityManager.getReference(Recipe.class, 1)).thenReturn(recipe);
        lenient().when(entityManager.getReference(Ingredient.class, 1)).thenReturn(ingredients.get(0));
        lenient().when(entityManager.getReference(Ingredient.class, 2)).thenReturn(ingredients.get(1));
        lenient().when(entityManager.getReference(Unit.class, 0)).thenReturn(units.get(0));

        List<IngredientItem> ingredients = new ArrayList<>();
        ingredients.add(new IngredientItem(1L, 1L, 2F));
        ingredients.add(new IngredientItem(2L, 3L, 10F));

        RecipeResponse response = recipeService.updateRecipe(1L,
                createRecipeRequest("Pizza update", "Best Pizza in town update", RecipeRequest.DietaryTypeEnum.VEGAN, 4, 60, "1- buy it frozen, 2- put it in the oven", ingredients));

        verify(recipeIngredientRepository, times(1)).deleteAllInBatch(anyList());
        assertRecipe(recipe, response);
    }

    @Test
    void testUpdateRecipeNotFoundFailure() {
        Mockito.when(recipeRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.updateRecipe(1L,
                createRecipeRequest("Pizza update", "Best Pizza in town update", RecipeRequest.DietaryTypeEnum.VEGAN, 4, 60, "1- buy it frozen, 2- put it in the oven", new ArrayList<>())));
    }

    @Test
    void testDeleteRecipeSuccess() {
        Mockito.when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        recipeService.deleteRecipe(1L);
        verify(recipeRepository, times(1)).delete(any(Recipe.class));
    }

    @Test
    void testDeleteRecipeFailure() {
        Mockito.when(recipeRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.deleteRecipe(1L));
    }

    /**
     * Asserts that the recipe model field values and correctly mapped to the recipe response
     *
     * @param expectedRecipe
     * @param actualRecipe
     */
    private void assertRecipe(Recipe expectedRecipe, RecipeResponse actualRecipe) {
        assertEquals(expectedRecipe.getId(), actualRecipe.getId());
        assertEquals(expectedRecipe.getName(), actualRecipe.getName());
        assertEquals(expectedRecipe.getDescription(), actualRecipe.getDescription());
        assertEquals(expectedRecipe.getDietaryType().toString(), actualRecipe.getDietaryType());
        assertEquals(expectedRecipe.getNumberOfServings(), actualRecipe.getNumberOfServings());
        assertEquals(expectedRecipe.getPrepTimeMinutes(), actualRecipe.getPrepTimeMinutes());
        assertEquals(expectedRecipe.getInstructions(), actualRecipe.getInstructions());

        assertEquals(expectedRecipe.getRecipeIngredients().size(), actualRecipe.getIngredients().size());
        for (int i = 0; i < expectedRecipe.getRecipeIngredients().size(); i++) {
            assertEquals(expectedRecipe.getRecipeIngredients().get(i).getIngredient().getName(), actualRecipe.getIngredients().get(i).getIngredient());
        }

        assertEquals(expectedRecipe.getRecipeIngredients().size(), actualRecipe.getIngredients().size());
        for (int i = 0; i < expectedRecipe.getRecipeIngredients().size(); i++) {
            assertEquals(expectedRecipe.getRecipeIngredients().get(i).getUnit().getName(), actualRecipe.getIngredients().get(i).getUnit());
        }

    }

    private RecipeRequest createRecipeRequest(String name, String description, RecipeRequest.DietaryTypeEnum dietaryType,
                                              int numberOfServings, int prepTimeMinutes, String instructions, List<IngredientItem> recipeIngredients) {

        RecipeRequest recipeRequest = new RecipeRequest();

        recipeRequest.setName(name);
        recipeRequest.setDescription(description);
        recipeRequest.setDietaryType(dietaryType);
        recipeRequest.setNumberOfServings(numberOfServings);
        recipeRequest.setPrepTimeMinutes(prepTimeMinutes);
        recipeRequest.setInstructions(instructions);
        recipeRequest.setIngredients(recipeIngredients);

        return recipeRequest;
    }

    /**
     * Return a dynamic mocked recipe model
     *
     * @param id
     * @param name
     * @param description
     * @param dietaryType
     * @param numberOfServings
     * @param prepTimeMinutes
     * @param instructions
     * @param recipeIngredients
     * @return
     */
    private Recipe returnMockedRecipe(Long id, String name, String description, DietaryType dietaryType,
                                      int numberOfServings, int prepTimeMinutes, String instructions, List<RecipeIngredient> recipeIngredients) {
        Recipe recipe = new Recipe();

        recipe.setId(id);
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setDietaryType(dietaryType);
        recipe.setNumberOfServings(numberOfServings);
        recipe.setPrepTimeMinutes(prepTimeMinutes);
        recipe.setInstructions(instructions);
        recipe.setRecipeIngredients(recipeIngredients);
        return recipe;
    }
}
