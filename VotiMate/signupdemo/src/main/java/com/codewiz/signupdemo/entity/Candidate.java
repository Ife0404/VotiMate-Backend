package com.codewiz.signupdemo.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "campaign_promises")
    private String campaignPromises;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "position")
    private String position;

    @Column(name = "level")
    private Integer level;
}

