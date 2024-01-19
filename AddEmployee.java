import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

class AddEmployee {

    public void addEmployeeDetails() {
        JFrame frame = new JFrame("Add Employee");
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(0, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameTextField = new JTextField();
        frame.add(nameLabel);
        frame.add(nameTextField);

        JLabel fnameLabel = new JLabel("Father's Name:");
        JTextField fnameTextField = new JTextField();
        frame.add(fnameLabel);
        frame.add(fnameTextField);

        JLabel dobLabel = new JLabel("Date of Birth (DD/MM/YYYY):");
        JTextField dobTextField = new JTextField();
        frame.add(dobLabel);
        frame.add(dobTextField);

        JLabel salaryLabel = new JLabel("Salary:");
        JTextField salaryTextField = new JTextField();
        frame.add(salaryLabel);
        frame.add(salaryTextField);

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressTextField = new JTextField();
        frame.add(addressLabel);
        frame.add(addressTextField);

        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneTextField = new JTextField();
        frame.add(phoneLabel);
        frame.add(phoneTextField);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailTextField = new JTextField();
        frame.add(emailLabel);
        frame.add(emailTextField);

        JLabel educationLabel = new JLabel("Highest Education:");
        JTextField educationTextField = new JTextField();
        frame.add(educationLabel);
        frame.add(educationTextField);

        JLabel designationLabel = new JLabel("Designation:");
        JTextField designationTextField = new JTextField();
        frame.add(designationLabel);
        frame.add(designationTextField);

        JLabel cnicLabel = new JLabel("CNIC Number:");
        JTextField cnicTextField = new JTextField();
        frame.add(cnicLabel);
        frame.add(cnicTextField);

        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee(nameTextField.getText(), fnameTextField.getText(),
                        dobTextField.getText(), salaryTextField.getText(),
                        addressTextField.getText(), phoneTextField.getText(),
                        emailTextField.getText(), educationTextField.getText(),
                        designationTextField.getText(), cnicTextField.getText());
                // frame.dispose(); 
            }
        });
        frame.add(addButton);

        frame.setVisible(true);
    }

    private void addEmployee(String name, String fname, String dob, String salary, String address, String phone, String email, String education, String designation, String cnic) {
        try {
            Conn conn = new Conn();
            if(validateInput(name, email, phone , cnic)){
            String query = "insert into employee values('" + name + "', '" + fname + "', '" + dob + "', '" + salary + "', '" + address + "', '" + phone + "', '" + email + "', '" + education + "', '" + designation + "', '" + cnic + "', '" + generateEmployeeId() + "')";
            conn.s.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Details added successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean validateInput(String name, String email, String phoneNumber , String cnic) {
        if ( !name.matches("[a-zA-Z]+") || !email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            JOptionPane.showMessageDialog(null, "Invalid Name or Email ");
            return false;
        }
    
        return true;
    }
    

    private String generateEmployeeId() {
        Random ran = new Random();
        return String.valueOf(ran.nextInt(999999));
    }
}