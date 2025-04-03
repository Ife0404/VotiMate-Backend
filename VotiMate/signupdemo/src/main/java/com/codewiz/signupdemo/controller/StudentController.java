package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.LoginRequest;
import com.codewiz.signupdemo.dto.LoginResponse;
import com.codewiz.signupdemo.dto.StudentRequest;
import com.codewiz.signupdemo.dto.StudentResponse;
import com.codewiz.signupdemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/signup")
    public ResponseEntity<StudentResponse> signup(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.signup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = studentService.login(request);
        return ResponseEntity.ok(response);
    }
}
