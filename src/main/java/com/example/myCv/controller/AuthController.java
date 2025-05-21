package com.example.myCv.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myCv.model.User;
import com.example.myCv.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               HttpSession session) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=Password+Mismatch";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/register?error=Email+Already+Exists";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        session.setAttribute("username", user.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Authentication authentication, Model model) {
        // If no session yet, fetch the logged-in user via Spring Security
        if (session.getAttribute("username") == null && authentication != null) {
            String email = authentication.getName(); // logged in email
            Optional<User> user = userRepository.findByEmail(email);
            user.ifPresent(value -> session.setAttribute("username", value.getName()));
        }

        model.addAttribute("username", session.getAttribute("username"));
        return "dashboard";
    }
}
