package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.ElectionRequest;
import com.codewiz.signupdemo.dto.ElectionResponse;
import com.codewiz.signupdemo.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @PostMapping
    public ResponseEntity<ElectionResponse> createElection(@Valid @RequestBody ElectionRequest electionRequest) {
        ElectionResponse response = electionService.createElection(electionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ElectionResponse>> getAllElections() {
        List<ElectionResponse> elections = electionService.getAllElections();
        return ResponseEntity.ok(elections);
    }
}

