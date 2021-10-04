package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Load Information";
    static String[] HEADERS = {"Heat Number", "Package Number", "Net Weight Kg", "Gross Weight Kg", "Net Weight Lbs", "Gross Weight Lbs", "Quantity", "Dimensions", "Grade", "Certificate Number", "BL", "Barcode", "Order", "Load", "Loader", "Load Date"};

    public static ByteArrayInputStream loadToExcel(List<RusalLineItem> lineItems) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            int rowInd = 1;
            for (RusalLineItem lineItem: lineItems){
                Row row = sheet.createRow(rowInd++);

                row.createCell(0).setCellValue(lineItem.getHeatNum());
                row.createCell(1).setCellValue(lineItem.getPackageNum());
                row.createCell(2).setCellValue(lineItem.getNetWeightKg());
                row.createCell(3).setCellValue(lineItem.getGrossWeightKg());
                row.createCell(4).setCellValue(Long.parseLong(lineItem.getNetWeightKg())*2.20462);
                row.createCell(5).setCellValue(Long.parseLong(lineItem.getGrossWeightKg())*2.20462);
                row.createCell(6).setCellValue(lineItem.getQuantity());
                row.createCell(7).setCellValue(lineItem.getDimension());
                row.createCell(8).setCellValue(lineItem.getGrade());
                row.createCell(9).setCellValue(lineItem.getCertificateNum());
                row.createCell(10).setCellValue(lineItem.getBlNum());
                row.createCell(11).setCellValue(lineItem.getBarcode());
                row.createCell(12).setCellValue(lineItem.getWorkOrder());
                row.createCell(13).setCellValue(lineItem.getLoadNum());
                row.createCell(14).setCellValue(lineItem.getLoader());
                row.createCell(15).setCellValue(lineItem.getLoadTime());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to import data to Excel file: " + e.getMessage());
        }
    }
}
