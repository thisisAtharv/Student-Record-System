import db.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class AddMarks {
    private static final Color LOGOUT_COLOR = new Color(188, 71, 73);
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color LABEL_COLOR = new Color(100, 100, 100);
    private JTextField studentIdField, emIIIField, pocField, pcpfField, dsaField, javaField, dbmsField;
    private MyJDBC myJDBC;
    private JFrame frame;
    private SchoolManagementSystem schoolManagementSystem;

    public AddMarks(SchoolManagementSystem sms) {
        this.schoolManagementSystem = sms;
        myJDBC = new MyJDBC();
        showWindow();
    }

    public void showWindow() {
        frame = new JFrame("Add Marks");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Label
        JLabel titleLabel = new JLabel("Add Student Marks");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(LOGOUT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Student ID
        addLabelAndField(panel, "Student ID:", studentIdField = createTextField(), gbc, 1);

        // Subjects
        addLabelAndField(panel, "EM-III:", emIIIField = createTextField(), gbc, 2);
        addLabelAndField(panel, "POC:", pocField = createTextField(), gbc, 3);
        addLabelAndField(panel, "PCPF:", pcpfField = createTextField(), gbc, 4);
        addLabelAndField(panel, "DSA:", dsaField = createTextField(), gbc, 5);
        addLabelAndField(panel, "JAVA:", javaField = createTextField(), gbc, 6);
        addLabelAndField(panel, "DBMS:", dbmsField = createTextField(), gbc, 7);

        // Button to submit
        JButton addMarksButton = new JButton("Add Marks");
        addMarksButton.setBackground(LOGOUT_COLOR);
        addMarksButton.setForeground(Color.WHITE);
        addMarksButton.setFont(new Font("Arial", Font.BOLD, 14));
        addMarksButton.setFocusPainted(false);
        addMarksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addMarksButton, gbc);

        // Action listener for the button
        addMarksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int studentId = Integer.parseInt(studentIdField.getText());
                    int emIII = Integer.parseInt(emIIIField.getText());
                    int poc = Integer.parseInt(pocField.getText());
                    int pcpf = Integer.parseInt(pcpfField.getText());
                    int dsa = Integer.parseInt(dsaField.getText());
                    int java = Integer.parseInt(javaField.getText());
                    int dbms = Integer.parseInt(dbmsField.getText());

                    // Validate marks
                    if (emIII < 0 || emIII > 100 || poc < 0 || poc > 100 || pcpf < 0 || pcpf > 100 ||
                            dsa < 0 || dsa > 100 || java < 0 || java > 100 || dbms < 0 || dbms > 100) {
                        JOptionPane.showMessageDialog(frame, "Marks must be between 0 and 100!");
                        return;
                    }

                    // Call the MyJDBC method to insert marks into the database
                    myJDBC.insertStudentMarks(studentId, emIII, poc, pcpf, dsa, java, dbms);
                    JOptionPane.showMessageDialog(frame, "Marks added successfully!");


                    schoolManagementSystem.loadMarksData();
                    schoolManagementSystem.initializeStudentMarksTab();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers for marks.");
                }catch (SQLIntegrityConstraintViolationException ex) {
                    // Display a popup if a duplicate entry error occurs
                    JOptionPane.showMessageDialog(null, "Student ID already exists. Please update marks instead of adding new ones.",
                            "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                }catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding marks: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error adding marks: " + ex.getMessage());
                }
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                SchoolManagementSystem.isWindowOpen = false;
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField textField, GridBagConstraints gbc, int gridY) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setForeground(LABEL_COLOR);
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(textField, gbc);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBorder(BorderFactory.createLineBorder(LOGOUT_COLOR, 1));
        return textField;
    }

    public static void main(String[] args) {
        new AddMarks(new SchoolManagementSystem());
    }
}