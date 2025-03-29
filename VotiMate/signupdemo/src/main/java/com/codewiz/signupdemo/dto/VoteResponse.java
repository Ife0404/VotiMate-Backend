package com.codewiz.signupdemo.dto;

import com.codewiz.signupdemo.entity.Vote;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VoteResponse {

    private Long id;
    private Long userId;
    private Long candidateId;
    private Long electionId;
    private LocalDateTime voteDate;
    private String candidateName;

    public VoteResponse(Vote vote, String candidateName) {
        this.id = vote.getId();
        this.userId = vote.getUserId();
        this.candidateId = vote.getCandidateId();
        this.electionId = vote.getElectionId();
        this.voteDate = vote.getVoteDate();
        this.candidateName = candidateName;
    }

}
