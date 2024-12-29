package sg.edu.nus.iss.vttp5_ssf_mini_project.models;

import java.util.List;

public class Recipe {

    // fields u wanna return as a response
    private Integer recipeId;  // link to id
    private String recipeName;   // link to title
    private List<String> ingredients;   // link to extendedIngredients (original)
    private List<String> cuisines;
    private List<String> mealTypes; // link to dishTypes
    private List<String> diets;
    private Integer servings;
    private List<String> instructions;    // link to analyzedInstructions (sizes->size)
    private Integer preparationTime;    // link to readyInMinutes
    private String imageUrl;    // link to image
    private String sourceUrl;   // link to spoonacularSourceUrl
    
    
    public Integer getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public List<String> getCuisines() {
        return cuisines;
    }
    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }
    public List<String> getMealTypes() {
        return mealTypes;
    }
    public void setMealTypes(List<String> mealTypes) {
        this.mealTypes = mealTypes;
    }
    public List<String> getDiets() {
        return diets;
    }
    public void setDiets(List<String> diets) {
        this.diets = diets;
    }
    public Integer getServings() {
        return servings;
    }
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    public List<String> getInstructions() {
        return instructions;
    }
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
    public Integer getPreparationTime() {
        return preparationTime;
    }
    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getSourceUrl() {
        return sourceUrl;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Recipe() {}

    public Recipe(Integer recipeId, String recipeName, List<String> ingredients, List<String> cuisines,
            List<String> mealTypes, List<String> diets, Integer servings, List<String> instructions, Integer preparationTime,
            String imageUrl, String sourceUrl) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.cuisines = cuisines;
        this.mealTypes = mealTypes;
        this.diets = diets;
        this.servings = servings;
        this.instructions = instructions;
        this.preparationTime = preparationTime;
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
    }


}