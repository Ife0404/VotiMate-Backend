package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class StudentDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(StudentDetailsService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String matricNumber) throws UsernameNotFoundException {
        logger.info("Loading user by matric number: {}", matricNumber);
        Student student = studentRepository.findByMatricNumber(matricNumber)
                .orElseThrow(() -> {
                    logger.error("Student not found: {}", matricNumber);
                    return new UsernameNotFoundException("Student not found: " + matricNumber);
                });
        logger.info("User loaded: {}, password hash: {}", student.getMatricNumber(), student.getPassword());
        return new User(student.getMatricNumber(), student.getPassword(), Collections.emptyList());
    }
}
