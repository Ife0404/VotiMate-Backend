package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.dto.StudentRequest;
import com.codewiz.signupdemo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Student saveStudent(StudentRequest studentRequest) {
        if (studentRepository.findByMatricNumber(studentRequest.getMatricNumber()).isPresent()) {
            throw new IllegalArgumentException("Matric number already exists");
        }
        Student student = new Student(
                studentRequest.getFirstName(),
                studentRequest.getLastName(),
                studentRequest.getMatricNumber(),
                studentRequest.getDepartment(),
                passwordEncoder.encode(studentRequest.getPassword())
        );
        return studentRepository.save(student);
    }

    public Optional<Student> findByMatricNumber(String matricNumber) {
        return studentRepository.findByMatricNumber(matricNumber);
    }

    // New method for deletion
    public void deleteStudent(Student student) {
        studentRepository.delete(student);
    }

}