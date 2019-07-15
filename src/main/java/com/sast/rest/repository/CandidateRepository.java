package com.sast.rest.repository;

import com.sast.rest.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for the Candidate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByFirstNameLikeOrLastNameLike(String firstName, String lastName);

}
