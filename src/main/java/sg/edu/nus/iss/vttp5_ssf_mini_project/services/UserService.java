package sg.edu.nus.iss.vttp5_ssf_mini_project.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.vttp5_ssf_mini_project.models.User;
import sg.edu.nus.iss.vttp5_ssf_mini_project.repositories.UserRepository;
import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.PasswordHasher;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public boolean usernameExists(String username) {

        return userRepo.usernameExists(username);
      }
    
      public boolean emailExists(String email) {
    
        return userRepo.emailExists(email);
      }
    
      public boolean isCorrectMatch(String username, String password) {
    
        return userRepo.isCorrectMatch(username, password);
      }
    
      public boolean hasUserIDKey(String userID) {
    
        return userRepo.hasUserIDKey(userID);
      }
    
      public String generateUserID() {
    
        UUID userIDInUUID = UUID.randomUUID();
        String userIDInString = userIDInUUID.toString().substring(0, 8);
    
        return userIDInString;
      }
    
      public void saveUser(User user) {
    
        String plainPassword = user.getPassword();
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
    
        user.setPassword(hashedPassword);
    
        userRepo.saveUser(user);
      }
    
      public User loadUser(String username) {
    
        User loggedUser = userRepo.loadUser(username);
    
        return loggedUser;
      }

//     // this whole block KIV:
//     @Autowired
//     // @Qualifier("notice")
//     RedisTemplate<String, Object> redisTemplate;

//     public boolean isUsernameUnique(String username) {
//         return !userRepository.isUsernameTaken(username);
//     }
    
//     public boolean isEmailUnique(String email) {
//         return !userRepository.isEmailTaken(email);
//     }

//     // Register user by first checking username and email uniqueness
//     public void registerUser(User user) {
//         // Ensure username and email are unique before saving
//         if (!isUsernameUnique(user.getUsername())) {
//             throw new RuntimeException("Username already taken.");
//         }

//         if (!isEmailUnique(user.getEmail())) {
//             throw new RuntimeException("Email already registered.");
//         }

//         // Save the user if unique
//         userRepository.save(user);
//     }
}
