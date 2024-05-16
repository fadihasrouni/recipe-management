package com.teamrockstars.fadihasrouni.recipesmanagement.repository;

import com.teamrockstars.fadihasrouni.recipesmanagement.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * IngredientRepository to handle ingredient database operations
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

}
