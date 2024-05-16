package com.teamrockstars.fadihasrouni.recipesmanagement.repository;

import com.teamrockstars.fadihasrouni.recipesmanagement.model.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * IngredientTypeRepository to handle ingredient type database operations
 */
public interface IngredientTypeRepository extends JpaRepository<IngredientType, Long> {

    Optional<IngredientType> findByName(String name);
}
