package com.sast.rest.controller;

import com.sast.rest.controller.util.HeaderUtil;
import com.sast.rest.controller.util.ResponseUtil;
import com.sast.rest.domain.Availability;
import com.sast.rest.domain.Employee;
import com.sast.rest.domain.TimeSlot;
import com.sast.rest.repository.EmployeeRepository;

import com.sast.rest.repository.TimeSlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing employees.
 */
@RestController
@RequestMapping("/v1/employees")
public class EmployeeResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeResource.class);

    private static final String ENTITY_NAME = "employee";

    private final EmployeeRepository employeeRepository;

    private final TimeSlotRepository timeSlotRepository;


    public EmployeeResource(EmployeeRepository employeeRepository, TimeSlotRepository timeSlotRepository) {
        this.employeeRepository = employeeRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * POST  /employees : Create a new employee.
     *
     * @param employee the employee to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employee, or with status 400 (Bad Request) if the employee has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
        log.debug("REST request to save Employee : {}", employee);
        if (employee.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new Employee cannot already have an ID")).body(null);
        }
        Employee result = employeeRepository.save(employee);
        return ResponseEntity.created(new URI("/v1/employees" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /employees : Updates an existing employee.
     *
     * @param employee the employee to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employee,
     * or with status 400 (Bad Request) if the employee is not valid,
     * or with status 500 (Internal Server Error) if the employee couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/")
    public ResponseEntity<Employee> updateEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
        log.debug("REST request to update Employee : {}", employee);
        if (employee.getId() == null) {
            return createEmployee(employee);
        }
        Employee result = employeeRepository.save(employee);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, employee.getId().toString()))
            .body(result);
    }

    /**
     * GET  /employees : get all the employee.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     */
    @GetMapping("/")
    public List<Employee> getAllEmployees() {
        log.debug("REST request to get all Employees");
        return employeeRepository.findAll();
        }

    /**
     * GET  /employees/:id : get the "id" employee.
     *
     * @param id the id of the employee to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employee, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        log.debug("REST request to get Employee : {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(employee);
    }

    /**
     * DELETE  /employees/:id : delete the "id" employee.
     *
     * @param id the id of the employee to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.debug("REST request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }


    /**
     * SEARCH  /search?query=:query: search for the employees corresponding
     * to the query.
     *
     * @param query the query of the employee search
     * @return the result of the search
     */
    @GetMapping("/search")
    public List<Employee> search(@RequestParam String query) {
        log.debug("REST request to search Employees for query {}", query);
        return StreamSupport
                .stream(employeeRepository.findByFirstNameLikeOrLastNameLike(query, query).spliterator(), false)
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
        Optional<Employee> employee = employeeRepository.findById(availability.getPersonId());
        List<TimeSlot> timeSlots = availability.getTimes().stream().map( time ->{
            return (new TimeSlot())
                    .setDate(nextWeek)
                    .setDay(availability.getDay())
                    .setFrom(time.getFrom())
                    .setTo(time.getTo())
                    .setPerson(employee.get());
        }).collect(Collectors.toList());
        timeSlotRepository.saveAll(timeSlots);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, availability.getDay().toString())).build();
    }



}
