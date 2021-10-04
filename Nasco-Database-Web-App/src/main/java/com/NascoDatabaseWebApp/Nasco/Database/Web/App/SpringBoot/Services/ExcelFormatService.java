package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;

@Service
public class ExcelFormatService {

    public ByteArrayInputStream formatAlgomaReport(MultipartFile excelInventoryReport) {
        List<String> utilizedColumns = Arrays.asList("Client Inventory", "Order", "Receiver", "Heat Number", "Mark", "Scope", "Other", "Weight per Unit", "Dimensions");

        return formatInventoryReport(excelInventoryReport, utilizedColumns);
    }

    public ByteArrayInputStream formatSsabReport(MultipartFile excelInventoryReport) {
        List<String> utilizedColumns = Arrays.asList("Client Name", "Order", "Receiver", "Product Type", "Quantity", "PO Number", "Lot Number", "Mark", "Other", "Weight per Unit", "Total Weight", "Dimensions", "Quantity per Package");

        return formatInventoryReport(excelInventoryReport, utilizedColumns);
    }

    private  ByteArrayInputStream formatInventoryReport(MultipartFile excelInventoryReport, List<String> utilizedColumns) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(excelInventoryReport.getInputStream()); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet workSheet = workbook.getSheetAt(0);

            workSheet = deleteUnusedColumns(workSheet, utilizedColumns);

            workSheet = formatAsTable(workSheet);

            workSheet = autoFitColumns(workSheet);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Unable to load data from Excel file: " + e.getMessage());
        }
    }

    // Remove all columns who's headers are not specified within utilizedColumns list
    public XSSFSheet deleteUnusedColumns(XSSFSheet workSheet, List<String> utilizedColumns) {
        XSSFRow firstRow = workSheet.getRow(0);

        int i = 0; int j = firstRow.getPhysicalNumberOfCells();

        while (i < j || firstRow.getCell(i).getRawValue().equals("")) {
            if (!utilizedColumns.contains(firstRow.getCell(i).getRawValue())) {
                workSheet.shiftColumns(i, i+1, -1);
                j--;
            } else {
                i++;
            }
        }
        return workSheet;
    }

    public XSSFSheet formatAsTable(XSSFSheet workSheet) {
        return workSheet;
    }

    public XSSFSheet autoFitColumns(XSSFSheet workSheet) {

        return workSheet;
    }
}
