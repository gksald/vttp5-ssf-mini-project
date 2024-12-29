package sg.edu.nus.iss.vttp5_ssf_mini_project.utilities;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class Utility {
    
    // for password validation
    public static final String PASSWORD_SPECIAL_CHARS = "@#$%^`<>&+=\"!ºª·#~%&'¿¡€,:;*/+-.=_\\[\\]\\(\\)\\|\\_\\?\\\\";
    public static final int PASSWORD_MIN_SIZE = 10;

    public static final String PASSWORD_VALIDATION_MESSAGE = new StringBuilder("Password Requirements:\n")
        .append("- At least 10 characters long.\n")
        .append("- Contains at least one digit.\n")
        .append("- Contains at least one lower case letter.\n")
        .append("- Contains at least one upper case letter.\n")
        .append("- Contains at least one special character.\n")
        .append("- No whitespaces, tabs, etc.\n")
        .toString();

    // for creating bean for redis template
    public static final String BEAN_REDIS = "userRedis";

    // for filtering recipes
    public static final String[] DISH_TYPE = {
        "main course",
        "side dish",
        "dessert",
        "appetizer",
        "salad",
        "bread",
        "breakfast",
        "soup",
        "beverage",
        "sauce",
        "marinade",
        "fingerfood",
        "snack",
        "drink"
    };

    public static final String[] CUISINE_TYPE = {
        "african",
        "asian",
        "american",
        "british",
        "cajun",
        "caribbean",
        "chinese",
        "eastern european",
        "european",
        "french",
        "german",
        "greek",
        "indian",
        "irish",
        "italian",
        "japanese",
        "jewish",
        "korean",
        "latin american",
        "mediterranean",
        "mexican",
        "middle eastern",
        "nordic",
        "southern",
        "spanish",
        "thai",
        "vietnamese"
    };

    public static final String[] DIET_TYPE = {
        "gluten free",
        "ketogenic",
        "vegetarian",
        "lacto-vegetarian",
        "ovo-vegetarian",
        "vegan",
        "pescetarian",
        "paleo",
        "primal",
        "low fodmap",
        "whole30"
    };

    // Converts a string into PascalCase format
    public static final String getPascalCase(final String words) {
        return Stream.of(words.trim().split("\\s"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "))
                .trim();
    }
}