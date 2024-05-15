package com.teamrockstars.fadihasrouni.recipesmanagement.service;

import com.teamrockstars.fadihasrouni.recipesmanagement.config.RecipeSpecification;
import com.teamrockstars.fadihasrouni.recipesmanagement.enums.DietaryType;
import com.teamrockstars.fadihasrouni.recipesmanagement.exception.customException.ResourceNotFoundException;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.*;
import com.teamrockstars.fadihasrouni.recipesmanagement.repository.RecipeIngredientRepository;
import com.teamrockstars.fadihasrouni.recipesmanagement.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;

    private final RecipeIngredientRepository recipeIngredientRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, RecipeIngredientRepository recipeIngredientRepository, EntityManager entityManager) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.entityManager = entityManager;
    }

    /**
     * Get a list of all recipes
     *
     * @return
     */
    public List<RecipeResponse> getRecipes(Integer numberOfServings, String dietaryType, String instructionsInclude,
                                           String ingredientIncludes, String ingredientExcludes) {
        List<Recipe> recipes = recipeRepository.findAll(
                Specification.where(RecipeSpecification.byNumberOfServings(numberOfServings))
                        .and(RecipeSpecification.byDietaryType(dietaryType))
                        .and(RecipeSpecification.byInstructionsInclude(instructionsInclude))
                        .and(RecipeSpecification.byIngredientIncludes(ingredientIncludes))
                        .and(RecipeSpecification.byIngredientExcludes(ingredientExcludes))
        );
        return convertToRecipeResponseList(recipes);
    }

    /**
     * Get Recipe by recipe id
     *
     * @param id
     * @return
     */
    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = findRecipeByIdOrThrow(id);

        return convertToRecipeResponse(recipe);
    }

    /**
     * Creates a recipe and save it in the database. Also creates all recipe items and save them
     * This method is transactional, if any operation failed it will be rolled back.
     *
     * @param recipeRequest
     * @return
     */
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest recipeRequest) {
        Recipe recipe = populateRecipeModel(recipeRequest, new Recipe());

        log.info("Creating a new recipe");
        recipe = recipeRepository.save(recipe);

        log.info("Creating recipe ingredient");
        recipe.setRecipeIngredients(
                recipeIngredientRepository.saveAll(populateRecipeIngredientList(recipeRequest.getIngredients(), recipe.getId())));

        return convertToRecipeResponse(recipe);
    }

    /**
     * Updates an existing recipe in the database. Also updates all recipe items and save them
     * This method is transactional, if any operation failed it will be rolled back.
     *
     * @param id
     * @param recipeRequest
     * @return
     */
    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest recipeRequest) {
        Recipe recipe = findRecipeByIdOrThrow(id);
        populateRecipeModel(recipeRequest, recipe);

        // Removing existing recipe ingredients to create new updated ingredients
        log.info("deleting recipe ingredients");
        if (recipe.getRecipeIngredients() != null && !recipe.getRecipeIngredients().isEmpty()) {
            recipeIngredientRepository.deleteAllInBatch(recipe.getRecipeIngredients());
        }

        log.info("updating recipe with id {}", recipe.getId());
        recipe = recipeRepository.save(recipe);

        log.info("adding updated recipe ingredients");
        recipe.setRecipeIngredients(
                recipeIngredientRepository.saveAll(populateRecipeIngredientList(recipeRequest.getIngredients(), recipe.getId())));

        return convertToRecipeResponse(recipe);
    }

    /**
     * Deletes the recipe from the database along with the recipe ingredients
     *
     * @param id
     */
    public void deleteRecipe(Long id) {
        Recipe recipe = findRecipeByIdOrThrow(id);
        recipeRepository.delete(recipe);
    }

    /**
     * Fetch a specific recipe from the database, if not found it will throw a resource not found exception
     *
     * @param id
     * @return
     */
    private Recipe findRecipeByIdOrThrow(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recipe with id " + id + " not found");
                    return  new ResourceNotFoundException("Couldn't find any recipe with the given id");
                });
    }

    /**
     * Fills in the recipe database model from the recipe request inputs
     *
     * @param recipeRequest
     * @return
     */
    private Recipe populateRecipeModel(RecipeRequest recipeRequest, Recipe recipe) {

        recipe.setName(recipeRequest.getName());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setDietaryType(DietaryType.valueOf(recipeRequest.getDietaryType().toString()));
        recipe.setNumberOfServings(recipeRequest.getNumberOfServings());
        recipe.setPrepTimeMinutes(recipeRequest.getPrepTimeMinutes());
        recipe.setInstructions(recipeRequest.getInstructions());

        return recipe;
    }

    /**
     * Fills a list of recipe ingredients (database model) from recipe ingredient items list request
     *
     * @param ingredientItems
     * @param recipeId
     * @return
     */
    private List<RecipeIngredient> populateRecipeIngredientList(List<IngredientItem> ingredientItems, Long recipeId) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (IngredientItem ingredient : ingredientItems) {
            recipeIngredients.add(populateRecipeIngredient(ingredient, recipeId));
        }

        return recipeIngredients;
    }

    /**
     * Fills in the recipe ingredients database model from the recipe ingredient items list request
     *
     * @param ingredientItem
     * @param recipeId
     * @return
     */
    private RecipeIngredient populateRecipeIngredient(IngredientItem ingredientItem, Long recipeId) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();

        recipeIngredient.setRecipe(entityManager.getReference(Recipe.class, recipeId));
        recipeIngredient.setIngredient(entityManager.getReference(Ingredient.class, ingredientItem.getIngredientId()));
        recipeIngredient.setUnit(entityManager.getReference(Unit.class, ingredientItem.getUnitId()));
        recipeIngredient.setQuantity(ingredientItem.getQuantity());

        return recipeIngredient;
    }

    /**
     * Converts from a recipe model (database representation) to a proper recipe response
     *
     * @param recipe
     * @return
     */
    private RecipeResponse convertToRecipeResponse(Recipe recipe) {
        RecipeResponse recipeResponse = new RecipeResponse();

        recipeResponse.setId(recipe.getId());
        recipeResponse.setName(recipe.getName());
        recipeResponse.setDescription(recipe.getDescription());
        recipeResponse.setDietaryType(recipe.getDietaryType().name());
        recipeResponse.setNumberOfServings(recipe.getNumberOfServings());
        recipeResponse.setPrepTimeMinutes(recipe.getPrepTimeMinutes());
        recipeResponse.setInstructions(recipe.getInstructions());

        List<IngredientResponse> ingredients = populateIngredientsList(recipe.getRecipeIngredients());

        recipeResponse.setIngredients(ingredients);

        return recipeResponse;
    }

    /**
     * Populated the ingredient response object from the recipe ingredient database object
     *
     * @param recipeIngredients
     * @return
     */
    private List<IngredientResponse> populateIngredientsList(List<RecipeIngredient> recipeIngredients) {
        List<IngredientResponse> ingredients = new ArrayList<>();

        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            IngredientResponse ingredientResponse = new IngredientResponse();

            ingredientResponse.setIngredient(recipeIngredient.getIngredient().getName());
            ingredientResponse.setQuantity(recipeIngredient.getQuantity());
            ingredientResponse.setUnit(recipeIngredient.getUnit().getName());

            ingredients.add(ingredientResponse);
        }
        return ingredients;
    }

    /**
     * Converts from a recipe list (database representation) to a list of recipe response
     *
     * @param recipes
     * @return
     */
    private List<RecipeResponse> convertToRecipeResponseList(List<Recipe> recipes) {

        return recipes.stream()
                .map(this::convertToRecipeResponse)
                .toList();
    }
}
