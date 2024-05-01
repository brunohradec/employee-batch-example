package me.bhradec.employeebatchexample.processors;

import me.bhradec.employeebatchexample.domain.Employee;
import me.bhradec.employeebatchexample.domain.enums.EmailSendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class SendWelcomeEmailProcessor implements ItemProcessor<Employee, Employee> {
    Logger logger = LoggerFactory.getLogger(SendWelcomeEmailProcessor.class);

    @Override
    public Employee process(Employee employee) {
        logger.info("Sending welcome email to {}", employee.getEmail());
        employee.setWelcomeEmailStatus(EmailSendStatus.SENT);
        return employee;
    }
}