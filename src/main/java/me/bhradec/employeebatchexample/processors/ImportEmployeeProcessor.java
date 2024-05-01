package me.bhradec.employeebatchexample.processors;

import me.bhradec.employeebatchexample.domain.Candidate;
import me.bhradec.employeebatchexample.domain.Employee;
import me.bhradec.employeebatchexample.domain.enums.EmailSendStatus;
import me.bhradec.employeebatchexample.domain.enums.EmployeeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class ImportEmployeeProcessor implements ItemProcessor<Candidate, Employee> {
    Logger logger = LoggerFactory.getLogger(ImportEmployeeProcessor.class);

    @Override
    public Employee process(@NonNull Candidate candidate) {
        logger.info("Creating employee from candidate: {}", candidate);

        return new Employee(
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getEmail(),
                EmployeeStatus.ACTIVE,
                EmailSendStatus.NOT_SENT
        );
    }
}
