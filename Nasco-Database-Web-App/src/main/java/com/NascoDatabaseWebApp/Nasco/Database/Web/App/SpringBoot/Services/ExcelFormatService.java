package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
        try (InputStream in = excelInventoryReport.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Workbook workbook = new XSSFWorkbook(in);

            Sheet workSheet = workbook.getSheetAt(0);

            try {

                workSheet = removeFirstRow(workSheet);

                workSheet = deleteUnusedColumns(workSheet, utilizedColumns);

                int lastRow = workSheet.getLastRowNum();
                int lastCol = workSheet.getRow(0).getLastCellNum();

                System.out.println("Rows: " + lastRow + "Cols: " + lastCol);

                Workbook resultWorkbook = new XSSFWorkbook();

                Sheet resultSheet = resultWorkbook.createSheet("Inventory Report");

                Sheet finalWorkSheet = workSheet;

                AreaReference reference = resultWorkbook.getCreationHelper().createAreaReference(
                        new CellReference(0,0), new CellReference(lastRow-1, lastCol-1)
                );

                formatAsTable((XSSFSheet) resultSheet, reference, lastCol);

                IntStream.range(0, lastRow).forEach(i -> {
                    Row row = resultSheet.createRow(i);
                    IntStream.range(0, lastCol).forEach(j -> {
                        Cell cell = finalWorkSheet.getRow(i).getCell(j);

                        String value;
                        switch (cell.getCellType()) {
                            case BOOLEAN:
                                value = Boolean.toString(cell.getBooleanCellValue());
                                break;

                            case NUMERIC:
                                value = Double.toString(cell.getNumericCellValue());
                                break;

                            case STRING:
                                value = cell.getStringCellValue();
                                break;

                            default:
                                value = "";
                                break;
                        }
                        row.createCell(j).setCellValue(value);
                    });
                });

                IntStream.range(0,lastCol).forEach(resultSheet::autoSizeColumn);

                resultWorkbook.write(out);

            } finally {
                try {
                    in.close();
                } catch (Exception ignore) {}
            }

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Unable to load data from Excel file: " + e.getMessage());
        }
    }

    public Sheet removeFirstRow(Sheet workSheet) {
        int lastRow = workSheet.getLastRowNum();

        workSheet.shiftRows(1, lastRow, -1);

        return workSheet;
    }

    // Remove all columns who's headers are not specified within utilizedColumns list
    public Sheet deleteUnusedColumns(Sheet workSheet, List<String> utilizedColumns) {
        Row firstRow = workSheet.getRow(0);

        int i = 0; int j = firstRow.getPhysicalNumberOfCells();

        while (firstRow.getCell(i) != null && i < j) {

            if (!utilizedColumns.contains(firstRow.getCell(i).getStringCellValue())) {
                workSheet.shiftColumns(i+1, firstRow.getLastCellNum(), -1);
                j--;
            } else {
                i++;
            }
        }
        return workSheet;
    }

    public Sheet formatAsTable(XSSFSheet workSheet, AreaReference reference, int lastColumn) {
        XSSFTable table = workSheet.createTable(reference);
        IntStream.range(1, lastColumn).forEach(j -> {
            table.getCTTable().getTableColumns().getTableColumnArray(j).setId(j+1);
        });
        table.getCTTable().addNewAutoFilter().setRef(table.getArea().formatAsString());

        table.setName("inventory");
        table.setDisplayName("inventory");
        table.getCTTable().addNewTableStyleInfo().setName("TableStyleLight8");

        return workSheet;
    }

}
