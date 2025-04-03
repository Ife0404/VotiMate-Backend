package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.CandidateRequest;
import com.codewiz.signupdemo.dto.CandidateResponse;
import com.codewiz.signupdemo.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping
    public ResponseEntity<CandidateResponse> addCandidate(@Valid @RequestBody CandidateRequest candidateRequest) {
        CandidateResponse response = candidateService.addCandidate(candidateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/election/{electionId}")
    public ResponseEntity<List<CandidateResponse>> getCandidatesByElection(@PathVariable Long electionId) {
        List<CandidateResponse> responses = candidateService.getCandidatesByElection(electionId);
        return ResponseEntity.ok(responses);
    }
}