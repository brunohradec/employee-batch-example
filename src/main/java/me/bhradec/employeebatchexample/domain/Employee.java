package me.bhradec.employeebatchexample.domain;

import jakarta.persistence.*;
import me.bhradec.employeebatchexample.domain.enums.EmailSendStatus;
import me.bhradec.employeebatchexample.domain.enums.EmployeeStatus;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailSendStatus welcomeEmailStatus;

    public Employee() {
    }

    public Employee(
            String firstName,
            String lastName,
            String email,
            EmployeeStatus status,
            EmailSendStatus welcomeEmailStatus) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.welcomeEmailStatus = welcomeEmailStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public EmailSendStatus getWelcomeEmailStatus() {
        return welcomeEmailStatus;
    }

    public void setWelcomeEmailStatus(EmailSendStatus welcomeEmailStatus) {
        this.welcomeEmailStatus = welcomeEmailStatus;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", welcomeEmailStatus=" + welcomeEmailStatus +
                '}';
    }
}
