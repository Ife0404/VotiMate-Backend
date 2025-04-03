package com.codewiz.signupdemo.dto;

import com.codewiz.signupdemo.entity.Candidate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id", "name", "position", "level", "campaignPromises", "imageUrl", "dateOfBirth", "electionId"})
public class CandidateResponse {
    private Long id;
    private String name;
    private Long electionId;
    private String campaignPromises;
    private String imageUrl;
    private String dateOfBirth;
    private String position;
    private Integer level;

    public CandidateResponse(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.electionId = candidate.getElection().getId();
        this.campaignPromises = candidate.getCampaignPromises();
        this.imageUrl = candidate.getImageUrl();
        this.dateOfBirth = candidate.getDateOfBirth() != null ? candidate.getDateOfBirth().toString() : null;
        this.position = candidate.getPosition();
        this.level = candidate.getLevel();
    }
}
