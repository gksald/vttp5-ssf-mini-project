package sg.edu.nus.iss.vttp5_ssf_mini_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.User;
import sg.edu.nus.iss.vttp5_ssf_mini_project.services.UserService;
import sg.edu.nus.iss.vttp5_ssf_mini_project.services.RecipeService;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired
    private UserService userSvc;

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/dashboard")
    public ModelAndView getDashboardPage(HttpSession sess) {

        ModelAndView mav = new ModelAndView();

        if (sess.getAttribute("loggedUser") != null) {

            User loggedUser = (User) sess.getAttribute("loggedUser");

            mav.addObject("loggedUser", loggedUser);
            mav.setViewName("dashboard");
        }

        else {
            mav.setViewName("homepage");
        }

        return mav;
    }

    @GetMapping("/logout")
    public ModelAndView logoutUser(HttpSession sess) {

        ModelAndView mav = new ModelAndView();

        sess.invalidate();

        mav.setViewName("homepage");
        return mav;
    }

    @GetMapping("/profile/{userID}")
    public ModelAndView getUserProfile(@PathVariable String userID, HttpSession sess) {

        ModelAndView mav = new ModelAndView();

        User loggedUser = (User) sess.getAttribute("loggedUser");

        // checks if the user is not logged in
        if (null == loggedUser) {

            mav.addObject("errorMessage", " to view account details.");
            mav.setViewName("accesserror1");
            mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
        }

        // checks if the requested user ID does not exist in the database
        else if (!userSvc.hasUserIDKey(userID)) {

            mav.addObject("errorMessage", "UserID %s is not found.".formatted(userID));
            mav.setViewName("accesserror2");
            mav.setStatus(HttpStatus.NOT_FOUND); // 404 NOT FOUND
        }

        // checks if the requested user ID is the same as the logged user ID (prevent user from viewing other profiles)
        else if (!userID.equals(loggedUser.getUserID())) {

            mav.addObject("errorMessage", "Unrestricted access to other account details.");
            mav.setViewName("accesserror3");
            mav.setStatus(HttpStatus.UNAUTHORIZED); // 401 UNAUTHORIZED
        }

        // user is logged in and the requested user ID exists
        else {

            mav.addObject("loggedUser", loggedUser);
            mav.addObject("favourites", recipeService.getFavouritesQuantity(userID));

            mav.setViewName("userprofile");
            mav.setStatus(HttpStatus.OK); // 200 OK
        }

        return mav;
    }
}