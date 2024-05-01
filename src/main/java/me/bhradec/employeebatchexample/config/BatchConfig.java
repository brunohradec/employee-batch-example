package me.bhradec.employeebatchexample.config;

import me.bhradec.employeebatchexample.domain.Candidate;
import me.bhradec.employeebatchexample.domain.Employee;
import me.bhradec.employeebatchexample.domain.enums.CandidateStatus;
import me.bhradec.employeebatchexample.domain.enums.EmailSendStatus;
import me.bhradec.employeebatchexample.processors.ImportEmployeeProcessor;
import me.bhradec.employeebatchexample.processors.SendWelcomeEmailProcessor;
import me.bhradec.employeebatchexample.repositories.CandidateRepository;
import me.bhradec.employeebatchexample.repositories.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;

@Configuration
public class BatchConfig {
    private final CandidateRepository candidateRepository;
    private final EmployeeRepository employeeRepository;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public BatchConfig(
            CandidateRepository candidateRepository,
            EmployeeRepository employeeRepository,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager) {

        this.candidateRepository = candidateRepository;
        this.employeeRepository = employeeRepository;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    // Readers:

    @Bean
    public RepositoryItemReader<Candidate> acceptedCandidateReader() {
        RepositoryItemReader<Candidate> reader = new RepositoryItemReader<>();

        reader.setRepository(candidateRepository);
        reader.setMethodName("findByStatus");
        reader.setArguments(List.of(CandidateStatus.ACCEPTED));
        reader.setSort(new HashMap<>() {{
            put("id", Sort.Direction.ASC);
        }});

        return reader;
    }

    @Bean
    public RepositoryItemReader<Employee> notEmailedEmployeeReader() {
        RepositoryItemReader<Employee> reader = new RepositoryItemReader<>();

        reader.setRepository(employeeRepository);
        reader.setMethodName("findByWelcomeEmailStatus");
        reader.setArguments(List.of(EmailSendStatus.NOT_SENT));
        reader.setSort(new HashMap<>() {{
            put("id", Sort.Direction.ASC);
        }});

        return reader;
    }

    // Writers:

    @Bean
    public RepositoryItemWriter<Employee> employeeWriter() {
        RepositoryItemWriter<Employee> writer = new RepositoryItemWriter<>();

        writer.setRepository(employeeRepository);
        writer.setMethodName("save");

        return writer;
    }

    // Processors:

    @Bean
    public ImportEmployeeProcessor importEmployeeProcessor() {
        return new ImportEmployeeProcessor();
    }

    @Bean
    public SendWelcomeEmailProcessor sendWelcomeEmailProcessor() {
        return new SendWelcomeEmailProcessor();
    }

    // Steps:

    @Bean
    public Step importEmployeesStep() {
        return new StepBuilder("importEmployeesStep", jobRepository)
                .<Candidate, Employee>chunk(10, platformTransactionManager)
                .reader(acceptedCandidateReader())
                .processor(importEmployeeProcessor())
                .writer(employeeWriter())
                .build();
    }

    @Bean
    public Step sendWelcomeEmailsStep() {
        return new StepBuilder("sendWelcomeEmailsStep", jobRepository)
                .<Employee, Employee>chunk(10, platformTransactionManager)
                .reader(notEmailedEmployeeReader())
                .processor(sendWelcomeEmailProcessor())
                .writer(employeeWriter())
                .build();
    }

    // Jobs:

    @Bean
    public Job initiateNewEmployeesJob() {
        return new JobBuilder("initiateEmployeesJob", jobRepository)
                .start(importEmployeesStep())
                .next(sendWelcomeEmailsStep())
                .build();
    }
}
