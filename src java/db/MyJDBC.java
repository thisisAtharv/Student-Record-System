package db;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyJDBC {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/login_schema";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Atharv@123";

    /**
     * Searches for a student by name or student ID.
     *
     * @param searchQuery The text to search for (can be student ID or name).
     * @return An Object array containing the matched student data or null if not found.
     */
    public static Object[] searchStudent(String searchQuery) {
        String query = "SELECT student_id, name, department, enrollment_date, status, contact_info FROM students WHERE student_id = ? OR LOWER(name) = LOWER(?)";
        Object[] studentData = null;

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, searchQuery.trim());  // Search by student ID
            pstmt.setString(2, searchQuery.trim());  // Or by name (case-insensitive)

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                studentData = new Object[]{
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDate("enrollment_date"),
                        rs.getString("status"),
                        rs.getString("contact_info")
                };
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentData;
    }


    /**
     * Retrieves student data from the 'students' table.
     *
     * @return List of Object arrays containing student data.
     */
    public static List<Object[]> getStudentData() {
        List<Object[]> studentData = new ArrayList<>();
        String query = "SELECT student_id, name, department, enrollment_date, status, contact_info FROM students";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("student_id"),
                        resultSet.getString("name"),
                        resultSet.getString("department"),
                        resultSet.getDate("enrollment_date"),
                        resultSet.getString("status"),
                        resultSet.getString("contact_info") // Changed to String
                };
                studentData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentData;
    }

    public static Object[][] getAllStudents() {
        String query = "SELECT student_id, name FROM students"; // Adjust based on your database
        List<Object[]> studentsList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String name = rs.getString("name");
                studentsList.add(new Object[]{studentId, name});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert List<Object[]> to Object[][]
        Object[][] studentsArray = new Object[studentsList.size()][];
        for (int i = 0; i < studentsList.size(); i++) {
            studentsArray[i] = studentsList.get(i);
        }

        return studentsArray;
    }

    public List<Object[]> getAllCourses() {
        List<Object[]> courses = new ArrayList<>();
        String query = "SELECT student_id, course_id, course_name, completion_percentage FROM courses";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] courseData = {
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("completion_percentage")
                };
                courses.add(courseData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static List<Object[]> getStudentRecords() {
        List<Object[]> studentRecords = new ArrayList<>();
        String query = "SELECT student_id, student_name, fees, fee_status, defaulter_status FROM student_records"; // Update table name accordingly

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] record = {
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getString("fees"),
                        rs.getString("fee_status"),
                        rs.getString("defaulter_status")
                };
                studentRecords.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentRecords;
    }

    // Method to delete a student record
    public static void deleteStudentRecord(int studentId) {
        String query = "DELETE FROM student_records WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add a student record
    public static void addStudent(int studentId, String studentName, String fees, String feeStatus, String defaulterStatus) {
        String query = "INSERT INTO student_records (student_id, student_name, fees, fee_status, defaulter_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, studentName);
            stmt.setString(3, fees);
            stmt.setString(4, feeStatus);
            stmt.setString(5, defaulterStatus);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a student record
    public static void updateStudent(String studentId, String studentName, String fees, String feeStatus, String defaulterStatus) {
        String query = "UPDATE student_records SET student_name = ?, fees = ?, fee_status = ?, defaulter_status = ? WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentName);
            stmt.setString(2, fees);
            stmt.setString(3, feeStatus);
            stmt.setString(4, defaulterStatus);
            stmt.setString(5, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer[]> getPassFailCounts() {
        Map<String, Integer[]> passFailCounts = new HashMap<>();

        // Count pass/fail for each subject assuming columns like em_iii, poc, pcpf, dsa, java, dbms exist
        String query = "SELECT " +
                "SUM(CASE WHEN em_iii >= 35 THEN 1 ELSE 0 END) AS em_iii_pass, " +
                "SUM(CASE WHEN em_iii < 35 THEN 1 ELSE 0 END) AS em_iii_fail, " +
                "SUM(CASE WHEN poc >= 35 THEN 1 ELSE 0 END) AS poc_pass, " +
                "SUM(CASE WHEN poc < 35 THEN 1 ELSE 0 END) AS poc_fail, " +
                "SUM(CASE WHEN pcpf >= 35 THEN 1 ELSE 0 END) AS pcpf_pass, " +
                "SUM(CASE WHEN pcpf < 35 THEN 1 ELSE 0 END) AS pcpf_fail, " +
                "SUM(CASE WHEN dsa >= 35 THEN 1 ELSE 0 END) AS dsa_pass, " +
                "SUM(CASE WHEN dsa < 35 THEN 1 ELSE 0 END) AS dsa_fail, " +
                "SUM(CASE WHEN java >= 35 THEN 1 ELSE 0 END) AS java_pass, " +
                "SUM(CASE WHEN java < 35 THEN 1 ELSE 0 END) AS java_fail, " +
                "SUM(CASE WHEN dbms >= 35 THEN 1 ELSE 0 END) AS dbms_pass, " +
                "SUM(CASE WHEN dbms < 35 THEN 1 ELSE 0 END) AS dbms_fail " +
                "FROM student_marks";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                passFailCounts.put("EM-III", new Integer[] {rs.getInt("em_iii_pass"), rs.getInt("em_iii_fail")});
                passFailCounts.put("POC", new Integer[] {rs.getInt("poc_pass"), rs.getInt("poc_fail")});
                passFailCounts.put("PCPF", new Integer[] {rs.getInt("pcpf_pass"), rs.getInt("pcpf_fail")});
                passFailCounts.put("DSA", new Integer[] {rs.getInt("dsa_pass"), rs.getInt("dsa_fail")});
                passFailCounts.put("JAVA", new Integer[] {rs.getInt("java_pass"), rs.getInt("java_fail")});
                passFailCounts.put("DBMS", new Integer[] {rs.getInt("dbms_pass"), rs.getInt("dbms_fail")});
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions appropriately
        }

        return passFailCounts;
    }



    public boolean checkIfStudentExists(int studentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM student_marks WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId); // Changed to setInt for consistency
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Returns true if student exists
            }
        }
        return false;  // Student does not exist
    }

    public boolean insertStudentMarks(int studentId, int emIII, int poc, int pcpf, int dsa, int java, int dbms) throws SQLException {
        try {
            if (checkIfStudentExists(studentId)) {
                System.out.println("Duplicate entry for student_id: " + studentId);
                return false; // Indicate that the insertion was not successful
            }

            String sql = "INSERT INTO student_marks (student_id, em_iii, poc, pcpf, dsa, java, dbms) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, emIII);
                pstmt.setInt(3, poc);
                pstmt.setInt(4, pcpf);
                pstmt.setInt(5, dsa);
                pstmt.setInt(6, java);
                pstmt.setInt(7, dbms);
                pstmt.executeUpdate();
                System.out.println("Marks inserted successfully!");
                return true; // Indicate that the insertion was successful
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
            return false; // Indicate failure
        }
    }


    public static void updateStudentMarks(Object studentId, int emIII, int poc, int pcpf, int dsa, int java, int dbms) {
        String query = "UPDATE student_marks SET em_iii = ?, poc = ?, pcpf = ?, dsa = ?, java = ?, dbms = ? WHERE student_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, emIII);
            preparedStatement.setInt(2, poc);
            preparedStatement.setInt(3, pcpf);
            preparedStatement.setInt(4, dsa);
            preparedStatement.setInt(5, java);
            preparedStatement.setInt(6, dbms);
            preparedStatement.setObject(7, studentId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }



    public static void deleteMarks(int studentId) {
        String sql = "DELETE FROM student_marks WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveStudentProfile(String studentId, String name, String department, String attendance, String courseCompletion, String cgpa, String defaulterStatus, String imagePath) {
        String sql = "INSERT INTO student_profiles (student_id, name, department, attendance, course_completion, cgpa, defaulter_status, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, name);
            pstmt.setString(3, department);
            pstmt.setString(4, attendance);
            pstmt.setString(5, courseCompletion);
            pstmt.setString(6, cgpa);
            pstmt.setString(7, defaulterStatus);
            pstmt.setString(8, imagePath);  // Store image path if needed

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String[] getStudentProfile(String studentId) {
        String sql = "SELECT * FROM student_profiles WHERE student_id = ?";
        String[] profileData = new String[8];  // Adjust the size according to your needs

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                profileData[0] = rs.getString("student_id");
                profileData[1] = rs.getString("name");
                profileData[2] = rs.getString("department");
                profileData[3] = rs.getString("attendance");
                profileData[4] = rs.getString("course_completion");
                profileData[5] = rs.getString("cgpa");
                profileData[6] = rs.getString("defaulter_status");
                profileData[7] = rs.getString("image_path"); // If you store image path
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profileData;
    }








    public static Object[][] getAlltheStudents() {
        String query = "SELECT student_id, name, department FROM students"; // Adjust based on your database
        List<Object[]> studentsList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String name = rs.getString("name");
                String department = rs.getString("department");
                studentsList.add(new Object[]{studentId, name, department});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert List<Object[]> to Object[][]
        Object[][] studentsArray = new Object[studentsList.size()][];
        for (int i = 0; i < studentsList.size(); i++) {
            studentsArray[i] = studentsList.get(i);
        }

        return studentsArray;
    }

    public static List<Object[]> getAllMarks() {
        List<Object[]> marksList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student_marks")) { // Replace with your table name

            while (rs.next()) {
                // Assuming your table has 7 columns: student_id, EM-III, POC, PCPF, DSA, JAVA, DBMS
                Object[] row = new Object[7];
                row[0] = rs.getInt("student_id");
                row[1] = rs.getInt("em_iii");
                row[2] = rs.getInt("poc");
                row[3] = rs.getInt("pcpf");
                row[4] = rs.getInt("dsa");
                row[5] = rs.getInt("java");
                row[6] = rs.getInt("dbms");

                marksList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marksList;
    }





    public static Object[][] getCoursesForStudent(String studentId) {
        String query = "SELECT course_id, course_name, completion_percentage FROM courses WHERE student_id = ?"; // Adjust based on your database
        List<Object[]> coursesList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String courseName = rs.getString("course_name");
                int completion = rs.getInt("completion_percentage");
                coursesList.add(new Object[]{courseId, courseName, completion});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert List<Object[]> to Object[][]
        Object[][] coursesArray = new Object[coursesList.size()][];
        for (int i = 0; i < coursesList.size(); i++) {
            coursesArray[i] = coursesList.get(i);
        }

        return coursesArray;
    }

    public static Object[][] getCoursesByStudentId(String studentId) {
        String query = "SELECT course_id, course_name, completion_percentage FROM courses WHERE student_id = ?";
        List<Object[]> coursesList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId); // Set the student ID
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String courseName = rs.getString("course_name");
                int completion = rs.getInt("completion_percentage");
                coursesList.add(new Object[]{courseId, courseName, completion});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert List<Object[]> to Object[][]
        Object[][] coursesArray = new Object[coursesList.size()][];
        for (int i = 0; i < coursesList.size(); i++) {
            coursesArray[i] = coursesList.get(i);
        }

        return coursesArray;
    }





    public static void insertAttendance(String studentId, String studentName, Date attendanceDate, String status) {
        String query = "INSERT INTO attendance (student_id, student_name, attendance_date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, studentName);
            pstmt.setDate(3, attendanceDate); // No need to convert to java.sql.Date here
            pstmt.setString(4, status);
            pstmt.executeUpdate(); // Execute the update
        } catch (SQLException e) {
            e.printStackTrace(); // Handle any SQL exceptions
        }
    }


    public static Map<String, String> calculateDefaulters() {
        Map<String, String> defaulterStatus = new HashMap<>();
        String query = "SELECT student_id, COUNT(*) AS total_days, SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_count " +
                "FROM attendance " +
                "WHERE attendance_date >= CURDATE() - INTERVAL 10 DAY " +
                "GROUP BY student_id";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                int totalDays = rs.getInt("total_days");
                int presentCount = rs.getInt("present_count");

                // Calculate attendance percentage
                double attendancePercentage = (double) presentCount / totalDays * 100;

                // Check defaulter status
                String status = attendancePercentage < 75 ? "Yes" : "No";
                defaulterStatus.put(studentId, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaulterStatus;
    }

    public static boolean isStudentDefaulter(String studentId) {
        String query = "SELECT COUNT(*) AS totalDays, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS presentDays " +
                "FROM attendance WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalDays = rs.getInt("totalDays");
                int presentDays = rs.getInt("presentDays");
                double attendancePercentage = (double) presentDays / totalDays * 100;

                return attendancePercentage < 75;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default in case of any error
    }


    public static void updateDefaulterStatus(String studentId, boolean isDefaulter) {
        String query = "UPDATE students SET status = ? WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, isDefaulter ? "Yes" : "No");
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }










    public static void addCourse(String studentId, String courseId, String courseName, int completion) {
        String query = "INSERT INTO courses (student_id, course_id, course_name, completion_percentage) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setString(3, courseName);
            pstmt.setInt(4, completion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void editCourse(String studentId, String courseId, String courseName, int completion) {
        String query = "UPDATE courses SET course_name = ?, completion_percentage = ? WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, courseName);
            pstmt.setInt(2, completion);
            pstmt.setString(3, studentId);
            pstmt.setString(4, courseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeCourse(String studentId, String courseId) {
        String query = "DELETE FROM courses WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean deleteStudentById(String studentId) {
        // First, delete attendance records associated with the student
        String deleteAttendanceQuery = "DELETE FROM attendance WHERE student_id = ?";
        String deleteStudentQuery = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement deleteAttendanceStmt = conn.prepareStatement(deleteAttendanceQuery);
             PreparedStatement deleteStudentStmt = conn.prepareStatement(deleteStudentQuery)) {

            // Delete attendance records
            deleteAttendanceStmt.setString(1, studentId);
            deleteAttendanceStmt.executeUpdate();

            // Now delete the student record
            deleteStudentStmt.setString(1, studentId);
            int rowsDeleted = deleteStudentStmt.executeUpdate();

            return rowsDeleted > 0;  // Return true if deletion was successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if an error occurred
        }
    }


    public static void updateStudent(String studentId, String name, String department, String enrollmentDate, String status, String contactInfo) {
        String sql = "UPDATE students SET name = ?, department = ?, enrollment_date = ?, status = ?, contact_info = ? WHERE student_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, department);
            pstmt.setString(3, enrollmentDate);
            pstmt.setString(4, status); // Use 'status' here
            pstmt.setString(5, contactInfo);
            pstmt.setString(6, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating student: " + e.getMessage());
        }
    }
    
}
