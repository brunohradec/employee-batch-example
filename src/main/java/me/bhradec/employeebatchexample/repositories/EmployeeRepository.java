package me.bhradec.employeebatchexample.repositories;

import me.bhradec.employeebatchexample.domain.Employee;
import me.bhradec.employeebatchexample.domain.enums.EmailSendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByWelcomeEmailStatus(EmailSendStatus status, Pageable pageable);
}
