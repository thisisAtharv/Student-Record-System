import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import db.MyJDBC;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.Map;
import java.sql.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import javax.swing.table.DefaultTableCellRenderer;


public class SchoolManagementSystem extends JFrame
{
    private static final Color TURQUOISE = new Color(0, 204, 204);
    private static final Color DARK_DEMO = new Color(255, 194, 209);
    private static final Color LOGOUT_COLOR = new Color(188, 71, 73);
    private static final Color LIGHT_LIGHT = new Color(255, 229, 236);
    private MyJDBC db; // Declare MyJDBC instance
    private JTabbedPane tabbedPane;
    private JTable summaryTable;
    private JTable studentTable;
    private JTable attendanceTable;
    private JButton refreshButton;
    private JTextField date;
    public static boolean isWindowOpen = false;
    private JButton addStudentGrades;
    private AddMarks addMarksFrame;
    private DefaultTableModel marksTableModel;
    private JTable marksTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private DefaultTableModel tableModel;
    private DefaultTableModel summaryModel;
    private List<Object[]> originalStudentData = new ArrayList<>();
    private TableRowSorter<DefaultTableModel> rowSortertwo;
    private CircularProgressBar emIIICircularProgressBar;
    private CircularProgressBar pocCircularProgressBar;
    private CircularProgressBar pcpfCircularProgressBar;
    private CircularProgressBar dsaCircularProgressBar;
    private CircularProgressBar javaCircularProgressBar;
    private CircularProgressBar dbmsCircularProgressBar;




    public SchoolManagementSystem() {
        setTitle("Student Record System SRS");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        db = new MyJDBC();


        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_LIGHT);

//        JLabel logoLabel = new JLabel("<html><span style='font-size:14px;'>AP</span><span style='font-size:18px; color:red;'>S</span><span style='font-size:14px;'>IT</span></html>", JLabel.LEFT);
//        logoLabel.setOpaque(true);
//        logoLabel.setBackground(LIGHT_LIGHT);
//        headerPanel.add(logoLabel, BorderLayout.WEST);


        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(LIGHT_LIGHT);

//        JButton homeButton = new JButton("Profile");
//        homeButton.setBackground(TURQUOISE);
//        homeButton.addActionListener(e -> showHomePage());
//        navPanel.add(homeButton);


        JButton studentButton = new JButton("Records");
        studentButton.setBackground(TURQUOISE);
        studentButton.addActionListener(e -> showStudentPage());
        navPanel.add(studentButton);

        JButton courseButton = new JButton("Courses");
        courseButton.setBackground(TURQUOISE);
        courseButton.addActionListener(e -> openStudentViewer()); // Add this line
        navPanel.add(courseButton);

        JButton generateReportsButton = new JButton("Reports");
        generateReportsButton.setBackground(TURQUOISE);
        navPanel.add(generateReportsButton);
        generateReportsButton.addActionListener(e -> {
            ProfileManager profileManager = new ProfileManager();
            profileManager.setVisible(true);

            profileManager.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    String[] profileData = profileManager.getProfileData();
                    openStudentReportSystem(profileData);  // Pass the profile data here
                }
            });
        });


//        JButton gardebutton2 = new JButton("Grades");
//        gardebutton2.setBackground(TURQUOISE);
//        navPanel.add(gardebutton2);
//        AbstractButton gradebutton2;
//        gardebutton2.addActionListener(e -> showgradepage());

        String[] navItems = {};
        for (String item : navItems) {
            JButton navButton = new JButton(item);
            navButton.setBackground(TURQUOISE);
            navButton.addActionListener(new NavigationActionListener(item));
            navPanel.add(navButton);
        }

        headerPanel.add(navPanel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(LIGHT_LIGHT);
//        userPanel.add(new JLabel("Profile Icon"));
//        userPanel.add(new JLabel("Admin"));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(LOGOUT_COLOR);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(SchoolManagementSystem.this,
                    "Are you sure you want to exit?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        userPanel.add(logoutButton);

        headerPanel.add(userPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Dashboard
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LIGHT_LIGHT);

        // Dashboard Tab
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(DARK_DEMO);

        // Search Bar

        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchField.getText());
            }

            private void search(String query) {
                if (query.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(LIGHT_LIGHT);
        searchPanel.add(new JLabel("Search by Name/ID:"));
        searchPanel.add(searchField);

        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(TURQUOISE);
        searchPanel.add(searchButton);
        searchButton.addActionListener(e -> {
            String searchQuery = searchField.getText().trim();  // Get the input from the search bar
            if (!searchQuery.isEmpty()) {
                Object[] studentData = MyJDBC.searchStudent(searchQuery);  // Use the search method from MyJDBC

                if (studentData != null) {
                    // Move the found row to the top
                    moveRowToTop(studentData);
                } else {
                    // If no student was found, show a message
                    JOptionPane.showMessageDialog(null, "Student found.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a name or student ID.");
            }
        });

        dashboardPanel.add(searchPanel, BorderLayout.NORTH);

        // Student List
        String[] columnNames = {"Student ID", "Name", "Department", "Enrollment Date", "Defaulter Status", "Contact Information"};
        tableModel = new DefaultTableModel(columnNames, 0);
        Object[][] data = fetchStudentData();
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        studentTable = new JTable(tableModel);
        studentTable.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane);
        dashboardPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);



        // Set up the RowSorter
        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);

        // Quick Links
        JPanel quickLinksPanel = new JPanel();
        quickLinksPanel.setBackground(LIGHT_LIGHT);
        JButton addNewStudentButton = new JButton("Add Student");
        addNewStudentButton.setBackground(TURQUOISE);
        addNewStudentButton.addActionListener(e -> showStudentDetailsForm());
        quickLinksPanel.add(addNewStudentButton);
        JButton updatestudent = new JButton("Update Student");
        updatestudent.setBackground(TURQUOISE);
        quickLinksPanel.add(updatestudent);
        JButton deleteButton = new JButton("Delete Student");
        deleteButton.setBackground(TURQUOISE);
        quickLinksPanel.add(deleteButton);


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new dialog (container)
                JDialog deleteDialog = new JDialog();
                deleteDialog.setTitle("Delete Student");
                deleteDialog.setSize(380, 120);
                deleteDialog.setLayout(new FlowLayout());
                deleteDialog.setLocationRelativeTo(null);

                // Set the background color of the dialog
                deleteDialog.getContentPane().setBackground(new Color(240, 248, 255));

                // Label and text field for entering the student ID
                JLabel deleteLabel = new JLabel("Enter Student ID:");
                JTextField deleteStudentField = new JTextField(10);

                // OK and Cancel buttons
                JButton okButton = new JButton("OK");
                okButton.setForeground(Color.WHITE);
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setForeground(Color.WHITE);
                // Set button background colors
                okButton.setBackground(new Color(188, 71, 73));
                cancelButton.setBackground(new Color(188, 71, 73));

                // Add components to the dialog
                deleteDialog.add(deleteLabel);
                deleteDialog.add(deleteStudentField);
                deleteDialog.add(okButton);
                deleteDialog.add(cancelButton);

                // OK button action
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String studentId = deleteStudentField.getText().trim();

                        if (!studentId.isEmpty()) {
                            boolean success = MyJDBC.deleteStudentById(studentId);

                            if (success) {
                                JOptionPane.showMessageDialog(null, "Student deleted successfully.");
                                refreshTable();  // Refresh table after deletion
                                deleteDialog.dispose();  // Close dialog after deletion
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to delete student. Check if the student ID exists.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Please enter a valid student ID.");
                        }
                    }
                });

                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteDialog.dispose();
                    }
                });

                deleteDialog.setVisible(true);
            }
        });

        updatestudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudent(); // Call the update method
            }
        });

        // Add Refresh Button
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(TURQUOISE);
        refreshButton.addActionListener(e -> refreshStudentData());
        quickLinksPanel.add(refreshButton);
        dashboardPanel.add(quickLinksPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Dashboard", dashboardPanel);

        //Student Marks Tab
        JPanel studentMarksPanel = new JPanel(new BorderLayout());
        studentMarksPanel.setBackground(LIGHT_LIGHT);
        tabbedPane.addTab("Student Marks", studentMarksPanel);
        // Define column names for marks table
        String[] marksColumnNames = {"Student ID", "EM-III", "POC", "PCPF", "DSA", "JAVA", "DBMS"};
        // Create DefaultTableModel for marks
        marksTableModel = new DefaultTableModel(marksColumnNames, 0) {
            @Override
            public void setValueAt(Object value, int row, int column) {
                if (column > 0) { // Assuming first column is Student ID, so skip it
                    try {
                        int marks = Integer.parseInt(value.toString());
                        if (marks < 1 || marks > 100) {
                            JOptionPane.showMessageDialog(null, "Please enter marks between 1 and 100.");
                            return; // Don't set invalid value
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid number for marks.");
                        return; // Don't set invalid value
                    }
                }
                super.setValueAt(value, row, column); // Set the valid value
            }
        };
        // Initialize marksTableModel
        marksTable = new JTable(marksTableModel); // Initialize marksTable with the model
        rowSortertwo = new TableRowSorter<>(marksTableModel);
        marksTable.setRowSorter(rowSortertwo);

        JTextField searchFieldtwo = new JTextField(15);
        searchFieldtwo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchFieldtwo.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchFieldtwo.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchFieldtwo.getText());
            }

            private void search(String query) {
                if (query.isEmpty()) {
                    rowSortertwo.setRowFilter(null);
                } else {
                    rowSortertwo.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });
        JPanel searchPaneltwo = new JPanel();
        searchPaneltwo.setBackground(LIGHT_LIGHT);
        searchPaneltwo.add(new JLabel("Search by Student ID:"));
        searchPaneltwo.add(searchFieldtwo);

        // Add JTable to JScrollPane for better visibility
        studentMarksPanel.add(searchPaneltwo, BorderLayout.NORTH);

        // Search button
        JButton searchButtonnew = new JButton("Search");
        searchButtonnew.setBackground(TURQUOISE);
        searchPaneltwo.add(searchButtonnew);
        searchButtonnew.addActionListener(e -> {
            String searchQuery = searchFieldtwo.getText().trim();  // Get the input from the search bar
            if (!searchQuery.isEmpty()) {
                Object[] studentData = MyJDBC.searchStudent(searchQuery);  // Use the search method from MyJDBC

                if (studentData != null) {
                    // Move the found row to the top
                    moveRowToTop(studentData);
                } else {
                    // If no student was found, show a message
                    JOptionPane.showMessageDialog(null, "Student found.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a name or student ID.");
            }
        });

        // Add the table to a JScrollPane
//        studentMarksPanel.add(new JScrollPane(marksTable), BorderLayout.CENTER);
        JScrollPane marksScrollPane = new JScrollPane(marksTable);
        studentMarksPanel.add(marksScrollPane,BorderLayout.CENTER);

// Initialize CircularProgressBar for each subject
        emIIICircularProgressBar = new CircularProgressBar(Color.GREEN);
        pocCircularProgressBar = new CircularProgressBar(Color.BLUE);
        pcpfCircularProgressBar = new CircularProgressBar(Color.RED);
        dsaCircularProgressBar = new CircularProgressBar(Color.ORANGE);
        javaCircularProgressBar = new CircularProgressBar(Color.MAGENTA);
        dbmsCircularProgressBar = new CircularProgressBar(Color.CYAN);

// Set preferred size for circular progress bars
        Dimension progressBarSize = new Dimension(100, 100); // Adjust size as needed
        emIIICircularProgressBar.setPreferredSize(progressBarSize);
        pocCircularProgressBar.setPreferredSize(progressBarSize);
        pcpfCircularProgressBar.setPreferredSize(progressBarSize);
        dsaCircularProgressBar.setPreferredSize(progressBarSize);
        javaCircularProgressBar.setPreferredSize(progressBarSize);
        dbmsCircularProgressBar.setPreferredSize(progressBarSize);

        // Create a panel to hold the progress bars
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayout(6, 1, 10, 10)); // 6 rows for 6 subjects

// EM-III Panel
        JPanel emIIIPanel = new JPanel(new BorderLayout());
        JLabel emIIILabel = new JLabel("EM-III", JLabel.CENTER); // Label for EM-III
        emIIILabel.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font for the label
        emIIIPanel.add(emIIILabel, BorderLayout.NORTH); // Add label to the top
        emIIIPanel.add(emIIICircularProgressBar, BorderLayout.CENTER); // Add progress bar to the center
        progressPanel.add(emIIIPanel); // Add EM-III panel to the main progress panel

// POC Panel
        JPanel pocPanel = new JPanel(new BorderLayout());
        JLabel pocLabel = new JLabel("POC", JLabel.CENTER); // Label for POC
        pocLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font for the label
        pocPanel.add(pocLabel, BorderLayout.NORTH); // Add label to the top
        pocPanel.add(pocCircularProgressBar, BorderLayout.CENTER); // Add progress bar to the center
        progressPanel.add(pocPanel); // Add POC panel to the main progress panel

// PCPF Panel
        JPanel pcpfPanel = new JPanel(new BorderLayout());
        JLabel pcpfLabel = new JLabel("PCPF", JLabel.CENTER); // Label for PCPF
        pcpfLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font for the label
        pcpfPanel.add(pcpfLabel, BorderLayout.NORTH); // Add label to the top
        pcpfPanel.add(pcpfCircularProgressBar, BorderLayout.CENTER); // Add progress bar to the center
        progressPanel.add(pcpfPanel); // Add PCPF panel to the main progress panel

// DSA Panel
        JPanel dsaPanel = new JPanel(new BorderLayout());
        JLabel dsaLabel = new JLabel("DSA", JLabel.CENTER); // Label for DSA
        dsaLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font for the label
        dsaPanel.add(dsaLabel, BorderLayout.NORTH); // Add label to the top
        dsaPanel.add(dsaCircularProgressBar, BorderLayout.CENTER); // Add progress bar to the center
        progressPanel.add(dsaPanel); // Add DSA panel to the main progress panel

// JAVA Panel
        JPanel javaPanel = new JPanel(new BorderLayout());
        JLabel javaLabel = new JLabel("JAVA", JLabel.CENTER); // Label for JAVA
        javaLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font for the label
        javaPanel.add(javaLabel, BorderLayout.NORTH); // Add label to the top
        javaPanel.add(javaCircularProgressBar, BorderLayout.CENTER); // Add progress bar to the center
        progressPanel.add(javaPanel); // Add JAVA panel to the main progress panel

// DBMS Panel
        JPanel dbmsPanel = new JPanel(new BorderLayout());
        JLabel dbmsLabel = new JLabel("DBMS", JLabel.CENTER); // Label for DBMS
        dbmsLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font for the label
        dbmsPanel.add(dbmsLabel, BorderLayout.NORTH); // Add label to the top
        dbmsPanel.add(dbmsCircularProgressBar, BorderLayout.CENTER); // Add progress bar to the center
        progressPanel.add(dbmsPanel); // Add DBMS panel to the main progress panel

        // Add the progress panel to the Student Marks tab
        studentMarksPanel.add(progressPanel, BorderLayout.EAST);



        // Create the add/delete marks button
        JPanel deletebuttonpanel = new JPanel(new FlowLayout());


        JButton addMarksButton = new JButton("Add Marks");
        addMarksButton.setPreferredSize(new Dimension(150,30));
        addMarksButton.setBackground(TURQUOISE);
        deletebuttonpanel.add(addMarksButton);
        addMarksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isWindowOpen) { // Only create a new window if one isn't open
                    addMarksFrame = new AddMarks(SchoolManagementSystem.this);
                    isWindowOpen = true; // Set the flag to indicate the window is open
                }
                else{
                    addMarksFrame.showWindow();
                }

            }
        });



        // Create the Update Marks button
        JButton updateMarksButton = new JButton("Update Marks");
        updateMarksButton.setPreferredSize(new Dimension(150, 30));
        updateMarksButton.setBackground(TURQUOISE); // Change to your desired color
        deletebuttonpanel.add(updateMarksButton);
        updateMarksButton.addActionListener(e -> updateMarks(marksTable));


        JButton deleteMarksButton = new JButton("Delete Marks");
        deleteMarksButton.setPreferredSize(new Dimension(150, 30));
        deleteMarksButton.setBackground(TURQUOISE);
        deletebuttonpanel.add(deleteMarksButton);
        deleteMarksButton.addActionListener(e -> deleteMarks(marksTable));


        studentMarksPanel.add(deletebuttonpanel,BorderLayout.SOUTH);

        loadMarksData(); // Load data into the marks table
        initializeStudentMarksTab();

        tabbedPane.addTab("Student Marks", studentMarksPanel);

        // Student Attendance Tab
        JPanel studentAttendancePanel = new JPanel(new GridLayout(5, 1, 10, 10));
        studentAttendancePanel.setBackground(LIGHT_LIGHT);
        tabbedPane.addTab("Student Attendance", studentAttendancePanel);

        String[] ColumnNames = {"Student ID", "Student Name", "Present"}; // Column names
        Object[][] studentData = MyJDBC.getAllStudents(); // Fetch data from the database

// Add a checkbox for marking attendance
        DefaultTableModel model = new DefaultTableModel(studentData, ColumnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Boolean.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
        };

        attendanceTable = new JTable(model);
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.getColumnModel().getColumn(2) // Assuming the "Present" checkbox is in the third column
                .setCellEditor(new DefaultCellEditor(new JCheckBox()));
        JScrollPane scrollPanes = new JScrollPane(attendanceTable);
        studentAttendancePanel.add(scrollPanes);
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 10)); // 10 pixels height for space
        studentAttendancePanel.add(spacerPanel);

        summaryModel = new DefaultTableModel(new Object[]{"Student Name", "Status"}, 0);
        summaryTable = new JTable(summaryModel);
        JScrollPane summaryScrollPane = new JScrollPane(summaryTable);
        studentAttendancePanel.add(summaryScrollPane);
        summaryTable.setPreferredScrollableViewportSize(new Dimension(400, 100));

        date = new JTextField();
        date.setFont(new Font("Tahoma", Font.ITALIC, 13));
        date.setColumns(10);
        date.setEditable(false);
        showDate();

        // Create a panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        studentAttendancePanel.add(buttonPanel);

        JLabel jb = new JLabel("DATE :",JLabel.CENTER);
        buttonPanel.add(jb);
        buttonPanel.add(date);

        JButton submitAttendanceButton = new JButton("Submit");
        submitAttendanceButton.setPreferredSize(new Dimension(100, 30)); // Set button size
        buttonPanel.add(submitAttendanceButton);
        submitAttendanceButton.setBackground(TURQUOISE);

        JButton refreshAttendanceButton = new JButton("Refresh");
        buttonPanel.add(refreshAttendanceButton);
        refreshAttendanceButton.setPreferredSize(new Dimension(100, 30));// Set a smaller size for the button
        refreshAttendanceButton.setBackground(TURQUOISE);
        refreshAttendanceButton.addActionListener(e -> refreshAttendanceTable());
        buttonPanel.add(refreshAttendanceButton);
        studentAttendancePanel.revalidate();
        studentAttendancePanel.repaint();

        submitAttendanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < attendanceTable.getRowCount(); i++) {
                    String studentId = attendanceTable.getValueAt(i, 0).toString();
                    String studentName = attendanceTable.getValueAt(i, 1).toString();
                    Boolean present = (Boolean) attendanceTable.getValueAt(i, 2); // Checkbox column for attendance
                    String status = (present != null && present) ? "Present" : "Absent"; // Determine status
                    java.util.Date attendanceDate = new java.util.Date();
                    java.sql.Date sqlAttendanceDate = new java.sql.Date(attendanceDate.getTime());
                    MyJDBC.insertAttendance(studentId, studentName, sqlAttendanceDate, status);
                }
                refreshSummaryTable();
                refreshDefaulterStatus();
            }
        });

        // Course Enrollment Tab
        JPanel courseEnrollmentPanel = new JPanel(new GridLayout(1, 1));

        // Available Courses
        JPanel availableCoursesPanel = new JPanel();
        availableCoursesPanel.setBackground(LIGHT_LIGHT);
        JTable availableCoursesTable = new JTable(new Object[][]{},
                new String[]{"Student ID", "Course ID", "Course Name", "Completion Percentage"});
        JScrollPane courseScrollPane = new JScrollPane(availableCoursesTable);
        availableCoursesPanel.add(courseScrollPane);
        courseEnrollmentPanel.add(availableCoursesPanel);
        tabbedPane.addTab("Student Courses", courseEnrollmentPanel);



// Assuming you have an instance of MyJDBC
        MyJDBC myJDBC = new MyJDBC();
        List<Object[]> courseData = myJDBC.getAllCourses();

// Convert the List to a 2D array for the JTable
        Object[][] courseDataArray = new Object[courseData.size()][4];
        for (int i = 0; i < courseData.size(); i++) {
            courseDataArray[i] = courseData.get(i);
        }

// Set the data in the table
        availableCoursesTable.setModel(new DefaultTableModel(courseDataArray, new String[]{"Student ID", "Course ID", "Course Name", "Completion Percentage"}));
        add(tabbedPane, BorderLayout.CENTER);

    }

    public void loadMarksData() {
        DefaultTableModel model = (DefaultTableModel) marksTable.getModel();
        model.setRowCount(0); // Clear existing rows

        // Fetch the updated data from the database (assuming MyJDBC has a method for this)
        List<Object[]> updatedMarksData = MyJDBC.getAllMarks(); // Implement getAllMarks() to fetch data

        // Re-populate the table with the updated data
        for (Object[] rowData : updatedMarksData) {
            model.addRow(rowData);
        }

        // Optionally, revalidate and repaint the table
//        initializeStudentMarksTab();
        marksTable.revalidate();
        marksTable.repaint();
    }


    private void refreshSummaryTable() {
        summaryModel.setRowCount(0);

        // Get all students from the attendance table
        for (int i = 0; i < attendanceTable.getRowCount(); i++) {
            String studentName = attendanceTable.getValueAt(i, 1).toString();
            Boolean present = (Boolean) attendanceTable.getValueAt(i, 2);
            String status = (present != null && present) ? "Present" : "Absent";

            // Add the student data to the summary model
            summaryModel.addRow(new Object[]{studentName, status});
        }
        refreshDefaulterStatus();
    }

    public void showDate() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(sf.format(d));
    }

    private void showHomePage() {
        ProfileManager profileManager = new ProfileManager();

        // Retrieve the profile data
        String[] profileData = profileManager.getProfileData();

        // Check if the data is not empty
        if (profileData[0] != null) {
            SwingUtilities.invokeLater(() -> {
                StudentReportSystem studentReportFrame = new StudentReportSystem(
                        profileData[0], // studentId
                        profileData[1], // name
                        profileData[2], // department
                        profileData[3], // attendance
                        profileData[4], // courseCompletion
                        profileData[5], // cgpa
                        profileData[6]  // defaulterStatus
                );
                studentReportFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please save your profile first.", "Profile Not Saved", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Object[][] fetchStudentData() {
        List<Object[]> studentDataList = MyJDBC.getStudentData();
        Object[][] data = new Object[studentDataList.size()][6];
        for (int i = 0; i < studentDataList.size(); i++) {
            data[i] = studentDataList.get(i);  // Fill the JTable data array
        }
        return data;
    }

    // Method to configure each JProgressBar
    private void configureProgressBar(JProgressBar progressBar, String label) {
        progressBar.setStringPainted(true); // Shows percentage value
        progressBar.setBorder(BorderFactory.createTitledBorder(label)); // Set subject as label
        progressBar.setFont(new Font("Arial", Font.BOLD, 14)); // Customize font
        progressBar.setForeground(Color.GREEN); // Customize progress bar color
    }

    public void refreshStudentData() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);
        Object[][] newData = fetchStudentData();
        for (Object[] row : newData) {
            model.addRow(row);
        }
        model.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "Student data refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshDefaulterStatus() {
        for (int i = 0; i < studentTable.getRowCount(); i++) {
            String studentId = studentTable.getValueAt(i, 0).toString();
            boolean isDefaulter = MyJDBC.isStudentDefaulter(studentId);
            MyJDBC.updateDefaulterStatus(studentId, isDefaulter);
        }
        refreshStudentData(); // Reload the student data after update
        // No more recursive call here
    }



    // Calculate completion percentages based on pass/fail counts from the database
    @SuppressWarnings("unused")
    public void addMarks(int studentId, int emIII, int poc, int pcpf, int dsa, int java, int dbms) {
        try {
            boolean success = db.insertStudentMarks(studentId, emIII, poc, pcpf, dsa, java, dbms);

            if (!success) {
                JOptionPane.showMessageDialog(null, "A student with this ID already exists.");
            } else {
                JOptionPane.showMessageDialog(null, "Marks added successfully!");
                // Call the method to update the progress bars
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            JOptionPane.showMessageDialog(null, "Error adding marks: " + e.getMessage());
        }
    }



    public void initializeStudentMarksTab() {
        // Get the pass/fail counts from the database
        Map<String, Integer[]> passFailCounts = db.getPassFailCounts();

        // Calculate the total number of students
        int totalStudents = marksTableModel.getRowCount();

        if (totalStudents > 0) {
            // Calculate and set progress for each subject
            Integer[] emIIIStats = passFailCounts.get("EM-III");
            if (emIIIStats != null) {
                int emIIIPassPercentage = (emIIIStats[0] * 100) / totalStudents;
                emIIICircularProgressBar.setProgress(emIIIPassPercentage);
            }

            Integer[] pocStats = passFailCounts.get("POC");
            if (pocStats != null) {
                int pocPassPercentage = (pocStats[0] * 100) / totalStudents;
                pocCircularProgressBar.setProgress(pocPassPercentage);
            }

            Integer[] pcpfStats = passFailCounts.get("PCPF");
            if (pcpfStats != null) {
                int pcpfPassPercentage = (pcpfStats[0] * 100) / totalStudents;
                pcpfCircularProgressBar.setProgress(pcpfPassPercentage);
            }

            Integer[] dsaStats = passFailCounts.get("DSA");
            if (dsaStats != null) {
                int dsaPassPercentage = (dsaStats[0] * 100) / totalStudents;
                dsaCircularProgressBar.setProgress(dsaPassPercentage);

            }

            Integer[] javaStats = passFailCounts.get("JAVA");
            if (javaStats != null) {
                int javaPassPercentage = (javaStats[0] * 100) / totalStudents;
                javaCircularProgressBar.setProgress(javaPassPercentage);
            }

            Integer[] dbmsStats = passFailCounts.get("DBMS");
            if (dbmsStats != null) {
                int dbmsPassPercentage = (dbmsStats[0] * 100) / totalStudents;
                dbmsCircularProgressBar.setProgress(dbmsPassPercentage);
            }
        } else {
// Optional: Set progress bars to 0 or handle no students case
            emIIICircularProgressBar.setProgress(0);
            pocCircularProgressBar.setProgress(0);
            pcpfCircularProgressBar.setProgress(0);
            dsaCircularProgressBar.setProgress(0);
            javaCircularProgressBar.setProgress(0);
            dbmsCircularProgressBar.setProgress(0);
        }
    }



    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            String studentId = studentTable.getValueAt(selectedRow, 0).toString();
            String name = studentTable.getValueAt(selectedRow, 1).toString();
            String department = studentTable.getValueAt(selectedRow, 2).toString();
            String enrollmentDate = studentTable.getValueAt(selectedRow, 3).toString();
            String defaulterStatus = studentTable.getValueAt(selectedRow, 4).toString();
            String contactInfo = studentTable.getValueAt(selectedRow, 5).toString();

            JTextField studentIdField = new JTextField(studentId);
            JTextField nameField = new JTextField(name);
            JTextField departmentField = new JTextField(department);
            JTextField enrollmentDateField = new JTextField(enrollmentDate);
            JTextField defaulterField = new JTextField(defaulterStatus);
            JTextField contactField = new JTextField(contactInfo);

            Object[] message = {
                    "Student ID:", studentIdField,
                    "Name:", nameField,
                    "Department:", departmentField,
                    "Enrollment Date:", enrollmentDateField,
                    "Defaulter Status:", defaulterField,
                    "Contact Info:", contactField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Update Student", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                MyJDBC.updateStudent(studentId, nameField.getText(), departmentField.getText(),
                        enrollmentDateField.getText(), defaulterField.getText(),
                        contactField.getText());
                refreshStudentData();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a student to update.");
        }
    }

    private void updateMarks(JTable marksTable) {
        int selectedRow = marksTable.getSelectedRow(); // Get the selected row
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to update marks.");
            return; // Exit the method if no row is selected
        }

        // Get the current marks from the selected row
        Object studentId = marksTable.getValueAt(selectedRow, 0); // Assuming the first column is Student ID
        Object emIII = marksTable.getValueAt(selectedRow, 1); // EM-III marks
        Object poc = marksTable.getValueAt(selectedRow, 2); // POC marks
        Object pcpf = marksTable.getValueAt(selectedRow, 3); // PCPF marks
        Object dsa = marksTable.getValueAt(selectedRow, 4); // DSA marks
        Object java = marksTable.getValueAt(selectedRow, 5); // JAVA marks
        Object dbms = marksTable.getValueAt(selectedRow, 6); // DBMS marks

        // Create a dialog to update marks
        JTextField emIIIField = new JTextField(emIII.toString());
        JTextField pocField = new JTextField(poc.toString());
        JTextField pcpfField = new JTextField(pcpf.toString());
        JTextField dsaField = new JTextField(dsa.toString());
        JTextField javaField = new JTextField(java.toString());
        JTextField dbmsField = new JTextField(dbms.toString());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("EM-III:"));
        panel.add(emIIIField);
        panel.add(new JLabel("POC:"));
        panel.add(pocField);
        panel.add(new JLabel("PCPF:"));
        panel.add(pcpfField);
        panel.add(new JLabel("DSA:"));
        panel.add(dsaField);
        panel.add(new JLabel("JAVA:"));
        panel.add(javaField);
        panel.add(new JLabel("DBMS:"));
        panel.add(dbmsField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Marks for Student ID: " + studentId, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Parse new marks from text fields
            int emIIIValue = Integer.parseInt(emIIIField.getText());
            int pocValue = Integer.parseInt(pocField.getText());
            int pcpfValue = Integer.parseInt(pcpfField.getText());
            int dsaValue = Integer.parseInt(dsaField.getText());
            int javaValue = Integer.parseInt(javaField.getText());
            int dbmsValue = Integer.parseInt(dbmsField.getText());

            // Update the marks in the table model
            marksTableModel.setValueAt(emIIIValue, selectedRow, 1);
            marksTableModel.setValueAt(pocValue, selectedRow, 2);
            marksTableModel.setValueAt(pcpfValue, selectedRow, 3);
            marksTableModel.setValueAt(dsaValue, selectedRow, 4);
            marksTableModel.setValueAt(javaValue, selectedRow, 5);
            marksTableModel.setValueAt(dbmsValue, selectedRow, 6);

            // Update the database as needed (this part is optional, based on your design)
             MyJDBC.updateStudentMarks(studentId, emIIIValue, pocValue, pcpfValue, dsaValue, javaValue, dbmsValue);
            initializeStudentMarksTab();
            // Make sure to implement the updateStudentMarks method in your MyJDBC class.
        }
    }




    private void deleteMarks(JTable marksTable) {
        int selectedRow = marksTable.getSelectedRow();

        if (selectedRow != -1) {
            Object studentIdObj = marksTable.getValueAt(selectedRow, 0); // Get value as Object
            int studentId;

            // Check if the student ID is already an Integer or needs to be parsed from a String
            if (studentIdObj instanceof Integer) {
                studentId = (Integer) studentIdObj;
            } else {
                studentId = Integer.parseInt(studentIdObj.toString()); // Handle as String if necessary
            }

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete marks for student ID: " + studentId + "?",
                    "Delete Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                // Delete marks in the database
                MyJDBC.deleteMarks(studentId);
                initializeStudentMarksTab();

                // Refresh the table data after deletion
                loadMarksData(); // Call your existing method to reload the data

                JOptionPane.showMessageDialog(this, "Marks deleted successfully.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete marks.");
        }
    }



    private void refreshAttendanceTable() {
        Object[][] updatedData = MyJDBC.getAllStudents();
        String[] columnNames = {"Student ID", "Student Name", "Present"};
        attendanceTable.setModel(new DefaultTableModel(updatedData, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 2 ? Boolean.class : String.class;
            }
        });
        attendanceTable.revalidate();
        attendanceTable.repaint();
    }

    private void openStudentViewer() {
        SwingUtilities.invokeLater(() -> new StudentViewer());
    }

    private void showStudentPage() {
        SwingUtilities.invokeLater(() -> {
            StudentRecordViewer studentRecordViewer = new StudentRecordViewer();
            studentRecordViewer.setVisible(true);
        });
    }

    private void openProfileManager() {
        SwingUtilities.invokeLater(() -> {
            ProfileManager profileManager = new ProfileManager();
            profileManager.setVisible(true);

            profileManager.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    String[] profileData = profileManager.getProfileData();
                    openStudentReportSystem(profileData);
                    
                }
            });
        });
    }


    private void openStudentReportSystem(String[] profileData) {
        SwingUtilities.invokeLater(() -> {
            StudentReportSystem reportSystem = new StudentReportSystem(profileData[0], profileData[1], profileData[2],
                    profileData[3], profileData[4], profileData[5], profileData[6]);
            reportSystem.setVisible(true);
        });

    }

    private void moveRowToTop(Object[] studentData) {
        tableModel.setRowCount(0);  // Clear the table model
        tableModel.addRow(studentData);  // Add the found student data at the top

        // Re-add other students from originalStudentData
        for (Object[] student : originalStudentData) {
            if (!Arrays.equals(student, studentData)) {
                tableModel.addRow(student);  // Add other rows back
            }
        }
    }

    private void showStudentDetailsForm() {
        SwingUtilities.invokeLater(() -> {
            StudentDetailsForm studentDetailsForm = new StudentDetailsForm(this);
            studentDetailsForm.setVisible(true);
        });
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);
        List<Object[]> students = MyJDBC.getStudentData();
        for (Object[] student : students) {
            model.addRow(student);
        }
    }

    private class NavigationActionListener implements ActionListener {
        private final String tabName;

        public NavigationActionListener(String tabName) {
            this.tabName = tabName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Navigation button clicked: " + tabName);
            StudentDetailsForm form = new StudentDetailsForm(SchoolManagementSystem.this);  // 'this' refers to the SchoolManagementSystem instance
            form.setVisible(true);
        }
    }

    public class CircularProgressBar extends JProgressBar {
        private final Color progressColor;

        public CircularProgressBar(Color progressColor) {
            this.progressColor = progressColor;
            setUI(new ProgressCircleUI(progressColor));
            setStringPainted(true); // Enable string painting
        }

        public void setProgress(int progress) {
            setValue(progress);
            setString(progress + "%"); // Set string to display percentage
        }

        private class ProgressCircleUI extends BasicProgressBarUI {
            private final Color progressColor; // Declare progressColor here
            public ProgressCircleUI(Color progressColor) {
                this.progressColor = progressColor;
            }

            @Override
            public Dimension getPreferredSize(JComponent c) {
                Dimension d = super.getPreferredSize(c);
                int v = Math.max(d.width, d.height);
                d.setSize(v, v);
                return d;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                JProgressBar progressBar = (JProgressBar) c;
                Rectangle rect = SwingUtilities.calculateInnerArea(progressBar, null);
                if (rect.isEmpty()) {
                    return;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                double start = 90d; // Starting angle for drawing the circle
                double degree = 360d * progressBar.getPercentComplete(); // Calculate the degree of progress
                double sz = Math.min(rect.width, rect.height);
                double cx = rect.getCenterX();
                double cy = rect.getCenterY();
                double or = sz * 0.5; // Outer radius
                double ir = or * 0.5; // Inner radius
                Shape inner = new Ellipse2D.Double(cx - ir, cy - ir, ir * 2d, ir * 2d);
                Shape outer = new Ellipse2D.Double(cx - or, cy - or, sz, sz);
                Shape sector = new Arc2D.Double(cx - or, cy - or, sz, sz, start, degree, Arc2D.PIE);

                Area foreground = new Area(sector);
                Area background = new Area(outer);
                Area hole = new Area(inner);

                foreground.subtract(hole);
                background.subtract(hole);

                g2.setPaint(new Color(0xDD_DD_DD)); // Background color
                g2.fill(background);

                g2.setPaint(progressColor); // Progress color
                g2.fill(foreground);

                g2.setPaint(Color.BLACK); // Text color
                g2.setFont(new Font("Arial", Font.PLAIN, 16));
                String text = progressBar.getString();
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.drawString(text, (int) (cx - textWidth / 2), (int) (cy + textHeight / 2));

                g2.dispose();
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchoolManagementSystem frame = new SchoolManagementSystem();
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        });
    }
}
