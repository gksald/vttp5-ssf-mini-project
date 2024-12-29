package sg.edu.nus.iss.vttp5_ssf_mini_project.models;

public class RecipeSearch {
    
    // optional search parameters
    private String query;
    private String type;
    private String cuisine;
    private String diet;
    private Integer minServings;
    private Integer maxServings;
    private String includeIngredients;
    private String excludeIngredients;

    // default search parameters set to true (required to display instructions and ingredients base on API response structure)
    private Boolean fillIngredients;
    private Boolean addRecipeInformation;
    private Boolean addRecipeInstructions;
    
    
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCuisine() {
        return cuisine;
    }
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
    public String getDiet() {
        return diet;
    }
    public void setDiet(String diet) {
        this.diet = diet;
    }
    public Integer getMinServings() {
        return minServings;
    }
    public void setMinServings(Integer minServings) {
        this.minServings = minServings;
    }
    public Integer getMaxServings() {
        return maxServings;
    }
    public void setMaxServings(Integer maxServings) {
        this.maxServings = maxServings;
    }
    public String getIncludeIngredients() {
        return includeIngredients;
    }
    public void setIncludeIngredients(String includeIngredients) {
        this.includeIngredients = includeIngredients;
    }
    public String getExcludeIngredients() {
        return excludeIngredients;
    }
    public void setExcludeIngredients(String excludeIngredients) {
        this.excludeIngredients = excludeIngredients;
    }


    public Boolean getFillIngredients() {
        return fillIngredients;
    }
    public void setFillIngredients(Boolean fillIngredients) {
        this.fillIngredients = fillIngredients;
    }
    public Boolean getAddRecipeInformation() {
        return addRecipeInformation;
    }
    public void setAddRecipeInformation(Boolean addRecipeInformation) {
        this.addRecipeInformation = addRecipeInformation;
    }
    public Boolean getAddRecipeInstructions() {
        return addRecipeInstructions;
    }
    public void setAddRecipeInstructions(Boolean addRecipeInstructions) {
        this.addRecipeInstructions = addRecipeInstructions;
    }
   
}