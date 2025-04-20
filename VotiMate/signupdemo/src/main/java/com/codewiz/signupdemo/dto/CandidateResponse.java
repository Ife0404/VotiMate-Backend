package com.codewiz.signupdemo.dto;

import com.codewiz.signupdemo.entity.Candidate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandidateResponse {
    private Long id;
    private String name;
    private Long electionId;
    private String campaignPromises;
    private String imageUrl;
    private String position;
    private Integer level;

    // Constructor for mapping from Candidate entity
    public CandidateResponse(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.electionId = candidate.getElection().getId();
        this.campaignPromises = candidate.getCampaignPromises();
        this.imageUrl = candidate.getImageUrl();
        this.position = candidate.getPosition();
        this.level = candidate.getLevel();
    }
}
