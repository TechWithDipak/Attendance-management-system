# 🏫 Attendance Management System (QR-Based)

## 📘 Overview
**Attendance Management System** is a Java-based desktop application designed to streamline student attendance tracking using **QR codes**.  
The system allows teachers and students to interact through an intuitive GUI — teachers can generate attendance sessions, while students can mark attendance by scanning QR codes. The system also integrates with a relational database for secure record storage and can export attendance data to Excel.

---

## ✨ Features
- 👩‍🏫 **Teacher Dashboard:** Manage attendance sessions, generate QR codes, and view reports.  
- 🎓 **Student Dashboard:** Scan QR codes to mark attendance instantly.  
- 📊 **Attendance Records:** Track, update, and export attendance in Excel format.  
- 🔐 **Secure Login:** Role-based login for teachers and students.  
- 🗄️ **Database Integration:** Uses SQL-based persistence for all records.  
- 🧩 **QR Code Utilities:** Built-in QR code generator and scanner.  
- 🌍 **Location Validator:** Optional validation of attendance based on device location.  

---

## 🧱 Tech Stack

| Component | Technology Used |
|------------|-----------------|
| Language | Java |
| Build Tool | Maven |
| GUI Framework | Swing (Java AWT) |
| Database | MySQL |
| Libraries | ZXing (QR Code), Apache POI (Excel Export), JDBC |
| IDE (recommended) | IntelliJ IDEA / Eclipse / NetBeans |

---

## ⚙️ Installation & Setup

### 🪜 1. Prerequisites
Ensure you have the following installed:
- **Java JDK 17+**
- **Apache Maven**
- **MySQL Server**
- **IDE** (IntelliJ, Eclipse, or NetBeans)

---

### ⚙️ 2. Database Setup
1. Open MySQL Workbench or CLI.  
2. Create a new database:
   \`\`\`sql
   CREATE DATABASE attendance_system;
   \`\`\`
3. Import the SQL file:
   \`\`\`sql
   USE attendance_system;
   SOURCE path/to/attendance_system.sql;
   \`\`\`
4. Update your database credentials inside `application.properties` (if applicable) or directly in `DatabaseConnection.java`.

---

### 🧩 3. Build and Run
Using **Maven CLI**:
\`\`\`bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.attendance.Main"
\`\`\`
Or simply run **Main.java** from your IDE.

---

## 🚀 Usage Guide

### 👩‍🏫 For Teachers:
- Log in using teacher credentials.
- Create new attendance sessions.
- Generate QR codes for students to scan.
- View or export attendance data.

### 🎓 For Students:
- Log in with student credentials.
- Scan the QR code displayed by the teacher using the in-app QR scanner.
- Confirmation appears when attendance is recorded successfully.

---

## 📤 Exporting Attendance
Teachers can export attendance reports in `.xlsx` format via the dashboard (powered by **Apache POI**).

---

## 🧠 QR Attendance Flow

1. Teacher creates a new session.  
2. System generates a **unique QR code** (encoded with session and time data).  
3. Students scan the code via their dashboard.  
4. The app validates and stores attendance in the database.

---
