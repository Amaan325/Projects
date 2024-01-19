import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {

    public Home() {
        setTitle("Employee Management System");
        setSize(400, 200);

        createMenuPanel();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 1));

        JButton addEmployeeButton = new JButton("Add Employee");
        JButton viewEmployeesButton = new JButton("View Employees");
        JButton updateEmployeeButton = new JButton("Update Employee");
        JButton removeEmployeeButton = new JButton("Remove Employee");

        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEmployee addEmployee = new AddEmployee();
                addEmployee.addEmployeeDetails();
            }
        });

        viewEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              new ViewEmployee();
            }
        });

        updateEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateEmployee updateEmployee = new UpdateEmployee();
                updateEmployee.updateEmployeeDetails();
            }
        });

        removeEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 new RemoveEmployee();
                
            }
        });

        menuPanel.add(addEmployeeButton);
        menuPanel.add(viewEmployeesButton);
        menuPanel.add(updateEmployeeButton);
        menuPanel.add(removeEmployeeButton);

        add(menuPanel);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Home();
            }
        });
    }
}
