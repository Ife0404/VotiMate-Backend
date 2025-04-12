package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.ElectionRepository;
import com.codewiz.signupdemo.dao.VoteRepository;
import com.codewiz.signupdemo.dto.ElectionResultResponse;
import com.codewiz.signupdemo.entity.Candidate;
import com.codewiz.signupdemo.entity.Election;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private VoteRepository voteRepository;

    public ElectionResultResponse getElectionResult(Long electionId) {
        logger.info("Fetching results for election ID: {}", electionId);

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> {
                    logger.error("Election not found with ID: {}", electionId);
                    return new IllegalArgumentException("Election not found with ID: " + electionId);
                });

        List<ElectionResultResponse.CandidateResult> candidateResults = election.getCandidates().stream()
                .map(candidate -> {
                    int voteCount = voteRepository.countVotesByCandidateId(candidate.getId());
                    return new ElectionResultResponse.CandidateResult(candidate.getName(), voteCount, 0.0);
                })
                .sorted((a, b) -> Integer.compare(b.getVoteCount(), a.getVoteCount()))
                .collect(Collectors.toList());

        int totalVotes = candidateResults.stream()
                .mapToInt(ElectionResultResponse.CandidateResult::getVoteCount)
                .sum();

        if (totalVotes > 0) {
            candidateResults.forEach(result -> {
                double percentage = (result.getVoteCount() * 100.0) / totalVotes;
                result.setPercentage(Math.round(percentage));
            });
        }

        ElectionResultResponse response = new ElectionResultResponse();
        response.setElectionId(election.getId());
        response.setElectionName(election.getName());
        response.setCandidateResults(candidateResults);
        response.setTotalVotes(totalVotes);

        logger.info("Results fetched for election ID: {} with total votes: {}", electionId, totalVotes);
        return response;
    }
}