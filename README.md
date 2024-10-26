# Student Record System (SRS) - Mini Project

## Description

The **Student Record System** aka Student Management System is a Java-based application designed to manage and track student records, including attendance, courses, fees, and report generation. This project includes the main code, SQL setup files, and dependencies, providing a complete solution for easy setup and usage.

## Project Structure

### Folder Breakdown

- **db mysql**
  - `Srsminiproject.sql`: Contains all SQL commands used in the project to help understand the database schema and operations.
  - `Dump.sql`: Database dump file for importing into MySQL Workbench to set up the database on your system.

- **lib jar**
  - Contains external libraries (JAR files) required in the project:
    - **itextpdf**: Used for PDF report generation.
    - **mysql-connector**: Used for MySQL database connectivity.

- **src java**
  - Contains all Java source files.
  - Inside this folder, you’ll find the **db** folder, which includes:
    - `MyJDBC.java`: The main JDBC file with essential code for database operations.

## Key Features

- **Admin Login Page**: Provides secure login for administrative access.
- **Dashboard Tab**: Displays a JTable with student records, allowing the admin to add, update, delete, and refresh student information.
- **Student Marks Tab**: Shows student marks, including pass/fail status in a progress bar. Admin can add, update, and delete student marks.
- **Student Attendance Tab**: Enables marking attendance and generating defaulter lists based on attendance.
- **Student Courses Feature**: Supports course enrollment, editing, and management for students.
- **Student Fees Feature**: Tracks and manages student fees.
- **Report Generation**: Creates detailed PDF reports for individual students, utilizing data from the dashboard and attendance records.

## Setup Instructions

### Step 1: Set Up the Database

1. Open MySQL Workbench.
2. Import the `Dump.sql` file from the **db mysql** folder to set up the database structure and initial data.

### Step 2: Add JAR Files to the Project

Add the JAR files in the **lib jar** folder to your Java project to ensure necessary dependencies are included.

- **In Eclipse or IntelliJ IDEA**:
  1. Right-click on your project in the Project Explorer.
  2. Select **Build Path > Add External Archives**.
  3. Navigate to the **lib jar** folder, select `itextpdf.jar` and `mysql-connector.jar`, and add them to your project.

- **In NetBeans**:
  1. Right-click on the **Libraries** node of your project.
  2. Select **Add JAR/Folder**.
  3. Navigate to the **lib jar** folder, select `itextpdf.jar` and `mysql-connector.jar`, and add them to your project.

Ensure these libraries are correctly added by checking the project’s **Libraries** section to enable PDF generation and database connectivity.

 ## Getting Started

After setting up the database and adding the necessary JAR files, you can start the application. The admin login page will appear first, allowing you to access the main dashboard and other tabs. Explore each tab to manage student data, track attendance, handle courses, and generate reports.

## Usage

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/thisisAtharv/Student-Record-System.git
   cd Student-Record-System
2. **Compile the Project**: Ensure your Java IDE is set up to recognize all necessary libraries and dependencies before running the project.
3. **Run the Program**: Launch the application to access the admin login page, which provides access to the main dashboard and other tabs. Explore each tab to manage student data, track attendance, manage courses, and generate reports.  

## Contributing

Contributions are welcome! If you have suggestions or improvements, please create a pull request or open an issue.

## License

IT IS FREE USE IT AS PER YOUR REQUIREMENT

## Author

Developed by [thisisAtharv](https://github.com/thisisAtharv).
