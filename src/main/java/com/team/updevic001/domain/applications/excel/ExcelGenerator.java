package com.team.updevic001.domain.applications.excel;

import com.team.updevic001.domain.applications.dto.CourseApplicationResponseDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static ByteArrayOutputStream generateExcel(List<CourseApplicationResponseDto> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Course Applications");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Course ID", "Email", "Full Name", "Phone", "Message", "Success", "Created At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Populate data rows
            int rowIndex = 1;
            for (CourseApplicationResponseDto dto : data) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(dto.getId().toString());
                row.createCell(1).setCellValue(dto.getCourseId().toString());
                row.createCell(2).setCellValue(dto.getEmail());
                row.createCell(3).setCellValue(dto.getFullName());
                row.createCell(4).setCellValue(dto.getPhone());
                row.createCell(5).setCellValue(dto.getResultMessage());
                row.createCell(6).setCellValue(dto.getCreatedAt().toString());
                row.createCell(7).setCellValue(dto.getCompletedAt().toString());
            }

            workbook.write(out);
            return out;
        }
    }
}
