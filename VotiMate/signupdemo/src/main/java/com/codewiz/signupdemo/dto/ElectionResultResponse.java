package com.codewiz.signupdemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ElectionResultResponse {
    private Long electionId;
    private String electionName;
    private List<CandidateResult> candidateResults;
    private int totalVotes;

    @Getter
    @Setter
    public static class CandidateResult {
        private String candidateName;
        private int voteCount;
        private double percentage;

        public CandidateResult(String candidateName, int voteCount, double percentage) {
            this.candidateName = candidateName;
            this.voteCount = voteCount;
            this.percentage = percentage;
        }
    }
}
