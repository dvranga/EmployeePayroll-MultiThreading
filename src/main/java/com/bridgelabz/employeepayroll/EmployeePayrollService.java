package com.bridgelabz.employeepayroll;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

    public enum IOService{FILE_IO, DB_IO}
    private List<EmployeePayrollData> employeePayrollList;
    private static EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioservice) {
        if (ioservice.equals(IOService.DB_IO)){
            this.employeePayrollList= employeePayrollDBService.readData();
        }
        return this.employeePayrollList;
    }

    public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollException {
        int result = employeePayrollDBService.updateEmployeeData(name, salary);
        if (result==0)return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData!=null) employeePayrollData.salary=salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollList.stream()
                .filter(employeePayrollData -> employeePayrollData.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean checkEmployeeInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollData = employeePayrollDBService.getEmployeePayrollData(name);
        boolean equals = employeePayrollData.get(0).equals(getEmployeePayrollData(name));
        return equals;

    }

    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService,
                                                                     LocalDate startDate, LocalDate endDate) {
        if (ioService.equals(IOService.DB_IO)) {
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
        }
        return null;
    }


    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println(employeePayrollData.name+" is adding");
            this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
                    employeePayrollData.startDate, employeePayrollData.gender);
            System.out.println(employeePayrollData.name+" added");
        });
    }

    public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender));
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return employeePayrollList.size();
    }

    public void addEmployeeToPayRollWIthThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee Adding:" + Thread.currentThread().getName());
                this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
                        employeePayrollData.startDate, employeePayrollData.gender);
                employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
                System.out.println("Employee Added: " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while (employeeAdditionStatus.containsValue(false)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        System.out.println(employeePayrollDataList);
    }



}
