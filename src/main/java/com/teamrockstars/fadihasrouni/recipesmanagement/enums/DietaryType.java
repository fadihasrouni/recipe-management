package com.teamrockstars.fadihasrouni.recipesmanagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(implementation = DietaryType.class)
public enum DietaryType {
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    PESCETARIAN("Pescetarian"),
    FLEXETARIAN("Flexetarian"),
    NON_VEGETARIAN("Non Vegetarian");

    public final String name;

    private DietaryType(String name) {
        this.name = name;
    }
}
