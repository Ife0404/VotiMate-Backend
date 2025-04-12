package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.dto.LoginRequest;
import com.codewiz.signupdemo.dto.LoginResponse;
import com.codewiz.signupdemo.dto.StudentRequest;
import com.codewiz.signupdemo.dto.StudentResponse;
import com.codewiz.signupdemo.entity.Student;
import com.codewiz.signupdemo.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public StudentResponse signup(StudentRequest request) {
        logger.info("Attempting to sign up student with matric number: {}", request.getMatricNumber());

        if (studentRepository.findByMatricNumber(request.getMatricNumber()).isPresent()) {
            logger.error("Signup failed: Matric number {} already exists", request.getMatricNumber());
            throw new IllegalArgumentException("Matric number already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        logger.info("Password hashed for matric number {}: {}", request.getMatricNumber(), hashedPassword);

        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setMatricNumber(request.getMatricNumber());
        student.setDepartment(request.getDepartment());
        student.setPassword(hashedPassword);
        student.setFaceEmbedding(request.getFaceEmbedding());

        Student savedStudent = studentRepository.save(student);
        logger.info("Student signed up successfully with ID: {}", savedStudent.getId());

        StudentResponse response = new StudentResponse();
        response.setId(savedStudent.getId());
        response.setMatricNumber(savedStudent.getMatricNumber());
        return response;
    }

    public LoginResponse login(LoginRequest request) {
        logger.info("Attempting login for matric number: {}", request.getMatricNumber());

        Student student = studentRepository.findByMatricNumber(request.getMatricNumber())
                .orElseThrow(() -> {
                    logger.error("Login failed: Invalid matric number {}", request.getMatricNumber());
                    return new IllegalArgumentException("Invalid matric number or password");
                });

        logger.info("Retrieved student: {}, stored password hash: {}", student.getMatricNumber(), student.getPassword());
        logger.info("Checking password match for {}: {}", request.getMatricNumber(), passwordEncoder.matches(request.getPassword(), student.getPassword()));
        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            logger.error("Login failed: Incorrect password for matric number {}", request.getMatricNumber());
            throw new IllegalArgumentException("Invalid matric number or password");
        }

        logger.info("Login successful for matric number: {}", request.getMatricNumber());
        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(student.getMatricNumber()));
        return response;
    }
}