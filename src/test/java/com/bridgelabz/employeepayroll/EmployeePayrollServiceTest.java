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
        System.out.println("Duration with thread: " + Duration.between(ThreadStart, ThreadEnd));
        Assert.assertEquals(14, employeePayrollService.countEntries(DB_IO));
    }

    @Test
    public void givenMultipleEmployess_ShouldAddIntoTheBothTheTablesUsingThreads() {
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
        employeePayrollService.addEmployeesToBothTables(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without thread: " + Duration.between(start, end));
        Instant ThreadStart = Instant.now();
        employeePayrollService.addEmployeeToBothTablesWIthThreads(Arrays.asList(arrayOfEmps));
        Instant ThreadEnd = Instant.now();
        System.out.println("Duration with thread: " + Duration.between(ThreadStart, ThreadEnd));
        Assert.assertEquals(14, employeePayrollService.countEntries(DB_IO));
    }

    @Test
    public void givenMultipleSalaries_ShouldUpdateForMultipleEmployeesAndSyncWithDB() {
        EmployeePayrollData[] arrayOfEmp = {
                new EmployeePayrollData(1, "Bill", 1100000.0),
                new EmployeePayrollData(2, "Terisa", 21000000.0),
                new EmployeePayrollData(3, "Charlie", 32000000.0),
                new EmployeePayrollData(4, "Mark", 40000100.0)
        };
        Instant start = Instant.now();
        employeePayrollService.updateSalariesOfMultipleEmployees(Arrays.asList(arrayOfEmp));
        Instant end = Instant.now();
        System.out.println("Duration With Thread: "+java.time.Duration.between(start, end));
        boolean result = employeePayrollService.checkEmployeeInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }
}
