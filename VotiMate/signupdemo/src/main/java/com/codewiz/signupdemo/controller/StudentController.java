package com.codewiz.signupdemo.controller;


import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.dto.LoginRequest;
import com.codewiz.signupdemo.entity.Student;
import com.codewiz.signupdemo.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/student")
public class StudentController {

    @Autowired
    public StudentService studentService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerStudent (@Valid @RequestBody Student student) {
        if (!studentService.isEmailUnique(student.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use.");
        }

        studentService.saveStudent(student);
        return ResponseEntity.ok("Student has been registered successfully");
    }

    //Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        Optional<Student> studentOpt = studentService.findByEmail(loginRequest.getEmail());


        if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(loginRequest.getPassword())) {
            //Login successful
            return ResponseEntity.ok("Login Successful");
        } else {
            //Login failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }


    //Delete endpoint
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllStudents() {
        studentService.deleteAllStudents();
        return ResponseEntity.ok("All students have been deleted.");
    }


}


