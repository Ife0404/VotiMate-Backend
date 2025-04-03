package com.codewiz.signupdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    @Column(name = "campaign_promises", length = 1000)
    private String campaignPromises;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "position")
    private String position;

    @Column(name = "level")
    private Integer level;
}