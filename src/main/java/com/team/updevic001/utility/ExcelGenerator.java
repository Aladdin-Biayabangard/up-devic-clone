package com.team.updevic001.utility;

import com.team.updevic001.model.dtos.application.TeacherApplicationResponseDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static ByteArrayOutputStream generateExcel(List<TeacherApplicationResponseDto> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Teacher Applications");

            // Header sətiri
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "ID",
                    "Full Name",
                    "Email",
                    "Teaching Field",
                    "Phone Number",
                    "LinkedIn",
                    "GitHub",
                    "Portfolio",
                    "Additional Info",
                    "Status",
                    "Result Message",
                    "Created At",
                    "Completed At"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data sətirləri
            int rowIndex = 1;
            for (TeacherApplicationResponseDto dto : data) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(dto.getId().toString());
                row.createCell(1).setCellValue(dto.getFullName());
                row.createCell(2).setCellValue(dto.getEmail());
                row.createCell(3).setCellValue(dto.getTeachingField());
                row.createCell(4).setCellValue(dto.getPhoneNumber());
                row.createCell(5).setCellValue(dto.getLinkedinProfile());
                row.createCell(6).setCellValue(dto.getGithubProfile());
                row.createCell(7).setCellValue(dto.getPortfolio() != null ? dto.getPortfolio() : "");
                row.createCell(8).setCellValue(dto.getAdditionalInfo() != null ? dto.getAdditionalInfo() : "");
                row.createCell(9).setCellValue(dto.getStatus().name());
                row.createCell(10).setCellValue(dto.getResultMessage() != null ? dto.getResultMessage() : "");
                row.createCell(11).setCellValue(dto.getCreatedAt().toString());
                row.createCell(12).setCellValue(dto.getCompletedAt() != null ? dto.getCompletedAt().toString() : "");
            }

            // Avtomatik sütun ölçüləri
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out;
        }
    }
}
