package com.teamrockstars.fadihasrouni.recipesmanagement.repository;

import com.teamrockstars.fadihasrouni.recipesmanagement.enums.DietaryType;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.Recipe;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * RecipeRepository to handle recipe database operations
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

}
