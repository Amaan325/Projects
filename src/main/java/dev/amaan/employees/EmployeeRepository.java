package dev.amaan.employees;
import java.sql.*;
import java.util.*;
/** JDBC persistence. Each operation owns and closes its connection and statements. */
public final class EmployeeRepository {
    private final String url, user, password;
    public EmployeeRepository(String url, String user, String password) {
        this.url = url; this.user = user; this.password = password;
    }
    private Connection connect() throws SQLException { return DriverManager.getConnection(url, user, password); }
    public void initialize() throws SQLException {
        try (var c = connect(); var s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS employees (id VARCHAR(36) PRIMARY KEY, name VARCHAR(120) NOT NULL, email VARCHAR(254) NOT NULL, department VARCHAR(120) NOT NULL, salary DECIMAL(12,2) NOT NULL CHECK (salary >= 0))");
        }
    }
    public List<Employee> search(String term) throws SQLException {
        // Literal substring search: user-supplied percent/underscore are not wildcards.
        String escaped = term.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        try (var c = connect(); var s = c.prepareStatement("SELECT * FROM employees WHERE LOWER(name) LIKE ? ESCAPE '!' OR LOWER(email) LIKE ? ESCAPE '!' OR LOWER(department) LIKE ? ESCAPE '!' ORDER BY name, id")) {
            for (int i = 1; i <= 3; i++) s.setString(i, "%" + escaped.toLowerCase(Locale.ROOT) + "%");
            try (var rs = s.executeQuery()) {
                List<Employee> result = new ArrayList<>();
                while (rs.next()) result.add(new Employee(rs.getString("id"), rs.getString("name"), rs.getString("email"), rs.getString("department"), rs.getBigDecimal("salary")));
                return result;
            }
        }
    }
    public void insert(Employee e) throws SQLException {
        try (var c = connect(); var s = c.prepareStatement("INSERT INTO employees (name,email,department,salary,id) VALUES (?,?,?,?,?)")) { bind(s,e); s.executeUpdate(); }
    }
    public boolean update(Employee e) throws SQLException {
        try (var c = connect(); var s = c.prepareStatement("UPDATE employees SET name=?,email=?,department=?,salary=? WHERE id=?")) { bind(s,e); return s.executeUpdate() == 1; }
    }
    public boolean delete(String id) throws SQLException {
        try (var c = connect(); var s = c.prepareStatement("DELETE FROM employees WHERE id=?")) { s.setString(1,id); return s.executeUpdate() == 1; }
    }
    private static void bind(PreparedStatement s, Employee e) throws SQLException {
        s.setString(1,e.name()); s.setString(2,e.email()); s.setString(3,e.department()); s.setBigDecimal(4,e.salary()); s.setString(5,e.id());
    }
}
