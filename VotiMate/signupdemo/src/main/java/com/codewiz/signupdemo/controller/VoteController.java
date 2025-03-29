package com.codewiz.signupdemo.controller;


import com.codewiz.signupdemo.entity.Vote;
import com.codewiz.signupdemo.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ResponseEntity<Vote> castVote(@RequestBody Vote vote) {
        return ResponseEntity.ok(voteService.castVote(vote));
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getVotesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(voteService.getVotesByUser(userId));
    }
}
