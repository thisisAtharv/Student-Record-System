import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class StudentReportSystem extends JFrame {
    private JTextArea reportArea;


    public StudentReportSystem(String studentId, String name, String department, String attendance,
                               String courseCompletion, String cgpa, String defaulterStatus) {

        setTitle("Student Report");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setText(generateReport(studentId, name, department, attendance, courseCompletion, cgpa, defaulterStatus));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton downloadButton = new JButton("Download Report");
        downloadButton.addActionListener(e -> downloadReport(studentId));
        add(downloadButton, BorderLayout.SOUTH);
    }

    private String generateReport(String studentId, String name, String department, String attendance,
                                  String courseCompletion, String cgpa, String defaulterStatus) {
        return "Student Report\n" +
                "====================\n" +
                "Student ID: " + studentId + "\n" +
                "Name: " + name + "\n" +
                "Department: " + department + "\n" +
                "Attendance: " + attendance + "\n" +
                "Course Completions: " + courseCompletion + "\n" +
                "CGPA: " + cgpa + "\n" +
                "Defaulter Status: " + defaulterStatus;
    }

        private void downloadReport(String studentId) {
        // Implement the functionality to download the report as a text file
        try {
            String fileName = studentId + "_Report.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(reportArea.getText());
            writer.close();
            JOptionPane.showMessageDialog(this, "Report downloaded as " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving the report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
