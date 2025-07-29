package com.EdumentumBackend.EdumentumBackend.controller.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
public class StudentControllerTest {

    @GetMapping("/test")
    public String studentGet() {
        return "This is Student API";
    }
}
