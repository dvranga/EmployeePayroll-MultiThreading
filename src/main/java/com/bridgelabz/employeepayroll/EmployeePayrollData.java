package com.bridgelabz.employeepayroll;

import java.time.LocalDate;

public class EmployeePayrollData {
    public int id;
    public String name;
    public double salary;
    public String gender;
    public LocalDate startDate;

    public EmployeePayrollData(int id, String name, String gender, double salary, LocalDate startDate) {
        this(id, name, salary, startDate);
        this.gender = gender;
    }

    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
        this(id, name, salary);
        this.startDate = startDate;
    }

    public EmployeePayrollData(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
}
