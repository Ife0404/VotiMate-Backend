package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.ElectionRepository;
import com.codewiz.signupdemo.dto.ElectionRequest;
import com.codewiz.signupdemo.dto.ElectionResponse;
import com.codewiz.signupdemo.entity.Election;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionService.class);

    @Autowired
    private ElectionRepository electionRepository;

    public ElectionResponse createElection(ElectionRequest electionRequest) {
        logger.info("Creating election: {}", electionRequest.getName());

        LocalDateTime startDate = LocalDateTime.parse(electionRequest.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDate = LocalDateTime.parse(electionRequest.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (endDate.isBefore(startDate)) {
            logger.error("End date {} is before start date {}", endDate, startDate);
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Election election = new Election();
        election.setName(electionRequest.getName());
        election.setStartDate(startDate);
        election.setEndDate(endDate);

        Election savedElection = electionRepository.save(election);
        logger.info("Election created with ID: {}", savedElection.getId());

        return new ElectionResponse(savedElection);
    }

    public List<ElectionResponse> getAllElections() {
        logger.info("Fetching all elections");
        return electionRepository.findAll().stream()
                .map(ElectionResponse::new)
                .collect(Collectors.toList());
    }
}