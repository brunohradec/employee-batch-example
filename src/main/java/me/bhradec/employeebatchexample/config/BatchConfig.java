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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
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

    /* A bean used to read data about candidates used for batch processing. In this example,
     * the RepositoryItemReader class is used that reads the data from a provided Spring Data
     * JPA repository. Spring Batch provides many classes used for reading data for batch
     * processing, including classes that can be used with JDBC, CSV files and so on. */
    @Bean
    public RepositoryItemReader<Candidate> acceptedCandidateReader() {
        RepositoryItemReader<Candidate> reader = new RepositoryItemReader<>();

        /* The Spring Data JPA repository that will be used to read data from the database
        is provided to the RepositoryItemReader. */
        reader.setRepository(candidateRepository);

        /* The name of the method that will be used to read data from the database is provided
         * to the RepositoryItemReader. The method must be defined in the provided repository. */
        reader.setMethodName("findByStatus");

        /* The arguments that will be passed to the method are provided to the RepositoryItemReader. */
        reader.setArguments(List.of(CandidateStatus.ACCEPTED));

        /* Sort must be added or else Spring Batch raises an exception. The sort must
         * be provided to stabilize the SQL query - batches of data that are being batch
         * processed are read in chunks of a given size from the database. Without the sort,
         * there is no guarantee that the ordering of things won't change on every fetch from
         * the database, causing some records to appear multiple times in different batches,
         * and some to never appear. */
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

    /* A bean used to write data about employees used for batch processing. In this example,
     * the RepositoryItemWriter class is used that writes the data to a provided Spring Data
     * JPA repository. Spring Batch provides many classes used for writing data for batch
     * processing, including classes that can be used with JDBC, CSV files and so on. */
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

    // Task executors:

    /* A bean used to execute tasks in parallel. In this example, the SimpleAsyncTaskExecutor
     * class is used that executes tasks in a separate thread. Spring Batch provides many
     * classes used for executing tasks in parallel, including classes that can be used with
     * a thread pool, a task scheduler and so on. The TaskExecutor is not necessary. Without
     * defining the TaskExecutor, batch processing will be executed sequentially without using
     * multiple threads. */
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(10);
        return executor;
    }

    // Steps:

    @Bean
    public Step importEmployeesStep() {
        return new StepBuilder("importEmployeesStep", jobRepository)
                .<Candidate, Employee>chunk(100, platformTransactionManager)
                .reader(acceptedCandidateReader())
                .processor(importEmployeeProcessor())
                .writer(employeeWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step sendWelcomeEmailsStep() {
        return new StepBuilder("sendWelcomeEmailsStep", jobRepository)
                .<Employee, Employee>chunk(100, platformTransactionManager)
                .reader(notEmailedEmployeeReader())
                .processor(sendWelcomeEmailProcessor())
                .writer(employeeWriter())
                .taskExecutor(taskExecutor())
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
