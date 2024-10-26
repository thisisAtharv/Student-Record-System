import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpPage {

    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(SignUpPage::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Fonts
        Font titleFont = new Font("Serif", Font.BOLD, 36);
        Font labelFont = new Font("SansSerif", Font.BOLD, 18);

        // Frame setup
        JFrame frame = new JFrame("Admin Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(new Color(255, 228, 225));
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo Image
        ImageIcon imageIcon = new ImageIcon("APSIT-removebg-preview.png");
        Image resizedImage = imageIcon.getImage().getScaledInstance(280, 65, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(imageLabel, gbc);

        // Title
        JLabel titleLabel = new JLabel("<html><span style='font-size: 40px; color: #FF5733;'>S</span>tudent Record System</html>");
        titleLabel.setForeground(new Color(100, 50, 70));
        titleLabel.setFont(titleFont);
        gbc.gridy = 1;
        frame.add(titleLabel, gbc);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        frame.add(usernameLabel, gbc);

        // Username TextField
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(labelFont);
        gbc.gridx = 1;
        frame.add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(passwordLabel, gbc);

        // Password Field
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(labelFont);
        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        // Sign Up Button
        JButton signUpButton = new JButton("Login");
        signUpButton.setFont(labelFont);
        signUpButton.setBackground(new Color(255, 85, 85));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        frame.add(signUpButton, gbc);

        // Add ActionListener to Sign Up Button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                // Simple validation
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Verify login credentials
                    boolean isValid = verifyLogin(username, password);

                    if (isValid)
                    {
                        System.out.println("Login Successful for user: " + username);
                        JOptionPane.showMessageDialog(frame, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Dispose the login frame and open the Dashboard
                        frame.dispose();
                        SchoolManagementSystem SchoolManagement = new SchoolManagementSystem();
                        SchoolManagement.setVisible(true);
                        SchoolManagement.setLocationRelativeTo(null);
                        SwingUtilities.invokeLater(SchoolManagementSystem::new);
                    }
                    else
                    {
                        System.out.println("Invalid Username or Password for user: " + username);
                        JOptionPane.showMessageDialog(frame, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Set the frame visible
        frame.setVisible(true);

    }
    private static boolean verifyLogin(String username, String password) {
        String url = "jdbc:mysql://127.0.0.1:3306/login_schema";  // Ensure this matches your database
        String user = "root";  // Your MySQL username
        String dbPassword = "Atharv@123";  // Your MySQL password

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);  // In real-world apps, compare hashed passwords

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Database verification successful for user: " + username);
                return true;  // Login successful
            } else {
                System.out.println("Database verification failed for user: " + username);
                return false;  // Login failed
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error occurred
        }
    }
}
