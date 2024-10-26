import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

public class ProfileManager extends JFrame {
    private static final Color FRAME_COLOR = new Color(234, 242, 215);
    private static final Color BUTTON_COLOR = new Color(103, 169, 131);
    private static final Color BUTTON_HOVER_COLOR = new Color(89, 150, 112);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private JLabel profilePicLabel;
    private JTextField studentIdField, nameField, attField, courserField, cgField;
    private JTextField defaulterField, deptField;
    private JButton uploadButton, saveButton, exitButton;

    private String imagePath; // To store the image path

    public ProfileManager() {
        setTitle("Profile");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(FRAME_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Profile Picture Section
        profilePicLabel = new JLabel("Profile Picture");
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        profilePicLabel.setPreferredSize(new Dimension(100, 125));
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(profilePicLabel, gbc);

        uploadButton = createStyledButton("Upload Picture", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(100, 125, java.awt.Image.SCALE_SMOOTH);

                    // Set the scaled image to the label
                    profilePicLabel.setIcon(new ImageIcon(image)); // Use new ImageIcon directly with the scaled Image
                    imagePath = selectedFile.getAbsolutePath(); // Store the path of the image for later use
                }
            }
        });


        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(uploadButton, gbc);

        // Student Details Section
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(createLabel("Student ID:"), gbc);
        studentIdField = new JTextField(20);
        gbc.gridx = 1;
        add(studentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(createLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(createLabel("Department:"), gbc);
        deptField = new JTextField(20);
        gbc.gridx = 1;
        add(deptField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(createLabel("Attendance:"), gbc);
        attField = new JTextField(20);
        gbc.gridx = 1;
        add(attField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(createLabel("Course Completions:"), gbc);
        courserField = new JTextField(20);
        gbc.gridx = 1;
        add(courserField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(createLabel("CGPA:"), gbc);
        cgField = new JTextField(20);
        gbc.gridx = 1;
        add(cgField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(createLabel("Defaulter Status:"), gbc);
        defaulterField = new JTextField(20);
        gbc.gridx = 1;
        add(defaulterField, gbc);

        // Button Panel for Save and Exit
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(FRAME_COLOR);

        // Save Button Action
        saveButton = createStyledButton("Save", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get user inputs
                String studentId = studentIdField.getText();
                String name = nameField.getText();
                String department = deptField.getText();
                String attendance = attField.getText();
                String courseCompletion = courserField.getText();
                String cgpa = cgField.getText();
                String defaulterStatus = defaulterField.getText();

                // Generate the PDF report
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save PDF Report");
                if (fileChooser.showSaveDialog(ProfileManager.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        Document document = new Document();
                        PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile() + ".pdf"));
                        document.open();

                        // Add content to the PDF
                        document.add(new Paragraph("Student Report"));
                        document.add(new Paragraph("Student ID: " + studentId));
                        document.add(new Paragraph("Name: " + name));
                        document.add(new Paragraph("Department: " + department));
                        document.add(new Paragraph("Attendance: " + attendance));
                        document.add(new Paragraph("Course Completions: " + courseCompletion));
                        document.add(new Paragraph("CGPA: " + cgpa));
                        document.add(new Paragraph("Defaulter Status: " + defaulterStatus));

                        // Add the profile picture if uploaded
                        if (imagePath != null && !imagePath.isEmpty()) {
                            try {
                                // Create a PDF Image instance from the image path
                                com.itextpdf.text.Image profileImage = com.itextpdf.text.Image.getInstance(imagePath); // Get instance of the image
                                profileImage.scaleToFit(100, 125); // Scale the image to fit
                                document.add(profileImage); // Add the image to the document
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(ProfileManager.this, "Error adding image: " + ex.getMessage());
                            }
                        }

                        document.close();
                        JOptionPane.showMessageDialog(ProfileManager.this, "Report Saved Successfully!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ProfileManager.this, "Error saving report: " + ex.getMessage());
                    }
                }
            }
        });

        buttonPanel.add(saveButton);

        // Exit Button Action
        exitButton = createStyledButton("Exit", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private JButton createStyledButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addActionListener(actionListener);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileManager().setVisible(true));
    }

    public String[] getProfileData() {
        return new String[]{
                studentIdField.getText(),
                nameField.getText(),
                deptField.getText(),
                attField.getText(),
                courserField.getText(),
                cgField.getText(),
                defaulterField.getText()
        };
    }
}
