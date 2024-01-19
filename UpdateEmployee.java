import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UpdateEmployee {

    public void updateEmployeeDetails() {
        JFrame frame = new JFrame("Update Employee");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(0, 2));

        String empId = JOptionPane.showInputDialog(frame, "Enter Employee ID to Update:");

        try {
            Conn c = new Conn();
            String query = "select * from employee where empId = '" + empId + "'";
            ResultSet rs = c.s.executeQuery(query);

            if (rs.next()) {
                JLabel nameLabel = new JLabel("Name:");
                JTextField nameTextField = new JTextField(rs.getString("name"));

                JLabel fnameLabel = new JLabel("Father's Name:");
                JTextField fnameTextField = new JTextField(rs.getString("fname"));

                JLabel dobLabel = new JLabel("Date of Birth:");
                JTextField dobTextField = new JTextField(rs.getString("dob"));

                JLabel cnicLabel = new JLabel("CNIC:");
                JTextField cnicTextField = new JTextField(rs.getString("cnic"));

                JLabel addressLabel = new JLabel("Address:");
                JTextField addressTextField = new JTextField(rs.getString("address"));

                JLabel salaryLabel = new JLabel("Salary:");
                JTextField salaryTextField = new JTextField(rs.getString("salary"));

                JLabel phoneLabel = new JLabel("Phone:");
                JTextField phoneTextField = new JTextField(rs.getString("phone"));

                JLabel emailLabel = new JLabel("Email:");
                JTextField emailTextField = new JTextField(rs.getString("email"));

                JLabel educationLabel = new JLabel("Highest Education:");
                JTextField educationTextField = new JTextField(rs.getString("education"));

                JLabel designationLabel = new JLabel("Designation:");
                JTextField designationTextField = new JTextField(rs.getString("designation"));

                
                JButton updateButton = new JButton("Update");
                updateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateEmployee(empId, nameTextField.getText(), fnameTextField.getText(), dobTextField.getText(), salaryTextField.getText(),
                                addressTextField.getText(), phoneTextField.getText(), emailTextField.getText(),
                                educationTextField.getText(), designationTextField.getText(), cnicTextField.getText());

                        frame.dispose(); 
                    }
                });

                frame.add(nameLabel);
                frame.add(nameTextField);
                frame.add(fnameLabel);
                frame.add(fnameTextField);
                frame.add(dobLabel);
                frame.add(dobTextField);
                frame.add(cnicLabel);
                frame.add(cnicTextField);
                frame.add(addressLabel);
                frame.add(addressTextField);
                frame.add(salaryLabel);
                frame.add(salaryTextField);
                frame.add(phoneLabel);
                frame.add(phoneTextField);
                frame.add(emailLabel);
                frame.add(emailTextField);
                frame.add(educationLabel);
                frame.add(educationTextField);
                frame.add(designationLabel);
                frame.add(designationTextField);
                frame.add(cnicLabel); 
                frame.add(cnicTextField); 
                frame.add(updateButton);
            } else {
                JOptionPane.showMessageDialog(null, "No employee found with the provided ID.");
            }

            c.conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    private void updateEmployee(String empId, String name, String fname, String dob, String salary, String address, String phone, String email, String education, String designation, String cnic) {
        try {
            Conn conn = new Conn();
            String updateQuery = "update employee set name = '" + name + "', fname = '" + fname + "', dob = '" + dob + "', salary = '" + salary + "', address = '" + address + "', phone = '" + phone + "', email = '" + email + "', education = '" + education + "', designation = '" + designation + "', cnic = '" + cnic + "' where empId = '" + empId + "'";

            int rowsAffected = conn.s.executeUpdate(updateQuery);

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Details updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update details.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UpdateEmployee().updateEmployeeDetails();
    }
}

class Conn {
    Connection conn;
    Statement s;

    public Conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "42395");
            s = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
