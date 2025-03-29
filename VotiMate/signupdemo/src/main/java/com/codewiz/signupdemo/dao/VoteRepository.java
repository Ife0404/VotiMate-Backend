package com.codewiz.signupdemo.dao;

import com.codewiz.signupdemo.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByUserId(Long userId);
    Page<Vote> findByUserId(Long userId, Pageable pageable);
    List<Vote> findByElectionId(Long electionId);
}
