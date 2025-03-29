package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StudentDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String matricNumber) throws UsernameNotFoundException {
        Student student = studentRepository.findByMatricNumber(matricNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with matric number: " + matricNumber));
        return new User(student.getMatricNumber(), student.getPassword(), new ArrayList<>());
    }
}
