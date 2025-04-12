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

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    public CandidateResponse createCandidate(CandidateRequest request) {
        logger.info("Creating candidate: {}", request.getName());

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> {
                    logger.error("Election not found with ID: {}", request.getElectionId());
                    return new IllegalArgumentException("Election not found");
                });

        Candidate candidate = new Candidate();
        candidate.setName(request.getName());
        candidate.setElection(election);
        candidate.setCampaignPromises(request.getCampaignPromises());
        candidate.setImageUrl(request.getImageUrl());
        candidate.setPosition(request.getPosition());
        candidate.setLevel(request.getLevel());

        Candidate savedCandidate = candidateRepository.save(candidate);
        logger.info("Candidate created with ID: {}", savedCandidate.getId());

        CandidateResponse response = new CandidateResponse();
        response.setId(savedCandidate.getId());
        response.setName(savedCandidate.getName());
        response.setElectionId(savedCandidate.getElection().getId());
        response.setCampaignPromises(savedCandidate.getCampaignPromises());
        response.setImageUrl(savedCandidate.getImageUrl());
        response.setPosition(savedCandidate.getPosition());
        response.setLevel(savedCandidate.getLevel());

        return response;
    }
}
