package com.team.updevic001.utility;

import com.team.updevic001.dao.entities.Teacher;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class Export {

    String path = "D://UpDevic-history/";

    public File exportToExcel(List<Teacher> teachers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Teacher's amount!");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("History of creation");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Teacher's name");
        header.createCell(3).setCellValue("Teacher's surname");
        header.createCell(4).setCellValue("Teacher's balance");

        int rowNum = 1;
        for (Teacher teacher : teachers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(LocalDateTime.now());
            row.createCell(1).setCellValue(teacher.getUser().getEmail());
            row.createCell(2).setCellValue(teacher.getUser().getFirstName());
            row.createCell(3).setCellValue(teacher.getUser().getLastName());
            row.createCell(4).setCellValue(String.valueOf(teacher.getBalance()));
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        LocalDate now = LocalDate.now();

        File file = new File(String.valueOf(now) + System.currentTimeMillis() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            System.out.println("Excel faylı yaradıldı: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.getStackTrace();

        } finally {
            workbook.close();
        }

        return file;
    }

}
