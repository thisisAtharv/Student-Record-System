import db.MyJDBC;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class StudentViewer extends JFrame {
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Color GREEN2 = new Color(89, 116, 69); // Update this color as needed
    private DefaultTableModel studentTableModel;
    private Map<String, Object[][]> studentCourses;

    public StudentViewer() {
        setTitle("Student Viewer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        studentCourses = new HashMap<>();
        initializeStudentCourses();

        String[] columnNames = {"Student ID", "Student Name", "Department"};
        Object[][] data = MyJDBC.getAlltheStudents();

        studentTableModel = new DefaultTableModel(data, columnNames);
        JTable studentTable = new JTable(studentTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(173, 216, 230));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        studentTable.setFont(LABEL_FONT);
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setBackground(GREEN2);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow != -1) {
                    String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
                    Object[][] courses = MyJDBC.getCoursesForStudent(studentId);
                    openCourseViewer(studentId, courses);
                }
            }
        });


        add(new JScrollPane(studentTable), BorderLayout.CENTER);
        setVisible(true);
    }

    private void openCourseViewer(String studentId, Object[][] courses) {
        CourseViewer courseViewer = new CourseViewer(studentId, courses);
        courseViewer.setVisible(true);
    }



    private void initializeStudentCourses() {
        studentCourses.put("23104037", new Object[][]{
                {"C101", "JAVA", 85},
                {"C102", "C++", 60}
        });
        studentCourses.put("23104038", new Object[][]{
                {"C201", "Python", 90},
                {"C202", "JavaScript", 75}
        });
        studentCourses.put("23104039", new Object[][]{
                {"C301", "C++", 80},
                {"C302", "C", 70}
        });
        studentCourses.put("23104040", new Object[][]{
                {"C401", "HTML", 95},
                {"C402", "CSS", 88}
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentViewer::new);
    }
}

class CourseViewer extends JFrame {
    private static final Color LIGHT_BACKGROUND = new Color(255, 229, 236);
    private static final Color GREEN1 = new Color(114, 151, 98);
    private static final Color GREEN2 = new Color(114, 151, 98);
    private static final Color GREEN3 = new Color(114, 151, 98);
    private static final Color GREEN4 = new Color(114, 151, 98);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);

    private DefaultTableModel courseTableModel;
    private JPanel progressPanel;
    private String studentId;

    public CourseViewer(String studentId, Object[][] courseData) {
        this.studentId = studentId;
        setTitle("Course Viewer");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        String[] columnNames = {"Course ID", "Course Name", "Completion (%)"};
        courseTableModel = new DefaultTableModel(courseData, columnNames);
        JTable courseTable = new JTable(courseTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(173, 216, 230));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        courseTable.setFont(LABEL_FONT);
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setBackground(GREEN2);
        courseTable.getTableHeader().setForeground(Color.WHITE);
        courseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayout(2, 2));
        updateProgressPanel();
        add(progressPanel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_BACKGROUND);

        JButton addCourseButton = createStyledButton("Add Course");
        addCourseButton.addActionListener(e -> addCourse());
        buttonPanel.add(addCourseButton);

        JButton editCourseButton = createStyledButton("Edit Course");
        editCourseButton.addActionListener(e -> editCourse(courseTable));
        buttonPanel.add(editCourseButton);

        JButton removeCourseButton = createStyledButton("Remove Course");
        removeCourseButton.addActionListener(e -> removeCourse(courseTable));
        buttonPanel.add(removeCourseButton);

        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> dispose());
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(GREEN3);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 50));
        return button;
    }

    private void updateProgressPanel() {
        progressPanel.removeAll();
        Color[] colors = {GREEN1, GREEN2, GREEN3, GREEN4};

        for (int i = 0; i < courseTableModel.getRowCount(); i++) {
            String courseName = (String) courseTableModel.getValueAt(i, 1);
            int completion = (Integer) courseTableModel.getValueAt(i, 2);
            progressPanel.add(new CoursePanel(courseName, completion, colors[i % colors.length]));
        }

        progressPanel.revalidate();
        progressPanel.repaint();
    }

    public void refreshCourseData() {
        Object[][] updatedCourses = MyJDBC.getCoursesByStudentId(studentId);
        System.out.println("Updated Courses: " + Arrays.deepToString(updatedCourses)); // Debugging output
        courseTableModel.setRowCount(0); // Clear existing rows
        for (Object[] course : updatedCourses) {
            courseTableModel.addRow(course); // Add new data
        }
    }


    private void addCourse() {
        JTextField courseIdField = new JTextField(10);
        JTextField courseNameField = new JTextField(10);
        JTextField completionField = new JTextField(5);
        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(new JLabel("Course ID:"));
        inputPanel.add(courseIdField);
        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(courseNameField);
        inputPanel.add(new JLabel("Completion (%):"));
        inputPanel.add(completionField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Add New Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String courseId = courseIdField.getText();
            String courseName = courseNameField.getText();
            String completionText = completionField.getText();

            try {
                int completion = Integer.parseInt(completionText);
                if (completion < 0 || completion > 100) {
                    throw new NumberFormatException();
                }
                courseTableModel.addRow(new Object[]{courseId, courseName, completion});
                MyJDBC.addCourse(studentId, courseId, courseName, completion); // Call to addCourse
                updateProgressPanel();
//                refreshCourseData(); // Refresh data to show updated information
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid completion percentage (0-100).",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCourse(JTable courseTable) {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            String courseId = (String) courseTableModel.getValueAt(selectedRow, 0);
            String courseName = (String) courseTableModel.getValueAt(selectedRow, 1);
            int completion = (Integer) courseTableModel.getValueAt(selectedRow, 2);

            JTextField courseIdField = new JTextField(courseId);
            JTextField courseNameField = new JTextField(courseName);
            JTextField completionField = new JTextField(String.valueOf(completion));
            JPanel inputPanel = new JPanel(new GridLayout(0, 2));
            inputPanel.add(new JLabel("Course ID:"));
            inputPanel.add(courseIdField);
            inputPanel.add(new JLabel("Course Name:"));
            inputPanel.add(courseNameField);
            inputPanel.add(new JLabel("Completion (%):"));
            inputPanel.add(completionField);

            int result = JOptionPane.showConfirmDialog(null, inputPanel,
                    "Edit Course", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newCourseId = courseIdField.getText();
                String newCourseName = courseNameField.getText();
                String completionText = completionField.getText();

                try {
                    int newCompletion = Integer.parseInt(completionText);
                    if (newCompletion < 0 || newCompletion > 100) {
                        throw new NumberFormatException();
                    }
                    MyJDBC.editCourse(studentId, newCourseId, newCourseName, newCompletion); // Update DB
                    courseTableModel.setValueAt(newCourseId, selectedRow, 0);
                    courseTableModel.setValueAt(newCourseName, selectedRow, 1);
                    courseTableModel.setValueAt(newCompletion, selectedRow, 2);
                    updateProgressPanel();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid completion percentage (0-100).",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }

            }
            else{
                JOptionPane.showMessageDialog(this, "Please select a course to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }

    }

    private void removeCourse(JTable courseTable) {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            String courseId = (String) courseTableModel.getValueAt(selectedRow, 0);
            MyJDBC.removeCourse(studentId, courseId);
            refreshCourseData();
            updateProgressPanel();
        }
        else {
            JOptionPane.showMessageDialog(this, "Please select a course to remove.", "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
        }

    }
}

class CoursePanel extends JPanel {
    private JProgressBar progressBar;
    private String courseName;

    public CoursePanel(String courseName, int completion, Color color) {
        this.courseName = courseName;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(courseName));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(completion);
        progressBar.setStringPainted(true);
        progressBar.setFont(progressBar.getFont().deriveFont(16f));
        progressBar.setUI(new ProgressCircleUI(color)); // Set the circular UI
        add(progressBar, BorderLayout.CENTER);
    }
}
class ProgressCircleUI extends BasicProgressBarUI {
    private final Color progressColor;

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
