package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.VoteRequest;
import com.codewiz.signupdemo.dto.VoteResponse;
import com.codewiz.signupdemo.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponse> castVote(@Valid @RequestBody VoteRequest voteRequest) {
        VoteResponse voteResponse = voteService.castVote(voteRequest);
        return new ResponseEntity<>(voteResponse, HttpStatus.CREATED);
    }
}