package com.teamrockstars.fadihasrouni.recipesmanagement.config;

import com.teamrockstars.fadihasrouni.recipesmanagement.enums.DietaryType;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.Ingredient;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.Recipe;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.RecipeIngredient;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecification {

    public static Specification<Recipe> byNumberOfServings(Integer numberOfServings) {
        if(numberOfServings == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("numberOfServings"), numberOfServings);
    }

    public static Specification<Recipe> byDietaryType(String dietaryType) {
        if(dietaryType == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("dietaryType"), DietaryType.valueOf(dietaryType));
    }

    public static Specification<Recipe> byInstructionsInclude (String instructionsInclude) {
        if(instructionsInclude == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("instructions"), "%" + instructionsInclude + "%");
    }

    public static Specification<Recipe> byIngredientIncludes (String ingredientIncludes) {
        if(ingredientIncludes == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            root.join("recipeIngredients", JoinType.INNER);
            return criteriaBuilder.like(root.get("recipeIngredients").get("ingredient").get("name"), "%" + ingredientIncludes + "%");
        };
    }

    public static Specification<Recipe> byIngredientExcludes (String ingredientExcludes) {
        if(ingredientExcludes == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            // Subquery to find recipe IDs that contain the ingredient
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<RecipeIngredient> recipeIngredientRoot = subquery.from(RecipeIngredient.class);
            Join<RecipeIngredient, Ingredient> ingredientJoin = recipeIngredientRoot.join("ingredient");

            subquery.select(recipeIngredientRoot.get("recipe").get("id"))
                    .where(criteriaBuilder.like(ingredientJoin.get("name"), "%" + ingredientExcludes + "%"));

            // Main query to exclude those recipes
            return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }
}
