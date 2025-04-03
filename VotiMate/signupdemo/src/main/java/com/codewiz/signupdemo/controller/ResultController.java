package com.codewiz.signupdemo.controller;

import com.codewiz.signupdemo.dto.ElectionResultResponse;
import com.codewiz.signupdemo.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping("/{electionId}")
    public ResponseEntity<ElectionResultResponse> getElectionResult(@PathVariable Long electionId) {
        ElectionResultResponse result = resultService.getElectionResult(electionId);
        return ResponseEntity.ok(result);
    }
}




