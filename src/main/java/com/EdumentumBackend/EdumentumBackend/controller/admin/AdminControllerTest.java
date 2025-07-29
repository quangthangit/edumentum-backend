package com.EdumentumBackend.EdumentumBackend.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminControllerTest {

    @GetMapping("/test")
    public String adminGet() {
        return "This is Admin API";
    }
}
