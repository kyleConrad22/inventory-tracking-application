package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface PdfRelease {

    default PDDocument readFile(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {

            try {
                return Loader.loadPDF(in);
            } finally {
                try {
                    in.close();
                } catch (Exception ignore) {}
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load data from PDF: " + e.getMessage());
        }
    }

    default String convertToText(PDDocument pdf) {
        PDFTextStripper stripper = new PDFTextStripper();
        try {
            return stripper.getText(pdf);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert PDF to text: " + e.getMessage());
        }
    }

    Release parseRelease(String convertedFile);
}
