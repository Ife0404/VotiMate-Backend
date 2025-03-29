package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.StudentRequest;
import com.codewiz.signupdemo.entity.Student;
import com.codewiz.signupdemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/signup")
    public ResponseEntity<Student> signup(@Valid @RequestBody StudentRequest studentRequest) {
        Student student = studentService.saveStudent(studentRequest);
        return ResponseEntity.ok(student);
    }

    // Login is now handled by JwtAuthenticationFilter, so no /login endpoint here

    // New GET mapping: Retrieve student by matricNumber
    @GetMapping("/{matricNumber}")
    public ResponseEntity<Student> getStudent(@PathVariable String matricNumber) {
        Student student = studentService.findByMatricNumber(matricNumber)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with matric number: " + matricNumber));
        return ResponseEntity.ok(student);
    }

    // New DELETE mapping: Delete student by matricNumber
    @DeleteMapping("/{matricNumber}")
    public ResponseEntity<String> deleteStudent(@PathVariable String matricNumber) {
        Student student = studentService.findByMatricNumber(matricNumber)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with matric number: " + matricNumber));
        studentService.deleteStudent(student);
        return ResponseEntity.ok("Student with matric number " + matricNumber + " deleted successfully");
    }
}

