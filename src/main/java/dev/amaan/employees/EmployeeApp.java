package dev.amaan.employees;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
/** Swing UI: JDBC operations run on a worker thread, never on the event dispatch thread. */
public final class EmployeeApp extends JFrame {
    private final EmployeeRepository repository;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department", "Salary"},0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField search = new JTextField(24);
    private final JLabel status = new JLabel("Starting…");
    private final List<JButton> buttons = new ArrayList<>();
    private List<Employee> employees = List.of();
    private EmployeeApp(EmployeeRepository repository) {
        super("Employee Manager — Java / JDBC"); this.repository = repository;
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1000,540); setLocationRelativeTo(null);
        var toolbar = new JPanel(); toolbar.add(new JLabel("Search:")); toolbar.add(search);
        button(toolbar,"Search",this::refresh); button(toolbar,"Add",()->edit(null));
        button(toolbar,"Edit selected",()->{ var e=selected(); if(e!=null) edit(e); });
        button(toolbar,"Delete selected",this::delete);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); table.setAutoCreateRowSorter(true);
        add(toolbar,BorderLayout.NORTH); add(new JScrollPane(table),BorderLayout.CENTER); add(status,BorderLayout.SOUTH);
        search.addActionListener(e->{ if(search.isEnabled()) refresh(); });
        setVisible(true);
        work(()->{repository.initialize(); return repository.search("");},this::showRows);
    }
    private void button(JPanel panel,String label,Runnable action) {
        var b=new JButton(label); b.addActionListener(e->action.run()); buttons.add(b); panel.add(b);
    }
    private Employee selected() {
        int row=table.getSelectedRow();
        if(row<0) { JOptionPane.showMessageDialog(this,"Select an employee first."); return null; }
        return employees.get(table.convertRowIndexToModel(row));
    }
    private void refresh() { String term=search.getText().strip(); work(()->repository.search(term),this::showRows); }
    private void showRows(List<Employee> rows) {
        employees=rows; model.setRowCount(0);
        for(var e:rows) model.addRow(new Object[]{e.id(),e.name(),e.email(),e.department(),e.salary()});
        status.setText(rows.size()+" employee(s)");
    }
    private void edit(Employee original) {
        var name=new JTextField(original==null?"":original.name());
        var email=new JTextField(original==null?"":original.email());
        var department=new JTextField(original==null?"":original.department());
        var salary=new JTextField(original==null?"0.00":original.salary().toPlainString());
        var form=new JPanel(new GridLayout(0,2,8,8));
        form.add(new JLabel("Name"));form.add(name);form.add(new JLabel("Email"));form.add(email);
        form.add(new JLabel("Department"));form.add(department);form.add(new JLabel("Salary (decimal point)"));form.add(salary);
        while(JOptionPane.showConfirmDialog(this,form,original==null?"Add employee":"Edit employee",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                var e=new Employee(original==null?UUID.randomUUID().toString():original.id(),name.getText(),email.getText(),department.getText(),new BigDecimal(salary.getText().strip()));
                String term=search.getText().strip();
                work(()->{ if(original==null) repository.insert(e); else if(!repository.update(e)) throw new IllegalStateException("Employee no longer exists. Refresh the list."); return repository.search(term); },this::showRows);
                return;
            } catch(IllegalArgumentException ex) { JOptionPane.showMessageDialog(this,ex instanceof NumberFormatException?"Enter a numeric salary, for example 2500.00.":ex.getMessage(),"Invalid input",JOptionPane.WARNING_MESSAGE); }
        }
    }
    private void delete() {
        var e=selected(); if(e==null) return;
        if(JOptionPane.showConfirmDialog(this,"Delete "+e.name()+"? This cannot be undone.","Confirm deletion",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        String term=search.getText().strip();
        work(()->{if(!repository.delete(e.id())) throw new IllegalStateException("Employee no longer exists. Refresh the list.");return repository.search(term);},this::showRows);
    }
    private <T> void work(Callable<T> task,Consumer<T> success) {
        buttons.forEach(b->b.setEnabled(false));search.setEnabled(false);status.setText("Working…");
        new SwingWorker<T,Void>() {
            protected T doInBackground() throws Exception { return task.call(); }
            protected void done() {
                try { success.accept(get()); }
                catch(Exception ex) {
                    status.setText("Operation failed. Check configuration and refresh.");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(EmployeeApp.this,"Could not complete the operation. Check the database configuration and console log.","Database error",JOptionPane.ERROR_MESSAGE);
                } finally {buttons.forEach(b->b.setEnabled(true));search.setEnabled(true);}
            }
        }.execute();
    }
    public static void main(String[] args) {
        var env=System.getenv();
        var repository=new EmployeeRepository(env.getOrDefault("EMPLOYEE_DB_URL","jdbc:h2:./data/employees"),env.getOrDefault("EMPLOYEE_DB_USER","sa"),env.getOrDefault("EMPLOYEE_DB_PASSWORD",""));
        SwingUtilities.invokeLater(()->new EmployeeApp(repository));
    }
}
