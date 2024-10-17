package com.codewiz.signupdemo.dao;

import com.codewiz.signupdemo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);
    Optional<Student> findByMatricNumber(String matricNumber);

    // Method for validating login using both email and password
    Optional<Student> findByEmailAndPassword(String email, String password);

}
