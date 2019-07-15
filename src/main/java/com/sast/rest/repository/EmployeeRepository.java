package com.sast.rest.repository;

import com.sast.rest.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    @Query("select e from Employee e join Availabilty a on (a.person_id = e.if) join Time t on where u.emailAddress = ?1")
    List<Employee> findByFirstNameLikeOrLastNameLike(String firstName, String lastName);
}
