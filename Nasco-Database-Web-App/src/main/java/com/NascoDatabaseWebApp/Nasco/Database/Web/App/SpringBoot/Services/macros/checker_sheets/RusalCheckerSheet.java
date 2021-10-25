package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.checker_sheets;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.util.*;

public class RusalCheckerSheet {

    private final List<String> HEADERS = Arrays.asList("Heat Number", "BL Number", "Mark", "Lot");

    public ByteArrayInputStream createCheckerSheet(List<RusalLineItem> items) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        addIdentifierToSheet(sheet, items.get(0).getBarge());

        Map<String, List<String>> hm = getHeaderMap(items);

        addHeaderRowsToSheet(sheet, hm.keySet());

        addItemsToSheet(sheet, hm);

        createTables(sheet);

        autoFitColumns(sheet);

        return ExcelHelper.writeWorkbookToStream(workbook);
    }

    private void addIdentifierToSheet(Sheet sheet, String identifier) {
        /* TODO - Add Implementation */
    }

    private void addHeaderRowsToSheet(Sheet sheet, Set<String> headers) {
        /* TODO - Add Implementation */
    }

    private void addItemsToSheet(Sheet sheet, Map<String, List<String>> itemMap) {
        /* TODO - Add Implementation */
    }
    
    private void createTables(Sheet sheet) {
        /* TODO - Add Implementation */
    }

    private void autoFitColumns(Sheet sheet) {
        /* TODO - Add Implementation */
    }

    private Map<String, List<String>> getHeaderMap(List<RusalLineItem> items) {
        Map<String, List<String>> hm = Map.of(
                HEADERS.get(0), new ArrayList<>(),
                HEADERS.get(1), new ArrayList<>(),
                HEADERS.get(2), new ArrayList<>(),
                HEADERS.get(3), new ArrayList<>()
        );

        items.forEach(item -> {
            hm.get(HEADERS.get(0)).add(item.getHeatNum());
            hm.get(HEADERS.get(1)).add(item.getBlNum());
            hm.get(HEADERS.get(2)).add(item.getMark());
            hm.get(HEADERS.get(3)).add(item.getLot());
        });

        return hm;
    }
}
