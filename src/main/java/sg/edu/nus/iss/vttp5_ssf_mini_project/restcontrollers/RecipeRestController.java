package sg.edu.nus.iss.vttp5_ssf_mini_project.restcontrollers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.Recipe;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.RecipeResult;
import sg.edu.nus.iss.vttp5_ssf_mini_project.services.RecipeService;

@RestController
@RequestMapping("/api")
public class RecipeRestController {
    
    @Autowired
    private RecipeService recipeService;

    // // Search recipes through API (returns JSON)
    // @PostMapping("/search")
    // public List<RecipeResult> searchRecipes(@RequestBody RecipeRequest searchRequest) {
    //     return recipeService.searchRecipes(searchRequest);
    // }

    // Get recipe details via API (returns JSON)
    // @GetMapping("/{id}")
    // public Recipe getRecipeDetails(@PathVariable String id) {
    //     return recipeService.getRecipeDetails(id);
    // }

    // testing api response --> this works
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
            @RequestParam(required = false) String excludeIngredients)

            {
                return recipeService.getRecipes(apiKey, query, type, cuisine, diet, minServings, maxServings, includeIngredients, excludeIngredients);
            }

    // Endpoint to add recipes to session
    @PostMapping("/addRecipesToSession")
    public ResponseEntity<String> addRecipesToSession(HttpSession session) {
        List<Recipe> recipes = recipeService.getRecipes(null, null, null, null, null, null, null, null, null); // Assuming this fetches recipes from a DB or API
        session.setAttribute("listedRecipes", recipes); // Store the list in session

    // Log the recipes added to the session
    System.out.println("Adding " + recipes.size() + " recipes to session");

        return ResponseEntity.ok("Recipes added to session");
    }

    // Endpoint to check if recipes are stored in session
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
        @PathVariable Integer recipeId,  // Use Integer instead of String
        HttpSession sess) {
        
        // Check if the user is logged in by verifying the session
        List<Recipe> recipes = (List<Recipe>) sess.getAttribute("listedRecipes");
        System.out.println("Recipes in session: " + recipes); // Debugging

        if (recipes == null) {
          String notFoundMessage = "You must be logged in to view recipes in JSON.";
        
          JsonObject notFound = Json.createObjectBuilder()
              .add("message", notFoundMessage)
              .build();
        
          return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED) // 401 UNAUTHORIZED
              .body(notFound.toString());
        }
            
       // Use the recipeId as an Integer
       Optional<Recipe> savedRecipeOptional = recipeService.extractRecipe(recipes, recipeId);
       System.out.println("Found recipe: " + savedRecipeOptional);
       if (!savedRecipeOptional.isPresent()) {
         String notFoundMessage = String.format("Recipe with recipeID %d not found", recipeId);
         
         JsonObject notFound = Json.createObjectBuilder()
             .add("message", notFoundMessage)
             .build();
         
         return ResponseEntity
             .status(HttpStatus.NOT_FOUND) // 404 NOT FOUND
             .body(notFound.toString());
       }
       
       // If recipe found, return it as JSON
       return ResponseEntity
           .status(HttpStatus.OK) // 200 OK
           .body(recipeService.convertRecipeToJson(savedRecipeOptional.get()));
      }
            

}
