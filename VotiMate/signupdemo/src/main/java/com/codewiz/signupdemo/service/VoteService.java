package com.codewiz.signupdemo.service;

import com.codewiz.signupdemo.dao.CandidateRepository;
import com.codewiz.signupdemo.dao.StudentRepository;
import com.codewiz.signupdemo.entity.Vote;
import com.codewiz.signupdemo.dao.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    public Vote castVote(Vote vote) {
        vote.setVoteDate(LocalDateTime.now());
        return voteRepository.save(vote);
    }

    public List<Vote> getVotesByUser(Long userId) {
        return voteRepository.findByUserId(userId);
    }
}
