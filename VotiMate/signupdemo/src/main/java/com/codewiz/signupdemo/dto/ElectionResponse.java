package com.codewiz.signupdemo.dto;

import com.codewiz.signupdemo.entity.Election;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ElectionResponse {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<CandidateResponse> candidates;

    public ElectionResponse(Election election) {
        this.id = election.getId();
        this.name = election.getName();
        this.startDate = election.getStartDate();
        this.endDate = election.getEndDate();
        this.candidates = election.getCandidates() != null ?
                election.getCandidates().stream()
                        .map(CandidateResponse::new)
                        .collect(Collectors.toList()) :
                List.of(); // Empty list if no candidates
    }
}
