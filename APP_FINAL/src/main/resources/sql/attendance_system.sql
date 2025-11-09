-- Create the database only if it doesn't already exist
CREATE DATABASE IF NOT EXISTS attendance_system;

-- Switch to the newly created or existing database
USE attendance_system;

-- Drop existing tables to avoid conflicts during re-creation
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;

-- Create the 'teachers' table to store teacher credentials and location
CREATE TABLE teachers (
  id INT PRIMARY KEY AUTO_INCREMENT,         -- Unique ID for each teacher
  name VARCHAR(100) NOT NULL,                -- Full name of the teacher
  username VARCHAR(50) UNIQUE NOT NULL,      -- Unique login username
  password VARCHAR(100) NOT NULL,            -- Password (should be hashed in production)
  
);

-- Create the 'students' table to store student credentials and location
CREATE TABLE students (
  id INT PRIMARY KEY AUTO_INCREMENT,         -- Unique ID for each student
  name VARCHAR(100) NOT NULL,                -- Full name of the student
  roll_no VARCHAR(50) NOT NULL,              -- Roll number for academic identification
  username VARCHAR(50) UNIQUE NOT NULL,      -- Unique login username
  password VARCHAR(100) NOT NULL,            -- Password (should be hashed in production)
  
);

-- Create the 'attendance' table to record attendance entries
CREATE TABLE attendance (
  id INT PRIMARY KEY AUTO_INCREMENT,         -- Unique ID for each attendance record
  student_id INT NOT NULL,                   -- Foreign key linking to 'students' table
  teacher_id INT NOT NULL,                   -- Foreign key linking to 'teachers' table
  course VARCHAR(100) NOT NULL,              -- Name of the course or subject
  date DATE NOT NULL,                        -- Date of the attendance entry
  status VARCHAR(20) NOT NULL,               -- Attendance status (e.g., Present, Absent)
  location VARCHAR(100),                     -- Optional: textual location info
  FOREIGN KEY (student_id) REFERENCES students(id),  -- Enforce student relationship
  FOREIGN KEY (teacher_id) REFERENCES teachers(id)   -- Enforce teacher relationship
);

-- Insert sample teacher records with location data
INSERT INTO teachers (name, username, password, latitude, longitude) VALUES
('Dipak', 'dipak', 'abc', 12.9716, 77.5946),     -- Teacher Dipak 
('puneet', 'puneet', 'abc', 12.9716, 77.5946);   -- Teacher Puneet 

-- Insert sample student records with location data
INSERT INTO students (name, roll_no, username, password, latitude, longitude) VALUES
('manish', 'CS101', 'manish', 'abc', 12.9716, 77.5946),   -- Student Manish
('saksham', 'CS102', 'sanu', 'abc', 12.9715, 77.5945);    -- Student Saksham