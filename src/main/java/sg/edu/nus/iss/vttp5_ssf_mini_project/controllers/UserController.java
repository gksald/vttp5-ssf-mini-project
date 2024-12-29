package sg.edu.nus.iss.vttp5_ssf_mini_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5_ssf_mini_project.models.User;
import sg.edu.nus.iss.vttp5_ssf_mini_project.services.UserService;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping({ "/", "/homepage"})
    public ModelAndView getHomePage(HttpSession sess) {

        ModelAndView mav = new ModelAndView();

        if (sess.getAttribute("loggedUser") == null) {

            mav.setViewName("homepage");
        } else {

            User loggedUser = (User) sess.getAttribute("loggedUser");

            mav.addObject("loggedUser", loggedUser);
            mav.setViewName("dashboard");
        }

        return mav;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterUser() {

        ModelAndView mav = new ModelAndView();

        mav.addObject("user", new User());

        mav.setViewName("registeruser");
        return mav;
    }

    @PostMapping("/register/post")
    public ModelAndView postRegisterUser(@Valid @ModelAttribute User user, BindingResult results) {

        ModelAndView mav = new ModelAndView();

        // syntactic validation errors
        if (results.hasErrors()) {

            mav.setViewName("registeruser");
            mav.setStatus(HttpStatus.BAD_REQUEST); // 400 BAD REQUEST

            return mav;
        }

        // semantic validation error 1 : username already exists in database
        if (userService.usernameExists(user.getUsername())) {

            mav.addObject("usernameExists", true);

            mav.setViewName("registeruser");
            mav.setStatus(HttpStatus.BAD_REQUEST); // 400 BAD REQUEST

            return mav;
        }

        // semantic validation error 2 : email already exists in database
        if (userService.emailExists(user.getEmail())) {

            mav.addObject("emailExists", true);

            mav.setViewName("registeruser");
            mav.setStatus(HttpStatus.BAD_REQUEST); // 400 BAD REQUEST

            return mav;
        }

        String userID = userService.generateUserID();
        user.setUserID(userID);

        userService.saveUser(user);

        mav.addObject("successfulRegistration", true);

        mav.setViewName("loginuser");
        mav.setStatus(HttpStatus.CREATED); // 201 CREATED

        return mav;
    }

    @GetMapping("/login")
    public ModelAndView getLoginUser() {

        ModelAndView mav = new ModelAndView();

        mav.addObject("user", new User());

        mav.setViewName("loginuser");
        return mav;
    }

    @PostMapping("/login/post")
    public ModelAndView postLoginUser(@ModelAttribute User user, HttpSession sess) {

        ModelAndView mav = new ModelAndView();

        // semantic validation error 1 : username does not exist in DB
        if (!userService.usernameExists(user.getUsername())) {

            mav.addObject("usernameNotFound", true);

            mav.setViewName("loginuser");
            mav.setStatus(HttpStatusCode.valueOf(400));

            return mav;
        }

        // semantic validation error 2 : password does not match correct username
        if (!userService.isCorrectMatch(user.getUsername(), user.getPassword())) {

            mav.addObject("incorrectPassword", true);

            mav.setViewName("loginuser");
            mav.setStatus(HttpStatusCode.valueOf(400));

            return mav;
        }

        User loggedUser = userService.loadUser(user.getUsername());
        sess.setAttribute("loggedUser", loggedUser);

        mav.addObject("loggedUser", loggedUser);

        mav.setViewName("dashboard");
        mav.setStatus(HttpStatusCode.valueOf(200));

        return mav;
    }
}
