package com.bridgelabz.employeepayroll;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

import static com.bridgelabz.employeepayroll.EmployeePayrollService.IOService.DB_IO;

public class EmployeePayrollServiceTest {
    private static EmployeePayrollService employeePayrollService;
    @BeforeClass
    public static void createEmployeePayrollService() {
        employeePayrollService = new EmployeePayrollService();
    }

    @Test
    public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Ranganath", "M", 1000000, LocalDate.now()),
                new EmployeePayrollData(0, "Harinath", "M", 2000000, LocalDate.now()),
                new EmployeePayrollData(0, "Akka", "F", 3000000, LocalDate.now()),
                new EmployeePayrollData(0, "Shiva", "M", 4000000, LocalDate.now()),
                new EmployeePayrollData(0, "Praveen", "F", 5000000, LocalDate.now()),
                new EmployeePayrollData(0, "Banu", "M", 6000000, LocalDate.now())
        };
        employeePayrollService.readEmployeePayrollData(DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without thread: " + Duration.between(start, end));
        Instant ThreadStart = Instant.now();
        employeePayrollService.addEmployeeToPayRollWIthThreads(Arrays.asList(arrayOfEmps));
        Instant ThreadEnd = Instant.now();
        System.out.println("Duration without thread: " + Duration.between(ThreadStart, ThreadEnd));
        Assert.assertEquals(15, employeePayrollService.countEnteries(DB_IO));
    }
}
