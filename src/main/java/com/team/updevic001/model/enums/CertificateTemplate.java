package com.team.updevic001.model.enums;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public enum CertificateTemplate {

    RED("RED CERTIFICATE", new DeviceRgb(255, 0, 0), 24, "/C://Users//Asus//Downloads//RedLogo.jpg"),
    BLUE("BLUE CERTIFICATE", new DeviceRgb(0, 102, 204), 22, "/C://Users//Asus//Downloads//blueLogo.jpg"),
    SIMPLE("SIMPLE CERTIFICATE", new DeviceRgb(255, 165, 0), 20, "/C://Users//Asus//Downloads//BlackLogo.jpg");

    private final String title;
    private final DeviceRgb color;
    private final int fontSize;
    private final String logoPath;

    CertificateTemplate(String title, DeviceRgb color, int fontSize, String logoPath) {
        this.title = title;
        this.color = color;
        this.fontSize = fontSize;
        this.logoPath = logoPath;
    }

    public ByteArrayOutputStream generateCertificate(String userName, String courseTitle, String score) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDocument);


        // Fonts
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Certificate Title
        Paragraph titleParagraph = new Paragraph(this.title)
                .setFont(boldFont)
                .setFontSize(this.fontSize)
                .setFontColor(this.color)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titleParagraph);

        // Logo (if any)
        try {
            ImageData imageData = ImageDataFactory.create(this.logoPath);
            Image logo = new Image(imageData).setWidth(100).setHeight(100).setTextAlignment(TextAlignment.CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("Logo not loaded: " + e.getMessage());
        }

        // User Information
        document.add(new Paragraph("This certificate is presented to \n" + userName)
                .setFont(regularFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("You have successfully completed the " + courseTitle + " course!")
                .setFont(regularFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

        // Score and Date
        document.add(new Paragraph("Your score: " + score)
                .setFont(regularFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Date: " + LocalDateTime.now())
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        // Signature Field
        document.add(new Paragraph("Signature: ___________________")
                .setFont(boldFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return outputStream;
    }
}
