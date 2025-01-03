package sg.edu.nus.iss.vttp5_ssf_mini_project.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.Recipe;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.User;
import sg.edu.nus.iss.vttp5_ssf_mini_project.repositories.RecipeRepository;
import sg.edu.nus.iss.vttp5_ssf_mini_project.services.RecipeService;
import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.Utility;

@Controller
@RequestMapping("")
public class RecipeController {

    @Value("${spoonacular.api.key}")
    String apiKey; // Replace with your actual API key or fetch from a secure location

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepo;

    @GetMapping("/recipesearch")
    public ModelAndView showRecipeSearchPage() {
        ModelAndView mav = new ModelAndView("recipesearch");

        // Add fields for dropdowns and multi-select
        mav.addObject("dishTypes", Arrays.asList(Utility.DISH_TYPE));
        mav.addObject("cuisineTypes", Arrays.asList(Utility.CUISINE_TYPE));
        mav.addObject("dietTypes", Arrays.asList(Utility.DIET_TYPE));
        mav.addObject("servingOptions", List.of(1, 2, 3, 4, 5, 6, 7, 8));

        return mav;
    }

    @PostMapping("/recipelist")
    public ModelAndView searchRecipes(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String includeIngredients,
        @RequestParam(required = false) String excludeIngredients,
        @RequestParam(name = "type", required = false) List<String> types,
        @RequestParam(name = "cuisine", required = false) List<String> cuisines,
        @RequestParam(name = "diet", required = false) List<String> diets,
        @RequestParam(required = false) Integer minServings,
        @RequestParam(required = false) Integer maxServings,
        HttpSession sess
    ) {

        // Call the RecipeService to fetch recipes
        List<Recipe> recipes = recipeService.getRecipes(
            apiKey,
            query,
            types != null ? String.join(",", types) : null,
            cuisines != null ? String.join(",", cuisines) : null,
            diets != null ? String.join(",", diets) : null,
            minServings,
            maxServings,
            includeIngredients,
            excludeIngredients
        );

        // Store the recipes in the session
        sess.setAttribute("recipes", recipes);

        // Add data to the ModelAndView
        ModelAndView mav = new ModelAndView("recipelist");
        mav.addObject("recipes", recipes);

        // Add dropdown options to preserve them after search
        mav.addObject("dishTypes", Arrays.asList(Utility.DISH_TYPE));
        mav.addObject("cuisineTypes", Arrays.asList(Utility.CUISINE_TYPE));
        mav.addObject("dietTypes", Arrays.asList(Utility.DIET_TYPE));
        mav.addObject("servingOptions", List.of(1, 2, 3, 4, 5, 6, 7, 8));

        return mav;
    }

    @PostMapping("/recipesave/{recipeId}")
    public ModelAndView getSaveRecipe(@PathVariable Integer recipeId, HttpSession sess) {
        ModelAndView mav = new ModelAndView();

        // Check if user is logged in
        User loggedUser = (User) sess.getAttribute("loggedUser");
        if (loggedUser == null) {
            // User not logged in, redirect to error view
            System.out.println("No logged-in user found in session.");
            mav.addObject("errorMessage", "Please log in to save recipes.");
            mav.setViewName("accesserror1");
            mav.setStatus(HttpStatus.UNAUTHORIZED);  // 401 UNAUTHORIZED

        } else {
            // User is logged in, proceed with saving the recipe
            String userID = loggedUser.getUserID();
            String savedRecipesId = recipeService.createSavedRecipesId(userID);

            // Retrieve the list of recipes from session
            List<Recipe> recipes = (List<Recipe>) sess.getAttribute("recipes");

            if (recipes == null || recipes.isEmpty()) {
                mav.addObject("errorMessage", "No recipes available to save.");
                mav.setViewName("error");
                mav.setStatus(HttpStatus.BAD_REQUEST);  // 400 BAD REQUEST
                return mav;
            } else {
                System.out.println("Session recipes: " + recipes); // Debug log to confirm recipes
            }

            // Extract the recipe using the extractRecipe method
            Optional<Recipe> optionalRecipe = recipeService.extractRecipe(recipes, recipeId);
            if (optionalRecipe.isPresent()) {
                Recipe savedRecipe = optionalRecipe.get();
                System.out.println("Recipe found: " + savedRecipe);
                // Save the recipe
                recipeRepo.saveRecipe(savedRecipesId, savedRecipe);

                // Load the list of saved recipes for the user
                List<Recipe> savedRecipes = recipeService.loadRecipeList(savedRecipesId);
                sess.setAttribute("listedRecipes", savedRecipes);

                // Add the saved recipes to the model
                mav.addObject("recipes", savedRecipes);
                mav.addObject("loggedUser", loggedUser);

                // Set the view name and HTTP status for success
                mav.setViewName("recipesave");
                mav.setStatus(HttpStatus.CREATED);  // 201 CREATED
            } else {
                // Recipe not found in the list
                mav.addObject("errorMessage", "Recipe not found.");
                mav.setViewName("error");
                mav.setStatus(HttpStatus.NOT_FOUND);  // 404 NOT FOUND
                return mav;
            }
        }

        return mav;
    }

    @GetMapping("/recipesave")
    public ModelAndView getFavourites(HttpSession sess) {

        ModelAndView mav = new ModelAndView();

        User loggedUser = (User) sess.getAttribute("loggedUser");
        if (null == loggedUser) {

            mav.addObject("errorMessage", "Please log in to view your Cookbook.");
            mav.setViewName("accesserror1");
            mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
        }

        else {

            String userID = loggedUser.getUserID();
            String savedRecipesId = recipeService.createSavedRecipesId(userID);

            List<Recipe> savedRecipes = recipeService.loadRecipeList(savedRecipesId);
            sess.setAttribute("listedRecipes", savedRecipes);

            mav.addObject("recipes", savedRecipes);
            mav.addObject("loggedUser", loggedUser);

            mav.setViewName("recipesave");
            mav.setStatus(HttpStatus.OK); // 200 OK
        }

        return mav;
    }

}
