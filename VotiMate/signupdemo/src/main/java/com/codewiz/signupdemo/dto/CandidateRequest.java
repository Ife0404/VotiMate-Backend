package com.codewiz.signupdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Election ID is required")
    private Long electionId;

    @NotBlank(message = "Campaign promises are required")
    private String campaignPromises;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Level is required")
    private Integer level;
}

