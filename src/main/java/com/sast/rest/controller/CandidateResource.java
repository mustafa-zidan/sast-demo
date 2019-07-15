package com.sast.rest.controller;

import com.sast.rest.controller.util.HeaderUtil;
import com.sast.rest.controller.util.ResponseUtil;
import com.sast.rest.domain.Availability;
import com.sast.rest.domain.Candidate;
import com.sast.rest.domain.TimeSlot;
import com.sast.rest.repository.CandidateRepository;
import com.sast.rest.repository.TimeSlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing candidates.
 */
@RestController
@RequestMapping("/v1/candidates")
public class CandidateResource {

    private final Logger log = LoggerFactory.getLogger(CandidateResource.class);

    private static final String ENTITY_NAME = "candidate";

    private final CandidateRepository candidateRepository;

    private final TimeSlotRepository timeSlotRepository;


    public CandidateResource(CandidateRepository candidateRepository, TimeSlotRepository timeSlotRepository) {
        this.candidateRepository = candidateRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * POST  / : Create a new candidate.
     *
     * @param candidate the candidate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new candidate, or with status 400 (Bad Request) if the candidate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/")
    public ResponseEntity<Candidate> getCandidate(@Valid @RequestBody Candidate candidate) throws URISyntaxException {
        log.debug("REST request to save Candidate : {}", candidate);
        if (candidate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new candidate cannot already have an ID")).body(null);
        }
        Candidate result = candidateRepository.save(candidate);
        return ResponseEntity.created(new URI("/v1/candidates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  / : Updates an existing Candidate.
     *
     * @param candidate the Candidate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated Candidate,
     * or with status 400 (Bad Request) if the Candidate is not valid,
     * or with status 500 (Internal Server Error) if the Candidate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/")
    public ResponseEntity<Candidate> updateCandidate(@Valid @RequestBody Candidate candidate) throws URISyntaxException {
        log.debug("REST request to update Candidate : {}", candidate);
        if (candidate.getId() == null) {
            return getCandidate(candidate);
        }
        Candidate result = candidateRepository.save(candidate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, candidate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /people : get all the candidates.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of people in body
     */
    @GetMapping("/")
    public List<Candidate> getAllCandidates() {
        log.debug("REST request to get all Candidates");
        return candidateRepository.findAll();
        }

    /**
     * GET  /:id : get the "id" candidate.
     *
     * @param id the id of the candidate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the candidate, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        log.debug("REST request to get Candidate : {}", id);
        Optional<Candidate> candidate = candidateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(candidate);
    }

    /**
     * DELETE  /:id : delete the "id" candidate.
     *
     * @param id the id of the candidate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletCandidate(@PathVariable Long id) {
        log.debug("REST request to delete Candidate : {}", id);
        candidateRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }


    /**
     * SEARCH /search?query=:query : search for the candidate corresponding
     * to the query.
     *
     * @param query the query of the candidate search
     * @return the result of the search
     */
    @GetMapping("/search")
    public List<Candidate> searchPeopleByNameAndGender(@RequestParam String query) {
        log.debug("REST request to search Candidate for query {}", query);
        return StreamSupport
                .stream(candidateRepository.findByFirstNameLikeOrLastNameLike(query, query).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * SUBMIT Availability  /submit-availability search for the employees corresponding
     * to the query.
     *
     * @param availability the query of the employee search
     * @return the result of the search
     */
    @PostMapping("/submit-availability")
    public ResponseEntity<Void> submitAvailabity(@Valid @RequestBody Availability availability) {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        Optional<Candidate> candidate = candidateRepository.findById(availability.getPersonId());
        List<TimeSlot> timeSlots = availability.getTimes().stream().map(time ->{
            return (new TimeSlot())
                    .setDate(nextWeek)
                    .setDay(availability.getDay())
                    .setFrom(time.getFrom())
                    .setTo(time.getTo())
                    .setPerson(candidate.get());
        }).collect(Collectors.toList());
        timeSlotRepository.saveAll(timeSlots);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, availability.getDay().toString())).build();
    }

    @GetMapping("/{id}/possible-interview-times")
    public List<TimeSlot> getPossibleInterviewTimes(@PathVariable Long id, @RequestParam ArrayList<Long> interviewers) {
        List<TimeSlot> candidateTimeSlots = timeSlotRepository.getFreeTimeSlots(Collections.singletonList(id));
        List<TimeSlot> freeTimeSlots = timeSlotRepository.getFreeTimeSlots(interviewers);
        return freeTimeSlots.stream().filter(timeSlot -> {
            return candidateTimeSlots.size() > 0 && candidateTimeSlots.stream().anyMatch(t -> {
                return t.getDay().equals(timeSlot.getDay()) && t.getFrom() >= timeSlot.getFrom() && t.getTo() <= timeSlot.getTo();
            }); } ).collect(Collectors.toList());
    }

}
