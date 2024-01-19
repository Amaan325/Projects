import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewEmployee extends JFrame {

    private Connection conn;
    private Statement s;
    private JTextArea employeeDetailsTextArea;

    public ViewEmployee() {
        establishConnection();

        setTitle("View Employees");
        setSize(800, 500);

        createViewEmployeePanel();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void establishConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "42395");
            s = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createViewEmployeePanel() {
        JPanel viewEmployeePanel = new JPanel();
        viewEmployeePanel.setLayout(new BorderLayout());

        JButton searchButton = new JButton("Search Employee");
        employeeDetailsTextArea = new JTextArea();
        employeeDetailsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(employeeDetailsTextArea);

        viewEmployeePanel.add(searchButton, BorderLayout.NORTH);
        viewEmployeePanel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });

        displayAllEmployees();

        add(viewEmployeePanel);
    }

    private void displayAllEmployees() {
        try {
            String query = "select * from employee";
            ResultSet rs = s.executeQuery(query);

            StringBuilder employeeInfo = new StringBuilder("List of Employees:\n");
            employeeInfo.append("==================================================================================================================================================================\n");
            employeeInfo.append(String.format("%-10s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s\n", "Emp ID", "Name", "Father's Name", "Salary", "Address", "Phone", "Email", "Designation", "CNIC", "Education", "DOB"));
            employeeInfo.append("==================================================================================================================================================================\n");

            while (rs.next()) {
                employeeInfo.append(String.format("%-10s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s\n",
                        rs.getString("empId"), rs.getString("name"), rs.getString("fname"), rs.getString("salary"),
                        rs.getString("address"), rs.getString("phone"), rs.getString("email"), rs.getString("designation"), rs.getString("cnic"), rs.getString("education"), rs.getString("dob")));
            }

            employeeDetailsTextArea.setText(employeeInfo.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSearchDialog() {
        String userInput = JOptionPane.showInputDialog(this, "Enter Employee ID to view details:");

        if (userInput != null && !userInput.trim().isEmpty()) {
            viewEmployeeDetails(userInput.trim());
        }
    }

    private void viewEmployeeDetails(String empId) {
        try {
            String query = "select * from employee where empId = " + empId;
            ResultSet employeeResultSet = s.executeQuery(query);

            StringBuilder employeeInfo = new StringBuilder();

            if (employeeResultSet.next()) {
                employeeInfo.append("Employee Details:\n");
                employeeInfo.append("==================================================================================================================================================================\n");
                employeeInfo.append("ID: ").append(employeeResultSet.getString("empId")).append("\n");
                employeeInfo.append("Name: ").append(employeeResultSet.getString("name")).append("\n");
                employeeInfo.append("Father's Name: ").append(employeeResultSet.getString("fname")).append("\n");
                employeeInfo.append("Date of Birth: ").append(employeeResultSet.getString("dob")).append("\n");
                employeeInfo.append("Address: ").append(employeeResultSet.getString("address")).append("\n");
                employeeInfo.append("Salary: ").append(employeeResultSet.getString("salary")).append("\n");
                employeeInfo.append("Phone: ").append(employeeResultSet.getString("phone")).append("\n");
                employeeInfo.append("Email: ").append(employeeResultSet.getString("email")).append("\n");
                employeeInfo.append("Highest Education: ").append(employeeResultSet.getString("education")).append("\n");
                employeeInfo.append("Designation: ").append(employeeResultSet.getString("designation")).append("\n");
                employeeInfo.append("CNIC Number: ").append(employeeResultSet.getString("cnic")).append("\n");
            } else {
                employeeInfo.append("No employee found with the provided ID.");
            }

            employeeDetailsTextArea.setText(employeeInfo.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ViewEmployee();
    }
    
}
