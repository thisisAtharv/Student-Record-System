import db.MyJDBC;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class StudentRecordViewer extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color TABLE_COLOR = new Color(255, 229, 236);
    private static final Color PENDING_COLOR = new Color(199, 91, 122);
    private static final Color HEADER_COLOR = new Color(75, 150, 200);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font CELL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private DefaultTableModel model;

    public StudentRecordViewer() {
        setTitle("Student Record Viewer");
        setSize(1500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        String[] columnNames = {"Student ID", "Student Name", "Fees", "Fee Status", "Status"};

        model = new DefaultTableModel(columnNames,0);
        studentTable = new JTable(model);
        rowSorter = new TableRowSorter<>(model);
        studentTable.setRowSorter(rowSorter);
        studentTable.setFillsViewportHeight(true);
        studentTable.setBackground(Color.WHITE);
        studentTable.setFont(CELL_FONT);

        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3) { 
                    String status = (String) value;
                    cell.setBackground("Pending".equalsIgnoreCase(status) ? PENDING_COLOR : TABLE_COLOR);
                } else {
                    cell.setBackground(TABLE_COLOR);
                }
                return cell;
            }
        });


        studentTable.getTableHeader().setBackground(HEADER_COLOR);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setFont(HEADER_FONT);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));

        setColumnWidths();

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);

        JTextField searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(searchField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(searchField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(searchField.getText()); }

            private void search(String query) {
                if (query.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });

        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(searchPanel, BorderLayout.NORTH);

        loadStudentRecords();

        // Add buttons for Add, Update, Delete
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.setBackground(HEADER_COLOR);
        JButton updateButton = new JButton("Update");
        updateButton.setBackground(HEADER_COLOR);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(HEADER_COLOR);

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);



    }
    private void loadStudentRecords() {
        // Clear the existing data in the table model
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0); // Clear existing rows

        // Fetch student records from the database
        List<Object[]> records = MyJDBC.getStudentRecords(); // Make sure getStudentRecords returns List<Object[]>

        // Add each record to the table model
        for (Object[] record : records) {
            model.addRow(record);
        }
    }

    private void addStudent() {
        // Open a dialog or a new frame to get student details
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField feesField = new JTextField();
        JComboBox<String> feeStatusCombo = new JComboBox<>(new String[]{"Pending", "Paid"});
        JComboBox<String> defaulterCombo = new JComboBox<>(new String[]{"Success", "Provisional"});

        Object[] message = {
                "Student ID:", idField,
                "Student Name:", nameField,
                "Fees:", feesField,
                "Fee Status:", feeStatusCombo,
                "Defaulter Status:", defaulterCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int studentId = Integer.parseInt(idField.getText());
            String studentName = nameField.getText();
            String fees = feesField.getText();
            String feeStatus = (String) feeStatusCombo.getSelectedItem();
            String defaulterStatus = (String) defaulterCombo.getSelectedItem();

            // Add student to the database
            MyJDBC.addStudent(studentId, studentName, fees, feeStatus, defaulterStatus);

            // Reload the table records
            loadStudentRecords();
        }
    }



    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.");
            return;
        }

        // Get current values
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        int studentId = (Integer) model.getValueAt(selectedRow, 0);
        String studentName = (String) model.getValueAt(selectedRow, 1);
        String fees = (String) model.getValueAt(selectedRow, 2);
        String feeStatus = (String) model.getValueAt(selectedRow, 3);
        String defaulterStatus = (String) model.getValueAt(selectedRow, 4);

        // Open a dialog to edit details
        JTextField nameField = new JTextField(studentName);
        JTextField feesField = new JTextField(fees);
        JComboBox<String> feeStatusCombo = new JComboBox<>(new String[]{"Pending", "Paid"});
        feeStatusCombo.setSelectedItem(feeStatus);
        JComboBox<String> defaulterCombo = new JComboBox<>(new String[]{"Success", "Provisional"});
        defaulterCombo.setSelectedItem(defaulterStatus);

        Object[] message = {
                "Student Name:", nameField,
                "Fees:", feesField,
                "Fee Status:", feeStatusCombo,
                "Defaulter Status:", defaulterCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String updatedName = nameField.getText();
            String updatedFees = feesField.getText();
            String updatedFeeStatus = (String) feeStatusCombo.getSelectedItem();
            String updatedDefaulterStatus = (String) defaulterCombo.getSelectedItem();

            // Update the student in the database
            MyJDBC.updateStudent(String.valueOf(studentId), updatedName, updatedFees, updatedFeeStatus, updatedDefaulterStatus);

            // Reload the table records
            loadStudentRecords();
        }
    }




    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (Integer) model.getValueAt(selectedRow, 0);
            MyJDBC.deleteStudentRecord(studentId);
            model.removeRow(selectedRow); // Remove from table
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Student Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setColumnWidths() {
        TableColumn column = studentTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(100); // Student ID
        column = studentTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(200); // Student Name
        column = studentTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(100); // Fees
        column = studentTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(100); // Fee Status
        column = studentTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(100); // Defaulter Status
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentRecordViewer frame = new StudentRecordViewer();
            frame.setVisible(true);
        });
    }
}
