package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.SheetContainer;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public abstract class CustomerReport {

    private final HashMap<AcceptedSheet, Integer> sheetMap = new HashMap<>();
    private final List<AcceptedSheet> ACCEPTED_SHEETS;
    private final List<String> INVENTORY_HEADERS;
    private final List<String> TAILGATE_HEADERS;
    public final Workbook outWorkbook = new XSSFWorkbook();
    public final HashMap<String, String> headerReplacementHM = new HashMap<>();

    protected CustomerReport(List<AcceptedSheet> ACCEPTED_SHEETS, List<List<String>> headers) {
        this.ACCEPTED_SHEETS = ACCEPTED_SHEETS;
        this.INVENTORY_HEADERS = headers.get(0);
        this.TAILGATE_HEADERS = headers.get(1);

        sheetMap.put(AcceptedSheet.TAILGATING, 2);
        sheetMap.put(AcceptedSheet.INVENTORY, 0);
    }

    // Creates a customer report given an array of files from user
    public ByteArrayInputStream createReport(MultipartFile[] inFiles) {

        for (MultipartFile inFile : inFiles) {
            Workbook inWorkbook = ExcelHelper.readExcelFile(inFile);

            SheetContainer sheetContainer = getFileType(inFile);

            parseFile(inWorkbook, sheetContainer);
        }
        return ExcelHelper.writeWorkbookToStream(outWorkbook);
    }

    // Returns AcceptedSheet from ACCEPTED_SHEETS if sheet is accepted, otherwise throws Runtime Exception
    public SheetContainer getFileType(MultipartFile inFile) {
        AtomicReference<SheetContainer> result = new AtomicReference<>();
        ACCEPTED_SHEETS.iterator().forEachRemaining(ACCEPTED_SHEET -> {
            if (Objects.requireNonNull(inFile.getOriginalFilename()).toUpperCase(Locale.ROOT).contains(ACCEPTED_SHEET.toString())) {
                int sheetIndex = 0;

                switch (ACCEPTED_SHEET) {
                    case TAILGATING:
                        sheetIndex = 2;
                        break;

                    case INVENTORY:
                        sheetIndex = 0;
                        break;
                }
                result.set(new SheetContainer(ACCEPTED_SHEET, sheetIndex));
            }
        });

        if (result.get() != null) {
            return result.get();
        } else {
            throw new RuntimeException("One of the selected workbooks was not of an accepted format!");
        }
    }

    // Determines action to take depending on accepted file type
    abstract void parseFile(Workbook inWorkbook, SheetContainer sheetContainer);

    public void formatTailgateReport(Sheet inSheet) {
        formatSheetAsTable(inSheet, outWorkbook, TAILGATE_HEADERS, "Shipped", "shipped", "TableStyleLight8", 18);
    }

    public void formatInventoryReport(Sheet inSheet) {
        formatSheetAsTable(inSheet, outWorkbook, INVENTORY_HEADERS, "Inventory", "inventory", "TableStyleLight8", 1);
    }

    private void formatSheetAsTable(Sheet inSheet, Workbook outWorkbook, List<String> headers, String title, String tableTitle, String tableStyle, int firstRowsToBeRemoved) {

        Sheet outSheet = outWorkbook.createSheet(title);

        ExcelHelper.removeFirstRows(inSheet, firstRowsToBeRemoved);
        ExcelHelper.deleteUnusedColumns(inSheet, headers);

        if (!headerReplacementHM.isEmpty()) {
            ExcelHelper.replaceHeaderValues(inSheet, headerReplacementHM);
        }

        int lastCol = inSheet.getRow(0).getLastCellNum();

        if (tableTitle != null) {
            int lastRow = ExcelHelper.getLastNonNullRow(inSheet);

            AreaReference reference = outWorkbook.getCreationHelper().createAreaReference(
                    new CellReference(0, 0), new CellReference(lastRow, lastCol-1)
            );
            ExcelHelper.formatAsTable((XSSFSheet) outSheet, reference, lastCol, tableTitle, tableStyle);
        }

        ExcelHelper.copyToWorkSheet(inSheet, outSheet);
        IntStream.range(0,lastCol).forEach(outSheet::autoSizeColumn);
    }
}
