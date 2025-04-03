package com.codewiz.signupdemo.dao;

import com.codewiz.signupdemo.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByStudentMatricNumberAndElectionId(String matricNumber, Long electionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.candidate.id = :candidateId")
    int countVotesByCandidateId(Long candidateId);
}