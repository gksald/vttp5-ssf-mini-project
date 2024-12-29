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
        
        // list -> redis key: savedRecipesID, value: recipeID
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        List<String> recipeIdList = listOps.range(savedRecipesId, 0, -1);
        if (recipeIdList == null) {
            return false; // Key doesn't exist
        }

        return recipeIdList.contains(recipeId);
    }


    public void saveRecipe(String savedRecipesId, Recipe savedRecipe) {
        
        // recipeKey (redis key) - prefix: DrecipeID-
        String recipeKey = "DrecipeId-" + savedRecipe.getRecipeId();
        // map -> redis key: recipeID, hash key: variable name, hash value: variable value
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

        // Log after adding recipe fields to Redis
        System.out.println("Recipe fields added to Redis for key: " + recipeKey);

        // list -> redis key: savedRecipesID, value: recipeID
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        // if (!redisTemplate.hasKey(savedRecipesId)) {
        //     redisTemplate.opsForList().leftPush(savedRecipesId, ""); // Initialize as list if not already done
        // }
        if (!redisTemplate.hasKey(savedRecipesId)) {
        // If the key doesn't exist, initialize it as a list
        redisTemplate.opsForList().leftPush(savedRecipesId, ""); // Initialize as a list with one dummy value
        // Immediately remove the empty string if it was pushed as a placeholder
        redisTemplate.opsForList().remove(savedRecipesId, 1, ""); // Removes the first occurrence of the empty string
        } else {
        // Check if the existing key is a list, otherwise delete it and reinitialize
        if (!redisTemplate.type(savedRecipesId).equals(DataType.LIST)) {
        redisTemplate.delete(savedRecipesId); // Delete the existing key if it's not a list
        redisTemplate.opsForList().leftPush(savedRecipesId, ""); // Reinitialize as a list
        System.out.println("Key was not a list, deleted and reinitialized as a list.");
    }
}

        listOps.rightPush(savedRecipesId, savedRecipe.getRecipeId().toString());
        System.out.println("Recipe ID added to the list: " + savedRecipesId);

        System.out.println("Saving recipe: " + savedRecipe.toString());
    
    }

    public List<Recipe> loadRecipeList (String savedRecipesId) {

        // list -> redis key: savedRecipesID, value: recipeID
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        List<String> recipeIdList = listOps.range(savedRecipesId, 0, -1);
        
        return recipeIdList
        .stream()
        .map(this::loadRecipe)
        .collect(Collectors.toList());
    }

    private Recipe loadRecipe(String recipeId) {

        // redis key: recipeID, hash key: variable name, hash value: variable value
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        // recipeKey (redis key) - prefix: DrecipeID-
        String recipeKey = String.format("DrecipeId-%s", recipeId);

        Recipe loadedRecipe = new Recipe();
        loadedRecipe.setRecipeId(Integer.parseInt(hashOps.get(recipeKey, "recipeId").toString()));
        loadedRecipe.setRecipeName(hashOps.get(recipeKey, "recipeName").toString());
        loadedRecipe.setIngredients(getCSVList(hashOps.get(recipeKey, "ingredients").toString()));
        loadedRecipe.setCuisines(getCSVList(hashOps.get(recipeKey, "cuisines").toString()));
        loadedRecipe.setMealTypes(getCSVList(hashOps.get(recipeKey, "mealTypes").toString()));
        loadedRecipe.setDiets(getCSVList(hashOps.get(recipeKey, "diets").toString()));
        loadedRecipe.setRecipeId(Integer.parseInt(hashOps.get(recipeKey, "servings").toString()));
        loadedRecipe.setInstructions(getCSVList(hashOps.get(recipeKey, "instructions").toString()));
        loadedRecipe.setRecipeId(Integer.parseInt(hashOps.get(recipeKey, "preparationTime").toString()));
        loadedRecipe.setImageUrl(hashOps.get(recipeKey, "imageUrl").toString());
        loadedRecipe.setSourceUrl(hashOps.get(recipeKey, "sourceUrl").toString());

            return loadedRecipe;

        }

    // CSV - Comma Separated Value string
    private List<String> getCSVList(String csv) {

        return Arrays.asList(csv.split(","));
    

    }


// // gpt eg 1
//     // Simulating a database with a Map where key is userID and value is a list of saved recipes
//     private Map<String, List<Recipe>> userRecipes = new HashMap<>();

//     // Add recipe to user's cookbook
//     public void saveRecipe(String userID, Recipe recipe) {
//         userRecipes.putIfAbsent(userID, new ArrayList<>());
//         userRecipes.get(userID).add(recipe);
//     }

//     // Retrieve all recipes saved by a user
//     public List<Recipe> getUserRecipes(String userID) {
//         return userRecipes.getOrDefault(userID, new ArrayList<>());
//     }

}
    

