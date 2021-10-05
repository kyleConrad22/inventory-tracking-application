package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper.readExcelFile;

public abstract class CustomerReport {

    private final List<AcceptedSheet> ACCEPTED_SHEETS;
    private final Workbook outWorkbook;


    protected CustomerReport (List<AcceptedSheet> ACCEPTED_SHEETS) {
        this.ACCEPTED_SHEETS = ACCEPTED_SHEETS;
        outWorkbook = new XSSFWorkbook();
    }

    // Creates a customer report given an array of files from user
    public ByteArrayInputStream createReport(MultipartFile[] inFiles) {

        for (MultipartFile inFile : inFiles) {
            Workbook inWorkbook = ExcelHelper.readExcelFile(inFile);

            parseFile(inWorkbook);
        }
        return ExcelHelper.writeWorkbookToStream(outWorkbook);
    }

    // Returns AcceptedSheet from ACCEPTED_SHEETS if sheet is accepted, otherwise throws Runtime Exception
    public AcceptedSheet getFileType(Workbook workbook) {
        AtomicReference<AcceptedSheet> result = new AtomicReference<>();
        workbook.sheetIterator().forEachRemaining(sheet -> {
            for (AcceptedSheet ACCEPTED_SHEET : ACCEPTED_SHEETS) {
                if (sheet.getSheetName().toUpperCase(Locale.ROOT).contains(ACCEPTED_SHEET.toString())) {
                    result.set(ACCEPTED_SHEET);
                }
            }
        });
        if (result.get() != null) {
            return result.get();
        } else {
            throw new RuntimeException("One of the selected workbooks was not of an accepted format!");
        }
    }

    // Determines action to take depending on accepted file type
    abstract void parseFile(Workbook inWorkbook);
}
