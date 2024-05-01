package me.bhradec.employeebatchexample.controllers;

import me.bhradec.employeebatchexample.services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    Logger logger = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/trigger-initiate-employees-job")
    public ResponseEntity<Void> triggerInitiateEmployeesJob() {
        logger.info("REST request to trigger the initiateEmployeesJob");

        jobService.triggerInitiateEmployeesJob();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
