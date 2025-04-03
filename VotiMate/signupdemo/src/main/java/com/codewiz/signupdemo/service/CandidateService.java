package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.CandidateRepository;
import com.codewiz.signupdemo.dao.ElectionRepository;
import com.codewiz.signupdemo.dto.CandidateRequest;
import com.codewiz.signupdemo.dto.CandidateResponse;
import com.codewiz.signupdemo.entity.Candidate;
import com.codewiz.signupdemo.entity.Election;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    public CandidateResponse addCandidate(CandidateRequest candidateRequest) {
        logger.info("Adding candidate: {} to election ID: {}", candidateRequest.getName(), candidateRequest.getElectionId());

        Election election = electionRepository.findById(candidateRequest.getElectionId())
                .orElseThrow(() -> {
                    logger.error("Election not found with ID: {}", candidateRequest.getElectionId());
                    return new IllegalArgumentException("Election not found with ID: " + candidateRequest.getElectionId());
                });

        Candidate candidate = new Candidate();
        candidate.setName(candidateRequest.getName());
        candidate.setElection(election);
        candidate.setCampaignPromises(candidateRequest.getCampaignPromises());
        candidate.setImageUrl(candidateRequest.getImageUrl());
        candidate.setDateOfBirth(candidateRequest.getDateOfBirth() != null
                ? LocalDate.parse(candidateRequest.getDateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE)
                : null);
        candidate.setPosition(candidateRequest.getPosition());
        candidate.setLevel(candidateRequest.getLevel());

        Candidate savedCandidate = candidateRepository.save(candidate);
        logger.info("Candidate added with ID: {}", savedCandidate.getId());

        return new CandidateResponse(savedCandidate);
    }

    public List<CandidateResponse> getCandidatesByElection(Long electionId) {
        logger.info("Fetching candidates for election ID: {}", electionId);
        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);
        return candidates.stream().map(CandidateResponse::new).collect(Collectors.toList());
    }
}