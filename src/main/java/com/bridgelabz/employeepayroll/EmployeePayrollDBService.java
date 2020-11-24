package com.bridgelabz.employeepayroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

    private int connectionCounter=0;
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService() {
    }

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null) {
            employeePayrollDBService = new EmployeePayrollDBService();
        }
        return employeePayrollDBService;
    }

    private synchronized Connection getConnection() throws SQLException {
        connectionCounter++;
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "root";
        Connection connection;
        System.out.println("Processing Thread"+Thread.currentThread().getName()+"Connecting to database with id: "+connectionCounter);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Processing thread: "+Thread.currentThread().getName()+ "Id : "+connectionCounter+
                connection + " connection successful");
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String query = "SELECT * from employee_payroll where is_active=1;";
        return this.getEmployeePayrollDataUsingDB(query);
    }


    public int updateEmployeeData(String name, double salary) throws EmployeePayrollException {
        return this.updateDataUsingStatement(name, salary);
    }

    private int updateDataUsingStatement(String name, double salary) throws EmployeePayrollException {
        String query = String.format("update employee_payroll set salary = %.2f where name= '%s' and is_active=1;", salary, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(query);
        }catch (SQLException e) {
            throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.DatabaseException);
        }
    }

    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM employee_payroll WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null) {
            this.prepareStatementForEmployeeData();
        }
        try {
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String query = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';",
                Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(query);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String query) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate,
                                                    String gender) {
        int employeeId = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO employee_payroll (name, gender,salary, start) VALUES" +
                    "('%s','%s','%s','%s')", name, gender, salary, Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details (employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES" +
                    "(%s,%s,%s,%s,%s,%s)", employeeId, salary, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return employeePayrollData;

    }
}
