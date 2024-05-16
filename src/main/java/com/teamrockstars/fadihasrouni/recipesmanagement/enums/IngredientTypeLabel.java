package com.teamrockstars.fadihasrouni.recipesmanagement.enums;

/**
 * Reflect database ingredient type names
 */
public enum IngredientTypeLabel {
    MEAT("Meat, chicken, sausage..."),
    SEA_FOOD("Fish and seafood");

    // TODO: support other ingredient types lables

    public final String name;

    private IngredientTypeLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
