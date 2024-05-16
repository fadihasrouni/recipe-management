package com.teamrockstars.fadihasrouni.recipesmanagement.unit.service;

import com.teamrockstars.fadihasrouni.recipesmanagement.model.Ingredient;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.IngredientRequest;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.IngredientType;
import com.teamrockstars.fadihasrouni.recipesmanagement.model.SimpleIngredientResponse;
import com.teamrockstars.fadihasrouni.recipesmanagement.repository.IngredientRepository;
import com.teamrockstars.fadihasrouni.recipesmanagement.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    List<Ingredient> ingredientsList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ingredientsList.add(createIngredient(1L, "Salt", null));
        ingredientsList.add(createIngredient(2L, "Pepper", null));
    }

    @Test
    void getAllIngredientsSuccess() {
        Mockito.when(ingredientRepository.findAll()).thenReturn(ingredientsList);

        List<SimpleIngredientResponse> ingredientResponseList = ingredientService.getAllIngredients();

        assertEquals(2, ingredientResponseList.size());
        assertEquals(1, ingredientResponseList.get(0).getId());
        assertEquals("Salt", ingredientResponseList.get(0).getName());
        assertEquals(2, ingredientResponseList.get(1).getId());
        assertEquals("Pepper", ingredientResponseList.get(1).getName());
    }

    @Test
    void createIngredientSuccess() {
        Mockito.when(ingredientRepository.save(Mockito.any(Ingredient.class))).thenReturn(ingredientsList.get(0));

        SimpleIngredientResponse ingredientResponse = ingredientService.createIngredient(createIngredientRequest("Salt"));

        assertEquals(1, ingredientResponse.getId());
        assertEquals("Salt", ingredientResponse.getName());
    }

    @Test
    void createIngredientDuplicateFailue() {
        Mockito.when(ingredientRepository.save(Mockito.any(Ingredient.class))).thenThrow(new DataIntegrityViolationException("Duplicate value"));

        assertThrows(DataIntegrityViolationException.class,
                () -> ingredientService.createIngredient(createIngredientRequest("Salt")));
    }

    /**
     * Create an ingredient model object and return it
     *
     * @param id
     * @param name
     * @param type
     * @return
     */
    private Ingredient createIngredient(Long id, String name, IngredientType type) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setType(type);

        return ingredient;
    }

    /**
     * Creates an ingredient request and returns it
     *
     * @param name
     * @return
     */
    private IngredientRequest createIngredientRequest(String name) {
        IngredientRequest ingredientRequest = new IngredientRequest();
        ingredientRequest.setName(name);
        return ingredientRequest;
    }
}
