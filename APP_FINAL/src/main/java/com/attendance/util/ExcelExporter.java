package com.attendance.util;

import com.attendance.model.AttendanceRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ExcelExporter {
    public static Path exportAttendance(List<AttendanceRecord> records, Path output) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Student ID");
            header.createCell(2).setCellValue("Teacher ID");
            header.createCell(3).setCellValue("Course");
            header.createCell(4).setCellValue("Date");
            header.createCell(5).setCellValue("Status");

            int rowIdx = 1;
            for (AttendanceRecord r : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getId());
                row.createCell(1).setCellValue(r.getStudentId());
                row.createCell(2).setCellValue(r.getTeacherId());
                row.createCell(3).setCellValue(r.getCourse());
                row.createCell(4).setCellValue(r.getDate().toString());
                row.createCell(5).setCellValue(r.getStatus());
            }

            for (int i = 0; i <= 5; i++) {
                sheet.autoSizeColumn(i);
            }

            output.toFile().getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(output.toFile())) {
                workbook.write(fos);
            }
            return output;
        }
    }
}


