package sg.edu.nus.iss.vttp5_ssf_mini_project.restcontrollers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.Recipe;
import sg.edu.nus.iss.vttp5_ssf_mini_project.services.RecipeService;

@RestController
@RequestMapping("/api")
public class RecipeRestController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/recipe")
    public List<Recipe> getRecipes(
            @RequestParam String apiKey,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String diet,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings,
            @RequestParam(required = false) String includeIngredients,
            @RequestParam(required = false) String excludeIngredients) {
        return recipeService.getRecipes(apiKey, query, type, cuisine, diet, minServings, maxServings, includeIngredients, excludeIngredients);
    }

    @PostMapping("/addRecipesToSession")
    public ResponseEntity<String> addRecipesToSession(HttpSession session) {
        List<Recipe> recipes = recipeService.getRecipes(null, null, null, null, null, null, null, null, null); // Assuming this fetches recipes from a DB or API
        session.setAttribute("listedRecipes", recipes);

        System.out.println("Adding " + recipes.size() + " recipes to session");

        return ResponseEntity.ok("Recipes added to session");
    }

    @GetMapping("/checkSession")
    public ResponseEntity<String> checkSession(HttpSession session) {
        List<Recipe> recipes = (List<Recipe>) session.getAttribute("listedRecipes");
        if (recipes == null || recipes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipes not found in session");
        }
        return ResponseEntity.ok("Recipes in session: " + recipes.size());
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<String> getRecipeInJson(
            @PathVariable Integer recipeId,
            HttpSession sess) {

        List<Recipe> recipes = (List<Recipe>) sess.getAttribute("listedRecipes");
        System.out.println("Recipes in session: " + recipes);

        if (recipes == null) {
            String notFoundMessage = "You must be logged in to view recipes in JSON.";

            JsonObject notFound = Json.createObjectBuilder()
                    .add("message", notFoundMessage)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(notFound.toString());
        }

        Optional<Recipe> savedRecipeOptional = recipeService.extractRecipe(recipes, recipeId);
        System.out.println("Found recipe: " + savedRecipeOptional);
        if (!savedRecipeOptional.isPresent()) {
            String notFoundMessage = String.format("Recipe with recipeID %d not found", recipeId);

            JsonObject notFound = Json.createObjectBuilder()
                    .add("message", notFoundMessage)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(notFound.toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recipeService.convertRecipeToJson(savedRecipeOptional.get()));
    }

    @DeleteMapping("/recipes/{savedRecipesId}/{recipeId}")
    public ResponseEntity<String> deleteRecipe(
            @PathVariable String savedRecipesId,
            @PathVariable String recipeId) {

        try {
            // Call the service method to delete the recipe
            recipeService.deleteRecipe(savedRecipesId, recipeId);
            return ResponseEntity.ok("Recipe with ID " + recipeId + " deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting recipe.");
        }
    }

}