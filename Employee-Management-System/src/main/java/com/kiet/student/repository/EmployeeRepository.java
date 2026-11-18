package com.kiet.student.repository;

import com.kiet.student.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {
    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) ->
            new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getDouble("salary")
            );

    public Employee save(Employee employee) {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getDepartment());
            ps.setDouble(3, employee.getSalary());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            employee.setId(keyHolder.getKey().intValue());
        }
        return employee;
    }

    public List<Employee> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, department, salary FROM employees ORDER BY id",
                employeeRowMapper);
    }

    public Optional<Employee> findById(int id) {
        List<Employee> results = jdbcTemplate.query(
                "SELECT id, name, department, salary FROM employees WHERE id = ?",
                employeeRowMapper, id);
        return results.stream().findFirst();
    }

    public boolean update(int id, Employee employee) {
        int rows = jdbcTemplate.update(
                "UPDATE employees SET name = ?, department = ?, salary = ? WHERE id = ?",
                employee.getName(), employee.getDepartment(), employee.getSalary(), id);
        return rows > 0;
    }

    public boolean deleteById(int id) {
        int rows = jdbcTemplate.update("DELETE FROM employees WHERE id = ?", id);
        return rows > 0;
    }
}
