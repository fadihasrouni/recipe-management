package com.teamrockstars.fadihasrouni.recipesmanagement.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamrockstars.fadihasrouni.recipesmanagement.controller.RecipeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmokeTest {

    @Autowired
    private RecipeController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }
}