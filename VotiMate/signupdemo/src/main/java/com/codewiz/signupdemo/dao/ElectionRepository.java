package com.codewiz.signupdemo.dao;

import com.codewiz.signupdemo.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Long> {
}
