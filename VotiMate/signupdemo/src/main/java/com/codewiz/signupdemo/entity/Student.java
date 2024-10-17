package com.codewiz.signupdemo.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StudentID", nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "Name is required")
    @Column(name = "StudName", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Department is required")
    @Column(name = "StudDepartment", nullable = false, length = 50)
    private String department;

    @NotNull(message = "Matric Number is required")
    @Column(name = "StudMatric", unique = true, nullable = false, length = 11)
    private String matricNumber;

    @NotNull(message = "Level is required")
    @Column(name = "StudLevel", nullable = false)
    private int level;

    @Column(name = "StudEmail", unique = true, nullable = false, length = 50)
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min=8, message = "Password must be at least 8 characters long")
    @Column(name = "StudPassword", nullable = false, length = 50)
    private String password;

    public Student() {

    }

    public Student( String name, String department, String matricNumber, int level, String email, String password) {
        this.name = name;
        this.department = department;
        this.matricNumber = matricNumber;
        this.level = level;
        this.email = email;
        this.password = password;
    }

    public @NotNull(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Name is required") String name) {
        this.name = name;
    }

    public @NotNull(message = "Department is required") String getDepartment() {
        return department;
    }

    public void setDepartment(@NotNull(message = "Department is required") String department) {
        this.department = department;
    }

    public @NotNull(message = "Matric Number is required") String getMatricNumber() {
        return matricNumber;
    }

    public void setMatricNumber(@NotNull(message = "Matric Number is required") String matricNumber) {
        this.matricNumber = matricNumber;
    }

    @NotNull(message = "Level is required")
    public int getLevel() {
        return level;
    }

    public void setLevel(@NotNull(message = "Level is required") int level) {
        this.level = level;
    }

    public @NotNull(message = "Email is required") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull(message = "Email is required") String email) {
        this.email = email;
    }

    public @NotNull(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password) {
        this.password = password;
    }
}
