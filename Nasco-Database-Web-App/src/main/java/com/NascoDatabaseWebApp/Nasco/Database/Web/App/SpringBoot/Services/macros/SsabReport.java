package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.SheetContainer;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Arrays;
import java.util.Collections;

public class SsabReport extends CustomerReport {

    public SsabReport() {
        super(
                Collections.singletonList(AcceptedSheet.INVENTORY),
          Arrays.asList(
              Arrays.asList("Client Name", "Order", "Receiver", "Product Type", "Quantity", "PO Number", "Lot Number", "Mark", "Other", "Weight per Unit", "Total Weight", "Dimensions", "Quantity per Package"),
              Collections.emptyList()
          )
        );
    }

    @Override
    void parseFile(Workbook inWorkbook, SheetContainer sheetContainer) {
        formatInventoryReport(inWorkbook.getSheetAt(sheetContainer.sheetIndex));
    }
}
