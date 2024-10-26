import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentDetailsForm extends JFrame {
    private final SchoolManagementSystem parentFrame;

    private JTextField studentIdField;
    private JTextField nameField;
    private JTextField deptField;
    private JTextField defaulterField;
    private JTextField contactField;
    private JTextField enrollDateField;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/login_schema";

    public StudentDetailsForm(SchoolManagementSystem parent) {
        this.parentFrame = parent;
        setTitle("Student Details Form");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        getContentPane().setBackground(new Color(240, 248, 255));

        studentIdField = createTextField();
        nameField = createTextField();
        deptField = createTextField();
        contactField = createTextField();
        defaulterField = createTextField();
        enrollDateField = createTextField();

        addFormComponent(gbc, "Student ID:", studentIdField, 0);
        addFormComponent(gbc, "Name:", nameField, 1);
        addFormComponent(gbc, "Department:", deptField, 2);
        addFormComponent(gbc, "Enrollment Date:", enrollDateField, 3);
        addFormComponent(gbc, "Defaulter:", defaulterField, 4);
        addFormComponent(gbc, "Contact Information:", contactField, 5);
        gbc.gridx = 1;

        JButton submitButton = createButton("Submit");
        submitButton.addActionListener(new SubmitActionListener());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        setupDatabase();
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBorder(BorderFactory.createLineBorder(new Color(188, 71, 73), 1));
        return textField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(188, 71, 73));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    private void addFormComponent(GridBagConstraints gbc, String label, Component component, int gridY) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(component, gbc);
    }

    private void setupDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students ("
                + "student_id VARCHAR(255) PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "department VARCHAR(255) NOT NULL, "
                + "enrollment_date DATE NOT NULL, "
                + "status VARCHAR(255) NOT NULL, "
                + "contact_info VARCHAR(255) NOT NULL"
                + ");";
        try (Connection conn = DriverManager.getConnection(DB_URL, "root", "Atharv@123");
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addStudentToDatabase(String studentId, String name, String department, String enrollmentDate, String status, String contactInfo) {
        String insertSQL = "INSERT INTO students (student_id, name, department, enrollment_date, status, contact_info) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, "root", "Atharv@123");
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, name);
            pstmt.setString(3, department);
            pstmt.setString(4, enrollmentDate);
            pstmt.setString(5, status);
            pstmt.setString(6, contactInfo);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student details added successfully.");
            parentFrame.refreshStudentData(); // Call the refresh method in the parent frame
            this.dispose(); // Close the form after successful submission
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student details.");
        }
    }

    private class SubmitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String studentId = studentIdField.getText();
            String name = nameField.getText();
            String department = deptField.getText();
            String enrollmentDate = enrollDateField.getText();
            String status = defaulterField.getText();
            String contactInfo = contactField.getText();
            addStudentToDatabase(studentId, name, department, enrollmentDate, status, contactInfo);
        }
    }
}
