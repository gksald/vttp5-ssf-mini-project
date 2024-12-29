package sg.edu.nus.iss.vttp5_ssf_mini_project.repositories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.vttp5_ssf_mini_project.models.Recipe;
import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.Utility;

@Repository
public class RecipeRepository {

    @Autowired
    @Qualifier(Utility.BEAN_REDIS)
    private RedisTemplate<String, String> redisTemplate;

    public boolean hasSavedRecipesIdKey(String savedRecipesId) {
        return redisTemplate.hasKey(savedRecipesId);
    }

    public boolean hasRecipeIdKey(String savedRecipesId, String recipeId) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> recipeIdList = listOps.range(savedRecipesId, 0, -1);

        if (recipeIdList == null) {
            return false; // Key doesn't exist
        }

        return recipeIdList.contains(recipeId);
    }

    public void saveRecipe(String savedRecipesId, Recipe savedRecipe) {
        // Recipe key prefix
        String recipeKey = "DrecipeId-" + savedRecipe.getRecipeId();

        // Hash operations to save recipe fields
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put(recipeKey, "recipeId", String.valueOf(savedRecipe.getRecipeId()));
        hashOps.put(recipeKey, "recipeName", String.join(",", savedRecipe.getRecipeName()));
        hashOps.put(recipeKey, "ingredients", String.join(",", savedRecipe.getIngredients()));
        hashOps.put(recipeKey, "cuisines", String.join(",", savedRecipe.getCuisines()));
        hashOps.put(recipeKey, "mealTypes", String.join(",", savedRecipe.getMealTypes()));
        hashOps.put(recipeKey, "diets", String.join(",", savedRecipe.getDiets()));
        hashOps.put(recipeKey, "servings", String.valueOf(savedRecipe.getServings()));
        hashOps.put(recipeKey, "instructions", String.join(",", savedRecipe.getInstructions()));
        hashOps.put(recipeKey, "preparationTime", String.valueOf(savedRecipe.getPreparationTime()));
        hashOps.put(recipeKey, "imageUrl", String.join(",", savedRecipe.getImageUrl()));
        hashOps.put(recipeKey, "sourceUrl", String.join(",", savedRecipe.getSourceUrl()));

        System.out.println("Recipe fields added to Redis for key: " + recipeKey);

        // List operations for savedRecipesId
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        if (!redisTemplate.hasKey(savedRecipesId)) {
            redisTemplate.opsForList().leftPush(savedRecipesId, "");    // to initialise the list
            redisTemplate.opsForList().remove(savedRecipesId, 1, "");   // to remove the empty string
        } else if (!redisTemplate.type(savedRecipesId).equals(DataType.LIST)) {
            redisTemplate.delete(savedRecipesId);
            redisTemplate.opsForList().leftPush(savedRecipesId, "");
            System.out.println("Key was not a list, deleted and reinitialized as a list.");
        }

        listOps.rightPush(savedRecipesId, savedRecipe.getRecipeId().toString());
        System.out.println("Recipe ID added to the list: " + savedRecipesId);
        System.out.println("Saving recipe: " + savedRecipe.toString());
    }

    public List<Recipe> loadRecipeList(String savedRecipesId) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> recipeIdList = listOps.range(savedRecipesId, 0, -1);

        return recipeIdList.stream()
                .map(this::loadRecipe)
                .collect(Collectors.toList());
    }

    private Recipe loadRecipe(String recipeId) {
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        String recipeKey = String.format("DrecipeId-%s", recipeId);
    
        Recipe loadedRecipe = new Recipe();
    
        // Safely retrieve and handle potential nulls
        loadedRecipe.setRecipeId(getIntFromHash(hashOps, recipeKey, "recipeId"));
        loadedRecipe.setRecipeName(getStringFromHash(hashOps, recipeKey, "recipeName"));
        loadedRecipe.setIngredients(getCSVList(getStringFromHash(hashOps, recipeKey, "ingredients")));
        loadedRecipe.setCuisines(getCSVList(getStringFromHash(hashOps, recipeKey, "cuisines")));
        loadedRecipe.setMealTypes(getCSVList(getStringFromHash(hashOps, recipeKey, "mealTypes")));
        loadedRecipe.setDiets(getCSVList(getStringFromHash(hashOps, recipeKey, "diets")));
        loadedRecipe.setServings(getIntFromHash(hashOps, recipeKey, "servings"));
        loadedRecipe.setInstructions(getCSVList(getStringFromHash(hashOps, recipeKey, "instructions")));
        loadedRecipe.setPreparationTime(getIntFromHash(hashOps, recipeKey, "preparationTime"));
        loadedRecipe.setImageUrl(getStringFromHash(hashOps, recipeKey, "imageUrl"));
        loadedRecipe.setSourceUrl(getStringFromHash(hashOps, recipeKey, "sourceUrl"));
    
        return loadedRecipe;
    }
    
    // Helper method to safely get a String from the Hash
    private String getStringFromHash(HashOperations<String, String, Object> hashOps, String recipeKey, String field) {
        Object value = hashOps.get(recipeKey, field);
        return value != null ? value.toString() : ""; // Return empty string if value is null
    }
    
    // Helper method to safely get an integer from the Hash
    private int getIntFromHash(HashOperations<String, String, Object> hashOps, String recipeKey, String field) {
        Object value = hashOps.get(recipeKey, field);
        return value != null ? Integer.parseInt(value.toString()) : 0; // Return 0 if value is null
    }
    

    private List<String> getCSVList(String csv) {
        return Arrays.asList(csv.split(","));
    }

    public void deleteRecipe(String savedRecipesId, String recipeId) {
        // Delete the recipe's hash (stored by its recipeId)
        String recipeKey = "DrecipeId-" + recipeId;
        redisTemplate.delete(recipeKey);
        System.out.println("Recipe hash deleted for key: " + recipeKey);
    
        // Remove the recipeId from the savedRecipesId list
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> currentList = listOps.range(savedRecipesId, 0, -1);
    
        System.out.println("Current list of recipe IDs: " + currentList);
    
        // If the recipeId exists in the list, remove it
        if (currentList != null && currentList.contains(recipeId)) {
            listOps.remove(savedRecipesId, 1, recipeId);
            System.out.println("Recipe ID removed from the list: " + savedRecipesId);
        } else {
            System.out.println("Recipe ID " + recipeId + " not found in the list.");
        }
    }
    
}