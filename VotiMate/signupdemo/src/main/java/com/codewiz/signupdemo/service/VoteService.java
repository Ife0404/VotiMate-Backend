package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.CandidateRepository;
import com.codewiz.signupdemo.dao.ElectionRepository;
import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.dao.VoteRepository;
import com.codewiz.signupdemo.dto.VoteRequest;
import com.codewiz.signupdemo.dto.VoteResponse;
import com.codewiz.signupdemo.entity.Candidate;
import com.codewiz.signupdemo.entity.Election;
import com.codewiz.signupdemo.entity.Student;
import com.codewiz.signupdemo.entity.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VoteService {

    private static final Logger logger = LoggerFactory.getLogger(VoteService.class);

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Transactional
    public VoteResponse castVote(VoteRequest voteRequest) {
        logger.info("Casting vote for matric number: {} in election ID: {}", voteRequest.getMatricNumber(), voteRequest.getElectionId());

        Student student = studentRepository.findByMatricNumber(voteRequest.getMatricNumber())
                .orElseThrow(() -> {
                    logger.error("Student not found with matric number: {}", voteRequest.getMatricNumber());
                    return new IllegalArgumentException("Student not found with matric number: " + voteRequest.getMatricNumber());
                });

        Election election = electionRepository.findById(voteRequest.getElectionId())
                .orElseThrow(() -> {
                    logger.error("Election not found with ID: {}", voteRequest.getElectionId());
                    return new IllegalArgumentException("Election not found with ID: " + voteRequest.getElectionId());
                });

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(election.getStartDate()) || now.isAfter(election.getEndDate())) {
            logger.error("Voting attempted outside election period for election ID: {}", election.getId());
            throw new IllegalStateException("Voting is not allowed outside the election period");
        }

        if (voteRepository.findByStudentMatricNumberAndElectionId(voteRequest.getMatricNumber(), voteRequest.getElectionId()).isPresent()) {
            logger.error("Student with matric number {} has already voted in election ID: {}", voteRequest.getMatricNumber(), election.getId());
            throw new IllegalStateException("Student has already voted in this election");
        }

        Candidate candidate = candidateRepository.findById(voteRequest.getCandidateId())
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", voteRequest.getCandidateId());
                    return new IllegalArgumentException("Candidate not found with ID: " + voteRequest.getCandidateId());
                });

        if (!candidate.getElection().getId().equals(election.getId())) {
            logger.error("Candidate ID {} does not belong to election ID: {}", candidate.getId(), election.getId());
            throw new IllegalArgumentException("Candidate does not belong to this election");
        }

        Vote vote = new Vote();
        vote.setStudent(student);
        vote.setCandidate(candidate);
        vote.setElection(election);
        vote.setTimestamp(now);

        Vote savedVote = voteRepository.save(vote);
        logger.info("Vote cast successfully with ID: {}", savedVote.getId());

        return new VoteResponse(savedVote);
    }
}

