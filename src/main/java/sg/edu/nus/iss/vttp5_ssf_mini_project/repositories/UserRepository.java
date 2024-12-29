package sg.edu.nus.iss.vttp5_ssf_mini_project.repositories;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.vttp5_ssf_mini_project.models.User;
import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.PasswordHasher;
import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.Utility;

@Repository
public class UserRepository {

    @Autowired
    @Qualifier(Utility.BEAN_REDIS)
    private RedisTemplate<String, String> template;
    
    public Set<String> getAllUserIDKeys() {
    
      Set<String> keys = template.keys("*");
    
      // avoid checking keys that are not userID (filter off all recipeID and savedrecipesID keys)
      Set<String> filteredKeys = keys.stream()
          .filter(key -> !key.startsWith("DrecipeID") && !key.startsWith("DsavedrecipesID"))
          .collect(Collectors.toSet());
    
      return filteredKeys;
    }
  
    public boolean usernameExists(String username) {
        
        Set<String> userIDKeys = getAllUserIDKeys();
        
        for (String userIDKey : userIDKeys) {
          Map<Object, Object> entries = template.opsForHash().entries(userIDKey);
          Object usernameInMap = entries.get("username");
          
          if (usernameInMap != null && usernameInMap.equals(username)) {
              return true;
          }
      }

        return false;
    }

    

  public boolean emailExists(String email) {

    Set<String> userIDKeys = getAllUserIDKeys();

    for (String userIDKey : userIDKeys) {
        Map<Object, Object> entries = template.opsForHash().entries(userIDKey);

        // Check if the "email" key exists and its value matches the input
        Object emailInMap = entries.get("email");
        if (emailInMap != null && emailInMap.equals(email)) {
            return true;
        }
    }

    return false;
}

  public boolean isCorrectMatch(String username, String password) {

    Set<String> userIDKeys = getAllUserIDKeys();

    for (String userIDKey : userIDKeys) {
        Map<Object, Object> entries = template.opsForHash().entries(userIDKey);

        // Check if the "username" exists and matches the input
        Object usernameInMap = entries.get("username");
        if (usernameInMap != null && usernameInMap.equals(username)) {

            // Check if the "password" exists and validate it
            Object passwordInMap = entries.get("password");
            if (passwordInMap != null && PasswordHasher.checkPassword(password, passwordInMap.toString())) {
                return true;
            }
        }
    }

    return false;
}


  public boolean hasUserIDKey(String userID) {

    return template.hasKey(userID);
  }

  public void saveUser(User user) {

    String userID = user.getUserID();

    template.opsForHash().put(userID, "username", user.getUsername());
    template.opsForHash().put(userID, "password", user.getPassword());
    template.opsForHash().put(userID, "name", user.getName());
    template.opsForHash().put(userID, "email", user.getEmail());
    template.opsForHash().put(userID, "birthDate", user.getBirthDate().toString());
    template.opsForHash().put(userID, "age", user.getAge().toString());
  }

  public User loadUser(String username) {
    User loggedUser = null;
    Set<String> userIDKeys = getAllUserIDKeys();

    for (String userIDKey : userIDKeys) {
        Map<Object, Object> entries = template.opsForHash().entries(userIDKey);

        // Check if "username" exists and matches the provided username
        if (entries.get("username") != null && entries.get("username").equals(username)) {
            // Fetch all fields
            String password = (String) template.opsForHash().get(userIDKey, "password");
            String name = (String) template.opsForHash().get(userIDKey, "name");
            String email = (String) template.opsForHash().get(userIDKey, "email");
            String birthDateStr = (String) template.opsForHash().get(userIDKey, "birthDate");
            String ageStr = (String) template.opsForHash().get(userIDKey, "age");

            // Validate fields
            if (password == null || name == null || email == null || birthDateStr == null || ageStr == null) {
                throw new IllegalArgumentException("Missing required user data in Redis for key: " + userIDKey);
            }

            try {
                LocalDate birthDate = LocalDate.parse(birthDateStr);
                Integer age = Integer.parseInt(ageStr);

                // Create User object
                loggedUser = new User(username, password, name, email, birthDate, age);
                loggedUser.setUserID(userIDKey);
            } catch (DateTimeParseException | NumberFormatException e) {
                throw new IllegalArgumentException("Invalid data format for user key: " + userIDKey, e);
            }

            break;
        }
    }

    return loggedUser;
}

  
}
