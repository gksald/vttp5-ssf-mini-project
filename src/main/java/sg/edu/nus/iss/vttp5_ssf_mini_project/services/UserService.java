package sg.edu.nus.iss.vttp5_ssf_mini_project.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
        return userIDInUUID.toString().substring(0, 8);
    }

    public void saveUser(User user) {
        String plainPassword = user.getPassword();
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        userRepo.saveUser(user);
    }

    public User loadUser(String username) {
        return userRepo.loadUser(username);
    }
}
