package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jdbc.Work;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Load Information";
    static String[] HEADERS = {"Heat Number", "Package Number", "Net Weight Kg", "Gross Weight Kg", "Net Weight Lbs", "Gross Weight Lbs", "Quantity", "Dimensions", "Grade", "Certificate Number", "BL", "Barcode", "Order", "Load", "Loader", "Load Date"};

    // Create an excel worksheet representation of items
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

    public static Workbook readSheetFromExcel(MultipartFile excelFile) {
        try (InputStream in = excelFile.getInputStream()) {

            try {
                return new XSSFWorkbook(in);

            } finally {
                try {
                    in.close();
                } catch (Exception ignore) {}
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to load data from Excel file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream writeWorkbookToStream(Workbook workbook) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Unable to write data to Excel file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream formatAlgomaReport(MultipartFile excelFile, int sheetIndex, List<String> headers, HashMap<String, String> replace) {
        Workbook workbook = readSheetFromExcel(excelFile);

        Workbook result = formatReport(workbook.getSheetAt(sheetIndex), headers, replace, "Inventory Report", "inventory", "TableStyleLight8", 18);

        return writeWorkbookToStream(result);
    }

    public static ByteArrayInputStream formatSsabReport(MultipartFile excelFile, int sheetIndex, List<String> headers) {
        Workbook workbook = readSheetFromExcel(excelFile);

        Workbook result = formatReport(workbook.getSheetAt(sheetIndex), headers, null, "Inventory Report", "inventory", "TableStyleLight8", 1);

        return writeWorkbookToStream(result);
    }

    // Format input inventory report as defined by headers and replace values
    public static Workbook formatReport(Sheet worksheet, List<String> headers, HashMap<String, String> replace, String title, String tableTitle, String tableStyle, int firstRowsToBeRemoved) {

        removeFirstRows(worksheet, firstRowsToBeRemoved);
        deleteUnusedColumns(worksheet, headers);

        if (replace != null) {
            replaceHeaderValues(worksheet, replace);
        }

        Workbook resultWorkbook = new XSSFWorkbook();
        Sheet resultSheet = resultWorkbook.createSheet(title);

        int lastCol = worksheet.getRow(0).getLastCellNum();

        if (tableTitle != null) {
            int lastRow = 0;
            while (worksheet.getRow(lastRow+1) != null) {
                lastRow++;
            }

            AreaReference reference = resultWorkbook.getCreationHelper().createAreaReference(
                    new CellReference(0, 0), new CellReference(lastRow, lastCol)
            );
            formatAsTable((XSSFSheet) resultSheet, reference, lastCol, tableTitle, tableStyle);
        }

        copyToWorkSheet(worksheet, resultSheet);
        IntStream.range(0,lastCol).forEach(resultSheet::autoSizeColumn);

        return resultWorkbook;
    }

    // Remove first row of worksheet
    public static void removeFirstRows(Sheet worksheet, int rows) {
        int lastRow = worksheet.getLastRowNum();

        worksheet.shiftRows(rows, lastRow, -rows);
    }

    // Remove all columns who's headers are not specified within headers list
    public static void deleteUnusedColumns(Sheet worksheet, List<String> headers) {
        Row firstRow = worksheet.getRow(0);

        int i = 0; int j = firstRow.getPhysicalNumberOfCells();

        while (firstRow.getCell(i) != null && i < j) {

            if (!headers.contains(firstRow.getCell(i).getStringCellValue())) {
                worksheet.shiftColumns(i+1, firstRow.getLastCellNum(), -1);
                j--;
            } else {
                i++;
            }
        }
    }

    // Format add table to worksheet with given specifications
    public static void formatAsTable(XSSFSheet worksheet, AreaReference reference, int lastTableColumn, String title, String tableStyle) {
        XSSFTable table = worksheet.createTable(reference);

        IntStream.range(1, lastTableColumn).forEach(j -> {
            table.getCTTable().getTableColumns().getTableColumnArray(j).setId(j+1);
        });

        table.getCTTable().addNewAutoFilter().setRef(table.getArea().formatAsString());

        table.setName(title);
        table.setDisplayName(title);
        table.getCTTable().addNewTableStyleInfo().setName(tableStyle);
    }

    // Copy all cells in source to receiver
    public static void copyToWorkSheet(Sheet source, Sheet receiver) {
        int lastRow = 0;
        while (source.getRow(lastRow+1) != null) {
            lastRow++;
        }

        int lastCol = source.getRow(0).getLastCellNum();

        System.out.println("Rows: " + lastRow + "Cols: " + lastCol);

        IntStream.range(0, lastRow).forEach(i -> {

            Row row = receiver.createRow(i);
            IntStream.range(0, lastCol).forEach(j -> {

                Cell cell = source.getRow(i).getCell(j);

                System.out.println(cell.getStringCellValue());

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
    }

    // Replace header values with values defined by replace hashmap
    public static void replaceHeaderValues(Sheet worksheet, HashMap<String, String> replace) {
        Row firstRow = worksheet.getRow(0);
        int lastCol = firstRow.getLastCellNum();

        IntStream.range(0, lastCol).forEach(j -> {
            if (replace.containsKey(firstRow.getCell(j).getStringCellValue())) {
                firstRow.getCell(j).setCellValue(replace.get(firstRow.getCell(j).getStringCellValue()));
            }
        });
    }
}
