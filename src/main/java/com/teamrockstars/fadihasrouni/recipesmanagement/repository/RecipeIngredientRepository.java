package com.teamrockstars.fadihasrouni.recipesmanagement.repository;

import com.teamrockstars.fadihasrouni.recipesmanagement.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * RecipeIngredientRepository to handle recipe database operations
 */
public interface RecipeIngredientRepository  extends JpaRepository<RecipeIngredient, Long> {

    List<RecipeIngredient> findByRecipeId(Long recipeId);
}
