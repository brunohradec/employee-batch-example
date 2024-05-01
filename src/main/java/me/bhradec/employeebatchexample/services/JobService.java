package me.bhradec.employeebatchexample.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobLauncher jobLauncher;
    private final Job intiateEmployeesJob;

    public JobService(JobLauncher jobLauncher, Job intiateEmployeesJob) {
        this.jobLauncher = jobLauncher;
        this.intiateEmployeesJob = intiateEmployeesJob;
    }

    public void triggerInitiateEmployeesJob() {
        try {
            JobParameters jobParams = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(intiateEmployeesJob, jobParams);
        } catch (JobInstanceAlreadyCompleteException exception) {
            logger.error("Job has already been completed.", exception);
        } catch (JobExecutionAlreadyRunningException exception) {
            logger.error("Job is already running.", exception);
        } catch (JobParametersInvalidException exception) {
            logger.error("Invalid job parameters.", exception);
        } catch (JobRestartException exception) {
            logger.error("Job has already been restarted.", exception);
        }
    }
}
