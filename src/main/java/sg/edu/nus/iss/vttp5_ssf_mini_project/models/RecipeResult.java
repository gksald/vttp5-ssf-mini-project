package sg.edu.nus.iss.vttp5_ssf_mini_project.models;

public class RecipeResult {
    
    private String id;  // The ID of the recipe
    private String title;  // The title of the recipe
    private String summary;  // A brief summary of the recipe
    private String imageUrl;  // URL for the recipe image
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
