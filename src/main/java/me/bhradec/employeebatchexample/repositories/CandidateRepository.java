package me.bhradec.employeebatchexample.repositories;

import me.bhradec.employeebatchexample.domain.Candidate;
import me.bhradec.employeebatchexample.domain.enums.CandidateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Page<Candidate> findByStatus(CandidateStatus status, Pageable pageable);
}
