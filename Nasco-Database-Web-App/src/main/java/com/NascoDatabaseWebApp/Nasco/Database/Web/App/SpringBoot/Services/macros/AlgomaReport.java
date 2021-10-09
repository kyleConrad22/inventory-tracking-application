package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.SheetContainer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AlgomaReport extends CustomerReport {

    public AlgomaReport() {
        super(
            Arrays.asList(AcceptedSheet.INVENTORY, AcceptedSheet.TAILGATING),
            Arrays.asList(
                Arrays.asList("Client Inventory", "Order", "Receiver", "Heat Number", "Mark", "Scope", "Other", "Weight per Unit", "Dimensions", "Quantity"),
                Arrays.asList("ROM/SOM Number", "Carrier", "Movement Date", "Receiver", "Order", "Heat Number", "Mark", "Scope", "Other", "Dimensions", "Total Weight", "Quantity")
            )
        );
        headerReplacementHM.put("Other", "Date Received");
    }

    @Override
    public void parseFile(Workbook workbook, SheetContainer sheetContainer) {
        switch (sheetContainer.sheetType) {
            case INVENTORY:
                formatInventoryReport(workbook.getSheetAt(sheetContainer.sheetIndex));
                break;

            case TAILGATING:
                formatTailgateReport(workbook.getSheetAt(sheetContainer.sheetIndex));
                createCoilBreakdown(outWorkbook);
                break;
        }
    }

    private void createCoilBreakdown(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Shipped");
        int lastRow = sheet.getLastRowNum();
        HashMap<String, Integer> orderCounts = countUniqueEntries(sheet, lastRow);

        addBreakdownTitle(workbook, sheet, lastRow + 4);
        lastRow = addOrderTable(workbook, sheet, lastRow + 5, orderCounts);

        addTotalCount(workbook, sheet, lastRow + 1, orderCounts);
    }

    private void addBreakdownTitle(Workbook workbook, Sheet sheet, int rowIndex) {
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 2, 3));

        Cell cell = sheet.createRow(rowIndex).createCell(2);
        cell.setCellValue("Customer Coil Breakdown");

        CellStyle style = workbook.createCellStyle();
        Font font  = workbook.createFont();

        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        cell.setCellStyle(style);
    }

    private int addOrderTable(Workbook workbook, Sheet sheet, int startRow, HashMap<String, Integer> orderCounts) {

        AreaReference areaReference = workbook.getCreationHelper().createAreaReference(
                new CellReference(startRow, 2), new CellReference(startRow + orderCounts.size(), 3)
        );

        ExcelHelper.formatAsTable((XSSFSheet) sheet, areaReference, 2, "breakdown", "TableStyleLight11");

        sheet.createRow(startRow).createCell(2).setCellValue("Receiver");
        sheet.getRow(startRow).createCell(3).setCellValue("Total");

        AtomicInteger i = new AtomicInteger(startRow + 1);
        orderCounts.forEach((order, count) -> {
            sheet.createRow(i.get()).createCell(2).setCellValue(order);
            sheet.getRow(i.get()).createCell(3).setCellValue(count);
            i.getAndIncrement();
        });

        return i.get();
    }

    private void addTotalCount(Workbook workbook, Sheet sheet, int rowIndex, HashMap<String, Integer> orderCounts) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Cell cell = sheet.createRow(rowIndex).createCell(2);
        cell.setCellValue("Coil Total");
        cell.setCellStyle(style);

        AtomicInteger total = new AtomicInteger(0);
        orderCounts.values().forEach(total::addAndGet);

        cell = sheet.getRow(rowIndex).createCell(3);
        cell.setCellValue(total.get());
        cell.setCellStyle(style);
    }

    private HashMap<String, Integer> countUniqueEntries(Sheet sheet, int lastRow) {
        IntStream rowsIndexes = IntStream.range(1, lastRow + 1);
        HashMap<String, Integer> entryCounts = new HashMap<>();


        rowsIndexes.forEach(i -> {
            String order = sheet.getRow(i).getCell(2).getStringCellValue();
            if (entryCounts.containsKey(order)) {
                entryCounts.put(order, entryCounts.get(order) + 1);

            } else {
                entryCounts.put(order, 1);
            }
        });
        return entryCounts;
    }
}
