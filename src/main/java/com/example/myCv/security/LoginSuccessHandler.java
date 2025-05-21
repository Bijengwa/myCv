package com.example.myCv.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.myCv.model.User;
import com.example.myCv.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName(); // the email used to login

        Optional<User> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(user -> {
            HttpSession session = request.getSession();
            session.setAttribute("username", user.getName()); // 🧠 Store user's name in session
        });

        response.sendRedirect("/dashboard");
    }
}
