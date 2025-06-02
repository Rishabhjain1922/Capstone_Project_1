package Capstone_Project_1.Evert.Organizer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "login.html"; // This tells Spring Boot to serve static/login.html
    }
}

