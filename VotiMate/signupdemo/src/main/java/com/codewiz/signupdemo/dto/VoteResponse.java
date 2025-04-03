package com.codewiz.signupdemo.dto;

import com.codewiz.signupdemo.entity.Vote;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VoteResponse {
    private Long id;
    private String matricNumber;
    private Long candidateId;
    private String candidateName;
    private Long electionId;
    private String electionName;
    private LocalDateTime timestamp;

    public VoteResponse(Vote vote) {
        this.id = vote.getId();
        this.matricNumber = vote.getStudent().getMatricNumber();
        this.candidateId = vote.getCandidate().getId();
        this.candidateName = vote.getCandidate().getName();
        this.electionId = vote.getElection().getId();
        this.electionName = vote.getElection().getName();
        this.timestamp = vote.getTimestamp();
    }
}