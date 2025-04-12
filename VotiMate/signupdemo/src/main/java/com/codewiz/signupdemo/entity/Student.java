package com.codewiz.signupdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "matric_number", unique = true)
    private String matricNumber;

    @Column(name = "department")
    private String department;

    @Column(name = "password")
    private String password;

    @Column(name = "face_embedding", columnDefinition = "BLOB")
    private byte[] faceEmbedding; // Added for face recognition

    @OneToMany(mappedBy = "student")
    private List<Vote> votes;
}