package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.CandidateRequest;
import com.codewiz.signupdemo.dto.CandidateResponse;
import com.codewiz.signupdemo.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping
    public ResponseEntity<CandidateResponse> createCandidate(@Valid @RequestBody CandidateRequest request) {
        CandidateResponse response = candidateService.createCandidate(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}