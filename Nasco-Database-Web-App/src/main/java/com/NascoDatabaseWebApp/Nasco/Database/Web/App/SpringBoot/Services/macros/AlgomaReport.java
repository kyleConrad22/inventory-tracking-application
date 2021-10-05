package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.apache.poi.ss.usermodel.Workbook;
import java.util.Arrays;

public class AlgomaReport extends CustomerReport {

    public AlgomaReport() {
        super(Arrays.asList(AcceptedSheet.INVENTORY, AcceptedSheet.TAILGATE));
    }

    @Override
    public void parseFile(Workbook inWorkbook) {
        switch (getFileType(inWorkbook)) {
            case INVENTORY:
                break;

            case TAILGATE:
                break;
        }
    }
}
