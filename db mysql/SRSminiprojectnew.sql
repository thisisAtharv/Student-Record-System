INSERT INTO `login_schema`.`adminlogin`
(
`username`,
`password`)
VALUES
(
"username",
"password");

-- SELECT * FROM adminlogin;
SELECT * FROM users;

show databases;
use login_schema;
SELECT * FROM students;
DELETE FROM students WHERE student_id='23104182';
SELECT student_id, name, department, enrollment_date, status, contact_info FROM students WHERE student_id = 12345678 OR name = 'Alice Johnson';
UPDATE users SET password='admin' WHERE id=1;
delete from attendance where student_id = '23104182';
SELECT * FROM users WHERE username = 'admin' AND password = 'admin';	
SELECT * FROM attendance;
ALTER TABLE student_records MODIFY COLUMN defaulter_status VARCHAR(20);
truncate table courses;	
select * from student_profiles;	
SELECT em_iii, poc, pcpf, dsa, java, dbms FROM student_marks;																														
UPDATE students SET name = 'Michael Jackson', department ='Music', enrollment_date = '1994-10-20', status = 'NO', contact_info = 9619175509 WHERE student_id = 46813579;
select * from student_records;
select * from student_marks;
-- SELECT student_id,name FROM students;
-- SELECT * FROM attendance;
INSERT INTO attendance (student_id,student_name,attendance_date,status) VALUES (91368024,'Sophie lee','1994-10-14','true');