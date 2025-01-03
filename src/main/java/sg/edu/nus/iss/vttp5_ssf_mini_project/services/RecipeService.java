package sg.edu.nus.iss.vttp5_ssf_mini_project.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.Recipe;
import sg.edu.nus.iss.vttp5_ssf_mini_project.repositories.RecipeRepository;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepo;

    @Value("${spoonacular.api.recipe.url}")
    private String apiRecipeUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Recipe> getRecipes(String apiKey, String query, String type, String cuisine, String diet, Integer minServings,
                                    Integer maxServings, String includeIngredients, String excludeIngredients) {

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key is required");
        }

        String requestUrl = buildRequestUrl(apiKey, query, type, cuisine, diet, minServings, maxServings, includeIngredients, excludeIngredients);

        try {
            RequestEntity<Void> request = RequestEntity.get(requestUrl).build();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to fetch recipes. HTTP Status: " + response.getStatusCode());
            }

            String jsonPayload = response.getBody();
            if (jsonPayload == null || jsonPayload.isEmpty()) {
                return Collections.emptyList();
            }

            JsonReader jsonReader = Json.createReader(new StringReader(jsonPayload));
            JsonObject jsonObject = jsonReader.readObject();
            JsonArray jsonResultsArray = jsonObject.getJsonArray("results");

            List<Recipe> recipes = new ArrayList<>();
            for (int i = 0; i < jsonResultsArray.size(); i++) {
                JsonObject jsonRecipe = jsonResultsArray.getJsonObject(i);
                recipes.add(parseRecipe(jsonRecipe));
            }

            return recipes;

        } catch (RestClientException e) {
            throw new RuntimeException("Error fetching recipes: " + e.getMessage(), e);
        }
    }

    private Recipe parseRecipe(JsonObject jsonRecipe) {
        Recipe recipe = new Recipe();

        recipe.setRecipeId(jsonRecipe.getInt("id", 0));
        recipe.setRecipeName(jsonRecipe.getString("title", ""));
        recipe.setServings(jsonRecipe.getInt("servings", 0));
        recipe.setPreparationTime(jsonRecipe.getInt("readyInMinutes", 0));
        recipe.setImageUrl(jsonRecipe.getString("image", ""));
        recipe.setSourceUrl(jsonRecipe.getString("sourceUrl", ""));

        recipe.setCuisines(mapJsonArrayToList(jsonRecipe.getJsonArray("cuisines")));
        recipe.setMealTypes(mapJsonArrayToList(jsonRecipe.getJsonArray("dishTypes")));
        recipe.setDiets(mapJsonArrayToList(jsonRecipe.getJsonArray("diets")));

        JsonArray ingredientsJsonArray = jsonRecipe.getJsonArray("extendedIngredients");
        List<String> ingredientsList = new ArrayList<>();
        if (ingredientsJsonArray != null) {
            for (int j = 0; j < ingredientsJsonArray.size(); j++) {
                JsonObject ingredientJson = ingredientsJsonArray.getJsonObject(j);
                ingredientsList.add(ingredientJson.getString("original", ""));
            }
        }
        recipe.setIngredients(ingredientsList);

        JsonArray instructionsJsonArray = jsonRecipe.getJsonArray("analyzedInstructions");
        List<String> instructionsList = new ArrayList<>();
        if (instructionsJsonArray != null && !instructionsJsonArray.isEmpty()) {
            JsonObject instructionsObject = instructionsJsonArray.getJsonObject(0);
            JsonArray stepsArray = instructionsObject.getJsonArray("steps");
            if (stepsArray != null) {
                for (int k = 0; k < stepsArray.size(); k++) {
                    JsonObject stepObject = stepsArray.getJsonObject(k);
                    instructionsList.add(stepObject.getString("step", ""));
                }
            }
        }
        recipe.setInstructions(instructionsList);

        return recipe;
    }

    private List<String> mapJsonArrayToList(JsonArray jsonArray) {
        if (jsonArray == null) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getString(i));
        }

        return list;
    }

    public String buildRequestUrl(String apiKey, String query, String type, String cuisine, String diet, Integer minServings,
                                   Integer maxServings, String includeIngredients, String excludeIngredients) {

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key cannot be null or empty");
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(apiRecipeUrl)
                .queryParam("apiKey", apiKey)
                .queryParam("fillIngredients", true)
                .queryParam("addRecipeInformation", true)
                .queryParam("addRecipeInstructions", true)
                .queryParam("number", 20);

        if (query != null && !query.isEmpty()) uriBuilder.queryParam("query", query);
        if (type != null && !type.isEmpty()) uriBuilder.queryParam("type", type);
        if (cuisine != null && !cuisine.isEmpty()) uriBuilder.queryParam("cuisine", cuisine);
        if (diet != null && !diet.isEmpty()) uriBuilder.queryParam("diet", diet);
        if (minServings != null) uriBuilder.queryParam("minServings", minServings);
        if (maxServings != null) uriBuilder.queryParam("maxServings", maxServings);
        if (includeIngredients != null && !includeIngredients.isEmpty()) uriBuilder.queryParam("includeIngredients", includeIngredients);
        if (excludeIngredients != null && !excludeIngredients.isEmpty()) uriBuilder.queryParam("excludeIngredients", excludeIngredients);

        return uriBuilder.toUriString();
    }

    public String createSavedRecipesId(String userID) {
        return "DsavedrecipesID-" + userID;
    }

    public Optional<Recipe> extractRecipe(List<Recipe> recipes, Integer recipeId) {
        return recipes.stream()
            .filter(recipe -> recipe.getRecipeId().equals(recipeId))
            .findFirst();
    }

    public void saveRecipe(String savedRecipesId, Recipe savedRecipe) {
        if (!recipeRepo.hasRecipeIdKey(savedRecipesId, savedRecipe.getRecipeId().toString())) {
            recipeRepo.saveRecipe(savedRecipesId, savedRecipe);
        }
    }

    public List<Recipe> loadRecipeList(String savedRecipesId) {
        if (!recipeRepo.hasSavedRecipesIdKey(savedRecipesId)) {
            return Collections.emptyList();
        }
        return recipeRepo.loadRecipeList(savedRecipesId);
    }

    public void deleteRecipe(String savedRecipesId, String recipeId) {
        recipeRepo.deleteRecipe(savedRecipesId, recipeId);
    }

    public int getFavouritesQuantity(String userID) {
        String savedRecipesId = createSavedRecipesId(userID);
        return loadRecipeList(savedRecipesId).size();
    }

    public String convertRecipeToJson(Recipe recipe) {
        return Json.createObjectBuilder()
            .add("recipe_id", recipe.getRecipeId())
            .add("recipe_name", recipe.getRecipeName())
            .add("ingredients", Json.createArrayBuilder(recipe.getIngredients()).build())
            .add("cuisines", Json.createArrayBuilder(recipe.getCuisines()).build())
            .add("meal_types", Json.createArrayBuilder(recipe.getMealTypes()).build())
            .add("diets", Json.createArrayBuilder(recipe.getDiets()).build())
            .add("servings", recipe.getServings())
            .add("instructions", Json.createArrayBuilder(recipe.getInstructions()).build())
            .add("preparation_time", recipe.getPreparationTime())
            .add("image_url", recipe.getImageUrl())
            .add("source_url", recipe.getSourceUrl())
            .build()
            .toString();
    }
    
}