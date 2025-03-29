package com.codewiz.signupdemo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long Id;

    @Getter @Setter
    @Column(nullable = false)
    private String firstName;

    @Getter @Setter
    @Column(nullable = false)
    private String lastName;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String matricNumber;

    @Getter @Setter
    @Column(nullable = false)
    private String department;

    @Getter @Setter
    @Column(nullable = false)
    private String password;

    public Student(String firstName, String lastName, String matricNumber, String department, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.matricNumber = matricNumber;
        this.department = department;
        this.password = password;
    }
}
