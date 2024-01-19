import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RemoveEmployee extends JFrame {

    private Connection conn;
    private Statement s;
    private JTextField empIdField;
    private JTextArea employeeDetailsTextArea;

    public RemoveEmployee() {
        establishConnection();

        setTitle("Remove Employee");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createRemoveEmployeePanel();

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

    private void createRemoveEmployeePanel() {
        JPanel removeEmployeePanel = new JPanel();
        removeEmployeePanel.setLayout(new GridLayout(4, 1));

        displayEmployeeDetails(removeEmployeePanel);

        empIdField = new JTextField();
        JButton deleteButton = new JButton("Delete Employee");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        removeEmployeePanel.add(new JLabel("Enter Employee ID to delete:"));
        removeEmployeePanel.add(empIdField);
        removeEmployeePanel.add(deleteButton);

        add(removeEmployeePanel);
    }

    private void displayEmployeeDetails(Container container) {
        try {
            String query = "select * from employee";
            ResultSet rs = s.executeQuery(query);

            StringBuilder employeeInfo = new StringBuilder("Employee Information:\n");

            while (rs.next()) {
                employeeInfo.append("ID: ").append(rs.getString("empId")).append(", Name: ").append(rs.getString("name"))
                        .append(", Father's Name: ").append(rs.getString("fname")).append(", DOB: ")
                        .append(rs.getString("dob")).append(", Designation: ").append(rs.getString("designation"))
                        .append("\n");
            }

            employeeDetailsTextArea = new JTextArea(employeeInfo.toString());
            container.add(new JScrollPane(employeeDetailsTextArea));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEmployee() {
        String empId = empIdField.getText();

        try {
            String query = "delete from employee where empId = '" + empId + "'";
            int rowsAffected = s.executeUpdate(query);

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Employee information deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with the provided ID.");
            }

            remove(employeeDetailsTextArea);
            displayEmployeeDetails(getContentPane());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RemoveEmployee();
            }
        });
    }
}
