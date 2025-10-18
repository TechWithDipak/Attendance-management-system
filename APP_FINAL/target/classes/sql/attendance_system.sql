CREATE DATABASE IF NOT EXISTS attendance_system;
USE attendance_system;

DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;

CREATE TABLE teachers (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  latitude DOUBLE,
  longitude DOUBLE
);

CREATE TABLE students (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  roll_no VARCHAR(50) NOT NULL,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  latitude DOUBLE,
  longitude DOUBLE
);

CREATE TABLE attendance (
  id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL,
  teacher_id INT NOT NULL,
  course VARCHAR(100) NOT NULL,
  date DATE NOT NULL,
  status VARCHAR(20) NOT NULL,
  location VARCHAR(100),
  FOREIGN KEY (student_id) REFERENCES students(id),
  FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

INSERT INTO teachers (name, username, password, latitude, longitude) VALUES
('Alice Johnson', 'alice', 'alice123', 12.9716, 77.5946),
('Bob Smith', 'bob', 'bob123', 12.9716, 77.5946);

INSERT INTO students (name, roll_no, username, password, latitude, longitude) VALUES
('Charlie Brown', 'CS101', 'charlie', 'charlie123', 12.9716, 77.5946),
('Dana White', 'CS102', 'dana', 'dana123', 12.9715, 77.5945);


