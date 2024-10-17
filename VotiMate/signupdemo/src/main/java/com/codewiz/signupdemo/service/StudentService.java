package com.codewiz.signupdemo.service;


import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public boolean isEmailUnique(String email) {
        // Query the StudentRepository to find a student with the given email
        Optional<Student> existingStudent = studentRepository.findByEmail(email);

        // If the Optional is empty, that means no student with the given email exists
        return existingStudent.isEmpty();
    }

    public Student saveStudent (Student student) {
        // Save the given student entity to the database using StudentRepository
        return studentRepository.save(student);
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }


    // Method to authenticate a student (login)
    public Optional<Student> login(String email, String password) {

        // To check if a student exists with the provided email and password
        return studentRepository.findByEmailAndPassword(email, password);
    }

    public void deleteAllStudents() {
        studentRepository.deleteAll();
    }


}
